package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
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
        Content c = getObstacle();
        if (c != null) {
            Coords free = Controller.get().getBoard().getFreePos(getCorners(), c);
            setDest(calcDest(free));
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

    private Content getObstacle() {
        Coords[] poss = getCorners();
        ItemType[] types = new ItemType[] {ItemType.OBSTACLE};
        return Controller.get().getBoard().lookForContent(poss, types, false);
    }

    public Coords getCenterBottomPos() {
        double width = getImage().getWidth();
        double height = getImage().getHeight();
        double x = pos.getX() + width/2;
        double y = pos.getY() + height;
        return new Coords(x, y);
    }

    public Coords calcDest(Coords difPos) {
        double width = getImage().getWidth();
        double height = getImage().getHeight();
        double x = difPos.getX() - width/2;
        double y = difPos.getY() - height;
        return new Coords(x, y);
    }

    public Coords[] getCorners() {
        double halfWidth = size.getWidth()/2.0;
        double halfHeight = size.getHeight()/2.0;
        Coords centerBottomPos = getCenterBottomPos();
        double centerX = centerBottomPos.getX();
        double centerY = centerBottomPos.getY();
        Coords top = new Coords(centerX, centerY - halfHeight);
        Coords bottom = new Coords(centerX, centerY + halfHeight);
        Coords right = new Coords(centerX + halfWidth, centerY);
        Coords left = new Coords(centerX - halfWidth, centerY);
        return new Coords[] {top, bottom, right, left};
    }

    public boolean onInteraction() {
        if (control == CreatureControl.NEUTRAL || control == CreatureControl.ENEMY) {
            return false;
        }
        if (control == CreatureControl.CONTROLABLE) {
            setControl(CreatureControl.CONTROL);
            return true;
        }
        return false;
    }

    public void onStopInteraction() {
        if (control == CreatureControl.CONTROL) {
            setControl(CreatureControl.CONTROLABLE);
        }
    }

    public boolean onInteractWith(Coords pos) {
        Coords[] poss = new Coords[] {pos};
        ItemType[] types = new ItemType[] {ItemType.CREATURE}; //TODO other types
        Content c = Controller.get().getBoard().lookForContent(poss, types, true);
        if (c == null) {
            return false;
        }
        PosItem item = c.getItem();
        if (c.getItem() instanceof Creature) {
            Creature cr = (Creature) item;
            return cr.getControl() == CreatureControl.CONTROLABLE || cr.getControl() == CreatureControl.CONTROL;
        }
        return true;
    }

    public Coords getDest() {
        return dest;
    }

    public void setDest(Coords pos) {
        this.dest = pos;
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
