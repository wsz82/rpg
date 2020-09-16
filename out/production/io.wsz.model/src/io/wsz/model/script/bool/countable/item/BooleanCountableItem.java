package io.wsz.model.script.bool.countable.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.script.bool.BooleanItemExpression;
import io.wsz.model.script.bool.countable.Countable;

public abstract class BooleanCountableItem<A extends Asset> extends BooleanItemExpression<A> {
    private static final long serialVersionUID = 1L;

    public BooleanCountableItem() {}

    public BooleanCountableItem(String itemID) {
        super(itemID);
    }

    public boolean isTrue(A checkedItem) {
        this.checkedItem = checkedItem;
        return itemHasAmount();
    }

    public abstract Countable<?> getCountable();

    protected abstract boolean itemHasAmount();

}
