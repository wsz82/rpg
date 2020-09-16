package io.wsz.model.item;

public enum CreatureControl {
    NEUTRAL,
    CONTROLLABLE,
    CONTROL,
    ENEMY;

    public static CreatureControl getDefault() {
        return NEUTRAL;
    }
}
