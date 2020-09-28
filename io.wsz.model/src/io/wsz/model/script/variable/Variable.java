package io.wsz.model.script.variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public abstract class Variable<V> implements Externalizable {
    private static final long serialVersionUID = 1L;

    protected String id;
    protected V value;

    public Variable() {
    }

    public Variable(V value) {
        this.value = value;
    }

    public Variable(String id, V value) {
        this.id = id;
        this.value = value;
    }

    public abstract VariableType getType();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public abstract void setValue(String newVal);

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;
        Variable<?> variable = (Variable<?>) o;
        return Objects.equals(getId(), variable.getId()) &&
                Objects.equals(getValue(), variable.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getValue());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(id);

        out.writeObject(value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        id = (String) in.readObject();

        value = (V) in.readObject();
    }
}
