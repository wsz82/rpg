package io.wsz.model.dialog;

import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.bool.BooleanItemExpression;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import io.wsz.model.script.bool.countable.variable.BooleanNumberGlobalVariable;
import io.wsz.model.script.bool.countable.variable.BooleanTrueFalseGlobalVariable;
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
    private static final Collection<Variable<Boolean>> GLOBAL_BOOLEAN_VARIABLES = new ArrayList<>(0);

    private List<BooleanItemVsItem> booleanPChasItemExpressions;
    private List<BooleanItemVsItem> booleanNPChasItemExpressions;
    private List<BooleanItemExpression> booleanPChasExpressions;
    private List<BooleanItemExpression> booleanNPChasExpressions;
    private List<BooleanNumberGlobalVariable> booleanNumberGlobalVariablesExpressions;
    private List<BooleanStringVariableEquals> booleanStringGlobalVariablesExpressions;
    private List<BooleanTrueFalseGlobalVariable> booleanTrueFalseGlobalVariablesExpressions;

    public boolean doMatch(Controller controller, Creature pc, PosItem npc) {
        return doMatchItemHasCountableRequirements(pc, booleanPChasItemExpressions)
                && doMatchItemHasCountableRequirements(npc, booleanNPChasItemExpressions)
                && doMatchItemHasRequirements(pc, booleanPChasExpressions)
                && doMatchItemHasRequirements(npc, booleanNPChasExpressions)
                && doMatchNumberGlobalVariablesExpressions(booleanNumberGlobalVariablesExpressions, controller)
                && doMatchStringGlobalVariablesExpressions(booleanStringGlobalVariablesExpressions, controller)
                && doMatchBooleanGlobalVariablesExpressions(booleanTrueFalseGlobalVariablesExpressions, controller);
    }

    private boolean doMatchBooleanGlobalVariablesExpressions(List<BooleanTrueFalseGlobalVariable> expressions, Controller controller) {
        GLOBAL_BOOLEAN_VARIABLES.clear();
        controller.getModel().getActivePlugin().getWorld().getGlobalVariables().stream()
                .filter(v -> v.getValue() instanceof Boolean)
                .map(v -> (Variable<Boolean>) v)
                .collect(Collectors.toCollection(() -> GLOBAL_BOOLEAN_VARIABLES));
        return expressions == null || expressions.stream()
                .allMatch(b -> {
                    Variable<Boolean> variable = GLOBAL_BOOLEAN_VARIABLES.stream()
                            .filter(v -> {
                                String variableID = b.getCheckingId();
                                return v.getID().equals(variableID);
                            })
                            .findFirst()
                            .orElse(null);
                    b.setCheckingObject(variable);
                    return variable == null || b.isTrue();
                });
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
                                String variableID = b.getCheckingId();
                                return v.getID().equals(variableID);
                            })
                            .findFirst()
                            .orElse(null);
                    b.setCheckingObject(variable);
                    return variable == null || b.isTrue();
                });
    }

    private boolean doMatchNumberGlobalVariablesExpressions(List<BooleanNumberGlobalVariable> expressions, Controller controller) {
        List<Variable<?>> globalVariables = controller.getModel().getActivePlugin().getWorld().getGlobalVariables();
        return expressions == null || expressions.stream()
                .allMatch(b -> {
                    Variable<?> variable = globalVariables.stream()
                            .filter(v -> {
                                String variableID = b.getCheckingId();
                                return v.getID().equals(variableID);
                            })
                            .findFirst()
                            .orElse(null);
                    b.setCheckingObject(variable);
                    return variable == null || b.isTrue();
                });
    }

    private boolean doMatchItemHasRequirements(PosItem item, List<BooleanItemExpression> expressions) {
        return expressions == null || expressions.stream()
                .allMatch(b -> {
                    b.setCheckingObject(item);
                    return b.isTrue();
                });
    }

    private boolean doMatchItemHasCountableRequirements(PosItem item, List<BooleanItemVsItem> expressions) {
        return expressions == null || expressions.stream()
                .allMatch(b -> {
                    b.setCheckingObject(item);
                    return b.isTrue();
                });
    }

    public boolean isEmpty() {
        return (booleanPChasItemExpressions == null || booleanPChasItemExpressions.isEmpty())
                && (booleanNPChasItemExpressions == null || booleanNPChasItemExpressions.isEmpty())
                && (booleanPChasExpressions == null || booleanPChasExpressions.isEmpty())
                && (booleanNPChasExpressions == null || booleanNPChasExpressions.isEmpty())
                && (booleanNumberGlobalVariablesExpressions == null || booleanNumberGlobalVariablesExpressions.isEmpty())
                && (booleanStringGlobalVariablesExpressions == null || booleanStringGlobalVariablesExpressions.isEmpty())
                && (booleanTrueFalseGlobalVariablesExpressions == null || booleanTrueFalseGlobalVariablesExpressions.isEmpty());
    }

    public List<BooleanItemVsItem> getBooleanPChasItemExpressions() {
        return booleanPChasItemExpressions;
    }

    public void setBooleanPChasItemExpressions(List<BooleanItemVsItem> booleanPChasItemExpressions) {
        this.booleanPChasItemExpressions = booleanPChasItemExpressions;
    }

    public List<BooleanItemVsItem> getBooleanNPChasItemExpressions() {
        return booleanNPChasItemExpressions;
    }

    public void setBooleanNPChasItemExpressions(List<BooleanItemVsItem> booleanNPChasItemExpressions) {
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

    public List<BooleanTrueFalseGlobalVariable> getBooleanTrueFalseGlobalVariablesExpressions() {
        return booleanTrueFalseGlobalVariablesExpressions;
    }

    public void setBooleanTrueFalseGlobalVariablesExpressions(List<BooleanTrueFalseGlobalVariable> booleanTrueFalseGlobalVariablesExpressions) {
        this.booleanTrueFalseGlobalVariablesExpressions = booleanTrueFalseGlobalVariablesExpressions;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(booleanPChasItemExpressions);
        out.writeObject(booleanNPChasItemExpressions);
        out.writeObject(booleanPChasExpressions);
        out.writeObject(booleanNPChasExpressions);
        out.writeObject(booleanNumberGlobalVariablesExpressions);
        out.writeObject(booleanStringGlobalVariablesExpressions);
        out.writeObject(booleanTrueFalseGlobalVariablesExpressions);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        booleanPChasItemExpressions = (List<BooleanItemVsItem>) in.readObject();
        booleanNPChasItemExpressions = (List<BooleanItemVsItem>) in.readObject();
        booleanPChasExpressions = (List<BooleanItemExpression>) in.readObject();
        booleanNPChasExpressions = (List<BooleanItemExpression>) in.readObject();
        booleanNumberGlobalVariablesExpressions = (List<BooleanNumberGlobalVariable>) in.readObject();
        booleanStringGlobalVariablesExpressions = (List<BooleanStringVariableEquals>) in.readObject();
        booleanTrueFalseGlobalVariablesExpressions = (List<BooleanTrueFalseGlobalVariable>) in.readObject();
    }
}
