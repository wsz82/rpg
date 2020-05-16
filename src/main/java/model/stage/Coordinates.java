package model.stage;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.MouseEvent;

public class Coordinates {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public Coordinates(int x, int y) {
        this.x.set(x);
        this.y.set(y);
    }

    public Coordinates(MouseEvent event) {
        this.x.set(event.getX());
        this.y.set(event.getY());
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public void setX(double x) {
        this.x.set(x);
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public void setY(double y) {
        this.y.set(y);
    }

    public double getX() {
        return x.get();
    }

    public double getY() {
        return y.get();
    }

    @Override
    public String toString() {
        return "" + x.get() + ", " + y.get();
    }
}
