package io.wsz.model.animation;

import io.wsz.model.item.PosItem;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

import static io.wsz.model.sizes.Paths.*;

public class Animation<A extends PosItem<?,?>> {
    protected static final FileFilter PNG_FILE_FILTER = f -> f.getName().endsWith(".png");
    protected static final Random RANDOM = new Random();

    private static final Map<String, List<ResolutionImage>> IDLES_WITHOUT_MAIN = new HashMap<>(0);
    private static final List<String> IDLES_FOR_RANDOM = new ArrayList<>(0);
    private static final int MIN_IDLE_UPDATE_TIME_SEC = 2;
    private static final int MAX_IDLE_UPDATE_TIME_SEC = 4;

    protected final Map<String, Map<String, List<File>>> idlesFiles = new HashMap<>(0);
    protected final Map<String, Map<String, List<ResolutionImage>>> idles = new HashMap<>(0);
    protected final String animationDir;
    private final String idlesOrEquivalent;

    private ResolutionImage basicMain;

    public Animation(String animationDir, String idlesOrEquivalent) {
        if (animationDir == null) {
            throw new NullPointerException();
        }
        if (animationDir.isEmpty()) {
            throw new IllegalArgumentException("Animation path is empty");
        }
        this.animationDir = animationDir;
        this.idlesOrEquivalent = idlesOrEquivalent;
    }

    public void play(A item) {
        AnimationPos animationPos = item.getAnimationPos();
        ResolutionImage nextIdle = getNextIdle(animationPos, item.getAnimationSpeed());
        if (nextIdle == null) return;
        item.setImage(nextIdle);
    }

    public final void initAllAnimations(File programDir) {
        File[] animationsDirs = getHighestAnimationFiles(programDir);
        if (animationsDirs == null || animationsDirs.length == 0) return;

        for (File animationDir : animationsDirs) {
            String fileName = animationDir.getName();
            if (fileName.equals(idlesOrEquivalent)) {
                initAnimationFiles(animationDir, idlesFiles);
                initIdlesOrEquivalent();
            }
            initOtherAnimations(animationDir, fileName);
        }
    }

    public void initOtherAnimations(File framesDir, String fileName) {}

    public void initIdlesOrEquivalent() {
        idles.clear();
        for (String animationKey : idlesFiles.keySet()) {
            Map<String, List<File>> animationFiles = idlesFiles.get(animationKey);
            Map<String, List<ResolutionImage>> animation = new HashMap<>(1);
            for (String sequenceKey : animationFiles.keySet()) {
                List<File> files = animationFiles.get(sequenceKey);
                List<ResolutionImage> sequence = new ArrayList<>(1);
                for (File file : files) {
                    initSequenceImage(sequence, file);
                }
                animation.put(sequenceKey, sequence);
            }
            idles.put(animationKey, animation);
        }
     }

    protected void initSequenceImage(List<ResolutionImage> sequence, File file) {
        ResolutionImage image = new ResolutionImage(file);
        sequence.add(image);
    }

    private void initAnimationFiles(File animationDir, Map<String, Map<String, List<File>>> files) {
        File[] animationFiles = animationDir.listFiles();
        if (animationFiles == null || animationFiles.length == 0) return;

        for (File subAnimationFile : animationFiles) {
            String name = subAnimationFile.getName();
            Map<String, List<File>> animation = getAnimationFiles(subAnimationFile);
            files.put(name, animation);
        }
    }

    private Map<String, List<File>> getAnimationFiles(File animationFile) {
        Map<String, List<File>> animation = new HashMap<>(0);
        File[] sequencesFiles = animationFile.listFiles();
        if (sequencesFiles == null || sequencesFiles.length == 0) return null;

        for (File sequenceFile : sequencesFiles) {
            boolean isNotDirectory = !sequenceFile.isDirectory();
            if (isNotDirectory) continue;
            List<File> sequence = getLoadedFilesSequence(sequenceFile);
            if (sequence == null || sequence.isEmpty()) continue;
            String sequenceName = sequenceFile.getName();
            animation.put(sequenceName, sequence);
        }
        return animation;
    }

