package io.wsz.model.script.bool;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class BooleanObjectExpression<O> extends BooleanExpression<O> implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected String checkingId;
    protected transient O checkingObject;

    public BooleanObjectExpression() {
    }

    public BooleanObjectExpression(String checkingId) {
        this.checkingId = checkingId;
    }

    public String getCheckingId() {
        return checkingId;
    }

    public void setCheckingId(String checkingId) {
        this.checkingId = checkingId;
    }

    public void setCheckingObject(O checkingObject) {
        this.checkingObject = checkingObject;
    }

    public O getCheckingObject() {
        return checkingObject;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(checkingId);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        checkingId = (String) in.readObject();
    }
}
