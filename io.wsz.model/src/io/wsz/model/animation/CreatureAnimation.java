package io.wsz.model.animation;

import io.wsz.model.item.Creature;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import static io.wsz.model.animation.MoveSide.*;
import static io.wsz.model.sizes.Paths.*;

public class CreatureAnimation {
    private static final FileFilter PNG_FILE_FILTER = f -> f.getName().endsWith(".png");
    private static final int MIN_PORTRAIT_UPDATE_TIME_SEC = 1;
    private static final int MAX_PORTRAIT_UPDATE_TIME_SEC = 4;
    private static final int MIN_STOP_WAIT_TIME_SEC = 3;
    private static final int MAX_STOP_WAIT_TIME_SEC = 6;
    private static final int MIN_IDLE_UPDATE_TIME_SEC = 7;
    private static final int MAX_IDLE_UPDATE_TIME_SEC = 21;
    private static final Random RANDOM = new Random();

    private final String animationDir;
    private final CreatureMoveAnimationFrames moveUp = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveUpRight = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveRight = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveDownRight = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveDown = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveDownLeft = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveLeft = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveUpLeft = new CreatureMoveAnimationFrames();
    private final CreatureIdleAnimationFrames idles = new CreatureIdleAnimationFrames();
    private final List<Image> portraits = new ArrayList<>(0);
    private final Map<String, File> creatureInventoryFiles = new HashMap<>(0);
    private final Map<String, Image> creatureInventoryPictures = new HashMap<>(0);

    public CreatureAnimation(String animationDir) {
        if (animationDir.isEmpty()) {
            throw new IllegalArgumentException("Animation path is empty");
        }
        this.animationDir = animationDir;
    }

