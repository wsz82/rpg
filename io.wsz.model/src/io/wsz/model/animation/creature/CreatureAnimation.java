package io.wsz.model.animation.creature;

import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.item.Creature;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.image.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.wsz.model.animation.creature.MoveDirection.*;
import static io.wsz.model.sizes.Paths.*;

public class CreatureAnimation extends Animation<Creature> {
    private static final int MIN_PORTRAIT_UPDATE_TIME_SEC = 1;
    private static final int MAX_PORTRAIT_UPDATE_TIME_SEC = 4;
    private static final int MIN_STOP_WAIT_TIME_SEC = 2;
    private static final int MAX_STOP_WAIT_TIME_SEC = 3;

    private final CreatureMoveAnimationFrames moveUp = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveUpRight = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveRight = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveDownRight = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveDown = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveDownLeft = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveLeft = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveUpLeft = new CreatureMoveAnimationFrames();
    private final List<Image> portraits = new ArrayList<>(0);
    private final Map<String, File> creatureInventoryFiles = new HashMap<>(0);
    private final Map<String, Image> creatureInventoryPictures = new HashMap<>(0);

    public CreatureAnimation(String animationDir) {
        super(animationDir);
    }

    @Override
    public void play(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        CreatureAnimationType curCreatureAnimationType = animationPos.getCurAnimation();
        Image nextFrame = switch (curCreatureAnimationType) {
            case IDLE -> getIdle(cr);
            case MOVE -> getMove(cr);
            case STOP -> getStop(cr);
        };
        if (nextFrame == null) return;
        cr.setImage(nextFrame);
    }

    @Override
    protected void initOtherAnimation(File framesDir, String fileName) {
        CreatureMoveAnimationFrames walkFrames = getMoveAnimationFrames(fileName);
        if (walkFrames != null) {
            initWalkFrames(framesDir, walkFrames);
        } else {
            switch (fileName) {
                case PORTRAIT -> initPortraitFrames(framesDir, portraits);
                case INVENTORY -> initInventoryCreaturePicturesFiles(framesDir, creatureInventoryFiles);
            }
        }
    }

    private void initInventoryCreaturePicturesFiles(File inventoryDir, Map<String, File> creatureInventoryFiles) {
        creatureInventoryFiles.clear();
        File[] inventoryFiles = inventoryDir.listFiles();
        if (inventoryFiles == null || inventoryFiles.length == 0) return;
        for (File inventoryFile : inventoryFiles) {
            boolean isNotPNGfile = !inventoryFile.getName().endsWith(".png");
            if (isNotPNGfile) continue;
            String fileName = inventoryFile.getName().replace(".png", "");
            creatureInventoryFiles.put(fileName, inventoryFile);
        }
    }

    public Image getCreatureInventoryImage(Creature cr, int width, int height) {
        Image emptyInventoryImage = creatureInventoryPictures.get(BASIC);
        if (emptyInventoryImage == null) {
            File emptyInventoryFile = creatureInventoryFiles.get(BASIC);
            if (emptyInventoryFile != null && emptyInventoryFile.exists()) {
                emptyInventoryImage = ResolutionImage.loadDefinedDimensionImage(emptyInventoryFile, width, height);
                creatureInventoryPictures.put(BASIC, emptyInventoryImage);
            }
        }
        return emptyInventoryImage;
    }

    private CreatureMoveAnimationFrames getMoveAnimationFrames(MoveDirection moveDirection) {
        if (moveDirection == null) return null;
        int ordinal = moveDirection.ordinal();
        String walk = walks[ordinal];
        return getMoveAnimationFrames(walk);
    }

    private CreatureMoveAnimationFrames getMoveAnimationFrames(String moveDirection) {
        return switch (moveDirection) {
            case WALK_N -> moveUp;
            case WALK_NE -> moveUpRight;
            case WALK_E -> moveRight;
            case WALK_SE -> moveDownRight;
            case WALK_S -> moveDown;
            case WALK_SW -> moveDownLeft;
            case WALK_W -> moveLeft;
            case WALK_NW -> moveUpLeft;
            default -> null;
        };
    }

    private void initPortraitFrames(File framesDir, List<Image> portrait) {
        portrait.clear();
        File[] imagesFiles = framesDir.listFiles(PNG_FILE_FILTER);
        if (imagesFiles == null || imagesFiles.length == 0) return;
        for (File imageFile : imagesFiles) {
            int portraitSize = Sizes.getPortraitSize();
            Image loadedFrame = ResolutionImage.loadDefinedDimensionImage(imageFile, portraitSize, portraitSize);
            portrait.add(loadedFrame);
        }
    }

    private void initWalkFrames(File framesDir, CreatureMoveAnimationFrames frames) {
        initWalkEmptyFrames(framesDir, frames);
    }

    private void initWalkEmptyFrames(File framesDir, CreatureMoveAnimationFrames frames) {
        String walkEmptyPath = framesDir + BASIC_DIR;
        File walkEmptyDir = new File(walkEmptyPath);
        List<Image> walkEmptyFrames = frames.getWalkEmptyFrames();
        initWalkFrames(frames, walkEmptyDir, walkEmptyFrames);
    }

    private void initWalkFrames(CreatureMoveAnimationFrames frames, File walkDir, List<Image> walkFrames) {
        walkFrames.clear();
        File[] imagesFiles = walkDir.listFiles(PNG_FILE_FILTER);
        if (imagesFiles == null || imagesFiles.length == 0) return;
        for (File imageFile : imagesFiles) {
            Image loadedFrame = ResolutionImage.loadImage(imageFile);
            boolean isStopFrame = imageFile.getName().equals("stop.png");
            if (isStopFrame) {
                frames.setEmptyStop(loadedFrame);
            } else {
                walkFrames.add(loadedFrame);
            }
        }
    }

