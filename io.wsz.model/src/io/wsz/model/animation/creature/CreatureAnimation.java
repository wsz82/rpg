package io.wsz.model.animation.creature;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;
import java.util.*;

import static io.wsz.model.sizes.Paths.*;

public class CreatureAnimation extends Animation<Creature> {
    private static final int MIN_PORTRAIT_UPDATE_TIME_SEC = 1;
    private static final int MAX_PORTRAIT_UPDATE_TIME_SEC = 4;
    private static final int MIN_STOP_WAIT_TIME_SEC = 2;
    private static final int MAX_STOP_WAIT_TIME_SEC = 3;
    private static final List<String> EQUIPPED_ITEMS_NAMES = new ArrayList<>(0);
    private static final StringBuilder BUILD_ANIMATION_NAME = new StringBuilder();

    private final Map<String, Map<String, List<ResolutionImage>>> walk = new HashMap<>(0);
    private final List<File> portraitsFiles = new ArrayList<>(0);
    private final List<ResolutionImage> portraits = new ArrayList<>(0);
    private final Map<String, File> creatureInventoryFiles = new HashMap<>(0);
    private final Map<String, ResolutionImage> creatureInventoryPictures = new HashMap<>(0);

    public CreatureAnimation(String animationDir) {
        super(animationDir);
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
            case PORTRAIT -> initPortraitPicturesFiles(framesDir, portraitsFiles);
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

    private void initPortraitPicturesFiles(File framesDir, List<File> portraitsFiles) {
        portraitsFiles.clear();
        File[] imagesFiles = framesDir.listFiles(PNG_FILE_FILTER);
        if (imagesFiles == null || imagesFiles.length == 0) return;
        for (File portraitFile : imagesFiles) {
            boolean isNotPNGfile = !portraitFile.getName().endsWith(PNG);
            if (isNotPNGfile) continue;
            portraitsFiles.add(portraitFile);
        }
    }

    public ResolutionImage getPortrait(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        long curTime = System.currentTimeMillis();
        if (curTime < animationPos.getNextPortraitUpdate()) return null;
        int randomDifTimeMillis = getRandomMillis(MAX_PORTRAIT_UPDATE_TIME_SEC, MIN_PORTRAIT_UPDATE_TIME_SEC);
        long nextPortraitUpdate = curTime + randomDifTimeMillis;
        animationPos.setNextPortraitUpdate(nextPortraitUpdate);
        List<ResolutionImage> portraits = getPortraits();
        int portraitsSize = portraits.size();
        if (portraitsSize == 0) return null;
        int randomIndex = RANDOM.nextInt(portraitsSize);
        return portraits.get(randomIndex);
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
        List<ResolutionImage> frames = walk.get(curMoveAnimation).get(stopPath);

        if (frames == null) {
            frames = walk.get(BASIC).get(stopPath);
            if (frames == null) {
                return null;
            }
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
        List<ResolutionImage> frames = walk.get(curMoveAnimation).get(nextMoveDirection);

        if (frames == null) {
            frames = walk.get(BASIC).get(nextMoveDirection);
            if (frames == null) {
                return null;
            }
        }
        int framesSize = frames.size();
        if (framesSize == 0) return null;

        long nextUpdate = getNextUpdate(framesSize, cr.getAnimationSpeed());
        animationPos.setNextFrameUpdate(nextUpdate);
        int nextFrameNumber = animationPos.getNextFrameNumber(framesSize);
        return frames.get(nextFrameNumber);
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

    public List<ResolutionImage> getPortraits() {
        int portraitSize = Sizes.getPortraitSize();
        if (portraitSize <= 0) return null;
        if (portraits.isEmpty()) {
            for (File imageFile : portraitsFiles) {
                ResolutionImage loadedFrame = new ResolutionImage(imageFile, portraitSize, portraitSize);
                portraits.add(loadedFrame);
            }
        }
        return portraits;
    }

    public void clearResizablePictures() {
        portraits.clear();
        creatureInventoryPictures.clear();
    }
}
