package io.wsz.model.dialog;

import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.bool.BooleanItemExpression;
import io.wsz.model.script.bool.BooleanVariableExpression;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Requirements implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<BooleanItemVsItem> booleanPChasItemExpressions;
    private List<BooleanItemVsItem> booleanNPChasItemExpressions;
    private List<BooleanItemExpression> booleanPChasExpressions;
    private List<BooleanItemExpression> booleanNPChasExpressions;
    private List<BooleanVariableExpression> globalVariablesExpressions;

    public boolean doMatch(Controller controller, Creature pc, PosItem npc) {
        return doMatchItemHasCountableRequirements(pc, booleanPChasItemExpressions)
                && doMatchItemHasCountableRequirements(npc, booleanNPChasItemExpressions)
                && doMatchItemHasRequirements(pc, booleanPChasExpressions)
                && doMatchItemHasRequirements(npc, booleanNPChasExpressions)
                && doMatchBooleanGlobalExpressions(globalVariablesExpressions, controller);
    }

    private boolean doMatchBooleanGlobalExpressions(List<BooleanVariableExpression> expressions, Controller controller) {
        return expressions == null || expressions.stream()
                .allMatch(b -> {
                    b.setUpVariables(controller);
                    return b.isTrue();
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
                && (globalVariablesExpressions == null || globalVariablesExpressions.isEmpty());
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

    public List<BooleanVariableExpression> getGlobalVariablesExpressions() {
        return globalVariablesExpressions;
    }

    public void setGlobalVariablesExpressions(List<BooleanVariableExpression> globalVariablesExpressions) {
        this.globalVariablesExpressions = globalVariablesExpressions;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(booleanPChasItemExpressions);
        out.writeObject(booleanNPChasItemExpressions);
        out.writeObject(booleanPChasExpressions);
        out.writeObject(booleanNPChasExpressions);
        out.writeObject(globalVariablesExpressions);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        booleanPChasItemExpressions = (List<BooleanItemVsItem>) in.readObject();
        booleanNPChasItemExpressions = (List<BooleanItemVsItem>) in.readObject();
        booleanPChasExpressions = (List<BooleanItemExpression>) in.readObject();
        booleanNPChasExpressions = (List<BooleanItemExpression>) in.readObject();
        globalVariablesExpressions = (List<BooleanVariableExpression>) in.readObject();
    }
}
