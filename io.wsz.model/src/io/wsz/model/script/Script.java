package io.wsz.model.script;

import io.wsz.model.Controller;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.command.Executable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;
import java.util.Objects;

public class Script implements Externalizable, Executable {
    private static final long serialVersionUID = 1L;

    public static Script parseScript(String s, Controller controller) {
        if (s == null || s.isEmpty()) return null;
        Script script = new Script(null);
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
        executables = new ArrayDeque<>(1);
        validator = new ScriptValidator(controller);
        if (initialText == null) return;
        this.initialText = initialText;
        String whiteSpace = "\\s+";
        String s = initialText.replaceAll(whiteSpace, "");
        parsePreBlock(s, executables, validator, controller);

        if (validator.isInvalid()) {
            validator.buildMessage();
            System.out.println(validator.getMessage());
        }
    }

    @Override
    public boolean tryExecute(Controller controller, PosItem<?, ?> firstAdversary, PosItem<?, ?> secondAdversary) {
        for (Executable executable : executables) {
            executable.tryExecute(controller, firstAdversary, secondAdversary);
        }
        return true;
    }

    public String getValidatorMessage() {
        if (validator != null) {
            return validator.getMessage();
        } else {
            return null;
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
        out.writeObject(executables);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = (String) in.readObject();
        initialText = (String) in.readObject();
        executables = (ArrayDeque<Executable>) in.readObject();
    }
}