    private List<File> getLoadedFilesSequence(File framesDir) {
        File[] imagesFiles = framesDir.listFiles(PNG_FILE_FILTER);
        if (imagesFiles == null || imagesFiles.length == 0) return null;
        List<File> sequence = new ArrayList<>(0);
        sequence.addAll(Arrays.asList(imagesFiles));
        return sequence;
    }

    public void initAnimations(File animationDir, Map<String, Map<String, List<ResolutionImage>>> animations) {
        File[] animationFiles = animationDir.listFiles();
        if (animationFiles == null || animationFiles.length == 0) return;

        for (File subAnimationFile : animationFiles) {
            String name = subAnimationFile.getName();
            Map<String, List<ResolutionImage>> animation = getAnimation(subAnimationFile);
            animations.put(name, animation);
        }
    }

    private Map<String, List<ResolutionImage>> getAnimation(File animationFile) {
        Map<String, List<ResolutionImage>> animation = new HashMap<>(0);
        File[] sequencesFiles = animationFile.listFiles();
        if (sequencesFiles == null || sequencesFiles.length == 0) return null;

        for (File sequenceFile : sequencesFiles) {
            boolean isNotDirectory = !sequenceFile.isDirectory();
            if (isNotDirectory) continue;
            List<ResolutionImage> sequence = getLoadedSequence(sequenceFile);
            if (sequence == null || sequence.isEmpty()) continue;
            String sequenceName = sequenceFile.getName();
            animation.put(sequenceName, sequence);
        }
        return animation;
    }

    private List<ResolutionImage> getLoadedSequence(File framesDir) {
        File[] imagesFiles = framesDir.listFiles(PNG_FILE_FILTER);
        if (imagesFiles == null || imagesFiles.length == 0) return null;
        List<ResolutionImage> sequence = new ArrayList<>(0);
        for (File imageFile : imagesFiles) {
            ResolutionImage loadedFrame = new ResolutionImage(imageFile);
            sequence.add(loadedFrame);
        }
        return sequence;
    }

    protected long getNextIdleUpdate(long curTime) {
        int randomTimeToStartIdleMillis = getRandomMillis(MAX_IDLE_UPDATE_TIME_SEC, MIN_IDLE_UPDATE_TIME_SEC);
        return curTime + randomTimeToStartIdleMillis;
    }

    protected int getRandomMillis(int maxIdleUpdateTimeSec, int minIdleUpdateTimeSec) {
        return (int) (Math.random() * (maxIdleUpdateTimeSec - minIdleUpdateTimeSec + 1) + minIdleUpdateTimeSec) * 1000;
    }

    protected long getNextUpdate(int framesSize, Double speed) {
        long frameDif = getFrameDuration(speed, framesSize);
        return System.currentTimeMillis() + frameDif;
    }

    private long getFrameDuration(double speed, int framesSize) {
        return (long) (1 / speed * 1000 / framesSize);
    }

    private File[] getHighestAnimationFiles(File programDir) {
        String path = programDir + animationDir;
        File pathFile = new File(path);
        return pathFile.listFiles();
    }

