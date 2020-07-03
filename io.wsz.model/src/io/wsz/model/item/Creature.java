package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import javafx.scene.image.Image;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.net.MalformedURLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import static io.wsz.model.item.CreatureControl.*;
import static io.wsz.model.item.ItemType.TELEPORT;
import static io.wsz.model.sizes.Sizes.CONSTANT_METER;

public class Creature extends PosItem<Creature> implements Containable {
    private static final long serialVersionUID = 1L;

    private static final ItemType[] CHECK_SURROUNDING_TYPES = new ItemType[] {TELEPORT};

    private final Coords[] corners = new Coords[]{new Coords(), new Coords(), new Coords(), new Coords(),
            new Coords(), new Coords(), new Coords(), new Coords()};
    private final Coords[] interactionCoords = new Coords[]{new Coords()};
    private final Coords centerBottom = new Coords();
    private final Coords reversCenterBottom = new Coords();

    private Location enteredToFlag;

    private Image portrait;
    private String portraitPath;
    private final Task task = new Task(this);
    private Inventory inventory;

    private CreatureSize size;
    private CreatureControl control;
    private Double speed;
    private Double range;
    private Integer strength;

    public Creature() {}

    public Creature(Creature prototype, String name, ItemType type, String path,
                    Boolean visible, Integer level,
                    List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(prototype, name, type, path,
                visible, level,
                coverLine, collisionPolygons);
    }

    public PosItem getCollision(Coords nextPos) {
        Coords[] poss = getCorners(nextPos);
        PosItem collidedObstacle = Controller.get().getBoard().lookForObstacle(poss, pos.getLocation());
        if (collidedObstacle == null) {
            return Controller.get().getBoard().getCornersCreature(poss, this);
        }
        return collidedObstacle;
    }

    private void checkSurrounding() {
        PosItem pi = getCornersContent(CHECK_SURROUNDING_TYPES);
        if (pi != null) {
            ItemType type = pi.getType();
            switch (type) {
                case TELEPORT -> ((Teleport) pi).enter(this);
            }
        }

        if (enteredToFlag != null && enteredToFlag == pos.getLocation()) {
            enteredToFlag = null;
            PosItem collided = getCollision(getCenterBottomPos());
            if (collided != null) {
                Coords freePos = Board.get().getFreePosAround(this);
                Coords reversed = reverseCenterBottomPos(freePos);
                pos.x = reversed.x;
                pos.y = reversed.y;
            }
        }
    }

    private PosItem getCornersContent(ItemType[] types) {
        Coords[] poss = getCorners();
        return Controller.get().getBoard().lookForContent(poss, types, false);
    }

    public Coords getCenterBottomPos() {
        return getCenterBottomPos(pos);
    }

    public Coords getCenterBottomPos(Coords pos) {
        double width = getImage().getWidth() / Sizes.getMeter();
        double height = getImage().getHeight() / Sizes.getMeter();
        centerBottom.x = pos.x + width/2;
        centerBottom.y = pos.y + height;
        return centerBottom;
    }

    public Coords reverseCenterBottomPos(Coords difPos) {
        if (difPos == null) {
            return null;
        }
        double width = getImage().getWidth() / Sizes.getMeter();
        double height = getImage().getHeight() / Sizes.getMeter();
        reversCenterBottom.x = difPos.x - width/2;
        reversCenterBottom.y = difPos.y - height;
        return reversCenterBottom;
    }

    public Coords[] getCorners() {
        Coords centerBottomPos = getCenterBottomPos();
        return getCorners(centerBottomPos);
    }

