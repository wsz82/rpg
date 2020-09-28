package io.wsz.model.script;

public enum Method {
    PC_HAS ("PC has"),
    NPC_HAS ("NPC has"),
    GLOBAL ("Global");

    private final String display;

    Method(String display) {
        this.display = display;
    }

    @Override
    public String toString() {
        return display;
    }
}
