package io.wsz.model.script;

import io.wsz.model.Controller;
import io.wsz.model.script.variable.Variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Script implements Externalizable {
    private static final long serialVersionUID = 1L;

    private static final String GLOBAL = "global";
    private static final String DOT = ".";
    private static final String SET = "=";

    private String initialText;
    private String globalVarID;
    private String value;

    public static Script parseScript(String s) {
        if (s == null || s.isEmpty()) return null;
        Script script = new Script();
        script.initialText = s;
        s = s.replaceAll("\\s+","");
        String globalDot = GLOBAL + DOT;
        if (s.startsWith(globalDot)) {
            s = s.replaceFirst(globalDot, "");
            int setIndex = s.indexOf(SET);

            if (setIndex != -1) {
                String globalVarID = s.substring(0, setIndex);
                script.globalVarID = globalVarID;
                s = s.replace(globalVarID + SET, "");
                String value = s;
                script.value = value;
            }
        }
        return script;
    }

    public void execute(Controller controller) {
        List<Variable<?>> globalVariables = controller.getModel().getActivePlugin().getWorld().getGlobalVariables();
        if (globalVariables == null || globalVariables.isEmpty()) return;
        Variable<?> globalVar = globalVariables.stream()
                .filter(v -> v.getID().equals(globalVarID))
                .findFirst()
                .orElse(null);
        if (globalVar == null) return;
        globalVar.setValue(value);
    }

    public String getInitialText() {
        return initialText;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(initialText);
        out.writeObject(globalVarID);
        out.writeObject(value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        initialText = (String) in.readObject();
        globalVarID = (String) in.readObject();
        value = (String) in.readObject();
    }
}
