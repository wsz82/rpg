package io.wsz.model.script.bool;

import io.wsz.model.Controller;

import java.io.Externalizable;

public abstract class BooleanExpression<O> implements Externalizable {
    private static final long serialVersionUID = 1L;

    public abstract boolean isTrue();

    public abstract void setUpVariables(Controller controller, O checkingOverride);

    public abstract String getCheckingId();
}
