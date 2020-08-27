package io.wsz.model.textures;

import io.wsz.model.sizes.Paths;
import io.wsz.model.stage.ResolutionImage;

import java.io.File;

public class Fog {
    private ResolutionImage image;

    public Fog() {}

    public final ResolutionImage getImage(File programDir) {
        if (image == null) {
            String path = programDir + Paths.TEXTURES_DIR + Paths.FOG_DIR + Paths.FOG_BASE_NAME_DIR;
            setImage(new ResolutionImage(path));
        }
        return image;
    }

    public void setImage(ResolutionImage image) {
        this.image = image;
    }
}
