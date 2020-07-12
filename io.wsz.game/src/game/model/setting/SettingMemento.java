package game.model.setting;

import io.wsz.model.sizes.FontSize;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class SettingMemento implements Externalizable {
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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeBoolean(isFullScreen);
        out.writeObject(fontSize);
        out.writeDouble(gameScrollSpeed);
        out.writeDouble(dialogScrollSpeed);
        out.writeBoolean(centerOnPc);
        out.writeBoolean(pauseOnInventory);
        out.writeInt(resolutionWidth);
        out.writeInt(resolutionHeight);
        out.writeBoolean(resizeWithResolution);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        isFullScreen = in.readBoolean();
        fontSize = (FontSize) in.readObject();
        gameScrollSpeed = in.readDouble();
        dialogScrollSpeed = in.readDouble();
        centerOnPc = in.readBoolean();
        pauseOnInventory = in.readBoolean();
        resolutionWidth = in.readInt();
        resolutionHeight = in.readInt();
        resizeWithResolution = in.readBoolean();
    }
}
