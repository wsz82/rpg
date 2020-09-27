package io.wsz.model.script.bool;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;

public abstract class BooleanItemExpression<A extends Asset> extends BooleanObjectExpression<A> {
    private static final long serialVersionUID = 1L;

    public BooleanItemExpression() {
    }

    public BooleanItemExpression(String checkingId) {
        super(checkingId);
    }

    public void setCheckingObject(A checkedItem) {
        this.checkingObject = checkedItem;
    }

    public A getCheckingObject() {
        return checkingObject;
    }

    @Override
    public void setChecker(Controller controller) {
        try {
            checkingObject = (A) controller.getItemOrAssetById(checkingId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
