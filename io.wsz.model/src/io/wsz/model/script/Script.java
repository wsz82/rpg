package io.wsz.model.script;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.command.*;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;
import java.util.Objects;

import static io.wsz.model.script.ScriptKeyWords.*;

public class Script implements Externalizable, Executable {
    private static final long serialVersionUID = 1L;

    public static Script parseScript(String s, Controller controller) {
        if (s == null || s.isEmpty()) return null;
        Script script = new Script();
        script.fillScript(s, controller);
        return script;
    }

    private String id;
    private String initialText;
    private ScriptValidator validator;
    private ArrayDeque<Executable> executables;

    public Script() {
    }

    public Script(String id) {
        this.id = id;
    }

    public void fillScript(String initialText, Controller controller) {
        if (executables != null) {
            executables.clear();
        }
        validator = new ScriptValidator(controller);
        if (initialText == null) return;
        this.initialText = initialText;
        String whiteSpace = "\\s+";
        initialText = initialText.replaceAll(whiteSpace, "");
        String[] commandsToParse = initialText.split(COMMAND_END);

        for (String commandToParse : commandsToParse) {
            Executable executable = null;
            if (commandToParse.startsWith(GLOBAL + DOT)) {
                executable = GlobalVariableSet.parseCommand(commandToParse, validator);
            } else if (commandToParse.startsWith(GIVE_TO_ADVERSARY)) {
                executable = GiveToAdversary.parseCommand(commandToParse, validator);
            } else if (commandToParse.startsWith(ADD_NEW)) {
                executable = AddNew.parseCommand(commandToParse, validator);
            } else if (commandToParse.startsWith(REMOVE)) {
                executable = Remove.parseCommand(commandToParse, validator);
            } else if (commandToParse.startsWith(RUN)) {
                executable = getScriptToRun(controller, commandToParse, validator);
            } else {
                validator.setSyntaxInvalid(commandToParse);
            }

            if (validator.isInvalid()) {
                validator.buildMessage();
                System.out.println(validator.getMessage());
                return;
            }

            if (executable != null) {
                if (executables == null) {
                    executables = new ArrayDeque<>(1);
                }
                executables.addFirst(executable);
            }
        }
    }

    private Executable getScriptToRun(Controller controller, String commandToParse, ScriptValidator validator) {
        String scriptToRunId = commandToParse.replaceFirst(RUN, "");
        Executable scriptToRun = controller.getScriptById(scriptToRunId);
        if (scriptToRun == null) {
            validator.setScriptIdInvalid(scriptToRunId);
        }
        return scriptToRun;
    }

    public void execute(Controller controller, PosItem firstAdversary, PosItem secondAdversary) {
        for (Executable executable : executables) {
            executable.execute(controller, firstAdversary, secondAdversary);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInitialText() {
        return initialText;
    }

    public String getValidatorMessage() {
        if (validator != null) {
            return validator.getMessage();
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Script)) return false;
        Script script = (Script) o;
        return Objects.equals(getId(), script.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(id);
        out.writeObject(initialText);
        out.writeObject(executables); //TODO write only id of script if is in executables?
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = (String) in.readObject();
        initialText = (String) in.readObject();
        executables = (ArrayDeque<Executable>) in.readObject();
    }
}
