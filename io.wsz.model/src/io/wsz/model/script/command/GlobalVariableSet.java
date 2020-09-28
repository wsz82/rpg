package io.wsz.model.script.command;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.ScriptValidator;
import io.wsz.model.script.variable.Variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import static io.wsz.model.script.ScriptKeyWords.*;

public class GlobalVariableSet implements Executable, Externalizable {
    private static final long serialVersionUID = 1L;

    public static Executable parseCommand(String s, ScriptValidator validator) {
        GlobalVariableSet command = new GlobalVariableSet();
        String globalDot = GLOBAL + DOT;
        s = s.replaceFirst(globalDot, "");
        int setIndex = s.indexOf(SET);

        if (setIndex != -1) {
            String globalVarId = s.substring(0, setIndex);
            command.globalVarId = globalVarId;
            s = s.replaceFirst(globalVarId + SET, "");
            String value = s;
            command.value = value;
            validator.validateGlobalVariable(globalVarId, value);
            s = s.replaceFirst(value, "");
            validator.validateIsEmpty(s);
            return command;
        }
        validator.setSyntaxInvalid(s);
        return null;
    }

    private String globalVarId;
    private String value;

    @Override
    public void execute(Controller controller, PosItem firstAdversary, PosItem secondAdversary) {
        Variable<?> globalVar = controller.getGlobalVariableById(globalVarId);
        if (globalVar == null) return;
        globalVar.setValue(value);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(globalVarId);
        out.writeObject(value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        globalVarId = (String) in.readObject();
        value = (String) in.readObject();
    }
}
