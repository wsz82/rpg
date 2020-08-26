package io.wsz.model.animation;

import io.wsz.model.item.PosItem;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import static io.wsz.model.sizes.Paths.*;

public class Animation<A extends PosItem> {
    protected static final FileFilter PNG_FILE_FILTER = f -> f.getName().endsWith(".png");
    protected static final Random RANDOM = new Random();

    private static final Map<String, List<Image>> IDLES_WITHOUT_MAIN = new HashMap<>(0);
    private static final List<List<Image>> IDLES_FOR_RANDOM = new ArrayList<>(0);
    private static final int MIN_IDLE_UPDATE_TIME_SEC = 2;
    private static final int MAX_IDLE_UPDATE_TIME_SEC = 4;

    protected final Map<String, Map<String, List<Image>>> idles = new HashMap<>(0);
    protected final String animationDir;

    public Animation(String animationDir) {
        if (animationDir.isEmpty()) {
            throw new IllegalArgumentException("Animation path is empty");
        }
        this.animationDir = animationDir;
    }

    public void play(A item) {
        AnimationPos animationPos = item.getAnimationPos();
        Image nextIdle = getNextIdle(animationPos, item.getAnimationSpeed());
        if (nextIdle == null) return;
        item.setImage(nextIdle);
    }

    public final void initAllAnimations(File programDir) {
        File[] animationsDirs = getAnimationFiles(programDir);
        if (animationsDirs == null || animationsDirs.length == 0) return;

        for (File animationDir : animationsDirs) {
            String fileName = animationDir.getName();
            if (fileName.equals(IDLE)) {
                initAnimations(animationDir, idles);
            }
            initOtherAnimations(animationDir, fileName);
        }
    }

    protected void initOtherAnimations(File framesDir, String fileName) {}

    public void initAnimations(File animationDir, Map<String, Map<String, List<Image>>> animations) {
        File[] animationFiles = animationDir.listFiles();
        if (animationFiles == null || animationFiles.length == 0) return;

        for (File subAnimationFile : animationFiles) {
            String name = subAnimationFile.getName();
            Map<String, List<Image>> animation = getAnimation(subAnimationFile);
            animations.put(name, animation);
        }
    }

    private Map<String, List<Image>> getAnimation(File animationFile) {
        Map<String, List<Image>> animation = new HashMap<>(0);
        File[] sequencesFiles = animationFile.listFiles();
        if (sequencesFiles == null || sequencesFiles.length == 0) return null;

        for (File sequenceFile : sequencesFiles) {
            boolean isNotDirectory = !sequenceFile.isDirectory();
            if (isNotDirectory) continue;
            List<Image> sequence = getSequence(sequenceFile);
            if (sequence == null || sequence.isEmpty()) continue;
            String sequenceName = sequenceFile.getName();
            animation.put(sequenceName, sequence);
        }
        return animation;
    }

    private List<Image> getSequence(File framesDir) {
        File[] imagesFiles = framesDir.listFiles(PNG_FILE_FILTER);
        if (imagesFiles == null || imagesFiles.length == 0) return null;
        List<Image> sequence = new ArrayList<>(0);
        for (File imageFile : imagesFiles) {
            Image loadedFrame = ResolutionImage.loadImage(imageFile);
            sequence.add(loadedFrame);
        }
        return sequence;
    }

    protected long getNextIdleUpdate(long curTime) {
        int randomTimeToStartIdleMillis = getRandomMillis(MAX_IDLE_UPDATE_TIME_SEC, MIN_IDLE_UPDATE_TIME_SEC);
        return curTime + randomTimeToStartIdleMillis;
    }

    protected long getNextUpdate(int framesSize, Double speed) {
        long frameDif = getFrameDuration(speed, framesSize);
        return System.currentTimeMillis() + frameDif;
    }

    protected int getRandomMillis(int maxIdleUpdateTimeSec, int minIdleUpdateTimeSec) {
        return (int) (Math.random() *
                (maxIdleUpdateTimeSec - minIdleUpdateTimeSec + 1) + minIdleUpdateTimeSec) * 1000;
    }

    private long getFrameDuration(double speed, int framesSize) {
        return (long) (1 / speed * 1000 / framesSize);
    }

    protected File[] getAnimationFiles(File programDir) {
        String path = programDir + animationDir;
        File pathFile = new File(path);
        return pathFile.listFiles();
    }

    protected Image getNextIdle(AnimationPos animationPos, double speed) {
        long curTime = System.currentTimeMillis();

        String curIdleAnimation = animationPos.getCurIdleAnimation();
        Map<String, List<Image>> curAnimation = idles.get(curIdleAnimation);
        if (curAnimation == null) return null;
        if (curAnimation.size() > 1) {
            List<Image> idleSequence = animationPos.getCurIdleSequence();
            if (idleSequence == null) {
                List<Image> randomIdleSequence = getRandomIdleSequence(animationPos, curTime);
                animationPos.setCurIdleSequence(randomIdleSequence);
            }
            long nearestIdleUpdate = animationPos.getNextTemporaryIdleUpdate();
            boolean isTimeToPlayTemporaryIdle = curTime > nearestIdleUpdate;
            boolean isTemporaryIdle = animationPos.isTemporaryIdle();
            if (isTimeToPlayTemporaryIdle && isTemporaryIdle) {
                return getNextTemporaryIdle(animationPos, speed);
            } else {
                return getNextConstantIdle(animationPos, speed);
            }
        } else {
            return getNextConstantIdle(animationPos, speed);
        }
    }

