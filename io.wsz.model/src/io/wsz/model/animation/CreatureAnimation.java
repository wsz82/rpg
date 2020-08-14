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
    private final List<Image> moveUp = new ArrayList<>(0);
    private final List<Image> moveUpRight = new ArrayList<>(0);
    private final List<Image> moveRight = new ArrayList<>(0);
    private final List<Image> moveDownRight = new ArrayList<>(0);
    private final List<Image> moveDown = new ArrayList<>(0);
    private final List<Image> moveDownLeft = new ArrayList<>(0);
    private final List<Image> moveLeft = new ArrayList<>(0);
    private final List<Image> moveUpLeft = new ArrayList<>(0);
    private final List<List<Image>> allSides = List.of(moveUp, moveUpRight, moveRight, moveDownRight,
            moveDown, moveDownLeft, moveLeft, moveUpLeft) ;

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

            List<Image> moveFrames = getMoveFrames(fileName);
            if (moveFrames != null) {
                initFrames(framesDir, moveFrames);
            } else {
                switch (fileName) {
                    case IDLE -> initFrames(framesDir, idle);
                    case PORTRAIT -> initPortraitFrames(framesDir, portrait);
                }
            }
        }
    }

    private List<Image> getMoveFrames(MoveSide moveSide) {
        String moveSideString = String.valueOf(moveSide.ordinal() + 1);
        return getMoveFrames(moveSideString);
    }

    private List<Image> getMoveFrames(String moveSideString) {
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

    private void initPortraitFrames(File framesDir, List<Image> portrait) {
        portrait.clear();
        File[] imagesFiles = framesDir.listFiles(PNGfileFilter);
        if (imagesFiles == null || imagesFiles.length == 0) return;
        for (File imageFile : imagesFiles) {
            Image loadedImage = loadPortrait(imageFile);
            portrait.add(loadedImage);
        }
    }

    private void initFrames(File framesDir, List<Image> framesList) {
        framesList.clear();
        File[] imagesFiles = framesDir.listFiles(f -> f.getName().endsWith(".png"));
        if (imagesFiles == null || imagesFiles.length == 0) return;
        for (File imageFile : imagesFiles) {
            Image loadedFrame = ResolutionImage.loadImage(imageFile);
            framesList.add(loadedFrame);
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
        List<Image> frames = getMoveFrames(moveSide);

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
                initFrames(file, idle);
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

    public List<Image> getMoveUp() {
        return moveUp;
    }

    public List<Image> getMoveUpRight() {
        return moveUpRight;
    }

    public List<Image> getMoveRight() {
        return moveRight;
    }

    public List<Image> getMoveDownRight() {
        return moveDownRight;
    }

    public List<Image> getMoveDown() {
        return moveDown;
    }

    public List<Image> getMoveDownLeft() {
        return moveDownLeft;
    }

    public List<Image> getMoveLeft() {
        return moveLeft;
    }

    public List<Image> getMoveUpLeft() {
        return moveUpLeft;
    }
}