    public Coords[] getCorners(Coords pos) {
        double halfWidth = getSize().getWidth()/2;
        double halfHeight = getSize().getHeight()/2;
        double centerX = pos.x;
        double centerY = pos.y;

        corners[0].x = centerX;
        corners[0].y = centerY - halfHeight;
        corners[1].x = centerX - halfWidth;
        corners[1].y = centerY;
        corners[2].x = centerX;
        corners[2].y = centerY + halfHeight;
        corners[3].x = centerX + halfWidth;
        corners[3].y = centerY;
        corners[4].x = centerX + 3/5.0*halfWidth;
        corners[4].y = centerY - 2/3.0*halfHeight;
        corners[5].x = centerX - 3/5.0*halfWidth;
        corners[5].y = centerY - 2/3.0*halfHeight;
        corners[6].x = centerX + 3/5.0*halfWidth;
        corners[6].y = centerY + 2/3.0*halfHeight;
        corners[7].x = centerX - 3/5.0*halfWidth;
        corners[7].y = centerY + 2/3.0*halfHeight;
        return corners;
    }

    public void onInteractWith(Coords pos) {
        interactionCoords[0].x = pos.x;
        interactionCoords[0].y = pos.y;
        ItemType[] types = ItemType.values();
        PosItem pi = Controller.get().getBoard().lookForContent(interactionCoords, types, true);
        if (pi == null) {
            return;
        }
        ItemType type = pi.getType();

        switch (type) {
            case CREATURE ->
                    resolveInteractionWithCreature((Creature) pi);
            case WEAPON, CONTAINER ->
                    setItemTask(pi);
            default ->
                    goTo(pos);
        }
    }

    private void resolveInteractionWithCreature(Creature cr) {
        CreatureControl control = cr.getControl();
        if (control.equals(CONTROLLABLE)) {
            cr.setControl(CONTROL);
            this.setControl(CONTROLLABLE);
        } else if (control.equals(NEUTRAL)) {
            setItemTask(cr);
        }
    }

    public void goTo(Coords pos) {
        this.task.setDest(reverseCenterBottomPos(pos));
    }

    private void setItemTask(PosItem e) {
        this.task.setItem(e);
    }

    public boolean creatureWithinRange(Creature cr) {
        Coords ePos = cr.getCenterBottomPos();
        Coords[] poss = getCorners();
        for (Coords corner : poss) {
            double dist = getDistance(corner.x, ePos.x, corner.y, ePos.y);
            if (dist <= getRange()) {
                return true;
            }
        }
        return false;
    }

    public boolean withinRange(PosItem e) {
        Coords ePos = e.getCenter();
        Coords[] poss = getCorners();
        for (Coords corner : poss) {
            double dist = getDistance(corner.x, ePos.x, corner.y, ePos.y);
            if (dist <= getRange()) {
                return true;
            }
        }
        return false;
    }

    public List<Equipment> getEquipmentWithinRange() {
        Coords[] poss = getCorners();
        return Controller.get().getBoard().getEquipmentWithinRange(poss, this);
    }

