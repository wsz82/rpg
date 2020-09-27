package io.wsz.model.script.bool.has;

import io.wsz.model.script.Not;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class Hasable implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected String checkedId;
    protected Not not;

    public Hasable() {
    }

    public Hasable(String checkedId, Not not) {
        this.checkedId = checkedId;
        this.not = not;
    }

    public abstract boolean has();

    public String getCheckedId() {
        return checkedId;
    }

    public void setCheckedId(String checkedId) {
        this.checkedId = checkedId;
    }

    public Not getNot() {
        return not;
    }

    public void setNot(Not not) {
        this.not = not;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(checkedId);
        out.writeObject(not);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        checkedId = (String) in.readObject();
        not = (Not) in.readObject();
    }
}
