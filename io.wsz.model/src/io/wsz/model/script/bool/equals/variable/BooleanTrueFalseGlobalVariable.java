package io.wsz.model.script.bool.equals.variable;

import io.wsz.model.Controller;
import io.wsz.model.script.bool.BooleanVariableExpression;
import io.wsz.model.script.variable.Variable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class BooleanTrueFalseGlobalVariable extends BooleanVariableExpression<Variable<Boolean>> {
    private static final long serialVersionUID = 1L;

    private EqualableTrueFalse equalable;

    public BooleanTrueFalseGlobalVariable() {
    }

    public BooleanTrueFalseGlobalVariable(String checkingId, EqualableTrueFalse equalable) {
        super(checkingId);
        this.equalable = equalable;
        this.equalable.setExpression(this);
    }

    @Override
    public boolean isTrue() {
        return equalable.isFitAmount();
    }

    @Override
    public void setUpVariables(Controller controller) {
        super.setUpVariables(controller);
        String checkedId = equalable.getCheckedId();
        if (checkedId != null) {
            Variable<?> variable = controller.getGlobalVariableById(checkedId);
            Object value = variable.getValue();
            try {
                equalable.setArgument((Boolean) value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public EqualableTrueFalse getEqualable() {
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
        equalable = (EqualableTrueFalse) in.readObject();
        equalable.setExpression(this);
    }
}
