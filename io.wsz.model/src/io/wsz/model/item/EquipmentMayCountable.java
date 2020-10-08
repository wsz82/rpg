package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.cursor.CursorType;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.item.list.AbstractEquipmentList;
import io.wsz.model.item.list.AbstractEquipmentMayCountableList;
import io.wsz.model.item.list.ItemsList;
import io.wsz.model.item.movement.InventoryCountableMover;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Board;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public abstract class EquipmentMayCountable<E extends EquipmentMayCountable<E,A>, A extends EquipmentAnimationPos>
        extends Equipment<E, A>{
    private static final long serialVersionUID = 1L;

    protected boolean isCountable;
    protected Integer amount;

    public EquipmentMayCountable() {
    }

    public EquipmentMayCountable(ItemType type, Controller controller) {
        super(type, controller);
    }

    public EquipmentMayCountable(E prototype) {
        super(prototype);
    }

    public EquipmentMayCountable(E other, boolean keepId) {
        super(other, keepId);
        this.isCountable = other.isCountable;
        this.amount = other.amount;
    }

    public abstract E cloneEquipment(boolean keepId);

    @Override
    public final void addItemToEquipmentList(AbstractEquipmentList list) {
        addItemToEquipmentMayCountableList(list.getEquipmentMayCountableList());
    }

    @Override
    public final void removeItemFromEquipmentList(AbstractEquipmentList list) {
        removeItemFromEquipmentMayCountableList(list.getEquipmentMayCountableList());
    }

    public abstract void addItemToEquipmentMayCountableList(AbstractEquipmentMayCountableList list);

    public abstract void removeItemFromEquipmentMayCountableList(AbstractEquipmentMayCountableList list);

    @Override
    public Double getWeight() {
        int factor = 1;
        if (isCountable()) {
            factor = getAmount();
        }
        return getUnitWeight() * factor;
    }

    public Double getUnitWeight() {
        if (weight == null) {
            if (isThisPrototype()) {
                return 0.0;
            }
            return prototype.getUnitWeight();
        }
        return weight;
    }

    @Override
    public Double getSize() {
        int factor = 1;
        if (isCountable()) {
            factor = getAmount();
        }
        return getUnitSize() * factor;
    }

    public Double getUnitSize() {
        if (size == null) {
            if (isThisPrototype()) {
                return 0.0;
            }
            return prototype.getUnitSize();
        }
        return size;
    }

    @Override
    public boolean isCountable() {
        if (prototype == null) {
            return isCountable;
        } else {
            return prototype.isCountable();
        }
    }

    @Override
    public void setCursor(CursorSetter cursorSetter) {
        if (isCountable()) {
            CursorType type = CursorType.PICK;
            type.setShowAmount(true);
            type.setAmount(getAmount());
            cursorSetter.set(type);
        } else {
            super.setCursor(cursorSetter);
        }
    }

    @Override
    public void addToLocation(Location location, ItemsList locationItems) {
        if (isCountable()) {
            drop(pos.x, pos.y, location, getController().getBoard());
        }
        onChangeLocationAction(location);
    }

    @Override
    protected void drop(double x, double y, Location location, Board board) {
        boolean isToDropAsIndividual = false;
        if (isCountable()) {
            EquipmentMayCountable<?,?> countable = getItemInDroppedPlace(location, x, y, board);
            if (countable != null && countable.isCountable() && countable.isUnitIdentical(countable)) {
                Integer addedAmount = this.getAmount();
                Integer alreadyInAmount = countable.getAmount();
                int sum = alreadyInAmount + addedAmount;
                countable.setAmount(sum);
            } else {
                isToDropAsIndividual = true;
            }
        } else {
            isToDropAsIndividual = true;
        }
        if (isToDropAsIndividual) {
            addItemToList(location.getItemsList());
            getController().getLogger().logItemAction(getName(), "dropped");
        }
    }

    private EquipmentMayCountable<?,?> getItemInDroppedPlace(Location location, double x, double y, Board board) {
        return board.lookForMayCountableEquipment(
                location.getItemsList().getMergedEquipmentMayCountable(), x, y, pos.level,
                this.getImageWidth(), this.getImageHeight());
    }

    @Override
    protected void moveCountableEquipment(InventoryCountableMover countableMover) {
        countableMover.move(this);
    }

    public void setCountable(boolean isCountable) {
        this.isCountable = isCountable;
    }

    public Integer getIndividualAmount() {
        return amount;
    }

    @Override
    public int getAmount() {
        if (amount == null) {
            if (isThisPrototype()) {
                return 1;
            }
            return prototype.getAmount();
        } else {
            return amount;
        }
    }

    @Override
    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    @Override
    public boolean isUnitIdentical(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipment)) return false;
        if (!super.isUnitIdentical(o)) return false;
        EquipmentMayCountable<?, ?> that = (EquipmentMayCountable<?, ?>) o;
        return isCountable() == that.isCountable();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EquipmentMayCountable)) return false;
        if (!super.equals(o)) return false;
        EquipmentMayCountable<?, ?> that = (EquipmentMayCountable<?, ?>) o;
        return isCountable() == that.isCountable() &&
                Objects.equals(getAmount(), that.getAmount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isCountable(), getAmount());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeBoolean(isCountable);

        out.writeObject(amount);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        isCountable = in.readBoolean();

        amount = (Integer) in.readObject();
    }
}
