package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.util.List;
import java.util.stream.Collectors;

public class Creature extends PosItem {
    private volatile Coords dest;
    private volatile CreatureSize size;
    private volatile CreatureControl control;
    private volatile int speed;

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
        int x1 = pos.getX();
        int x2 = dest.getX();
        int y1 = pos.getY();
        int y2 = dest.getY();
        if (x1 == x2 && y1 == y2) {
            dest = null;
            return;
        }
        double dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double moveDist = speed;
        if (dist < speed) {
            moveDist = dist;
        }
        int x3 = x1 + (int) (moveDist/dist * (x2 - x1));
        int y3 = y1 + (int) (moveDist/dist * (y2 - y1));
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
        int x = pos.getX() + (int) (width/2);
        int y = pos.getY() + (int) height;
        return new Coords(x, y);
    }

    public Coords calcDest(Coords difPos) {
        if (difPos == null) {
            return null;
        }
        double width = getImage().getWidth();
        double height = getImage().getHeight();
        int x = difPos.getX() - (int) (width/2);
        int y = difPos.getY() - (int) height;
        return new Coords(x, y);
    }

    public Coords[] getCorners() {
        int halfWidth = size.getWidth()/2;
        int halfHeight = size.getHeight()/2;
        Coords centerBottomPos = getCenterBottomPos();
        int centerX = centerBottomPos.getX();
        int centerY = centerBottomPos.getY();
        Coords top = new Coords(centerX, centerY - halfHeight);
        Coords bottom = new Coords(centerX, centerY + halfHeight);
        Coords right = new Coords(centerX + halfWidth, centerY);
        Coords left = new Coords(centerX - halfWidth, centerY);
        return new Coords[] {top, bottom, right, left};
    }

    public void interact() {
        if (control == CreatureControl.CONTROL) {
            setControl(CreatureControl.CONTROLLABLE);
        } else if (control == CreatureControl.CONTROLLABLE) {
            looseAllControl();
            setControl(CreatureControl.CONTROL);
        }
    }

    private void looseAllControl() {
        Controller.get().getBoard().getControlledCreatures()
                .forEach(Creature::loseControl);
    }

    public void loseControl() {
        setControl(CreatureControl.CONTROLLABLE);
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
        int targetX = targetPos.getX();
        int targetWidth = target.getWidth();
        int targetY = targetPos.getY();
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
        if (cr.getControl().equals(CreatureControl.CONTROLLABLE)) {
            cr.setControl(CreatureControl.CONTROL);
            this.setControl(CreatureControl.CONTROLLABLE);
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
