package model.stage;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

public class Coordinates {
    private int x;
    private int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates(Region region, MouseEvent event) {
        this.x = (int) event.getX();
        int currentScreenHeight = (int) region.getHeight();
        this.y = ((int) event.getY() - currentScreenHeight + 1) / -1;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "" + x + ", " + y;
    }
}
