package io.wsz.model.script.bool.countable.variable;

import io.wsz.model.Controller;
import io.wsz.model.script.bool.BooleanVariableExpression;
import io.wsz.model.script.variable.Variable;

public abstract class BooleanNumberGlobalVariable<N extends Number> extends BooleanVariableExpression<Variable<N>> {
    private static final long serialVersionUID = 7884887528603964806L;

    public BooleanNumberGlobalVariable() {
    }

    public BooleanNumberGlobalVariable(String checkingId) {
        super(checkingId);
    }

    public abstract CountableVariable<N> getCountable();

    @Override
    public void setUpVariables(Controller controller, Variable<N> checkingOverride) {
        super.setUpVariables(controller, checkingOverride);
        CountableVariable<N> countable = getCountable();
        String checkedId = countable.getCheckedId();
        if (checkedId != null) {
            Variable<?> variable = controller.getGlobalVariableById(checkedId);
            Object value = variable.getValue();
            try {
                countable.setArgument((N) value);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
