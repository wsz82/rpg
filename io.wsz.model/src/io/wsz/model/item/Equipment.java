package io.wsz.model.item;

import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class Equipment<E extends Equipment, B extends EquipmentAnimationPos> extends PosItem<E, B> implements Takeable {
    private static final long serialVersionUID = 1L;

    public static List<Equipment> cloneEquipmentList(List<Equipment> equipment) {
        List<Equipment> clone = new ArrayList<>(equipment.size());
        for (Equipment e : equipment) {
            clone.add(e.cloneEquipment());
        }
        return clone;
    }

    protected InventoryPlaceType occupiedPlace;
    protected EquipmentType equipmentType;
    protected Double weight;
    protected Integer size;

    public Equipment() {}

    public Equipment(ItemType type) {
        super(type);
    }

    public Equipment(E prototype, Boolean visible) {
        super(prototype, visible);
    }

    public Equipment(Equipment other) {
        super(other);
        this.occupiedPlace = other.occupiedPlace;
        this.weight = other.weight;
        this.size = other.size;
    }

    public abstract Equipment cloneEquipment();

    public InventoryPlaceType getIndividualOccupiedPlace() {
        return occupiedPlace;
    }

    public InventoryPlaceType getOccupiedPlace() {
        if (prototype == null) {
            return occupiedPlace;
        } else {
            return prototype.getOccupiedPlace();
        }
    }

    public void setOccupiedPlace(InventoryPlaceType occupiedPlace) {
        this.occupiedPlace = occupiedPlace;
    }

    public EquipmentType getIndividualEquipmentType() {
        return equipmentType;
    }

    public EquipmentType getEquipmentType() {
        if (equipmentType == null) {
            if (isThisPrototype()) {
                return EquipmentType.DEFAULT;
            }
            return prototype.getEquipmentType();
        } else {
            return equipmentType;
        }
    }

    public void setEquipmentType(EquipmentType equipmentType) {
        this.equipmentType = equipmentType;
    }


    public Double getIndividualWeight() {
        return weight;
    }

    public Double getWeight() {
        if (weight == null) {
            if (isThisPrototype()) {
                return 0.0;
            }
            return prototype.weight;
        }
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Integer getIndividualSize() {
        return size;
    }

    public Integer getSize() {
        if (size == null) {
            if (isThisPrototype()) {
                return 0;
            }
            return prototype.size;
        }
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public boolean tryTake(Creature cr) {
        boolean doesFitInventory = !cr.getInventory().fitsInventory(this);
        if (doesFitInventory) {
            System.out.println(getName() + " does not fit " + cr.getName() + " inventory");
            return false;
        }
        Coords crCenter = cr.getCenter();
        double xFrom = crCenter.x;
        double yFrom = crCenter.y;
        Coords toCoords = getInteractionCoords();
        double xTo = toCoords.x;
        double yTo = toCoords.y;
        PosItem obstacleOnWay = getController().getBoard().getObstacleOnWay(
                pos.getLocation(), pos.level, xFrom, yFrom, this, xTo, yTo);

        if (obstacleOnWay != null) {
            String message = getName() + " cannot be taken: behind " + obstacleOnWay.getName();
            System.out.println(message);
            return false;
        } else {
            setVisible(false);
            cr.getPos().getLocation().getItemsToRemove().add(this);
            System.out.println(getName() + " taken");
            return true;
        }
    }

    @Override
    public boolean tryDrop(Creature cr, double x, double y) {
        double tempX = pos.x;
        double tempY = pos.y;
        int tempLevel = pos.level;
        Location tempLocation = pos.getLocation();
        pos.x = x;
        pos.y = y;
        pos.level = cr.getPos().level;
        Location l = cr.getPos().getLocation();
        pos.setLocation(l);
        boolean outOfLocation = x < 0 || y < 0
                || x > l.getWidth() || y > l.getHeight();
        PosItem obstacle = null;
        if (!outOfLocation) {
            obstacle = getCollision();
        }
        PosItem obstacleOnWay = null;
        if (obstacle == null) {
            Coords crCenter = cr.getCenter();
            double xFrom = crCenter.x;
            double yFrom = crCenter.y;
            Coords toCoords = getInteractionCoords();
            double xTo = toCoords.x;
            double yTo = toCoords.y;
            obstacleOnWay = getController().getBoard().getObstacleOnWay(l, pos.level, xFrom, yFrom, this, xTo, yTo);
        }
        if (obstacle != null || outOfLocation || obstacleOnWay != null) {
            pos.x = tempX;
            pos.y = tempY;
            pos.level = tempLevel;
            pos.setLocation(tempLocation);
            String message = getName() + " cannot be dropped here";
            if (obstacle != null) {
                message += ": collides with " + obstacle.getName();
            } else if (outOfLocation) {
                message += ": beyond location";
            } else {
                message += ": behind " + obstacleOnWay.getName();
            }
            System.out.println(message);
            return false;
        } else {
            l.getItemsToAdd().add(this);
            System.out.println(getName() + " dropped");
            return true;
        }
    }

    @Override
    public abstract B getAnimationPos();



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;
        if (!super.equals(o)) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(getWeight(), equipment.getWeight()) &&
                Objects.equals(getSize(), equipment.getSize());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getWeight(), getSize());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(occupiedPlace);

        out.writeObject(equipmentType);

        out.writeObject(weight);

        out.writeObject(size);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        occupiedPlace = (InventoryPlaceType) in.readObject();

        equipmentType = (EquipmentType) in.readObject();

        weight = (Double) in.readObject();

        size = (Integer) in.readObject();
    }
}
