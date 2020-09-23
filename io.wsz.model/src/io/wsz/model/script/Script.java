package io.wsz.model.script;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.command.Executable;
import io.wsz.model.script.command.GiveToAdversary;
import io.wsz.model.script.command.GlobalVariableSet;
import io.wsz.model.script.command.add.AddNew;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;

import static io.wsz.model.script.ScriptKeyWords.*;

public class Script implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String initialText;
    private ArrayDeque<Executable> executables;

    public static Script parseScript(String s) {
        if (s == null || s.isEmpty()) return null;
        Script script = new Script();
        script.initialText = s;
        s = s.replaceAll("\\s+", "");
        String[] commandsToParse = s.split(";");

        for (String commandToParse : commandsToParse) {
            Executable executable = null;
            if (commandToParse.startsWith(GLOBAL + DOT)) {
                executable = GlobalVariableSet.parseCommand(commandToParse);
            } else if (commandToParse.startsWith(GIVE_TO_ADVERSARY)) {
                executable = GiveToAdversary.parseCommand(commandToParse);
            } else if (commandToParse.startsWith(ADD_NEW)) {
                executable = AddNew.parseCommand(commandToParse);
            }

            if (executable != null) {
                if (script.executables == null) {
                    script.executables = new ArrayDeque<>(1);
                }
                script.executables.addFirst(executable);
            }
        }
        return script;
    }

    public void execute(Controller controller, PosItem firstAdversary, PosItem secondAdversary) {
        for (Executable executable : executables) {
            executable.execute(controller, firstAdversary, secondAdversary);
        }
    }

    public String getInitialText() {
        return initialText;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(initialText);
        out.writeObject(executables);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        initialText = (String) in.readObject();
        executables = (ArrayDeque<Executable>) in.readObject();
    }
}
