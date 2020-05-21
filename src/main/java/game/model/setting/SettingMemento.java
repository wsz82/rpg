package game.model.setting;

import java.io.Serializable;

public class SettingMemento implements Serializable {
    private boolean isFullScreen;

    public SettingMemento() {
    }

    public SettingMemento(boolean isFullScreen) {
        this.isFullScreen = isFullScreen;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }
}
