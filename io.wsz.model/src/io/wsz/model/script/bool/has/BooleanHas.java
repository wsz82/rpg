package io.wsz.model.script.bool.has;

import io.wsz.model.asset.Asset;
import io.wsz.model.script.Not;
import io.wsz.model.script.bool.BooleanExpression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class BooleanHas<A extends Asset> extends BooleanExpression<A> {
    private static final long serialVersionUID = 1L;

    protected Not not;

    @Override
    public boolean isTrue(A checkedItem) {
        this.checkedItem = checkedItem;
        return itemHas();
    }

    protected abstract boolean itemHas();

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
