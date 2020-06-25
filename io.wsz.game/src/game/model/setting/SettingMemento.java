package game.model.setting;

import io.wsz.model.sizes.FontSize;

import java.io.Serializable;

public class SettingMemento implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean isFullScreen;
    private FontSize fontSize;

    public SettingMemento() {}

    public SettingMemento(boolean isFullScreen, FontSize fontSize) {
        this.isFullScreen = isFullScreen;
        this.fontSize = fontSize;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public void setFontSize(FontSize fontSize) {
        this.fontSize = fontSize;
    }
}
