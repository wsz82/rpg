package io.wsz.model.script.variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Objects;

public class Variable<V> implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String ID;
    private V value;

    public Variable() {
    }

    public Variable(String ID, V value) {
        this.ID = ID;
        this.value = value;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public void setValue(String newVal) {
        if (value instanceof Boolean) {
            value = (V) getBooleanValue(newVal);
        } else if (value instanceof Integer) {
            value = (V) getIntegerValue(newVal);
        } else if (value instanceof Double) {
            value = (V) getDoubleValue(newVal);
        } else if (value instanceof String) {
            value = (V) newVal;
        }
    }

    private Boolean getBooleanValue(String newValue) {
        return Boolean.parseBoolean(newValue);
    }

    private Double getDoubleValue(String newValue) {
        try {
            return Double.parseDouble(newValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Integer getIntegerValue(String newValue) {
        try {
            return Integer.parseInt(newValue);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return ID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Variable)) return false;
        Variable<?> variable = (Variable<?>) o;
        return Objects.equals(getID(), variable.getID()) &&
                Objects.equals(getValue(), variable.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getID(), getValue());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(ID);

        out.writeObject(value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        ID = (String) in.readObject();

        value = (V) in.readObject();
    }
}
