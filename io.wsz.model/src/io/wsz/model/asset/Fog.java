package io.wsz.model.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.image.Image;

import java.io.File;

public class Fog extends Asset {
    protected Image image;

    public Fog() {
        setName("fog");
        setType(ItemType.FOG);
        setPath("fog.png");
    }

    public final Image getImage(File programDir) {
        if (image == null) {
            String type = getType().toString().toLowerCase();
            String path = getPath();
            setImage(ResolutionImage.loadImage(programDir, type, path));
        }
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
