package io.wsz.model.stage;

import java.io.Serializable;

public class Coords implements Serializable {
    private double x;
    private double y;

    public Coords(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean is0() {
        return x == 0 && y == 0;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "" + x + ", " + y;
    }
}
