package model.items;

import javafx.scene.image.Image;
import model.stage.Coordinates;

public abstract class ImageItem extends Item{
    private Image image;

    public ImageItem(String name, ItemType type, Coordinates coords, int level, Image image) {
        super(name, type, coords, level);
        this.image = image;
    }
}
