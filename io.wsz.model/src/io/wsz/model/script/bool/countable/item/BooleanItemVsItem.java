package io.wsz.model.script.bool.countable.item;

import io.wsz.model.asset.Asset;
import io.wsz.model.script.bool.countable.Countable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BooleanItemVsItem extends BooleanCountableItem<Asset> {
    private static final long serialVersionUID = 1L;

    private CountableConcreteItem countable;

    public BooleanItemVsItem() {}

    public BooleanItemVsItem(String itemID, CountableConcreteItem countable) {
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
        countable = (CountableConcreteItem) in.readObject();
        countable.setExpression(this);
    }
}
