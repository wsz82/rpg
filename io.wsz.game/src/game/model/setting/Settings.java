package game.model.setting;

import io.wsz.model.Controller;
import io.wsz.model.sizes.FontSize;
import io.wsz.model.sizes.Sizes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Settings implements Externalizable {
    private static final long serialVersionUID = 1L;

    private double barPart;
    private FontSize fontSize;
    private double gameScrollSpeed;
    private double dialogScrollSpeed;
    private boolean isCenterOnPC;
    private boolean isPauseOnInventory;
    private int resolutionWidth;
    private int resolutionHeight;
    private boolean isShowBar;
    private String language;
    private boolean isFullScreen;

    public void initDefaultSettings() {
        barPart = 0.08;
        gameScrollSpeed = 0.2;
        dialogScrollSpeed = 0.2;
        isCenterOnPC = false;
        isPauseOnInventory = true;
        resolutionWidth = 1600;
        resolutionHeight = 900;
        isShowBar = true;
        language = "English";
        fontSize = FontSize.M;
        isFullScreen = true;
    }

    public double getBarPart() {
        return barPart;
    }

    public void setBarPart(double barPart) {
        this.barPart = barPart;
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

    public boolean isCenterOnPC() {
        return isCenterOnPC;
    }

    public void setCenterOnPC(boolean centerOnPC) {
        this.isCenterOnPC = centerOnPC;
    }

    public boolean isPauseOnInventory() {
        return isPauseOnInventory;
    }

    public void setPauseOnInventory(boolean pauseOnInventory) {
        this.isPauseOnInventory = pauseOnInventory;
    }

    public int getResolutionWidth() {
        return resolutionWidth;
    }

    public void setResolutionWidth(int resolutionWidth, Controller controller) {
        setMeter(resolutionWidth, controller);
        this.resolutionWidth = resolutionWidth;
    }

    public void setMeter(int resolutionWidth, Controller controller) {
        Sizes.setReloadImages(true);
        Sizes.setMeter(resolutionWidth, controller);
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public void setResolutionHeight(int resolutionHeight, Controller controller) {
        setVerticalMeter(resolutionHeight, controller);
        this.resolutionHeight = resolutionHeight;
    }

    public void setVerticalMeter(int resolutionHeight, Controller controller) {
        Sizes.setReloadImages(true);
        Sizes.setVerticalMeter(resolutionHeight, controller);
    }

    public boolean isShowBar() {
        return isShowBar;
    }

    public void setShowBar(boolean showBar) {
        this.isShowBar = showBar;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public void setFontSize(FontSize fontSize) {
        Sizes.setReloadDialogImages(true);
        this.fontSize = fontSize;
    }

    public boolean isFullScreen() {
        return isFullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        isFullScreen = fullScreen;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(barPart);
        out.writeObject(fontSize);
        out.writeDouble(gameScrollSpeed);
        out.writeDouble(dialogScrollSpeed);
        out.writeBoolean(isCenterOnPC);
        out.writeBoolean(isPauseOnInventory);
        out.writeInt(resolutionWidth);
        out.writeInt(resolutionHeight);
        out.writeBoolean(isShowBar);
        out.writeObject(language);
        out.writeBoolean(isFullScreen);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        barPart = in.readDouble();
        fontSize = (FontSize) in.readObject();
        gameScrollSpeed = in.readDouble();
        dialogScrollSpeed = in.readDouble();
        isCenterOnPC = in.readBoolean();
        isPauseOnInventory = in.readBoolean();
        resolutionWidth = in.readInt();
        resolutionHeight = in.readInt();
        isShowBar = in.readBoolean();
        language = (String) in.readObject();
        isFullScreen = in.readBoolean();
    }
}
