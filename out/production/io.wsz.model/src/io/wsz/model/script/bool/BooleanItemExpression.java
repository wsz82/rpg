package io.wsz.model.script.bool;

import io.wsz.model.asset.Asset;

public abstract class BooleanItemExpression<A extends Asset> extends BooleanExpression<A> {
    private static final long serialVersionUID = 1L;

    protected A checkedItem;

    public BooleanItemExpression() {
    }

    public BooleanItemExpression(String checkedID) {
        super(checkedID);
    }

    public A getCheckedItem() {
        return checkedItem;
    }
}
