package model.items;

import javafx.scene.image.Image;
import model.stage.Coordinates;

public class MoveZone extends ImageItem {

    public MoveZone(String name, ItemType type, Coordinates coords, int level, Image image) {
        super(name, type, coords, level, image);
    }
}
