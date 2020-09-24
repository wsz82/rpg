package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.cursor.CursorType;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.asset.Asset;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import io.wsz.model.world.World;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class Equipment<E extends Equipment<?, ?>, B extends EquipmentAnimationPos> extends PosItem<E, B> implements Takeable {
    private static final long serialVersionUID = 1L;

    public static List<Equipment> cloneEquipmentList(List<Equipment> equipment, boolean keepId) {
        List<Equipment> clone = new ArrayList<>(equipment.size());
        for (Equipment e : equipment) {
            clone.add(e.cloneEquipment(keepId));
        }
        return clone;
    }

    protected InventoryPlaceType occupiedPlace;
    protected EquipmentType equipmentType;
    protected Double weight;
    protected Double size;

    public Equipment() {}

    public Equipment(ItemType type, Controller controller) {
        super(type, controller);
    }

    public Equipment(E prototype) {
        super(prototype);
    }

    public Equipment(Equipment other, boolean keepId) {
        super(other, keepId);
        this.occupiedPlace = other.occupiedPlace;
        this.weight = other.weight;
        this.size = other.size;
    }

    @Override
    public boolean tryTake(Creature cr) {
        boolean doesFitInventory = !cr.getInventory().fitsInventory(this);
        if (doesFitInventory) {
            getController().getLogger().logItemDoesNotFitInventory(getName(), cr.getName());
            return false;
        }
        Coords crCenter = cr.getCenter();
        double xFrom = crCenter.x;
        double yFrom = crCenter.y;
        Coords toCoords = getInteractionPoint();
        double xTo = toCoords.x;
        double yTo = toCoords.y;
        PosItem obstacleOnWay = getController().getBoard().getObstacleOnWay(
                pos.getLocation(), pos.level, xFrom, yFrom, this, xTo, yTo);

        if (obstacleOnWay != null) {
            getController().getLogger().logItemCannotBeTakenBecauseIsBehind(getName(), obstacleOnWay.getName());
            return false;
        } else {
            cr.getPos().getLocation().getItemsToRemove().add(this);
            getController().getLogger().logItemAction(getName(), "taken");
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
        Board board = getController().getBoard();
        if (obstacle == null) {
            Coords crCenter = cr.getCenter();
            double xFrom = crCenter.x;
            double yFrom = crCenter.y;
            Coords toCoords = getInteractionPoint();
            double xTo = toCoords.x;
            double yTo = toCoords.y;
            obstacleOnWay = board.getObstacleOnWay(l, pos.level, xFrom, yFrom, this, xTo, yTo);
        }
        if (obstacle != null || outOfLocation || obstacleOnWay != null) {
            pos.x = tempX;
            pos.y = tempY;
            pos.level = tempLevel;
            pos.setLocation(tempLocation);
            String message = "cannot be dropped here";
            if (obstacle != null) {
                message += ": collides with " + obstacle.getName();
            } else if (outOfLocation) {
                message += ": beyond location";
            } else {
                message += ": behind " + obstacleOnWay.getName();
            }
            getController().getLogger().logItemAction(getName(), message);
            return false;
        } else {
            drop(x, y, l, board);
            return true;
        }
    }

    protected void drop(double x, double y, Location l, Board board) {
        l.getItemsToAdd().add(this);
        getController().getLogger().logItemAction(getName(), "dropped");
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        CreatureSize size = cr.getSize();
        if (withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (getObstacleOnWay(cr) != null) return false;
            boolean fits = cr.getIndividualInventory().fitsInventory(this);
            if (fits) {
                cr.getIndividualInventory().tryAdd(this, true);
                if (tryTake(cr)) {
                    pos.x = 0;
                    pos.y = 0;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public abstract B getAnimationPos();

    public abstract Equipment cloneEquipment(boolean keepId);

    public boolean isCountable() {
        return false;
    }

    @Override
    public void restoreReferences(Controller controller, List<Asset> assets, World world) {
        super.restoreReferences(controller, assets, world);
        restoreEquipmentType(world.getEquipmentTypes());
        restoreOccupiedPlace(controller, world.getInventoryPlaces());
    }

    private void restoreEquipmentType(List<EquipmentType> types) {
        EquipmentType serEquipmentType = equipmentType;
        if (serEquipmentType == null) {
            return;
        }
        Optional<EquipmentType> optType = types.stream()
                .filter(t -> t.getId().equals(serEquipmentType.getId()))
                .findFirst();
        EquipmentType equipmentType = optType.orElse(null);
        if (equipmentType == null) {
            throw new NullPointerException("Equipment type \"" + serEquipmentType.getId() + "\" should be in list of equipment types");
        }
        setEquipmentType(equipmentType);
    }

    private void restoreOccupiedPlace(Controller controller, List<InventoryPlaceType> places) {
        InventoryPlaceType serOccupiedPlace = occupiedPlace;
        InventoryPlaceType place = controller.getReferencedPlaceType(places, serOccupiedPlace);
        if (place == null) return;
        setOccupiedPlace(place);
    }

    @Override
    public void setCursor(CursorSetter cursorSetter) {
        cursorSetter.set(CursorType.PICK);
    }

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
            return prototype.getWeight();
        }
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getIndividualSize() {
        return size;
    }

    public Double getSize() {
        if (size == null) {
            if (isThisPrototype()) {
                return 0.0;
            }
            return prototype.getSize();
        }
        return size;
    }

    public void setSize(Double size) {
        this.size = size;
    }

    @Override
    public boolean isUnitIdentical(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;
        if (!super.isUnitIdentical(o)) return false;
        Equipment equipment = (Equipment) o;
        return Objects.equals(getIndividualWeight(), equipment.getIndividualWeight()) &&
                Objects.equals(getIndividualSize(), equipment.getIndividualSize());
    }

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

        size = (Double) in.readObject();
    }
}
