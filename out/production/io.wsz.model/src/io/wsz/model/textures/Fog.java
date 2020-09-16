package io.wsz.model.textures;

import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static io.wsz.model.sizes.Paths.*;

public class Fog {
    private static final Random RANDOM = new Random();

    private List<ResolutionImage> fogs;
    private double fogSize;

    public Fog() {}

    public void initAllFogs(File programDir) {
        String programFogDir = programDir + TEXTURES_DIR + FOG_DIR;
        File file = new File(programFogDir);
        if (fogs == null) {
            fogs = new ArrayList<>(0);
        }
        updateFogImages(file);
    }

    public ResolutionImage getRandomFog() {
        int idlesSize = fogs.size();
        int randomIndex = RANDOM.nextInt(idlesSize);
        return fogs.get(randomIndex);
    }

    private void updateFogImages(File framesDir) {
        File[] imagesFiles = framesDir.listFiles(f -> f.getName().endsWith(PNG));
        if (imagesFiles == null || imagesFiles.length == 0) return;
        for (int i = 0; i < imagesFiles.length; i++) {
            File imageFile = imagesFiles[i];
            ResolutionImage loadedFrame = new ResolutionImage(imageFile);
            if (i < fogs.size()) {
                ResolutionImage actualResolutionImage = fogs.get(i);
                actualResolutionImage.setFxImage(loadedFrame.getFxImage());
                actualResolutionImage.setWidth(loadedFrame.getWidth());
                actualResolutionImage.setHeight(loadedFrame.getHeight());
            } else {
                fogs.add(loadedFrame);
            }
            fogSize = loadedFrame.getWidth() / Sizes.getMeter();
        }
    }

    public double getFogSize() {
        return fogSize;
    }

    public double getHalfFogSize() {
        return fogSize/2;
    }
}
