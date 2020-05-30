package io.wsz.model.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.stage.Coords;

public class Creature extends Item {
    private Coords dest;
    private int speed = 30;

    public Creature(Asset asset, Coords pos, int level) {
        super(asset, pos, level);
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public void move() {
        if (dest == null) {
            return;
        }
        double x1 = pos.getX();
        double x2 = dest.getX();
        double y1 = pos.getY();
        double y2 = dest.getY();
        if ((int) x1 == (int) x2 && (int) y1 == (int) y2) {
            return;
        }
        double dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double moveDist = speed;
        if (dist < speed) {
            moveDist = dist;
        }
        double x3 = x1 + moveDist/dist * (x2 - x1);
        double y3 = y1 + moveDist/dist * (y2 - y1);
        pos.setX(x3);
        pos.setY(y3);
    }

    public Coords getDest() {
        return dest;
    }

    public void setDest(Coords dest) {
        this.dest = dest;
    }

    @Override
    public void update() {
        move();
    }
}