    public void initAllFrames(File programDir) {
        String path = programDir + animationDir;
        File pathFile = new File(path);
        File[] animationsDirs = pathFile.listFiles();
        if (animationsDirs == null || animationsDirs.length == 0) return;

        for (File framesDir : animationsDirs) {
            String fileName = framesDir.getName();

            CreatureMoveAnimationFrames moveFrames = getMoveAnimationFrames(fileName);
            if (moveFrames != null) {
                initFrames(framesDir, moveFrames);
            } else {
                switch (fileName) {
                    case IDLE -> initIdleFrames(framesDir, idles);
                    case PORTRAIT -> initPortraitFrames(framesDir, portraits);
                    case INVENTORY -> initInventoryCreaturePicturesFiles(framesDir, creatureInventoryFiles);
                }
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
        Image emptyInventoryImage = creatureInventoryPictures.get(INVENTORY_EMPTY);
        if (emptyInventoryImage == null) {
            File emptyInventoryFile = creatureInventoryFiles.get(INVENTORY_EMPTY);
            if (emptyInventoryFile != null && emptyInventoryFile.exists()) {
                emptyInventoryImage = ResolutionImage.loadDefinedDimensionImage(emptyInventoryFile, width, height);
                creatureInventoryPictures.put(INVENTORY_EMPTY, emptyInventoryImage);
            }
        }
        return emptyInventoryImage;
    }

    private CreatureMoveAnimationFrames getMoveAnimationFrames(MoveSide moveSide) {
        if (moveSide == null) return null;
        int ordinal = moveSide.ordinal();
        String walk = walks[ordinal];
        return getMoveAnimationFrames(walk);
    }

    private CreatureMoveAnimationFrames getMoveAnimationFrames(String moveSideString) {
        return switch (moveSideString) {
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

    private void initIdleFrames(File framesDir, CreatureIdleAnimationFrames idles) {
        List<List<Image>> idleSequences = idles.getIdleSequences();
        idleSequences.clear();
        File[] idleSequencesFiles = framesDir.listFiles();
        if (idleSequencesFiles == null || idleSequencesFiles.length == 0) return;
        for (File idleSequenceFile : idleSequencesFiles) {
            if (idleSequenceFile.getName().endsWith(".png")) {
                Image loadedFrame = ResolutionImage.loadImage(idleSequenceFile);
                idles.setMain(loadedFrame);
                continue;
            }
            if (idleSequenceFile.isDirectory()) {
                List<Image> sequence = new ArrayList<>(0);
                initIdleSequence(idleSequenceFile, sequence);
                if (!sequence.isEmpty()) {
                    idleSequences.add(sequence);
                }
            }
        }
    }

    private void initIdleSequence(File framesDir, List<Image> sequence) {
        File[] imagesFiles = framesDir.listFiles(PNG_FILE_FILTER);
        if (imagesFiles == null || imagesFiles.length == 0) return;
        for (File imageFile : imagesFiles) {
            Image loadedFrame = ResolutionImage.loadImage(imageFile);
            sequence.add(loadedFrame);
        }
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

    private void initFrames(File framesDir, CreatureMoveAnimationFrames frames) {
        List<Image> moveFrames = frames.getMoveFrames();
        moveFrames.clear();
        File[] imagesFiles = framesDir.listFiles(PNG_FILE_FILTER);
        if (imagesFiles == null || imagesFiles.length == 0) return;
        for (File imageFile : imagesFiles) {
            Image loadedFrame = ResolutionImage.loadImage(imageFile);
            boolean isStopFrame = imageFile.getName().equals("stop.png");
            if (isStopFrame) {
                frames.setStop(loadedFrame);
            } else {
                moveFrames.add(loadedFrame);
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

    public void updateIdleAnimation(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        long curTime = System.currentTimeMillis();

        long timeToStartPlayIdleAfterStop = animationPos.getTimeToStartPlayIdleAfterStop();
        if (curTime < timeToStartPlayIdleAfterStop) return;

        long nextIdleUpdate = animationPos.getNextIdleUpdate();
        List<Image> idleSequence = animationPos.getIdleSequence();
        if (curTime < nextIdleUpdate) {
            cr.setImage(idles.getMain());
        } else if (idleSequence == null) {
            int randomTimeToStartIdleMillis = getRandomMillis(MAX_IDLE_UPDATE_TIME_SEC, MIN_IDLE_UPDATE_TIME_SEC);
            nextIdleUpdate = curTime + randomTimeToStartIdleMillis;
            animationPos.setNextIdleUpdate(nextIdleUpdate);

            List<List<Image>> idleSequences = idles.getIdleSequences();
            int idleSequencesSize = idleSequences.size();
            int randomIndex = RANDOM.nextInt(idleSequencesSize);
            List<Image> sequence = idleSequences.get(randomIndex);
            animationPos.setIdleSequence(sequence);
        } else {
            playIdleSequence(cr, animationPos);
        }
    }

    private void playIdleSequence(Creature cr, CreatureAnimationPos animationPos) {
        long curTime = System.currentTimeMillis();
        if (curTime < animationPos.getNextIdleFrameUpdate()) return;
        Image nextFrame = getNextIdleFrame(cr, animationPos);
        if (nextFrame == null) return;
        cr.setImage(nextFrame);
    }

    private Image getNextIdleFrame(Creature cr, CreatureAnimationPos animationPos) {
        int frameNumber = animationPos.getIdleFrame();
        List<Image> frames = animationPos.getIdleSequence();

        if (frames == null) return null;
        int framesSize = frames.size();
        if (framesSize == 0) return null;

        long frameDif = getFrameDif(cr.getSpeed(), framesSize);
        long nextUpdate = System.currentTimeMillis() + frameDif;
        animationPos.setNextIdleFrameUpdate(nextUpdate);

        int nextFrameNumber = frameNumber + 1;
        if (nextFrameNumber >= framesSize) {
            resetIdlePlay(animationPos);
            cr.setImage(idles.getMain());
            return null;
        }
        animationPos.setIdleFrame(nextFrameNumber);
        return frames.get(nextFrameNumber);
    }

    private void resetIdlePlay(CreatureAnimationPos animationPos) {
        animationPos.setIdleSequence(null);
        animationPos.setIdleFrame(-1);
    }

    public void updateStopAnimation(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();

        int randomTimeToStartIdleMillis = getRandomMillis(MAX_STOP_WAIT_TIME_SEC, MIN_STOP_WAIT_TIME_SEC);
        long curTime = System.currentTimeMillis();
        long timeToPlayIdleAfterStop = curTime + randomTimeToStartIdleMillis;
        animationPos.setTimeToStartPlayIdleAfterStop(timeToPlayIdleAfterStop);

        MoveSide moveSide = animationPos.getMoveSide();
        if (moveSide == null) return;
        CreatureMoveAnimationFrames animationFrames = getMoveAnimationFrames(moveSide);
        Image stop = animationFrames.getStop();
        if (stop == null) return;
        cr.setImage(stop);
    }

    private int getRandomMillis(int maxIdleUpdateTimeSec, int minIdleUpdateTimeSec) {
        return (int) (Math.random() *
                (maxIdleUpdateTimeSec - minIdleUpdateTimeSec + 1) + minIdleUpdateTimeSec) * 1000;
    }

    public void updateMoveAnimation(Creature cr, double xFrom, double yFrom, double xTo, double yTo) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        long curTime = System.currentTimeMillis();
        if (curTime < animationPos.getNextMoveUpdate()) return;
        MoveSide nextMoveSide = getMoveSide(xFrom, yFrom, xTo, yTo);
        if (nextMoveSide == null) return;
        animationPos.setMoveSide(nextMoveSide);
        Image nextFrame = getNextMoveFrame(cr.getSpeed(), animationPos);
        if (nextFrame == null) return;
        cr.setImage(nextFrame);
    }

    public MoveSide getMoveSide(double xFrom, double yFrom, double xTo, double yTo) {
        xTo -= xFrom;
        yTo -= yFrom;
        yTo = -yTo;
        double moveRadian = Math.atan2(yTo, xTo);
        MoveSide nextMoveSide;
        double one8 = Math.PI / 4;
        double one16 = Math.PI / 8;
        double nextRadian = one16;

        if (moveRadian >= nextRadian && moveRadian < (nextRadian += one8)) {
            nextMoveSide = UP_RIGHT;
        } else if (moveRadian >= nextRadian && moveRadian < (nextRadian += one8)) {
            nextMoveSide = UP;
        } else if (moveRadian >= nextRadian && moveRadian < (nextRadian += one8)) {
            nextMoveSide = UP_LEFT;
        } else if (moveRadian >= nextRadian && moveRadian < (nextRadian + one8)) {
            nextMoveSide = LEFT;
        } else if (moveRadian <= (nextRadian = -one16) && moveRadian > (nextRadian -= one8)) {
            nextMoveSide = DOWN_RIGHT;
        } else if (moveRadian <= nextRadian && moveRadian > (nextRadian -= one8)) {
            nextMoveSide = DOWN;
        } else if (moveRadian <= nextRadian && moveRadian > (nextRadian -= one8)) {
            nextMoveSide = DOWN_LEFT;
        } else if (moveRadian <= nextRadian && moveRadian > (nextRadian - one8)) {
            nextMoveSide = LEFT;
        } else {
            nextMoveSide = RIGHT;
        }
        return nextMoveSide;
    }

    private Image getNextMoveFrame(double speed, CreatureAnimationPos animationPos) {
        int frameNumber = animationPos.getMoveFrame();
        MoveSide moveSide = animationPos.getMoveSide();
        CreatureMoveAnimationFrames animationFrames = getMoveAnimationFrames(moveSide);
        List<Image> frames = animationFrames.getMoveFrames();

        if (frames == null) return null;
        int framesSize = frames.size();
        if (framesSize == 0) return null;

        long frameDif = getFrameDif(speed, framesSize);
        long nextUpdate = System.currentTimeMillis() + frameDif;
        animationPos.setNextMoveUpdate(nextUpdate);

        int nextFrameNumber = frameNumber + 1;
        if (nextFrameNumber >= framesSize) {
            nextFrameNumber = 0;
        }
        animationPos.setMoveFrame(nextFrameNumber);
        return frames.get(nextFrameNumber);
    }

    private long getFrameDif(double speed, int framesSize) {
        return (long) (1 / speed * 1000 / framesSize);
    }

    public Image getMainIdle(File programDir) {
        if (idles.getMain() == null) {
            String path = programDir + animationDir + IDLE_PATH;
            File idleDir = new File(path);
            File[] imagesFiles = idleDir.listFiles(PNG_FILE_FILTER);
            if (imagesFiles == null || imagesFiles.length == 0) return null;
            File firstImageFile = imagesFiles[0];
            Image loadedImage = ResolutionImage.loadImage(firstImageFile);
            idles.setMain(loadedImage);
        }
        return idles.getMain();
    }

    public List<Image> getPortraits(File programDir) {
        if (portraits.isEmpty()) {
            String path = programDir + animationDir + PORTRAIT_PATH;
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
