package io.wsz.model.animation.openable;

import io.wsz.model.animation.Animation;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;
import java.util.*;

import static io.wsz.model.sizes.Paths.*;

public class OpenableAnimation<O extends PosItem<?,?>> extends Animation<O> {
    private static final List<String> SEQUENCES_FOR_RANDOM = new ArrayList<>(0);

    private final Map<String, Map<String, List<ResolutionImage>>> operating = new HashMap<>(0);

    public OpenableAnimation(String animationDir, String idlesOrEquivalent) {
        super(animationDir, idlesOrEquivalent);
    }

    @Override
    public void initOtherAnimations(File animationDir, String fileName) {
        if (fileName.equals(OPERATING)) {
            operating.clear();
            initAnimations(animationDir, operating);
        }
    }

    public ResolutionImage getOperatingImage(boolean isOpening, double speed, OpenableAnimationPos animationPos) {
        long curTime = System.currentTimeMillis();
        boolean isNotTimeForNextFrameUpdate = curTime < animationPos.getNextFrameUpdate();
        if (isNotTimeForNextFrameUpdate) return null;

        String curOperatingAnimation = animationPos.getCurOperatingAnimation();
        if (curOperatingAnimation == null) {
            return finishOperatingAnimationWithNull(animationPos);
        }
        Map<String, List<ResolutionImage>> operatingAnimations = operating.get(curOperatingAnimation);
        if (operatingAnimations == null) {
            return finishOperatingAnimationWithNull(animationPos);
        }
        String curOperatingSequence = animationPos.getCurOperatingSequence();
        if (curOperatingSequence == null) {
            curOperatingSequence = getRandomOperatingSequence(operatingAnimations.keySet());
            animationPos.setCurOperatingSequence(curOperatingSequence);
            animationPos.setFrameNumber(-1);
        }
        List<ResolutionImage> frames = operatingAnimations.get(curOperatingSequence);
        if (frames == null || frames.isEmpty()) {
            return finishOperatingAnimationWithNull(animationPos);
        }

        int size = frames.size();

        long nextFrameUpdateTime = getNextUpdate(size, speed);
        animationPos.setNextFrameUpdate(nextFrameUpdateTime);

        int nextFrame;

        if (isOpening) {
            nextFrame = animationPos.getNextFrameNumber(size);
        } else {
            nextFrame = animationPos.getPreviousFrameNumber(size);
        }
        if (animationPos.isCycleFinished()) {
            finishOperatingAnimation(animationPos);
        }
        return frames.get(nextFrame);
    }

    private ResolutionImage finishOperatingAnimationWithNull(OpenableAnimationPos animationPos) {
        finishOperatingAnimation(animationPos);
        return null;
    }

    private void finishOperatingAnimation(OpenableAnimationPos animationPos) {
        animationPos.setOpenableAnimationType(OpenableAnimationType.IDLE);
        animationPos.setCurOperatingSequence(null);
    }

    private String getRandomOperatingSequence(Set<String> keySet) {
        SEQUENCES_FOR_RANDOM.addAll(keySet);
        int size = SEQUENCES_FOR_RANDOM.size();
        int randomIndex = RANDOM.nextInt(size);
        return SEQUENCES_FOR_RANDOM.get(randomIndex);
    }

    public ResolutionImage getBasicMainOpen(File programDir) {
        Map<String, List<ResolutionImage>> basic = idles.get(BASIC_OPEN);

        boolean basicNotLoaded = basic == null || basic.isEmpty();
        boolean mainNotLoaded = true;
        List<ResolutionImage> main = null;
        if (!basicNotLoaded) {
            main = basic.get(MAIN);
            mainNotLoaded = main == null || main.isEmpty();
        }
        boolean openImageNotLoaded = true;
        int firstIndex = 0;
        if (!mainNotLoaded) {
            ResolutionImage openImage = main.get(firstIndex);
            openImageNotLoaded = openImage == null;
        }
        if (basicNotLoaded || mainNotLoaded || openImageNotLoaded) {
            String path = programDir + animationDir + IDLE_DIR + BASIC_OPEN_DIR + MAIN_DIR;
            File idleDir = new File(path);
            File[] imagesFiles = idleDir.listFiles(PNG_FILE_FILTER);
            if (imagesFiles == null || imagesFiles.length == 0) return null;
            File firstImageFile = imagesFiles[firstIndex];
            ResolutionImage loadedImage = new ResolutionImage(firstImageFile);

            if (basic == null) {
                basic = new HashMap<>(1);
                idles.put(BASIC_OPEN, basic);
            }
            if (main == null) {
                main = new ArrayList<>(1);
                basic.put(MAIN, main);
            }
            main.add(loadedImage);
        }
        return idles.get(BASIC_OPEN).get(MAIN).get(firstIndex);
    }

    public boolean isNotOpenable(File programDir) {
        return getBasicMainOpen(programDir) == null;
    }
}