    private Image getNextTemporaryIdle(AnimationPos animationPos, double speed) {
        List<Image> frames = animationPos.getCurIdleSequence();

        if (frames == null) return null;
        int framesSize = frames.size();
        if (framesSize == 0) return null;

        long curTime = System.currentTimeMillis();
        boolean isNotTimeForNextFrame = curTime < animationPos.getNextFrameUpdate();
        if (isNotTimeForNextFrame) return null;
        long nextUpdate = getNextUpdate(framesSize, speed);
        animationPos.setNextFrameUpdate(nextUpdate);

        if (frames.size() > 1) {
            return forceGetNextTemporaryIdle(animationPos, framesSize, speed);
        } else {
            animationPos.setTemporaryIdle(false);
            animationPos.setCurIdleSequence(null);
            return frames.get(0);
        }
    }

    private Image forceGetNextTemporaryIdle(AnimationPos animationPos, int framesSize, double speed) {
        List<Image> frames = animationPos.getCurIdleSequence();

        int nextFrameNumber = animationPos.getNextFrameNumber(framesSize);
        if (animationPos.isCycleFinished()) {
            return getNextConstantIdleAfterTemporary(animationPos, speed);
        }
        return frames.get(nextFrameNumber);
    }

    private Image getNextConstantIdleAfterTemporary(AnimationPos animationPos, double speed) {
        animationPos.setCurIdleSequence(null);
        animationPos.setTemporaryIdle(false);
        animationPos.setFrameNumber(0);
        return getNextConstantIdle(animationPos, speed);
    }

    private Image getNextConstantIdle(AnimationPos animationPos, double speed) {
        String curIdleAnimation = animationPos.getCurIdleAnimation();
        List<Image> frames = idles.get(curIdleAnimation).get(MAIN);

        if (frames == null) return null;
        int framesSize = frames.size();
        if (framesSize == 0) return null;

        long curTime = System.currentTimeMillis();
        boolean isNotTimeForNextFrame = curTime < animationPos.getNextFrameUpdate();
        if (isNotTimeForNextFrame) return null;
        long nextUpdate = getNextUpdate(framesSize, speed);
        animationPos.setNextFrameUpdate(nextUpdate);

        if (frames.size() > 1) {
            return forceGetNextConstantIdle(animationPos, framesSize, speed, frames);
        } else {
            animationPos.setTemporaryIdle(true);
            return frames.get(0);
        }
    }

    private Image forceGetNextConstantIdle(AnimationPos animationPos, int framesSize, double speed, List<Image> frames) {
        int nextFrameNumber = animationPos.getNextFrameNumber(framesSize);
        if (animationPos.isCycleFinished()) {
            long curTime = System.currentTimeMillis();
            boolean isTimeToPlayTemporary = curTime >= animationPos.getNextTemporaryIdleUpdate();
            if (isTimeToPlayTemporary) {
                boolean isIdleSequenceSet = animationPos.getCurIdleSequence() != null;
                if (isIdleSequenceSet) {
                    return playTemporaryIdleAfterConstant(animationPos, speed);
                }
            }
        }
        return frames.get(nextFrameNumber);
    }

    private Image playTemporaryIdleAfterConstant(AnimationPos animationPos, double speed) {
        animationPos.setTemporaryIdle(true);
        animationPos.setFrameNumber(0);
        return getNextTemporaryIdle(animationPos, speed);
    }

    private List<Image> getRandomIdleSequence(AnimationPos animationPos, long curTime) {
        long nextIdleUpdate = getNextIdleUpdate(curTime);
        animationPos.setNextTemporaryIdleUpdate(nextIdleUpdate);
        String curIdleAnimation = animationPos.getCurIdleAnimation();
        Map<String, List<Image>> animation = idles.get(curIdleAnimation);
        return getRandomIdleSequence(animation);
    }

    protected List<Image> getRandomIdleSequence(Map<String, List<Image>> idles) {
        IDLES_WITHOUT_MAIN.clear();
        IDLES_WITHOUT_MAIN.putAll(idles);
        IDLES_WITHOUT_MAIN.remove(MAIN);
        IDLES_FOR_RANDOM.clear();
        IDLES_FOR_RANDOM.addAll(IDLES_WITHOUT_MAIN.values());
        int idlesSize = IDLES_FOR_RANDOM.size();
        int randomIndex = RANDOM.nextInt(idlesSize);
        return IDLES_FOR_RANDOM.get(randomIndex);
    }

    public Image getBasicMain(File programDir) {
        Map<String, List<Image>> basic = idles.get(BASIC);

        boolean basicNotLoaded = basic == null || basic.isEmpty();
        boolean mainNotLoaded = true;
        List<Image> main = null;
        if (!basicNotLoaded) {
            main = basic.get(MAIN);
            mainNotLoaded = main == null || main.isEmpty();
        }
        boolean imageNotLoaded = true;
        int firstIndex = 0;
        if (!mainNotLoaded) {
            Image openImage = main.get(firstIndex);
            imageNotLoaded = openImage == null;
        }
        if (basicNotLoaded || mainNotLoaded || imageNotLoaded) {
            String path = programDir + animationDir + IDLE_DIR + BASIC_DIR + MAIN_DIR;
            File idleDir = new File(path);
            File[] imagesFiles = idleDir.listFiles(PNG_FILE_FILTER);
            if (imagesFiles == null || imagesFiles.length == 0) return null;
            File firstImageFile = imagesFiles[firstIndex];
            Image loadedImage = ResolutionImage.loadImage(firstImageFile);

            if (basic == null) {
                basic = new HashMap<>(1);
                idles.put(BASIC, basic);
            }
            if (main == null) {
                main = new ArrayList<>(1);
                basic.put(MAIN, main);
            }
            main.add(loadedImage);
        }
        return idles.get(BASIC).get(MAIN).get(firstIndex);
    }
}
