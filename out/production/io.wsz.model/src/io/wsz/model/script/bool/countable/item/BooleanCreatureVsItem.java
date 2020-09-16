package io.wsz.model.script.bool.countable.item;

import io.wsz.model.item.Creature;
import io.wsz.model.script.bool.countable.Countable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BooleanCreatureVsItem extends BooleanCountableItem<Creature> {
    private static final long serialVersionUID = 1L;

    private CountableCreature countable;

    public BooleanCreatureVsItem() {}

    public BooleanCreatureVsItem(String itemID, CountableCreature countable) { //TODO delete class or make generic edit view
        super(itemID);
        this.countable = countable;
        this.countable.setExpression(this);
    }

    @Override
    protected boolean itemHasAmount() {
        return countable.isFitAmount();
    }

    @Override
    public Countable<Integer> getCountable() {
        return countable;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(countable);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        countable = (CountableCreature) in.readObject();
        countable.setExpression(this);
    }
}