    protected ResolutionImage getNextIdle(AnimationPos animationPos, double speed) {
        long curTime = System.currentTimeMillis();

        String curIdleAnimation = animationPos.getCurIdleAnimation();
        Map<String, List<ResolutionImage>> curAnimation = idles.get(curIdleAnimation);
        if (curAnimation == null) return null;
        if (curAnimation.size() > 1) {
            String idleSequence = animationPos.getCurIdleSequence();
            if (idleSequence == null) {
                String randomIdleSequence = getRandomIdleSequence(animationPos, curTime);
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

    private ResolutionImage getNextTemporaryIdle(AnimationPos animationPos, double speed) {
        String curIdleAnimation = animationPos.getCurIdleAnimation();
        if (curIdleAnimation == null) return null;
        String curIdleSequence = animationPos.getCurIdleSequence();
        if (curIdleSequence == null) return null;
        List<ResolutionImage> frames = idles.get(curIdleAnimation).get(curIdleSequence);

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

    private ResolutionImage forceGetNextTemporaryIdle(AnimationPos animationPos, int framesSize, double speed) {
        String curIdleAnimation = animationPos.getCurIdleAnimation();
        if (curIdleAnimation == null) return null;
        String curIdleSequence = animationPos.getCurIdleSequence();
        if (curIdleSequence == null) return null;
        List<ResolutionImage> frames = idles.get(curIdleAnimation).get(curIdleSequence);

        int nextFrameNumber = animationPos.getNextFrameNumber(framesSize);
        if (animationPos.isCycleFinished()) {
            animationPos.setCurIdleSequence(null);
            animationPos.setTemporaryIdle(false);
        }
        return frames.get(nextFrameNumber);
    }

    private ResolutionImage getNextConstantIdle(AnimationPos animationPos, double speed) {
        String curIdleAnimation = animationPos.getCurIdleAnimation();
        List<ResolutionImage> frames = idles.get(curIdleAnimation).get(MAIN);

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

    private ResolutionImage forceGetNextConstantIdle(AnimationPos animationPos, int framesSize, double speed, List<ResolutionImage> frames) {
        int nextFrameNumber = animationPos.getNextFrameNumber(framesSize);
        if (animationPos.isCycleFinished()) {
            long curTime = System.currentTimeMillis();
            boolean isTimeToPlayTemporary = curTime >= animationPos.getNextTemporaryIdleUpdate();
            if (isTimeToPlayTemporary) {
                boolean isIdleSequenceSet = animationPos.getCurIdleSequence() != null;
                if (isIdleSequenceSet) {
                    animationPos.setTemporaryIdle(true);
                }
            }
        }
        return frames.get(nextFrameNumber);
    }

    private String getRandomIdleSequence(AnimationPos animationPos, long curTime) {
        long nextIdleUpdate = getNextIdleUpdate(curTime);
        animationPos.setNextTemporaryIdleUpdate(nextIdleUpdate);
        String curIdleAnimation = animationPos.getCurIdleAnimation();
        Map<String, List<ResolutionImage>> animation = idles.get(curIdleAnimation);
        return getRandomIdleSequence(animation);
    }

    protected String getRandomIdleSequence(Map<String, List<ResolutionImage>> idles) {
        IDLES_WITHOUT_MAIN.clear();
        IDLES_WITHOUT_MAIN.putAll(idles);
        IDLES_WITHOUT_MAIN.remove(MAIN);
        IDLES_FOR_RANDOM.clear();
        IDLES_FOR_RANDOM.addAll(IDLES_WITHOUT_MAIN.keySet());
        int idlesSize = IDLES_FOR_RANDOM.size();
        int randomIndex = RANDOM.nextInt(idlesSize);
        return IDLES_FOR_RANDOM.get(randomIndex);
    }

    public ResolutionImage getBasicMain(File programDir) {
        if (basicMain == null) {
            loadBasicMain(programDir);
        }
        return basicMain;
    }

    private void loadBasicMain(File programDir) {
        Map<String, List<ResolutionImage>> basic = idles.get(BASIC);
        boolean basicNotLoaded = basic == null || basic.isEmpty();
        boolean mainNotLoaded = true;
        List<ResolutionImage> main = null;
        if (!basicNotLoaded) {
            main = basic.get(MAIN);
            mainNotLoaded = main == null || main.isEmpty();
        }
        boolean imageNotLoaded = true;
        int firstIndex = 0;
        if (!mainNotLoaded) {
            ResolutionImage openImage = main.get(firstIndex);
            imageNotLoaded = openImage == null;
        }
        if (basicNotLoaded || mainNotLoaded || imageNotLoaded) {
            String path = programDir + animationDir + IDLE_DIR + BASIC_DIR + MAIN_DIR;
            File idleDir = new File(path);
            File[] imagesFiles = idleDir.listFiles(PNG_FILE_FILTER);
            if (imagesFiles == null || imagesFiles.length == 0) return;
            File firstImageFile = imagesFiles[firstIndex];
            ResolutionImage loadedImage = new ResolutionImage(firstImageFile);

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
        basicMain = idles.get(BASIC).get(MAIN).get(firstIndex);
    }
}
