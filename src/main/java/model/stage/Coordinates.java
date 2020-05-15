package model.stage;

import javafx.scene.input.MouseEvent;

public class Coordinates {
    private final int x;
    private final int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates(MouseEvent event) {
        this.x = (int) event.getX();
        this.y = (int) event.getY();
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
