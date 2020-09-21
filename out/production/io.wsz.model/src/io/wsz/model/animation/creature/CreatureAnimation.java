package io.wsz.model.animation.creature;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;
import java.util.*;

import static io.wsz.model.sizes.Paths.*;

public class CreatureAnimation extends Animation<Creature> {
    private static final int MIN_STOP_WAIT_TIME_SEC = 2;
    private static final int MAX_STOP_WAIT_TIME_SEC = 3;
    private static final List<String> EQUIPPED_ITEMS_NAMES = new ArrayList<>(0);
    private static final StringBuilder BUILD_ANIMATION_NAME = new StringBuilder();

    private final Map<String, Map<String, List<ResolutionImage>>> walk = new HashMap<>(0);
    private final Map<String, File> creatureInventoryFiles = new HashMap<>(0);
    private final Map<String, ResolutionImage> creatureInventoryPictures = new HashMap<>(0);

    public CreatureAnimation(String animationDir, String idlesOrEquivalent) {
        super(animationDir, idlesOrEquivalent);
    }

    @Override
    public void play(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        CreatureAnimationType curCreatureAnimationType = animationPos.getCurAnimation();

        resolveEquippedItemsAnimationName(cr, animationPos);

        ResolutionImage nextFrame = switch (curCreatureAnimationType) {
            case IDLE -> getIdle(cr);
            case MOVE -> getMove(cr);
            case STOP -> getStop(cr);
        };
        if (nextFrame == null) return;
        cr.setImage(nextFrame);
    }

    private void resolveEquippedItemsAnimationName(Creature cr, CreatureAnimationPos animationPos) {
        String animationToPlay = BASIC;
        Map<InventoryPlaceType, Equipment> equippedItems = cr.getInventory().getEquippedItems();
        if (!equippedItems.isEmpty()) {
            EQUIPPED_ITEMS_NAMES.clear();
            equippedItems.values().stream()
                    .forEach(e -> EQUIPPED_ITEMS_NAMES.add(e.getEquipmentType().getId()));
            EQUIPPED_ITEMS_NAMES.sort(Comparator.naturalOrder());
            BUILD_ANIMATION_NAME.delete(0, BUILD_ANIMATION_NAME.length());
            for (int i = 0; i < EQUIPPED_ITEMS_NAMES.size(); i++) {
                String subName = EQUIPPED_ITEMS_NAMES.get(i);
                if (i != 0) {
                    BUILD_ANIMATION_NAME.append("_");
                }
                BUILD_ANIMATION_NAME.append(subName);
            }
            animationToPlay = BUILD_ANIMATION_NAME.toString();
        }
        animationPos.setCurIdleAnimation(animationToPlay);
        animationPos.setCurMoveAnimation(animationToPlay);
    }

    @Override
    public void initOtherAnimations(File framesDir, String fileName) {
        switch (fileName) {
            case INVENTORY -> initInventoryCreaturePicturesFiles(framesDir, creatureInventoryFiles);
            case WALK -> initAnimations(framesDir, walk);
        }
    }

    private void initInventoryCreaturePicturesFiles(File inventoryDir, Map<String, File> creatureInventoryFiles) {
        creatureInventoryFiles.clear();
        File[] inventoryFiles = inventoryDir.listFiles();
        if (inventoryFiles == null || inventoryFiles.length == 0) return;
        for (File inventoryFile : inventoryFiles) {
            boolean isNotPNGfile = !inventoryFile.getName().endsWith(PNG);
            if (isNotPNGfile) continue;
            String fileName = inventoryFile.getName().replace(PNG, "");
            creatureInventoryFiles.put(fileName, inventoryFile);
        }
    }

    public ResolutionImage getCreatureInventoryImage(String name, int width, int height) {
        ResolutionImage inventoryImage = creatureInventoryPictures.get(name);
        if (inventoryImage == null) {
            File inventoryFile = creatureInventoryFiles.get(name);
            if (inventoryFile != null && inventoryFile.exists()) {
                inventoryImage = new ResolutionImage(inventoryFile, width, height);
                creatureInventoryPictures.put(name, inventoryImage);
            }
        }
        return inventoryImage;
    }

    public ResolutionImage getInventoryBasicForEditor(File programDir) {
        ResolutionImage basicInventoryImage = creatureInventoryPictures.get(BASIC);
        if (basicInventoryImage == null) {
            String path = programDir + animationDir + INVENTORY_DIR + BASIC_DIR + PNG;
            basicInventoryImage = new ResolutionImage(path);
            creatureInventoryPictures.put(BASIC, basicInventoryImage);
        }
        return basicInventoryImage;
    }

