package io.wsz.model.script;

public enum BooleanType {
    TRUE ("is true"),
    FALSE ("is false");

    private String display;

    BooleanType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
