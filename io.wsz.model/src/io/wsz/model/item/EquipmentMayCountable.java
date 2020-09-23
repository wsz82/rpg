package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.cursor.CursorType;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public abstract class EquipmentMayCountable<E extends EquipmentMayCountable<E,?>, A extends EquipmentAnimationPos>
        extends Equipment<E, A>{
    private static final long serialVersionUID = 1L;

    public static void merge(EquipmentMayCountable from, EquipmentMayCountable to) {
        to.setAmount(to.getAmount() + from.getAmount());
    }

    private boolean isCountable;
    private Integer amount;

    public EquipmentMayCountable() {
    }

    public EquipmentMayCountable(ItemType type, Controller controller) {
        super(type, controller);
    }

    public EquipmentMayCountable(E prototype) {
        super(prototype);
    }

    public EquipmentMayCountable(EquipmentMayCountable<E,A> other, boolean keepId) {
        super(other, keepId);
        this.isCountable = other.isCountable;
        this.amount = other.amount;
    }

    public abstract EquipmentMayCountable cloneEquipment(boolean keepId);

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

    public void setCountable(boolean isCountable) {
        this.isCountable = isCountable;
    }

    public Integer getIndividualAmount() {
        return amount;
    }

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
