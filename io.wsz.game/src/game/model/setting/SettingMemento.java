package game.model.setting;

import io.wsz.model.sizes.FontSize;

import java.io.Serializable;

public class SettingMemento implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean isFullScreen;
    private FontSize fontSize;
    private double gameScrollSpeed;
    private double dialogScrollSpeed;

    public SettingMemento() {}

    public SettingMemento(boolean isFullScreen, FontSize fontSize, double gameScrollSpeed, double dialogScrollSpeed) {
        this.isFullScreen = isFullScreen;
        this.fontSize = fontSize;
        this.gameScrollSpeed = gameScrollSpeed;
        this.dialogScrollSpeed = dialogScrollSpeed;
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

    public double getGameScrollSpeed() {
        return gameScrollSpeed;
    }

    public void setGameScrollSpeed(double gameScrollSpeed) {
        this.gameScrollSpeed = gameScrollSpeed;
    }

    public double getDialogScrollSpeed() {
        return dialogScrollSpeed;
    }

    public void setDialogScrollSpeed(double dialogScrollSpeed) {
        this.dialogScrollSpeed = dialogScrollSpeed;
    }
}
