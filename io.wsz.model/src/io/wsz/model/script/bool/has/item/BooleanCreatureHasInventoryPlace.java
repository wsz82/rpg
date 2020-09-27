package io.wsz.model.script.bool.has.item;

import io.wsz.model.item.Creature;
import io.wsz.model.script.bool.BooleanItemExpression;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BooleanCreatureHasInventoryPlace extends BooleanItemExpression<Creature> {
    private static final long serialVersionUID = 1L;

    private HasableCreatureInventoryPlace hasable;

    public BooleanCreatureHasInventoryPlace() {
    }

    public BooleanCreatureHasInventoryPlace(String checkingId, HasableCreatureInventoryPlace hasable) {
        super(checkingId);
        this.hasable = hasable;
    }

    @Override
    public void setCheckingObject(Creature item) {
        this.checkingObject = item;
    }

    @Override
    public boolean isTrue() {
        return hasable.has();
    }

    public HasableCreatureInventoryPlace getHasable() {
        return hasable;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(hasable);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        hasable = (HasableCreatureInventoryPlace) in.readObject();
        hasable.setExpression(this);
    }
}
