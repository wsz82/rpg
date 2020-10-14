package io.wsz.model.script.variable;

import java.util.List;

public abstract class AbstractVariables {

    public Variable<?> getVariableById(String id) {
        Variable<?> global = getGlobal(id, getStrings());
        if (global == null) global = getGlobal(id, getBooleans());
        if (global == null) global = getGlobal(id, getIntegers());
        if (global == null) global = getGlobal(id, getDecimals());
        return global;
    }

    public <A extends Variable<?>> A getGlobal(String id, List<A> globals) {
        return globals.stream()
                .filter(g -> g.getId().equals(id))
                .findFirst().orElse(null);
    }

    public abstract List<VariableString> getStrings();

    public abstract List<VariableBoolean> getBooleans();

    public abstract List<VariableInteger> getIntegers();

    public abstract List<VariableDecimal> getDecimals();
}
