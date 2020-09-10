package io.wsz.model.script.bool;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class BooleanExpression<O> implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected String checkedID;

    public BooleanExpression() {
    }

    public BooleanExpression(String checkedID) {
        this.checkedID = checkedID;
    }

    public abstract boolean isTrue(O checkedObject);

    public String getCheckedID() {
        return checkedID;
    }

    public void setCheckedID(String checkedID) {
        this.checkedID = checkedID;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(checkedID);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        checkedID = (String) in.readObject();
    }
}
