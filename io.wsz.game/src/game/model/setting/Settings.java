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
    private boolean centerOnPC;
    private boolean pauseOnInventory;
    private int resolutionWidth;
    private int resolutionHeight;
    private boolean showBar;
    private String locale;

    public void initDefaultSettings() {
        barPart = 0.08;
        gameScrollSpeed = 0.2;
        dialogScrollSpeed = 0.2;
        centerOnPC = false;
        pauseOnInventory = true;
        resolutionWidth = 1600;
        resolutionHeight = 900;
        showBar = true;
        locale = "English";
        fontSize = FontSize.M;
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
        return centerOnPC;
    }

    public void setCenterOnPC(boolean centerOnPC) {
        this.centerOnPC = centerOnPC;
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

    public void setResolutionWidth(int resolutionWidth, Controller controller) {
        Sizes.setReloadImages(true);
        Sizes.setMeter(resolutionWidth, controller);
        this.resolutionWidth = resolutionWidth;
    }

    public int getResolutionHeight() {
        return resolutionHeight;
    }

    public void setResolutionHeight(int resolutionHeight, Controller controller) {
        Sizes.setReloadImages(true);
        Sizes.setVerticalMeter(resolutionHeight, controller);
        this.resolutionHeight = resolutionHeight;
    }

    public boolean isShowBar() {
        return showBar;
    }

    public void setShowBar(boolean showBar) {
        this.showBar = showBar;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public FontSize getFontSize() {
        return fontSize;
    }

    public void setFontSize(FontSize fontSize) {
        Sizes.setReloadDialogImages(true);
        this.fontSize = fontSize;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(barPart);
        out.writeObject(fontSize);
        out.writeDouble(gameScrollSpeed);
        out.writeDouble(dialogScrollSpeed);
        out.writeBoolean(centerOnPC);
        out.writeBoolean(pauseOnInventory);
        out.writeInt(resolutionWidth);
        out.writeInt(resolutionHeight);
        out.writeBoolean(showBar);
        out.writeObject(locale);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        barPart = in.readDouble();
        fontSize = (FontSize) in.readObject();
        gameScrollSpeed = in.readDouble();
        dialogScrollSpeed = in.readDouble();
        centerOnPC = in.readBoolean();
        pauseOnInventory = in.readBoolean();
        resolutionWidth = in.readInt();
        resolutionHeight = in.readInt();
        showBar = in.readBoolean();
        locale = (String) in.readObject();
    }
}
