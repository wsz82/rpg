package io.wsz.model.stage;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.input.MouseEvent;

public class Coords {
    private final DoubleProperty x = new SimpleDoubleProperty();
    private final DoubleProperty y = new SimpleDoubleProperty();
    private final IntegerProperty z = new SimpleIntegerProperty();

    public Coords(double x, double y, int z) {
        this.x.set(x);
        this.y.set(y);
        this.z.set(z);
    }

    public Coords(MouseEvent event, int z) {
        this.x.set(event.getX());
        this.y.set(event.getY());
        this.z.set(z);
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

    public int getZ() {
        return z.get();
    }

    public IntegerProperty zProperty() {
        return z;
    }

    public void setZ(int z) {
        this.z.set(z);
    }

    @Override
    public String toString() {
        return "" + x.get() + ", " + y.get() + ", " + z.get();
    }
}
