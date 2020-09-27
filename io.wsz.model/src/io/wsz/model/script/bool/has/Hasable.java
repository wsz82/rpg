package io.wsz.model.script.bool.has;

import io.wsz.model.script.Not;
import io.wsz.model.script.bool.Comparable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class Hasable extends Comparable {
    private static final long serialVersionUID = 1L;

    protected Not not;

    public Hasable() {
    }

    public Hasable(String checkedId, Not not) {
        super(checkedId);
        this.not = not;
    }

    public abstract boolean has();

    public Not getNot() {
        return not;
    }

    public void setNot(Not not) {
        this.not = not;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(not);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        not = (Not) in.readObject();
    }
}
