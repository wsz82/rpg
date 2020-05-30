package io.wsz.model.plugin;

import java.io.Serializable;

class CoordinatesSerializable implements Serializable {
    private double x;
    private double y;
    private int z;

    CoordinatesSerializable(double x, double y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    double getX() {
        return x;
    }

    void setX(double x) {
        this.x = x;
    }

    double getY() {
        return y;
    }

    void setY(double y) {
        this.y = y;
    }

    int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }
}
