package io.wsz.model.script.variable;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

public class Variables implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<VariableString> strings;
    private List<VariableBoolean> booleans;
    private List<VariableInteger> integers;
    private List<VariableDecimal> decimals;

    public Variable<?> getVariableById(String id) {
        Variable<?> global = getGlobal(id, strings);
        if (global == null) global = getGlobal(id, booleans);
        if (global == null) global = getGlobal(id, integers);
        if (global == null) global = getGlobal(id, decimals);
        return global;
    }

    public <A extends Variable<?>> A getGlobal(String id, List<A> globals) {
        return globals.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst().orElse(null);
    }

    public List<VariableString> getStrings() {
        return strings;
    }

    public void setStrings(List<VariableString> strings) {
        this.strings = strings;
    }

    public List<VariableBoolean> getBooleans() {
        return booleans;
    }

    public void setBooleans(List<VariableBoolean> booleans) {
        this.booleans = booleans;
    }

    public List<VariableInteger> getIntegers() {
        return integers;
    }

    public void setIntegers(List<VariableInteger> integers) {
        this.integers = integers;
    }

    public List<VariableDecimal> getDecimals() {
        return decimals;
    }

    public void setDecimals(List<VariableDecimal> decimals) {
        this.decimals = decimals;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(strings);
        out.writeObject(booleans);
        out.writeObject(integers);
        out.writeObject(decimals);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        strings = (List<VariableString>) in.readObject();
        booleans = (List<VariableBoolean>) in.readObject();
        integers = (List<VariableInteger>) in.readObject();
        decimals = (List<VariableDecimal>) in.readObject();
    }
}
