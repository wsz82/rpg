package io.wsz.model.script.bool.countable;

import io.wsz.model.asset.Asset;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.script.CompareOperator;
import io.wsz.model.script.bool.BooleanExpression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public abstract class BooleanCountable<A extends Asset> extends BooleanExpression<A> {
    private static final long serialVersionUID = 1L;

    protected CompareOperator compareOperator;
    protected int argument;

    public BooleanCountable() {}

    public BooleanCountable(CompareOperator compareOperator, String itemID, int argument) {
        this.compareOperator = compareOperator;
        this.itemID = itemID;
        this.argument = argument;
    }

    @Override
    public boolean isTrue(A checkedItem) {
        this.checkedItem = checkedItem;
        return itemHasAmount();
    }

    protected boolean itemHasAmount() {
        return switch (compareOperator) {
            case EQUAL -> isEqual();
            case NOT_EQUAL -> isNotEqual();
            case GREATER -> isGreater();
            case GREATER_OR_EQUAL -> isGreaterOrEqual();
            case LESSER -> isLesser();
            case LESSER_OR_EQUAL -> isLesserOrEqual();
        };
    }

    protected boolean isLesserOrEqual() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return true;
        }
        return amount <= argument;
    }

    protected boolean isLesser() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return true;
        }
        return amount < argument;
    }

    protected boolean isGreaterOrEqual() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return false;
        }
        return amount >= argument;
    }

    protected boolean isGreater() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return false;
        }
        return amount > argument;
    }

    protected boolean isNotEqual() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return true;
        }
        return amount != argument;
    }

    protected boolean isEqual() {
        long amount;
        try {
            amount = getAmount();
        } catch (NullPointerException e) {
            return false;
        }
        return amount == argument;
    }

    protected abstract long getAmount();

    protected long getCreatureAmount(Creature cr) {
        List<Equipment> items = cr.getItems();
        long count = items.stream()
                .filter(i -> i.getAssetId().equals(itemID))
                .count();
        count += cr.getInventory().getEquippedItems().values().stream()
                .filter(i -> i.getAssetId().equals(itemID))
                .count();
        return count;
    }

    public CompareOperator getCompareOperator() {
        return compareOperator;
    }

    public void setCompareOperator(CompareOperator compareOperator) {
        this.compareOperator = compareOperator;
    }

    public int getArgument() {
        return argument;
    }

    public void setArgument(int argument) {
        this.argument = argument;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(compareOperator);
        out.writeInt(argument);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        compareOperator = (CompareOperator) in.readObject();
        argument = in.readInt();
    }
}
