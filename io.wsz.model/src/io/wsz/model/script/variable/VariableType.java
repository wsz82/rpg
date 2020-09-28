package io.wsz.model.script.variable;

public enum VariableType {
    STRING ("String"),
    BOOLEAN ("Boolean"),
    INTEGER ("Integer"),
    DECIMAL ("Decimal");

    private final String display;

    VariableType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
