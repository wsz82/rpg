package io.wsz.model.script.bool;

import io.wsz.model.asset.Asset;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class BooleanExpression<A extends Asset> implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected String itemID;
    protected A checkedItem;

    public abstract boolean isTrue(A checkedItem);

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(itemID);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        itemID = (String) in.readObject();
    }
}
