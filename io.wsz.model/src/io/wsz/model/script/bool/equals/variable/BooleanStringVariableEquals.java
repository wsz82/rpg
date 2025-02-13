package io.wsz.model.script.bool.equals.variable;

import io.wsz.model.Controller;
import io.wsz.model.script.bool.BooleanVariableExpression;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableString;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BooleanStringVariableEquals extends BooleanVariableExpression<VariableString> {
    private static final long serialVersionUID = 1L;

    private EqualableStringVariable equalable;

    public BooleanStringVariableEquals() {
    }

    public BooleanStringVariableEquals(String checkingId, EqualableStringVariable equalable) {
        super(checkingId);
        this.equalable = equalable;
        this.equalable.setExpression(this);
    }

    @Override
    public boolean isTrue() {
        return equalable.isFitAmount();
    }

    @Override
    public void setUpVariables(Controller controller, VariableString checkingOverride) {
        super.setUpVariables(controller, checkingOverride);
        String checkedId = equalable.getCheckedId();
        if (checkedId != null) {
            Variable<?> variable = controller.getGlobalVariableById(checkedId);
            Object value = variable.getValue();
            if (value instanceof String) {
                equalable.setArgument((String) value);
            }
        }
    }

    public EqualableStringVariable getEqualable() {
        return equalable;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);

        out.writeObject(equalable);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);

        equalable = (EqualableStringVariable) in.readObject();
        equalable.setExpression(this);
    }
}
