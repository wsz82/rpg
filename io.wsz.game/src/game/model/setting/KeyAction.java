package game.model.setting;

public enum KeyAction {
    PAUSE ("pause"),
    INVENTORY ("inventory"),
    HIDE_PORTRAITS ("hide_portraits"),
    LAYER_UP ("layer_up"),
    LAYER_DOWN ("layer_down");

    private final String localeCode;

    KeyAction(String localeCode) {
        this.localeCode = localeCode;
    }

    public String getLocaleCode() {
        return localeCode;
    }
}
