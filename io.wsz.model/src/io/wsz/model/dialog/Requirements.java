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

    private List<BooleanExpression> PChasAssetExpressions;
    private List<BooleanExpression> NPChasAssetExpressions;
    private List<BooleanExpression> PChasItemExpressions;
    private List<BooleanExpression> NPChasItemExpressions;
    private List<BooleanExpression> PChasExpressions;
    private List<BooleanExpression> NPChasExpressions;
    private List<BooleanExpression> globalVariablesExpressions;

    public boolean doMatch(Controller controller, Creature pc, PosItem npc) {
        return doMatchExpressions(PChasAssetExpressions, pc, controller)
                && doMatchExpressions(NPChasAssetExpressions, npc, controller)
                && doMatchExpressions(PChasItemExpressions, pc, controller)
                && doMatchExpressions(NPChasItemExpressions, npc, controller)
                && doMatchExpressions(PChasExpressions, pc, controller)
                && doMatchExpressions(NPChasExpressions, npc, controller)
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
        return (PChasAssetExpressions == null || PChasAssetExpressions.isEmpty())
                && (NPChasAssetExpressions == null || NPChasAssetExpressions.isEmpty())
                && (PChasExpressions == null || PChasExpressions.isEmpty())
                && (NPChasExpressions == null || NPChasExpressions.isEmpty())
                && (globalVariablesExpressions == null || globalVariablesExpressions.isEmpty())
                && (PChasItemExpressions == null || PChasItemExpressions.isEmpty())
                && (NPChasItemExpressions == null || NPChasItemExpressions.isEmpty());
    }

    public List<BooleanExpression> getPChasAssetExpressions() {
        return PChasAssetExpressions;
    }

    public void setPChasAssetExpressions(List<BooleanExpression> PChasAssetExpressions) {
        this.PChasAssetExpressions = PChasAssetExpressions;
    }

    public List<BooleanExpression> getNPChasAssetExpressions() {
        return NPChasAssetExpressions;
    }

    public void setNPChasAssetExpressions(List<BooleanExpression> NPChasAssetExpressions) {
        this.NPChasAssetExpressions = NPChasAssetExpressions;
    }

    public List<BooleanExpression> getPChasExpressions() {
        return PChasExpressions;
    }

    public void setPChasExpressions(List<BooleanExpression> PChasExpressions) {
        this.PChasExpressions = PChasExpressions;
    }

    public List<BooleanExpression> getNPChasExpressions() {
        return NPChasExpressions;
    }

    public void setNPChasExpressions(List<BooleanExpression> NPChasExpressions) {
        this.NPChasExpressions = NPChasExpressions;
    }

    public List<BooleanExpression> getGlobalVariablesExpressions() {
        return globalVariablesExpressions;
    }

    public void setGlobalVariablesExpressions(List<BooleanExpression> globalVariablesExpressions) {
        this.globalVariablesExpressions = globalVariablesExpressions;
    }

    public List<BooleanExpression> getPChasItemExpressions() {
        return PChasItemExpressions;
    }

    public void setPChasItemExpressions(List<BooleanExpression> PChasItemExpressions) {
        this.PChasItemExpressions = PChasItemExpressions;
    }

    public List<BooleanExpression> getNPChasItemExpressions() {
        return NPChasItemExpressions;
    }

    public void setNPChasItemExpressions(List<BooleanExpression> NPChasItemExpressions) {
        this.NPChasItemExpressions = NPChasItemExpressions;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(PChasAssetExpressions);
        out.writeObject(NPChasAssetExpressions);
        out.writeObject(PChasItemExpressions);
        out.writeObject(NPChasItemExpressions);
        out.writeObject(PChasExpressions);
        out.writeObject(NPChasExpressions);
        out.writeObject(globalVariablesExpressions);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        PChasAssetExpressions = (List<BooleanExpression>) in.readObject();
        NPChasAssetExpressions = (List<BooleanExpression>) in.readObject();
        PChasItemExpressions = (List<BooleanExpression>) in.readObject();
        NPChasItemExpressions = (List<BooleanExpression>) in.readObject();
        PChasExpressions = (List<BooleanExpression>) in.readObject();
        NPChasExpressions = (List<BooleanExpression>) in.readObject();
        globalVariablesExpressions = (List<BooleanExpression>) in.readObject();
    }
}