    public Image getPortrait(Creature cr, File programDir) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        long curTime = System.currentTimeMillis();
        if (curTime < animationPos.getNextPortraitUpdate()) return null;
        int randomDifTimeMillis = getRandomMillis(MAX_PORTRAIT_UPDATE_TIME_SEC, MIN_PORTRAIT_UPDATE_TIME_SEC);
        long nextPortraitUpdate = curTime + randomDifTimeMillis;
        animationPos.setNextPortraitUpdate(nextPortraitUpdate);
        List<Image> portraits = getPortraits(programDir);
        int portraitsSize = portraits.size();
        if (portraitsSize == 0) return null;
        int randomIndex = RANDOM.nextInt(portraitsSize);
        return portraits.get(randomIndex);
    }

    private Image getStop(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();

        int randomTimeToStartIdleMillis = getRandomMillis(MAX_STOP_WAIT_TIME_SEC, MIN_STOP_WAIT_TIME_SEC);
        long curTime = System.currentTimeMillis();
        long timeToPlayIdleAfterStop = curTime + randomTimeToStartIdleMillis;
        animationPos.setTimeToStartPlayIdleAfterStop(timeToPlayIdleAfterStop);

        MoveDirection moveDirection = animationPos.getMoveSide();
        if (moveDirection == null) return null;
        CreatureMoveAnimationFrames animationFrames = getMoveAnimationFrames(moveDirection);
        return animationFrames.getEmptyStop();
    }

    private Image getMove(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        long curTime = System.currentTimeMillis();
        if (curTime < animationPos.getNextFrameUpdate()) return null;

        MoveDirection nextMoveDirection = getMoveDirection(cr);
        if (nextMoveDirection == null) return null;
        animationPos.setMoveSide(nextMoveDirection);

        MoveDirection moveDirection = animationPos.getMoveSide();
        CreatureMoveAnimationFrames animationFrames = getMoveAnimationFrames(moveDirection);
        List<Image> frames = animationFrames.getWalkEmptyFrames();

        if (frames == null) return null;
        int framesSize = frames.size();
        if (framesSize == 0) return null;

        long nextUpdate = getNextUpdate(framesSize, cr.getSpeed());
        animationPos.setNextFrameUpdate(nextUpdate);
        int nextFrameNumber = animationPos.getNextFrameNumber(framesSize);
        return frames.get(nextFrameNumber);
    }

    public MoveDirection getMoveDirection(Creature cr) {
        Coords pos = cr.getPos();
        double xFrom = pos.x;
        double yFrom = pos.y;
        Coords dest = cr.getTask().getDest();
        double xTo = dest.x;
        double yTo = dest.y;

        return getMoveDirection(xFrom, yFrom, xTo, yTo);
    }

    public MoveDirection getMoveDirection(double xFrom, double yFrom, double xTo, double yTo) {
        xTo -= xFrom;
        yTo -= yFrom;
        yTo = -yTo;
        double moveRadian = Math.atan2(yTo, xTo);
        MoveDirection nextMoveDirection;
        double one8 = Math.PI / 4;
        double one16 = Math.PI / 8;
        double nextRadian = one16;

        if (moveRadian >= nextRadian && moveRadian < (nextRadian += one8)) {
            nextMoveDirection = UP_RIGHT;
        } else if (moveRadian >= nextRadian && moveRadian < (nextRadian += one8)) {
            nextMoveDirection = UP;
        } else if (moveRadian >= nextRadian && moveRadian < (nextRadian += one8)) {
            nextMoveDirection = UP_LEFT;
        } else if (moveRadian >= nextRadian && moveRadian < (nextRadian + one8)) {
            nextMoveDirection = LEFT;
        } else if (moveRadian <= (nextRadian = -one16) && moveRadian > (nextRadian -= one8)) {
            nextMoveDirection = DOWN_RIGHT;
        } else if (moveRadian <= nextRadian && moveRadian > (nextRadian -= one8)) {
            nextMoveDirection = DOWN;
        } else if (moveRadian <= nextRadian && moveRadian > (nextRadian -= one8)) {
            nextMoveDirection = DOWN_LEFT;
        } else if (moveRadian <= nextRadian && moveRadian > (nextRadian - one8)) {
            nextMoveDirection = LEFT;
        } else {
            nextMoveDirection = RIGHT;
        }
        return nextMoveDirection;
    }

    protected Image getIdle(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        return getNextIdle(animationPos, cr.getSpeed());
    }

    @Override
    protected Image getNextIdle(AnimationPos animationPos, double speed) {
        long curTime = System.currentTimeMillis();

        long timeToStartPlayIdleAfterStop = ((CreatureAnimationPos) animationPos).getTimeToStartPlayIdleAfterStop();
        if (curTime < timeToStartPlayIdleAfterStop) return null;
        return super.getNextIdle(animationPos, speed);
    }

    public List<Image> getPortraits(File programDir) {
        if (portraits.isEmpty()) {
            String path = programDir + animationDir + PORTRAIT_DIR;
            File file = new File(path);
            if (file.exists()) {
                initPortraitFrames(file, portraits);
            }
        }
        return portraits;
    }

    public void clearResizablePictures() {
        portraits.clear();
        creatureInventoryPictures.clear();
    }
}