    public double getDistance(double x1, double x2, double y1, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    private void checkTask() {
        if (task.getDest() == null && task.getItem() == null) {
            return;
        }
        task.doTask();
    }

    public double getCreatureLeft(double imgLeft, double imgWidth) {
        double width = getSize().getWidth();
        if (imgWidth < width) {
            double halfDif = (width - imgWidth) / 2;
            imgLeft -= halfDif;
        }
        return imgLeft;
    }

    public double getCreatureRight(double imgWidth, double imgRight) {
        double width = getSize().getWidth();
        if (imgWidth < width) {
            double halfDif = (width - imgWidth) / 2;
            imgRight += halfDif;
        }
        return imgRight;
    }

    public double getCreatureTop(double imgTop, double imgHeight) {
        double halfHeight = getCreatureHalfHeight();
        double imgBottom = imgTop + imgHeight;
        if (imgHeight < halfHeight) {
            imgTop = imgBottom - halfHeight;
        }
        return imgTop;
    }

    public double getCreatureBottom(double imgBottom) {
        double halfHeight = getCreatureHalfHeight();
        imgBottom += halfHeight;
        return imgBottom;
    }

    private double getCreatureHalfHeight() {
        double height = getSize().getHeight();
        return height / 2;
    }

    public Task getTask() {
        return task;
    }

    public Inventory getIndividualInventory() {
        return inventory;
    }

    public Inventory getInventory() {
        if (inventory == null) {
            if (prototype == null) {
                return new Inventory(this);
            }
            return prototype.inventory;
        } else {
            return inventory;
        }
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
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

    public Double getIndividualRange() {
        return range;
    }

    public Double getRange() {
        if (range == null) {
            if (prototype == null) {
                return 0.0;
            }
            return prototype.range;
        } else {
            return range;
        }
    }

    public void setRange(Double range) {
        this.range = range;
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

    public Integer getIndividualStrength() {
        return strength;
    }

    public Integer getStrength() {
        if (strength == null) {
            if (prototype == null) {
                return 0;
            }
            return prototype.strength;
        } else {
            return strength;
        }
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public Image getPortrait() {
        if (this.portrait == null) {
            setPortrait(loadPortraitFromPath(getPortraitPath()));
        }
        return portrait;
    }

    private Image loadPortraitFromPath(String fileName) {
        String path = getRelativeTypePath(getType()) + File.separator + fileName;
        if (path.isEmpty()) {
            throw new NoSuchElementException();
        }
        File fixedFile = new File(Controller.getProgramDir() + path);
        String url = null;
        try {
            url = fixedFile.toURI().toURL().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url == null) {
            return null;
        }

        int portraitSize = Sizes.getPortraitSize();
        if (portraitSize == 0) {
            return null;
        }

        if (Sizes.getTrueMeter() == CONSTANT_METER) {
            return new Image(url, portraitSize, portraitSize, false, false, true);
        } else {
            Dimension d = new Dimension(portraitSize, portraitSize);
            if (d == null) {
                throw new NullPointerException(url + " dimension is null");
            }
            Dimension rd = getRequestedDimension(d);
            return getChangedImage(url, d, rd);
        }
    }

    public void setPortrait(Image portrait) {
        this.portrait = portrait;
    }

    public String getIndividualPortraitPath() {
        return portraitPath;
    }

    public String getPortraitPath() {
        if (portraitPath == null || portraitPath.isEmpty()) {
            if (prototype == null) {
                return "";
            }
            return prototype.portraitPath;
        } else {
            return portraitPath;
        }
    }

    public void setPortraitPath(String portraitPath) {
        this.portraitPath = portraitPath;
    }

    @Override
    public void changeLocation(Location from, Location target, Layer targetLayer, double targetX, double targetY) {
        super.changeLocation(from, target, targetLayer, targetX, targetY);
        Coords rawPos = new Coords(targetX, targetY, null);
        Coords reversed = reverseCenterBottomPos(rawPos);
        this.pos.x = reversed.x;
        this.pos.y = reversed.y;
        task.clear();
        enteredToFlag = target;
    }

    @Override
    public void update() {
        checkSurrounding();
        checkTask();
    }

    @Override
    public List<Equipment> getItems() {
        return inventory.getItems();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Creature creature = (Creature) o;
        return Objects.equals(getTask(), creature.getTask()) &&
                Objects.equals(getInventory(), creature.getInventory()) &&
                getSize() == creature.getSize() &&
                getControl() == creature.getControl() &&
                Objects.equals(getSpeed(), creature.getSpeed()) &&
                Objects.equals(getRange(), creature.getRange()) &&
                Objects.equals(getStrength(), creature.getStrength());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getTask(), getInventory(), getSize(), getControl(), getSpeed(), getRange(), getStrength());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(task);

        out.writeObject(inventory);

        out.writeObject(size);

        out.writeObject(control);

        out.writeObject(speed);

        out.writeObject(range);

        out.writeObject(strength);

        out.writeObject(portraitPath);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        Task readTask = (Task) in.readObject();
        task.setDest(readTask.getDest());
        task.setItem(readTask.getItem());

        inventory = (Inventory) in.readObject();

        size = (CreatureSize) in.readObject();

        control = (CreatureControl) in.readObject();

        speed = (Double) in.readObject();

        range = (Double) in.readObject();

        strength = (Integer) in.readObject();

        portraitPath = (String) in.readObject();
    }
}
