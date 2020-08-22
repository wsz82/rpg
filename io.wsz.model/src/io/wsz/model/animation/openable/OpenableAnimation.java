package io.wsz.model.animation.openable;

import io.wsz.model.stage.ResolutionImage;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.wsz.model.sizes.Paths.*;

public class OpenableAnimation {
    private final String animationDir;
    private final Map<String, Map<String, List<Image>>> idles;
    private final FileFilter pngFileFilter;

    public OpenableAnimation(String animationDir, Map<String, Map<String, List<Image>>> idles, FileFilter pngFileFilter) {
        this.animationDir = animationDir;
        this.idles = idles;
        this.pngFileFilter = pngFileFilter;
    }

    public Image getBasicMainOpen(File programDir) {
        Map<String, List<Image>> basic = idles.get(BASIC_OPEN);

        boolean basicNotLoaded = basic == null || basic.isEmpty();
        boolean mainNotLoaded = true;
        List<Image> main = null;
        if (!basicNotLoaded) {
            main = basic.get(MAIN);
            mainNotLoaded = main == null || main.isEmpty();
        }
        boolean openImageNotLoaded = true;
        int firstIndex = 0;
        if (!mainNotLoaded) {
            Image openImage = main.get(firstIndex);
            openImageNotLoaded = openImage == null;
        }
        if (basicNotLoaded || mainNotLoaded || openImageNotLoaded) {
            String path = programDir + animationDir + IDLE_DIR + BASIC_OPEN_DIR + MAIN_DIR;
            File idleDir = new File(path);
            File[] imagesFiles = idleDir.listFiles(pngFileFilter);
            if (imagesFiles == null || imagesFiles.length == 0) return null;
            File firstImageFile = imagesFiles[firstIndex];
            Image loadedImage = ResolutionImage.loadImage(firstImageFile);

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
