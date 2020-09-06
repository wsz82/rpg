package io.wsz.model.dialog;

import io.wsz.model.item.Creature;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.BooleanCountableExpression;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Requirements implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<BooleanCountableExpression> booleanPChasExpressions;
    private List<BooleanCountableExpression> booleanNPChasExpressions;

    public boolean doMatch(Creature pc, PosItem npc) {
        return doMatchCreatureRequirements(pc, booleanPChasExpressions) && doMatchCreatureRequirements(npc, booleanNPChasExpressions);
    }

    private boolean doMatchCreatureRequirements(PosItem npc, List<BooleanCountableExpression> expressions) {
        return expressions == null || expressions.stream()
                .allMatch(b -> b.isTrue(npc));
    }

    public List<BooleanCountableExpression> getBooleanPChasExpressions() {
        return booleanPChasExpressions;
    }

    public void setBooleanPChasExpressions(List<BooleanCountableExpression> booleanPChasExpressions) {
        this.booleanPChasExpressions = booleanPChasExpressions;
    }

    public List<BooleanCountableExpression> getBooleanNPChasExpressions() {
        return booleanNPChasExpressions;
    }

    public void setBooleanNPChasExpressions(List<BooleanCountableExpression> booleanNPChasExpressions) {
        this.booleanNPChasExpressions = booleanNPChasExpressions;
    }

    public boolean isEmpty() {
        return (booleanPChasExpressions == null || booleanPChasExpressions.isEmpty())
                && (booleanNPChasExpressions == null || booleanNPChasExpressions.isEmpty());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(booleanPChasExpressions);

        out.writeObject(booleanNPChasExpressions);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        booleanPChasExpressions = (List<BooleanCountableExpression>) in.readObject();

        booleanNPChasExpressions = (List<BooleanCountableExpression>) in.readObject();
    }
}
