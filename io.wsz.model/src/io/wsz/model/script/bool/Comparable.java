package io.wsz.model.script.bool;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class Comparable implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected String checkedId;

    public Comparable() {
    }

    public Comparable(String checkedId) {
        this.checkedId = checkedId;
    }

    public String getCheckedId() {
        return checkedId;
    }

    public void setCheckedId(String checkedId) {
        this.checkedId = checkedId;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(checkedId);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        checkedId = (String) in.readObject();
    }
}
