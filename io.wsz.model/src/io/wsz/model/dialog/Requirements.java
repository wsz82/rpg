package io.wsz.model.dialog;

import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.bool.BooleanExpression;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Requirements implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<BooleanExpression> booleanPChasItemExpressions;
    private List<BooleanExpression> booleanNPChasItemExpressions;
    private List<BooleanExpression> booleanPChasExpressions;
    private List<BooleanExpression> booleanNPChasExpressions;
    private List<BooleanExpression> globalVariablesExpressions;

    public boolean doMatch(Controller controller, Creature pc, PosItem npc) {
        return doMatchExpressions(booleanPChasItemExpressions, pc, controller)
                && doMatchExpressions(booleanNPChasItemExpressions, npc, controller)
                && doMatchExpressions(booleanPChasExpressions, pc, controller)
                && doMatchExpressions(booleanNPChasExpressions, npc, controller)
                && doMatchExpressions(globalVariablesExpressions, null, controller);
    }

    private boolean doMatchExpressions(List<BooleanExpression> expressions, PosItem override, Controller controller) {
        return expressions == null || expressions.stream()
                .allMatch(b -> {
                    b.setUpVariables(controller, override);
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

    public List<BooleanExpression> getBooleanPChasItemExpressions() {
        return booleanPChasItemExpressions;
    }

    public void setBooleanPChasItemExpressions(List<BooleanExpression> booleanPChasItemExpressions) {
        this.booleanPChasItemExpressions = booleanPChasItemExpressions;
    }

    public List<BooleanExpression> getBooleanNPChasItemExpressions() {
        return booleanNPChasItemExpressions;
    }

    public void setBooleanNPChasItemExpressions(List<BooleanExpression> booleanNPChasItemExpressions) {
        this.booleanNPChasItemExpressions = booleanNPChasItemExpressions;
    }

    public List<BooleanExpression> getBooleanPChasExpressions() {
        return booleanPChasExpressions;
    }

    public void setBooleanPChasExpressions(List<BooleanExpression> booleanPChasExpressions) {
        this.booleanPChasExpressions = booleanPChasExpressions;
    }

    public List<BooleanExpression> getBooleanNPChasExpressions() {
        return booleanNPChasExpressions;
    }

    public void setBooleanNPChasExpressions(List<BooleanExpression> booleanNPChasExpressions) {
        this.booleanNPChasExpressions = booleanNPChasExpressions;
    }

    public List<BooleanExpression> getGlobalVariablesExpressions() {
        return globalVariablesExpressions;
    }

    public void setGlobalVariablesExpressions(List<BooleanExpression> globalVariablesExpressions) {
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
        booleanPChasItemExpressions = (List<BooleanExpression>) in.readObject();
        booleanNPChasItemExpressions = (List<BooleanExpression>) in.readObject();
        booleanPChasExpressions = (List<BooleanExpression>) in.readObject();
        booleanNPChasExpressions = (List<BooleanExpression>) in.readObject();
        globalVariablesExpressions = (List<BooleanExpression>) in.readObject();
    }
}
