package io.wsz.model.item;

import io.wsz.model.stage.Coords;

public class Creature extends PosItem {
    private Coords dest;
    private CreatureSize size;
    private CreatureControl control;
    private int speed;

    public Creature(String name, ItemType type, String path, Coords pos, int level) {
        super(name, type, path, pos, level);
    }

    public Creature(String name, ItemType type, String path, Coords pos, int level,
                    Coords dest, CreatureSize size, CreatureControl control, int speed) {
        super(name, type, path, pos, level);
        this.dest = dest;
        this.size = size;
        this.control = control;
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

    public Coords getCenterBottomPos() {
        double width = getImage().getWidth();
        double height = getImage().getHeight();
        double x = pos.getX() + width/2;
        double y = pos.getY() + height;
        return new Coords(x, y, pos.getZ());
    }

    public Coords calcCenterBottomPos(Coords difPos) {
        double width = getImage().getWidth();
        double height = getImage().getHeight();
        double x = difPos.getX() - width/2;
        double y = difPos.getY() - height;
        return new Coords(x, y, difPos.getZ());
    }

    public Coords getDest() {
        return dest;
    }

    public void setDest(Coords dest) {
        this.dest = dest;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public CreatureSize getSize() {
        return size;
    }

    public void setSize(CreatureSize size) {
        this.size = size;
    }

    public CreatureControl getControl() {
        return control;
    }

    public void setControl(CreatureControl control) {
        this.control = control;
    }

    @Override
    public void update() {
        move();
    }
}
