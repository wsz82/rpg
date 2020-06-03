package io.wsz.model.stage;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.input.MouseEvent;

public class Coords {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();

    public Coords(double x, double y) {
        this.x.set(x);
        this.y.set(y);
    }

    public Coords(MouseEvent event) {
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

    public boolean is0() {
        return x.get() == 0 && y.get() == 0;
    }

    @Override
    public String toString() {
        return "" + x.get() + ", " + y.get();
    }
}
