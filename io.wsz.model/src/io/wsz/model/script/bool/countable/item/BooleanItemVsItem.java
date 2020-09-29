package io.wsz.model.script.bool.countable.item;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.script.bool.BooleanItemExpression;
import io.wsz.model.script.variable.Variable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

//TODO change name
public class BooleanItemVsItem extends BooleanItemExpression<Asset> {
    private static final long serialVersionUID = 1L;

    private CountableItem countable;

    public BooleanItemVsItem() {}

    public BooleanItemVsItem(String checkingId, CountableItem countable) {
        super(checkingId);
        this.countable = countable;
        this.countable.setExpression(this);
    }

    @Override
    public boolean isTrue() {
        return itemHasAmount();
    }

    @Override
    public void setUpVariables(Controller controller, Asset checkingOverride) {
        super.setUpVariables(controller, checkingOverride);
        String checkedVariableId = countable.getCheckedVariableId();
        if (checkedVariableId != null) {
            Variable<?> variable = controller.getGlobalVariableById(checkedVariableId);
            Object value = variable.getValue();
            try {
                countable.setArgument((Integer) value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected boolean itemHasAmount() {
        return countable.isFitAmount();
    }

    public CountableItem getCountable() {
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
        countable = (CountableItem) in.readObject();
        countable.setExpression(this);
    }
}
