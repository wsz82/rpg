package model.items;

import javafx.scene.image.Image;
import model.stage.Coordinates;

public class Landscape extends ImageItem {

    public Landscape(String name, ItemType type, Coordinates coords, int level, Image image) {
        super(name, type, coords, level, image);
    }
}
