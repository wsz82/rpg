package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;

import java.util.List;
import java.util.stream.Collectors;

import static io.wsz.model.item.CreatureControl.CONTROL;
import static io.wsz.model.item.CreatureControl.CONTROLLABLE;
import static io.wsz.model.item.ItemType.CREATURE;
import static io.wsz.model.item.ItemType.TELEPORT;

public class Creature extends PosItem {
    private volatile Coords dest;
    private volatile CreatureSize size;
    private volatile CreatureControl control;
    private volatile int speed;

    public Creature(String name, ItemType type, String path, Coords pos, int level, boolean generic,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path, pos, level, generic, coverLine, collisionPolygons);
    }

    public Creature(String name, ItemType type, String path, Coords pos, int level, boolean generic,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons,
                    Coords dest, CreatureSize size, CreatureControl control, int speed) {
        super(name, type, path, pos, level, generic, coverLine, collisionPolygons);
        this.dest = dest;
        this.size = size;
        this.control = control;
        this.speed = speed;
    }

    public void move() {
        if (dest == null) {
            return;
        }
        int x1 = pos.x;
        int x2 = dest.x;
        int y1 = pos.y;
        int y2 = dest.y;
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
        Coords nextPos = new Coords(x3, y3);
        Content c = getCollision(nextPos);
        if (c != null) {
            dest = null;
            return;
        }
        pos = nextPos;
    }

    private Content getCollision(Coords nextPos) {
        Coords[] poss = getCorners(posToCenter(nextPos));
        return Board.get().lookForObstacle(poss);
    }

    private void checkSurrounding() {
        ItemType[] types = new ItemType[] {TELEPORT};
        Content c = getCornersContent(types);
        if (c != null) {
            PosItem item = c.getItem();
            ItemType type = item.getType();
            switch (type) {
                case TELEPORT -> enterTeleport((Teleport) item);
            }
        }
        Creature cr = getCornersCreature(this);
        if (cr != null) {
            escapeCreature(cr);
        }
    }

    private void escapeCreature(Creature cr) {
        Coords free = Controller.get().getBoard().getFreePosCreature(getCorners(), cr);
        setDest(centerToPos(free));
    }

    private Creature getCornersCreature(Creature cr) {
        return Controller.get().getBoard().getCornersCreature(getCorners(), cr);
    }

    private Content getCornersContent(ItemType[] types) {
        Coords[] poss = getCorners();
        return Controller.get().getBoard().lookForContent(poss, types, false);
    }

    public Coords posToCenter() {
        return posToCenter(pos);
    }

    public Coords posToCenter(Coords pos) {
        double width = getImage().getWidth();
        double height = getImage().getHeight();
        int x = pos.x + (int) (width/2);
        int y = pos.y + (int) height;
        return new Coords(x, y);
    }

    public Coords centerToPos(Coords difPos) {
        if (difPos == null) {
            return null;
        }
        double width = getImage().getWidth();
        double height = getImage().getHeight();
        int x = difPos.x - (int) (width/2);
        int y = difPos.y - (int) height;
        return new Coords(x, y);
    }

    public Coords[] getCorners() {
        Coords centerBottomPos = posToCenter();
        return getCorners(centerBottomPos);
    }

    public Coords[] getCorners(Coords pos) {
        int halfWidth = size.getWidth()/2;
        int halfHeight = size.getHeight()/2;
        int centerX = pos.x;
        int centerY = pos.y;

        Coords N = new Coords(centerX, centerY - halfHeight);
        Coords W = new Coords(centerX - halfWidth, centerY);
        Coords S = new Coords(centerX, centerY + halfHeight);
        Coords E = new Coords(centerX + halfWidth, centerY);
        Coords NE = new Coords(centerX + (int) (3/5.0*halfWidth), centerY - (int) (2/3.0*halfHeight));
        Coords NW = new Coords(centerX - (int) (3/5.0*halfWidth), centerY - (int) (2/3.0*halfHeight));
        Coords SE = new Coords(centerX + (int) (3/5.0*halfWidth), centerY + (int) (2/3.0*halfHeight));
        Coords SW = new Coords(centerX - (int) (3/5.0*halfWidth), centerY + (int) (2/3.0*halfHeight));
        return new Coords[] {N, NW, W, SW, S, SE, E, NE};
    }

    public void interact() {
        if (control == CONTROL) {
            setControl(CONTROLLABLE);
        } else if (control == CONTROLLABLE) {
            looseAllControl();
            setControl(CONTROL);
        }
    }

    private void looseAllControl() {
        Controller.get().getBoard().getControlledCreatures()
                .forEach(Creature::loseControl);
    }

    public void loseControl() {
        setControl(CONTROLLABLE);
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
            default -> setDest(centerToPos(pos));
        }
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
        int targetX = targetPos.x;
        int targetWidth = target.getWidth();
        int targetY = targetPos.y;
        int targetHeight = target.getHeight();
        if (targetX < targetWidth && targetY < targetHeight) {
            Location from = Controller.get().getCurrentLocation().getLocation();
            changeLocation(from, target, targetLayer, targetX, targetY);
            if (control.equals(CONTROL)) {
                Controller.get().setUpdatedLocation(target);
                Controller.get().getCurrentLayer().setLayer(targetLayer);
                centerScreenOn(targetX, targetY);
            }
            dest = null;
        }
    }

    private void centerScreenOn(double targetX, double targetY) {
        Controller.get().centerScreenOn(targetX, targetY);
    }

    private void interactWithCreature(Creature cr) {
        if (cr.getControl().equals(CONTROLLABLE)) {
            cr.setControl(CONTROL);
            this.setControl(CONTROLLABLE);
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
        if (getPos() != null) {
            return;
        }
        Controller.get().getLocationsList().forEach(l -> {
            l.getContents().get().stream()
                    .filter(c -> c.getItem().getType().equals(CREATURE))
                    .filter(c -> c.getItem().getName().equals(getName()))
                    .filter(c -> c.getItem().isGeneric())
                    .forEach(c -> ((Creature) c.getItem()).setSpeed(speed));
        });
    }

    public CreatureSize getSize() {
        return size;
    }

    public void setSize(CreatureSize size) {
        this.size = size;
        if (getPos() != null) {
            return;
        }
        Controller.get().getLocationsList().forEach(l -> {
            l.getContents().get().stream()
                    .filter(c -> c.getItem().getType().equals(CREATURE))
                    .filter(c -> c.getItem().getName().equals(getName()))
                    .filter(c -> c.getItem().isGeneric())
                    .forEach(c -> ((Creature) c.getItem()).setSize(size));
        });
    }

    public CreatureControl getControl() {
        return control;
    }

    public void setControl(CreatureControl control) {
        this.control = control;
        if (getPos() != null) {
            return;
        }
        Controller.get().getLocationsList().forEach(l -> {
            l.getContents().get().stream()
                    .filter(c -> c.getItem().getType().equals(CREATURE))
                    .filter(c -> c.getItem().getName().equals(getName()))
                    .filter(c -> c.getItem().isGeneric())
                    .forEach(c -> ((Creature) c.getItem()).setControl(control));
        });
    }

    @Override
    public void changeLocation(Location from, Location target, Layer targetLayer, int targetX, int targetY) {
        super.changeLocation(from, target, targetLayer, targetX, targetY);
        pos = centerToPos(new Coords(targetX, targetY));
    }

    @Override
    public void update() {
        checkSurrounding();
        move();
    }
}
