package game.model.setting;

import javafx.scene.input.KeyCode;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class Key implements Externalizable {
    private static final long serialVersionUID = 1L;

    private KeyAction action;
    private KeyCode code;

    public Key() {
    }

    public Key(KeyAction action, KeyCode code) {
        this.action = action;
        this.code = code;
    }

    public KeyAction getAction() {
        return action;
    }

    public void setAction(KeyAction action) {
        this.action = action;
    }

    public KeyCode getCode() {
        return code;
    }

    public void setCode(KeyCode code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Key{" +
                "action=" + action +
                ", code=" + code +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return action == key.action &&
                code == key.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(action, code);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(action);
        out.writeObject(code);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        action = (KeyAction) in.readObject();
        code = (KeyCode) in.readObject();
    }
}
