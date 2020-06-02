package io.wsz.model.plugin;

import java.io.Serializable;

public class CoordsSerializable implements Serializable {
    private double x;
    private double y;

    public CoordsSerializable(double x, double y) {
        this.x = x;
        this.y = y;
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
}
