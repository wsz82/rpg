package io.wsz.model.script.bool;

import io.wsz.model.Controller;
import io.wsz.model.script.variable.Variable;

public abstract class BooleanVariableExpression<V extends Variable<?>> extends BooleanObjectExpression<V> {
    private static final long serialVersionUID = 1L;

    public BooleanVariableExpression() {
    }

    public BooleanVariableExpression(String checkingId) {
        super(checkingId);
    }

    public void setCheckingObject(V variable) {
        this.checkingObject = variable;
    }

    public V getCheckingObject() {
        return checkingObject;
    }

    @Override
    public void setUpVariables(Controller controller) {
        try {
            checkingObject = (V) controller.getGlobalVariableById(checkingId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
