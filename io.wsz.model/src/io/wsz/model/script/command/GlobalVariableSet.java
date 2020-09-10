package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.variable.Variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import static io.wsz.model.script.ScriptKeyWords.*;

public class GlobalVariableSet implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static Executable parseCommand(String s) {
        GlobalVariableSet command = new GlobalVariableSet();
        String globalDot = GLOBAL + DOT;
        s = s.replaceFirst(globalDot, "");
        int setIndex = s.indexOf(SET);

        if (setIndex != -1) {
            String globalVarID = s.substring(0, setIndex);
            command.globalVarID = globalVarID;
            s = s.replace(globalVarID + SET, "");
            String value = s;
            command.value = value;
            return command;
        }
        return null;
    }

    private String globalVarID;
    private String value;

    public GlobalVariableSet() {
    }

    @Override
    public void execute(Controller controller, PosItem firstAdversary, PosItem secondAdversary) {
        List<Variable<?>> globalVariables = controller.getModel().getActivePlugin().getWorld().getGlobalVariables();
        if (globalVariables == null || globalVariables.isEmpty()) return;
        Variable<?> globalVar = globalVariables.stream()
                .filter(v -> v.getID().equals(globalVarID))
                .findFirst()
                .orElse(null);
        if (globalVar == null) return;
        globalVar.setValue(value);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(globalVarID);
        out.writeObject(value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        globalVarID = (String) in.readObject();
        value = (String) in.readObject();
    }
}
