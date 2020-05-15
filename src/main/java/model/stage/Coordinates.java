package model.stage;

import javafx.scene.input.MouseEvent;

public class Coordinates {
    private final double x;
    private final double y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinates(MouseEvent event) {
        this.x = event.getX();
        this.y = event.getY();
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public String toString() {
        return "" + x + ", " + y;
    }
}
