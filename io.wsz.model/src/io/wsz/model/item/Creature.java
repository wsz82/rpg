package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.util.List;
import java.util.stream.Collectors;

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
        ItemType[] types = new ItemType[] {ItemType.OBSTACLE, ItemType.TELEPORT};
        Content c = getCornersContent(types);
        if (c != null) {
            PosItem item = c.getItem();
            ItemType type = item.getType();
            switch (type) {
                case OBSTACLE -> escapeObstacle(c);
                case TELEPORT -> enterTeleport((Teleport) item);
            }
        }
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

    private void escapeObstacle(Content c) {
        Coords free = Controller.get().getBoard().getFreePos(getCorners(), c);
        setDest(calcDest(free));
    }

    private Content getCornersContent(ItemType[] types) {
        Coords[] poss = getCorners();
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
        if (difPos == null) {
            return null;
        }
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

    public void interact() {
        if (control == CreatureControl.CONTROL) {
            setControl(CreatureControl.CONTROLABLE);
        } else if (control == CreatureControl.CONTROLABLE) {
            looseAllControl();
            setControl(CreatureControl.CONTROL);
        }
    }

    private void looseAllControl() {
        Controller.get().getBoard().getControlledCreatures()
                .forEach(Creature::loseControl);
    }

    public void loseControl() {
        setControl(CreatureControl.CONTROLABLE);
    }

    public void onInteractWith(Coords pos) {
        Coords[] poss = new Coords[] {pos};
        ItemType[] types = ItemType.values();
        Content c = Controller.get().getBoard().lookForContent(poss, types, true);
        if (c == null) {
            return;
        }
        PosItem item = c.getItem(); //TODO other types
        ItemType type = item.getType();
        switch (type) {
            case CREATURE -> interactWithCreature((Creature) item);
            case OBSTACLE -> doNothing();
            default -> setDest(calcDest(pos));
        }
    }

    private void doNothing() {
    }

    private void enterTeleport(Teleport t) {
        List<Location> singleLocation = Controller.get().getLocationsList().stream()
                .filter(l -> l.getName().equals(t.getLocationName()))
                .collect(Collectors.toList());
        Location target = singleLocation.get(0);
        if (target == null) {
            return;
        }
        int targetLevel = t.getExitLevel();
        List<Layer> singleLayer = target.getLayers().get().stream()
                .filter(l -> l.getLevel() == targetLevel)
                .collect(Collectors.toList());
        Layer targetLayer = singleLayer.get(0);
        if (targetLayer == null) {
            return;
        }
        Coords targetPos = t.getExit();
        double targetX = targetPos.getX();
        int targetWidth = target.getWidth();
        double targetY = targetPos.getY();
        int targetHeight = target.getHeight();
        if (targetX < targetWidth && targetY < targetHeight) {
            Location from = Controller.get().getCurrentLocation().getLocation();
            changeLocation(from, target, targetLayer, targetX, targetY);
            Controller.get().getCurrentLocation().setLocation(target);
            Controller.get().getCurrentLayer().setLayer(targetLayer);
            centerScreenOn(targetX, targetY);
            dest = null;
        }
    }

    private void centerScreenOn(double targetX, double targetY) {
        Controller.get().centerScreenOn(targetX, targetY);
    }

    private void interactWithCreature(Creature cr) {
        if (cr.getControl().equals(CreatureControl.CONTROLABLE)) {
            cr.setControl(CreatureControl.CONTROL);
            this.setControl(CreatureControl.CONTROLABLE);
        }
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
