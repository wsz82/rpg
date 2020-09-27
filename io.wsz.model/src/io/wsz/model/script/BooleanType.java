package io.wsz.model.script;

public enum BooleanType {
    TRUE ("true"),
    FALSE ("false");

    private final String display;

    BooleanType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
