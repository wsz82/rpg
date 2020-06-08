package io.wsz.model.stage;

import java.io.Serializable;

public class Coords implements Serializable {
    private volatile int x;
    private volatile int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean is0() {
        return x == 0 && y == 0;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "X: " + x + ", Y: " + y;
    }
}
