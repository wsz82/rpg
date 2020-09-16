package io.wsz.model.dialog;

import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.bool.BooleanItemExpression;
import io.wsz.model.script.bool.countable.item.BooleanCountableItem;
import io.wsz.model.script.bool.countable.variable.BooleanNumberGlobalVariable;
import io.wsz.model.script.bool.equals.variable.BooleanStringVariableEquals;
import io.wsz.model.script.variable.Variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Requirements implements Externalizable {
    private static final long serialVersionUID = 1L;
    private static final Collection<Variable<String>> GLOBAL_STRING_VARIABLES = new ArrayList<>(0);

    private List<BooleanCountableItem> booleanPChasItemExpressions;
    private List<BooleanCountableItem> booleanNPChasItemExpressions;
    private List<BooleanItemExpression> booleanPChasExpressions;
    private List<BooleanItemExpression> booleanNPChasExpressions;
    private List<BooleanNumberGlobalVariable> booleanNumberGlobalVariablesExpressions;
    private List<BooleanStringVariableEquals> booleanStringGlobalVariablesExpressions;

    public boolean doMatch(Controller controller, Creature pc, PosItem npc) {
        return doMatchItemHasCountableRequirements(pc, booleanPChasItemExpressions)
                && doMatchItemHasCountableRequirements(npc, booleanNPChasItemExpressions)
                && doMatchItemHasRequirements(pc, booleanPChasExpressions)
                && doMatchItemHasRequirements(npc, booleanNPChasExpressions)
                && doMatchNumberGlobalVariablesExpressions(booleanNumberGlobalVariablesExpressions, controller)
                && doMatchStringGlobalVariablesExpressions(booleanStringGlobalVariablesExpressions, controller);
    }

    private boolean doMatchStringGlobalVariablesExpressions(List<BooleanStringVariableEquals> expressions, Controller controller) {
        GLOBAL_STRING_VARIABLES.clear();
        controller.getModel().getActivePlugin().getWorld().getGlobalVariables().stream()
                .filter(v -> v.getValue() instanceof String)
                .map(v -> (Variable<String>) v)
                .collect(Collectors.toCollection(() -> GLOBAL_STRING_VARIABLES));
        return expressions == null || expressions.stream()
                .allMatch(b -> {
                    Variable<String> variable = GLOBAL_STRING_VARIABLES.stream()
                            .filter(v -> {
                                String variableID = b.getCheckedID();
                                return v.getID().equals(variableID);
                            })
                            .findFirst()
                            .orElse(null);
                    return variable == null || b.isTrue(variable);
                });
    }

    private boolean doMatchNumberGlobalVariablesExpressions(List<BooleanNumberGlobalVariable> expressions, Controller controller) {
        List<Variable<?>> globalVariables = controller.getModel().getActivePlugin().getWorld().getGlobalVariables();
        return expressions == null || expressions.stream()
                .allMatch(b -> {
                    Variable<?> variable = globalVariables.stream()
                            .filter(v -> {
                                String variableID = b.getCheckedID();
                                return v.getID().equals(variableID);
                            })
                            .findFirst()
                            .orElse(null);
                    return variable == null || b.isTrue(variable);
                });
    }

    private boolean doMatchItemHasRequirements(PosItem item, List<BooleanItemExpression> expressions) {
        return expressions == null || expressions.stream()
                .allMatch(b -> b.isTrue(item));
    }

    private boolean doMatchItemHasCountableRequirements(PosItem item, List<BooleanCountableItem> expressions) {
        return expressions == null || expressions.stream()
                .allMatch(b -> b.isTrue(item));
    }

    public boolean isEmpty() {
        return (booleanPChasItemExpressions == null || booleanPChasItemExpressions.isEmpty())
                && (booleanNPChasItemExpressions == null || booleanNPChasItemExpressions.isEmpty())
                && (booleanPChasExpressions == null || booleanPChasExpressions.isEmpty())
                && (booleanNPChasExpressions == null || booleanNPChasExpressions.isEmpty())
                && (booleanNumberGlobalVariablesExpressions == null || booleanNumberGlobalVariablesExpressions.isEmpty())
                && (booleanStringGlobalVariablesExpressions == null || booleanStringGlobalVariablesExpressions.isEmpty());
    }

    public List<BooleanCountableItem> getBooleanPChasItemExpressions() {
        return booleanPChasItemExpressions;
    }

    public void setBooleanPChasItemExpressions(List<BooleanCountableItem> booleanPChasItemExpressions) {
        this.booleanPChasItemExpressions = booleanPChasItemExpressions;
    }

    public List<BooleanCountableItem> getBooleanNPChasItemExpressions() {
        return booleanNPChasItemExpressions;
    }

    public void setBooleanNPChasItemExpressions(List<BooleanCountableItem> booleanNPChasItemExpressions) {
        this.booleanNPChasItemExpressions = booleanNPChasItemExpressions;
    }

    public List<BooleanItemExpression> getBooleanPChasExpressions() {
        return booleanPChasExpressions;
    }

    public void setBooleanPChasExpressions(List<BooleanItemExpression> booleanPChasExpressions) {
        this.booleanPChasExpressions = booleanPChasExpressions;
    }

    public List<BooleanItemExpression> getBooleanNPChasExpressions() {
        return booleanNPChasExpressions;
    }

    public void setBooleanNPChasExpressions(List<BooleanItemExpression> booleanNPChasExpressions) {
        this.booleanNPChasExpressions = booleanNPChasExpressions;
    }

    public List<BooleanNumberGlobalVariable> getBooleanNumberGlobalVariablesExpressions() {
        return booleanNumberGlobalVariablesExpressions;
    }

    public void setBooleanNumberGlobalVariablesExpressions(List<BooleanNumberGlobalVariable> booleanNumberGlobalVariablesExpressions) {
        this.booleanNumberGlobalVariablesExpressions = booleanNumberGlobalVariablesExpressions;
    }

    public List<BooleanStringVariableEquals> getBooleanStringGlobalVariablesExpressions() {
        return booleanStringGlobalVariablesExpressions;
    }

    public void setBooleanStringGlobalVariablesExpressions(List<BooleanStringVariableEquals> booleanStringGlobalVariablesExpressions) {
        this.booleanStringGlobalVariablesExpressions = booleanStringGlobalVariablesExpressions;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(booleanPChasItemExpressions);
        out.writeObject(booleanNPChasItemExpressions);
        out.writeObject(booleanPChasExpressions);
        out.writeObject(booleanNPChasExpressions);
        out.writeObject(booleanNumberGlobalVariablesExpressions);
        out.writeObject(booleanStringGlobalVariablesExpressions);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        booleanPChasItemExpressions = (List<BooleanCountableItem>) in.readObject();
        booleanNPChasItemExpressions = (List<BooleanCountableItem>) in.readObject();
        booleanPChasExpressions = (List<BooleanItemExpression>) in.readObject();
        booleanNPChasExpressions = (List<BooleanItemExpression>) in.readObject();
        booleanNumberGlobalVariablesExpressions = (List<BooleanNumberGlobalVariable>) in.readObject();
        booleanStringGlobalVariablesExpressions = (List<BooleanStringVariableEquals>) in.readObject();
    }
}
