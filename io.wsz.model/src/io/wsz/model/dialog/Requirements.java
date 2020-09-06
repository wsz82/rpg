package io.wsz.model.dialog;

import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.bool.countable.BooleanCountable;
import io.wsz.model.script.bool.has.BooleanHas;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Requirements implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<BooleanCountable> booleanPChasItemExpressions;
    private List<BooleanCountable> booleanNPChasItemExpressions;
    private List<BooleanHas> booleanPChasExpressions;
    private List<BooleanHas> booleanNPChasExpressions;

    public boolean doMatch(Creature pc, PosItem npc) {
        return doMatchPChasCountableRequirements(pc, booleanPChasItemExpressions)
                && doMatchPChasCountableRequirements(npc, booleanNPChasItemExpressions)
                && doMatchPcHasRequirements(pc, booleanPChasExpressions)
                && doMatchPcHasRequirements(npc, booleanNPChasExpressions);
    }

    private boolean doMatchPcHasRequirements(PosItem item, List<BooleanHas> expressions) {
        return expressions == null || expressions.stream()
                .allMatch(b -> b.isTrue(item));
    }

    private boolean doMatchPChasCountableRequirements(PosItem item, List<BooleanCountable> expressions) {
        return expressions == null || expressions.stream()
                .allMatch(b -> b.isTrue(item));
    }

    public boolean isEmpty() {
        return (booleanPChasItemExpressions == null || booleanPChasItemExpressions.isEmpty())
                && (booleanNPChasItemExpressions == null || booleanNPChasItemExpressions.isEmpty())
                && (booleanPChasExpressions == null || booleanPChasExpressions.isEmpty())
                && (booleanNPChasExpressions == null || booleanNPChasExpressions.isEmpty());
    }

    public List<BooleanCountable> getBooleanPChasItemExpressions() {
        return booleanPChasItemExpressions;
    }

    public void setBooleanPChasItemExpressions(List<BooleanCountable> booleanPChasItemExpressions) {
        this.booleanPChasItemExpressions = booleanPChasItemExpressions;
    }

    public List<BooleanCountable> getBooleanNPChasItemExpressions() {
        return booleanNPChasItemExpressions;
    }

    public void setBooleanNPChasItemExpressions(List<BooleanCountable> booleanNPChasItemExpressions) {
        this.booleanNPChasItemExpressions = booleanNPChasItemExpressions;
    }

    public List<BooleanHas> getBooleanPChasExpressions() {
        return booleanPChasExpressions;
    }

    public void setBooleanPChasExpressions(List<BooleanHas> booleanPChasExpressions) {
        this.booleanPChasExpressions = booleanPChasExpressions;
    }

    public List<BooleanHas> getBooleanNPChasExpressions() {
        return booleanNPChasExpressions;
    }

    public void setBooleanNPChasExpressions(List<BooleanHas> booleanNPChasExpressions) {
        this.booleanNPChasExpressions = booleanNPChasExpressions;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(booleanPChasItemExpressions);
        out.writeObject(booleanNPChasItemExpressions);
        out.writeObject(booleanPChasExpressions);
        out.writeObject(booleanNPChasExpressions);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        booleanPChasItemExpressions = (List<BooleanCountable>) in.readObject();
        booleanNPChasItemExpressions = (List<BooleanCountable>) in.readObject();
        booleanPChasExpressions = (List<BooleanHas>) in.readObject();
        booleanNPChasExpressions = (List<BooleanHas>) in.readObject();
    }
}