    private ResolutionImage getStop(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();

        int randomTimeToStartIdleMillis = getRandomMillis(MAX_STOP_WAIT_TIME_SEC, MIN_STOP_WAIT_TIME_SEC);
        long curTime = System.currentTimeMillis();
        long timeToPlayIdleAfterStop = curTime + randomTimeToStartIdleMillis;
        animationPos.setNextTimeToStartPlayIdleAfterStop(timeToPlayIdleAfterStop);

        String curMoveAnimation = animationPos.getCurMoveAnimation();
        String moveDirection = animationPos.getMoveDirection();
        String stopPath = moveDirection + DIVIDER + STOP;
        Map<String, List<ResolutionImage>> moveAnimations = walk.get(curMoveAnimation);
        List<ResolutionImage> frames;
        if (moveAnimations == null) {
            frames = getBasicMoveDirection(stopPath);
        } else {
            frames = moveAnimations.get(stopPath);
        }

        if (frames == null) {
            frames = getBasicMoveDirection(stopPath);
            if (frames == null) return null;
        }

        int framesSize = frames.size();
        if (framesSize == 0) return null;

        long nextUpdate = getNextUpdate(framesSize, cr.getAnimationSpeed());
        animationPos.setNextFrameUpdate(nextUpdate);
        int nextFrameNumber = animationPos.getNextFrameNumber(framesSize);
        return frames.get(nextFrameNumber);
    }

    private ResolutionImage getMove(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        long curTime = System.currentTimeMillis();
        if (curTime < animationPos.getNextFrameUpdate()) return null;

        String nextMoveDirection = getMoveDirection(cr);
        animationPos.setMoveDirection(nextMoveDirection);

        String curMoveAnimation = animationPos.getCurMoveAnimation();
        Map<String, List<ResolutionImage>> moveAnimations = walk.get(curMoveAnimation);
        List<ResolutionImage> frames;
        if (moveAnimations == null) {
            frames = getBasicMoveDirection(nextMoveDirection);
        } else {
            frames = moveAnimations.get(nextMoveDirection);
        }

        if (frames == null) {
            frames = getBasicMoveDirection(nextMoveDirection);
            if (frames == null) return null;
        }
        int framesSize = frames.size();
        if (framesSize == 0) return null;

        long nextUpdate = getNextUpdate(framesSize, cr.getAnimationSpeed());
        animationPos.setNextFrameUpdate(nextUpdate);
        int nextFrameNumber = animationPos.getNextFrameNumber(framesSize);
        return frames.get(nextFrameNumber);
    }

    private List<ResolutionImage> getBasicMoveDirection(String nextMoveDirection) {
        return walk.get(BASIC).get(nextMoveDirection);
    }

    public String getMoveDirection(Creature cr) {
        Coords pos = cr.getPos();
        double xFrom = pos.x;
        double yFrom = pos.y;
        Coords dest = cr.getTask().getDest();
        double xTo = dest.x;
        double yTo = dest.y;

        return getMoveDirection(xFrom, yFrom, xTo, yTo);
    }

    public String getMoveDirection(double xFrom, double yFrom, double xTo, double yTo) {
        xTo -= xFrom;
        yTo -= yFrom;
        yTo = -yTo;
        double moveRadian = Math.atan2(yTo, xTo);
        String nextMoveDirection;
        double one8 = Math.PI / 4;
        double one16 = Math.PI / 8;
        double nextRadian = one16;

        if (moveRadian >= nextRadian && moveRadian < (nextRadian += one8)) {
            nextMoveDirection = NE;
        } else if (moveRadian >= nextRadian && moveRadian < (nextRadian += one8)) {
            nextMoveDirection = N;
        } else if (moveRadian >= nextRadian && moveRadian < (nextRadian += one8)) {
            nextMoveDirection = NW;
        } else if (moveRadian >= nextRadian && moveRadian < (nextRadian + one8)) {
            nextMoveDirection = W;
        } else if (moveRadian <= (nextRadian = -one16) && moveRadian > (nextRadian -= one8)) {
            nextMoveDirection = SE;
        } else if (moveRadian <= nextRadian && moveRadian > (nextRadian -= one8)) {
            nextMoveDirection = S;
        } else if (moveRadian <= nextRadian && moveRadian > (nextRadian -= one8)) {
            nextMoveDirection = SW;
        } else if (moveRadian <= nextRadian && moveRadian > (nextRadian - one8)) {
            nextMoveDirection = W;
        } else {
            nextMoveDirection = E;
        }
        return nextMoveDirection;
    }

    protected ResolutionImage getIdle(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        return getNextIdle(animationPos, cr.getAnimationSpeed());
    }

    @Override
    protected ResolutionImage getNextIdle(AnimationPos animationPos, double speed) {
        long curTime = System.currentTimeMillis();

        long timeToStartPlayIdleAfterStop = ((CreatureAnimationPos) animationPos).getNextTimeToStartPlayIdleAfterStop();
        if (curTime < timeToStartPlayIdleAfterStop) return null;
        return super.getNextIdle(animationPos, speed);
    }

    public void clearInventoryPictures() {
        creatureInventoryPictures.clear();
    }
}
