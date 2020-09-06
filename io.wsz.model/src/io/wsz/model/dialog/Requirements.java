package io.wsz.model.dialog;

import io.wsz.model.item.Creature;
import io.wsz.model.script.BooleanCreatureItemExpression;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Requirements implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<BooleanCreatureItemExpression> booleanPChasExpressions;

    public List<BooleanCreatureItemExpression> getBooleanPChasExpressions() {
        return booleanPChasExpressions;
    }

    public boolean doMatch(Creature pc) {
        boolean doMatchPChasRequirements = booleanPChasExpressions == null || doMatchPChasRequirements(pc, booleanPChasExpressions);

        return doMatchPChasRequirements;
    }

    private boolean doMatchPChasRequirements(Creature pc, List<BooleanCreatureItemExpression> booleanPChasExpressions) {
        return booleanPChasExpressions.stream()
                .allMatch(b -> b.isTrue(pc));
    }

    public void setBooleanPChasExpressions(List<BooleanCreatureItemExpression> booleanPChasExpressions) {
        this.booleanPChasExpressions = booleanPChasExpressions;
    }

    public boolean isEmpty() {
        return booleanPChasExpressions == null || booleanPChasExpressions.isEmpty();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(booleanPChasExpressions);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        booleanPChasExpressions = (List<BooleanCreatureItemExpression>) in.readObject();
    }
}
