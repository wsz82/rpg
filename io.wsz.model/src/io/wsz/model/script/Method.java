package io.wsz.model.script;

public enum Method {
    PC_HAS ("PC has"),
    NPC_HAS ("NPC has"),
    GLOBAL ("Global");

    private String display;

    Method(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
