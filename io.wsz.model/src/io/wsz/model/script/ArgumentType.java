package io.wsz.model.script;

public enum ArgumentType {
    ASSET ("asset"),
    INVENTORY_PLACE ("inventory place"),
    ITEM ("item");

    private final String display;

    ArgumentType(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
