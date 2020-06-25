package game.model.setting;

import io.wsz.model.sizes.FontSize;

import java.io.Serializable;

public class SettingMemento implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean isFullScreen;
    private FontSize fontSize;
    private double gameScrollSpeed;
    private double dialogScrollSpeed;
    private boolean centerOnPc;
    private boolean pauseOnInventory;

    public SettingMemento() {}

    public SettingMemento(boolean isFullScreen, FontSize fontSize, double gameScrollSpeed, double dialogScrollSpeed,
                          boolean centerOnPc, boolean pauseOnInventory) {
        this.isFullScreen = isFullScreen;
        this.fontSize = fontSize;
        this.gameScrollSpeed = gameScrollSpeed;
        this.dialogScrollSpeed = dialogScrollSpeed;
        this.centerOnPc = centerOnPc;
        this.pauseOnInventory = pauseOnInventory;
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

    public boolean isCenterOnPc() {
        return centerOnPc;
    }

    public void setCenterOnPc(boolean centerOnPc) {
        this.centerOnPc = centerOnPc;
    }

    public boolean isPauseOnInventory() {
        return pauseOnInventory;
    }

    public void setPauseOnInventory(boolean pauseOnInventory) {
        this.pauseOnInventory = pauseOnInventory;
    }
}
