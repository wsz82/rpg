package io.wsz.model.animation;

import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.animation.MoveSide.*;
import static io.wsz.model.sizes.Sizes.CONSTANT_METER;

public class CreatureAnimation {
    private static final String IDLE = "idle";
    private static final String IDLE_PATH = File.separator + IDLE;
    private static final String PORTRAIT = "portrait";
    private static final String PORTRAIT_PATH = File.separator + PORTRAIT;
    private static final FileFilter PNGfileFilter = f -> f.getName().endsWith(".png");

    private final String animationDir;
    private final List<Image> idle = new ArrayList<>(0);
    private final List<Image> portrait = new ArrayList<>(0);
    private final CreatureMoveAnimationFrames moveUp = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveUpRight = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveRight = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveDownRight = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveDown = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveDownLeft = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveLeft = new CreatureMoveAnimationFrames();
    private final CreatureMoveAnimationFrames moveUpLeft = new CreatureMoveAnimationFrames();

    public CreatureAnimation(String animationDir) {
        if (animationDir.isEmpty()) {
            throw new IllegalArgumentException("Animation path is empty");
        }
        this.animationDir = animationDir;
    }

    public void initAllFrames() {
        String path = Controller.getProgramDir() + animationDir;
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
                    case IDLE -> initIdleFrames(framesDir, idle);
                    case PORTRAIT -> initPortraitFrames(framesDir, portrait);
                }
            }
        }
    }

    private CreatureMoveAnimationFrames getMoveAnimationFrames(MoveSide moveSide) {
        if (moveSide == null) return null;
        String moveSideString = String.valueOf(moveSide.ordinal() + 1);
        return getMoveAnimationFrames(moveSideString);
    }

    private CreatureMoveAnimationFrames getMoveAnimationFrames(String moveSideString) {
        return switch (moveSideString) {
            case "1" -> moveUp;
            case "2" -> moveUpRight;
            case "3" -> moveRight;
            case "4" -> moveDownRight;
            case "5" -> moveDown;
            case "6" -> moveDownLeft;
            case "7" -> moveLeft;
            case "8" -> moveUpLeft;
            default -> null;
        };
    }

    private void initIdleFrames(File framesDir, List<Image> idle) {
        idle.clear();
        File[] imagesFiles = framesDir.listFiles(PNGfileFilter);
        if (imagesFiles == null || imagesFiles.length == 0) return;
        for (File imageFile : imagesFiles) {
            Image loadedFrame = ResolutionImage.loadImage(imageFile);
            idle.add(loadedFrame);
        }
    }

    private void initPortraitFrames(File framesDir, List<Image> portrait) {
        portrait.clear();
        File[] imagesFiles = framesDir.listFiles(PNGfileFilter);
        if (imagesFiles == null || imagesFiles.length == 0) return;
        for (File imageFile : imagesFiles) {
            Image loadedFrame = loadPortrait(imageFile);
            portrait.add(loadedFrame);
        }
    }

    private void initFrames(File framesDir, CreatureMoveAnimationFrames frames) {
        List<Image> moveFrames = frames.getMoveFrames();
        moveFrames.clear();
        File[] imagesFiles = framesDir.listFiles(PNGfileFilter);
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

    private Image loadPortrait(File file) {
        String url = null;
        try {
            url = file.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }

        int portraitSize = Sizes.getPortraitSize();
        if (portraitSize == 0) {
            return null;
        }

        if (Sizes.getTrueMeter() == CONSTANT_METER) {
            return new Image(url, portraitSize, portraitSize, false, false, false);
        } else {
            Dimension d = new Dimension(portraitSize, portraitSize);
            Dimension rd = ResolutionImage.getRequestedDimension(d);
            return ResolutionImage.getResizedImage(url, d, rd);
        }
    }

    public void updateStopAnimation(Creature cr) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        MoveSide moveSide = animationPos.getMoveSide();
        if (moveSide == null) return;
        CreatureMoveAnimationFrames animationFrames = getMoveAnimationFrames(moveSide);
        Image stop = animationFrames.getStop();
        if (stop == null) return;
        cr.setImage(stop);
    }

    public void updateMoveAnimation(Creature cr, double xFrom, double yFrom, double xTo, double yTo) {
        CreatureAnimationPos animationPos = cr.getAnimationPos();
        long curTime = System.currentTimeMillis();
        if (curTime < animationPos.getNextFrameUpdate()) return;
        MoveSide nextMoveSide = getMoveSide(xFrom, yFrom, xTo, yTo);
        if (nextMoveSide == null) return;
        animationPos.setMoveSide(nextMoveSide);
        Image nextFrame = getNextFrame(cr.getSpeed(), animationPos);
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

    private Image getNextFrame(double speed, CreatureAnimationPos animationPos) {
        int frameNumber = animationPos.getFrame();
        MoveSide moveSide = animationPos.getMoveSide();
        CreatureMoveAnimationFrames animationFrames = getMoveAnimationFrames(moveSide);
        List<Image> frames = animationFrames.getMoveFrames();

        if (frames == null) return null;
        int framesSize = frames.size();
        if (framesSize == 0) return null;

        long frameDif = (long) (1 / speed * 1000 / framesSize);
        long nextUpdate = System.currentTimeMillis() + frameDif;
        animationPos.setNextFrameUpdate(nextUpdate);

        int nextFrameNumber = frameNumber + 1;
        if (nextFrameNumber >= framesSize) {
            nextFrameNumber = 0;
        }
        animationPos.setFrame(nextFrameNumber);
        return frames.get(nextFrameNumber);
    }

    public List<Image> getIdle() {
        if (idle.isEmpty()) {
            String path = Controller.getProgramDir() + animationDir + IDLE_PATH;
            File file = new File(path);
            if (file.exists()) {
                initIdleFrames(file, idle);
            }
        }
        return idle;
    }

    public List<Image> getPortrait() {
        if (portrait.isEmpty()) {
            String path = Controller.getProgramDir() + animationDir + PORTRAIT_PATH;
            File file = new File(path);
            if (file.exists()) {
                initPortraitFrames(file, portrait);
            }
        }
        return portrait;
    }

    public void clearPortrait() {
        portrait.clear();
    }

    public CreatureMoveAnimationFrames getMoveUp() {
        return moveUp;
    }

    public CreatureMoveAnimationFrames getMoveUpRight() {
        return moveUpRight;
    }

    public CreatureMoveAnimationFrames getMoveRight() {
        return moveRight;
    }

    public CreatureMoveAnimationFrames getMoveDownRight() {
        return moveDownRight;
    }

    public CreatureMoveAnimationFrames getMoveDown() {
        return moveDown;
    }

    public CreatureMoveAnimationFrames getMoveDownLeft() {
        return moveDownLeft;
    }

    public CreatureMoveAnimationFrames getMoveLeft() {
        return moveLeft;
    }

    public CreatureMoveAnimationFrames getMoveUpLeft() {
        return moveUpLeft;
    }
}
