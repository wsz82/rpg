package io.wsz.model.item;

public enum CreatureControl {
    NEUTRAL,
    CONTROLLABLE,
    CONTROL,
    ENEMY;

    private static final long serialVersionUID = 1L;

    public static CreatureControl getDefault() {
        return NEUTRAL;
    }
}
