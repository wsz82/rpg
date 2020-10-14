package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;

public class IfExecutable implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    private final ArrayDeque<Executable> executables = new ArrayDeque<>();
    private IfCondition condition;

    @Override
    public boolean tryExecute(Controller controller, PosItem<?, ?> firstAdversary, PosItem<?, ?> secondAdversary) {
        if (condition.isTrue(controller)) {
            for (Executable executable : executables) {
                executable.tryExecute(controller, firstAdversary, secondAdversary);
            }
            return true;
        }
        return false;
    }

    public ArrayDeque<Executable> getExecutables() {
        return executables;
    }

    public void setCondition(IfCondition condition) {
        this.condition = condition;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(executables);
        out.writeObject(condition);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        ArrayDeque<Executable> executables = (ArrayDeque<Executable>) in.readObject();
        this.executables.addAll(executables);
        condition = (IfCondition) in.readObject();
    }
}
