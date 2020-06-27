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
    private int resolutionWidth;
    private int resolutionHeight;
    private boolean resizeWithResolution;

    public SettingMemento() {}

    public SettingMemento(boolean isFullScreen, FontSize fontSize, double gameScrollSpeed, double dialogScrollSpeed,
                          boolean centerOnPc, boolean pauseOnInventory, int resolutionWidth, int resolutionHeight,
                          boolean resizeWithResolution) {
        this.isFullScreen = isFullScreen;
        this.fontSize = fontSize;
        this.gameScrollSpeed = gameScrollSpeed;
        this.dialogScrollSpeed = dialogScrollSpeed;
        this.centerOnPc = centerOnPc;
        this.pauseOnInventory = pauseOnInventory;
        this.resolutionWidth = resolutionWidth;
        this.resolutionHeight = resolutionHeight;
        this.resizeWithResolution = resizeWithResolution;
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

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public void setResolutionWidth(int resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public void setResolutionHeight(int resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    public boolean isResizeWithResolution() {
        return resizeWithResolution;
    }

    public void setResizeWithResolution(boolean resizeWithResolution) {
        this.resizeWithResolution = resizeWithResolution;
    }
}
