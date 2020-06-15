package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;

import java.util.List;

import static io.wsz.model.Constants.METER;
import static io.wsz.model.Constants.SECOND;
import static io.wsz.model.item.CreatureControl.CONTROL;
import static io.wsz.model.item.CreatureControl.CONTROLLABLE;
import static io.wsz.model.item.ItemType.TELEPORT;

public class Creature extends PosItem<Creature> {
    private Coords dest;
    private CreatureSize size;
    private CreatureControl control;
    private Double speed;

    public Creature(Creature prototype, String name, ItemType type, String path,
                    Boolean visible, Coords pos, Integer level,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path,
                visible, pos, level,
                coverLine, collisionPolygons);
    }

    public Creature(Creature prototype, String name, ItemType type, String path,
                    Boolean visible, Coords pos, Integer level,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons,
                    Coords dest, CreatureSize size, CreatureControl control, Double speed) {
        super(prototype, name, type, path,
                visible, pos, level,
                coverLine, collisionPolygons);
        this.dest = dest;
        this.size = size;
        this.control = control;
        this.speed = speed;
    }

    public void move() {
        if (dest == null) {
            return;
        }
        double x1 = pos.x;
        double x2 = dest.x;
        double y1 = pos.y;
        double y2 = dest.y;
        if (x1 == x2 && y1 == y2) {
            dest = null;
            return;
        }
        double dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        double moveDist = getSpeed();
        if (dist < getSpeed()) {
            moveDist = dist;
        }
        double x3 = x1 + (moveDist/dist * (x2 - x1)) / SECOND;
        double y3 = y1 + (moveDist/dist * (y2 - y1)) / SECOND;
        Coords nextPos = new Coords(x3, y3);
        PosItem pi = getCollision(nextPos);
        if (pi != null) {
            dest = null;
            return;
        }
        pos = nextPos;
    }

    private PosItem getCollision(Coords nextPos) {
        Coords[] poss = getCorners(posToCenter(nextPos));
        return Board.get().lookForObstacle(poss);
    }

    private void checkSurrounding() {
        ItemType[] types = new ItemType[] {TELEPORT};
        PosItem pi = getCornersContent(types);
        if (pi != null) {
            ItemType type = pi.getType();
            switch (type) {
                case TELEPORT -> ((Teleport) pi).enter(this);
            }
        }
        Creature cr = getCornersCreature();
        if (cr != null) {
            escapeCreature(cr);
        }
    }

    private void escapeCreature(Creature cr) {
        Coords free = Controller.get().getBoard().getFreePosCreature(getCorners(), cr);
        setDest(centerToPos(free));
    }

    private Creature getCornersCreature() {
        return Controller.get().getBoard().getCornersCreature(getCorners(), this);
    }

    private PosItem getCornersContent(ItemType[] types) {
        Coords[] poss = getCorners();
        return Controller.get().getBoard().lookForContent(poss, types, false);
    }

    public Coords posToCenter() {
        return posToCenter(pos);
    }

    public Coords posToCenter(Coords pos) {
        double width = getImage().getWidth() / METER;
        double height = getImage().getHeight() / METER;
        double x = pos.x + width/2;
        double y = pos.y + height;
        return new Coords(x, y);
    }

    public Coords centerToPos(Coords difPos) {
        if (difPos == null) {
            return null;
        }
        double width = getImage().getWidth() / METER;
        double height = getImage().getHeight() / METER;
        double x = difPos.x - width/2;
        double y = difPos.y - height;
        return new Coords(x, y);
    }

    public Coords[] getCorners() {
        Coords centerBottomPos = posToCenter();
        return getCorners(centerBottomPos);
    }

    public Coords[] getCorners(Coords pos) {
        double halfWidth = getSize().getWidth()/2;
        double halfHeight = getSize().getHeight()/2;
        double centerX = pos.x;
        double centerY = pos.y;

        Coords N = new Coords(centerX, centerY - halfHeight);
        Coords W = new Coords(centerX - halfWidth, centerY);
        Coords S = new Coords(centerX, centerY + halfHeight);
        Coords E = new Coords(centerX + halfWidth, centerY);
        Coords NE = new Coords(centerX + 3/5.0*halfWidth, centerY - 2/3.0*halfHeight);
        Coords NW = new Coords(centerX - 3/5.0*halfWidth, centerY - 2/3.0*halfHeight);
        Coords SE = new Coords(centerX + 3/5.0*halfWidth, centerY + 2/3.0*halfHeight);
        Coords SW = new Coords(centerX - 3/5.0*halfWidth, centerY + 2/3.0*halfHeight);
        return new Coords[] {N, NW, W, SW, S, SE, E, NE};
    }

    public void interact() {
        if (getControl() == CONTROL) {
            setControl(CONTROLLABLE);
        } else if (getControl() == CONTROLLABLE) {
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
        PosItem pi = Controller.get().getBoard().lookForContent(poss, types, true);
        if (pi == null) {
            return;
        }
        ItemType type = pi.getType();
        switch (type) {
            case CREATURE -> interactWithCreature((Creature) pi);
            default -> setDest(centerToPos(pos));
        }
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

    public Double getIndividualSpeed() {
        return speed;
    }

    public Double getSpeed() {
        if (speed == null) {
            if (prototype == null) {
                return 0.0;
            }
            return prototype.speed;
        } else {
            return speed;
        }
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public CreatureSize getIndividualSize() {
        return size;
    }

    public CreatureSize getSize() {
        if (size == null) {
            if (prototype == null) {
                return CreatureSize.getDefault();
            }
            return prototype.size;
        } else {
            return size;
        }
    }

    public void setSize(CreatureSize size) {
        this.size = size;
    }

    public CreatureControl getIndividualControl() {
        return control;
    }

    public CreatureControl getControl() {
        if (control == null) {
            if (prototype == null) {
                return CreatureControl.getDefault();
            }
            return prototype.getControl();
        } else {
            return control;
        }
    }

    public void setControl(CreatureControl control) {
        this.control = control;
    }

    @Override
    public void changeLocation(Location from, Location target, Layer targetLayer, double targetX, double targetY) {
        super.changeLocation(from, target, targetLayer, targetX, targetY);
        pos = centerToPos(new Coords(targetX, targetY));
    }

    @Override
    public void update() {
        checkSurrounding();
        move();
    }
}
