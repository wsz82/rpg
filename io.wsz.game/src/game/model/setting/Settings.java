package game.model.setting;

import io.wsz.model.Controller;
import io.wsz.model.sizes.FontSize;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;
import javafx.scene.input.KeyCode;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class Settings implements Externalizable {
    private static final long serialVersionUID = 1L;

    private FontSize fontSize;
    private double gameScrollSpeed;
    private double dialogScrollSpeed;
    private boolean isCenterOnPC;
    private boolean isPauseOnInventory;
    private int horizontalResolution;
    private int verticalResolution;
    private boolean isShowBar;
    private String language;
    private boolean isFullScreen;
    private List<Key> keys;
    private double windowX;
    private double windowY;
    private double windowWidth;
    private double windowHeight;

    public Settings() {}

    public void initDefaultSettings() {
        gameScrollSpeed = 0.2;
        dialogScrollSpeed = 0.2;
        isCenterOnPC = false;
        isPauseOnInventory = true;
        horizontalResolution = 1600;
        verticalResolution = 900;
        isShowBar = true;
        language = Paths.ENGLISH;
        fontSize = FontSize.M;
        isFullScreen = true;
        keys = new ArrayList<>();
        initDefaultKeys(keys);
        windowWidth = 800;
        windowHeight = 600;
    }

    private void initDefaultKeys(List<Key> keys) {
        Key pause = new Key(KeyAction.PAUSE, KeyCode.SPACE);
        keys.add(pause);
        Key inventory = new Key(KeyAction.INVENTORY, KeyCode.I);
        keys.add(inventory);
        Key hidePortraits = new Key(KeyAction.HIDE_PORTRAITS, KeyCode.P);
        keys.add(hidePortraits);
        Key layerUp = new Key(KeyAction.LAYER_UP, KeyCode.PAGE_UP);
        keys.add(layerUp);
        Key layerDown = new Key(KeyAction.LAYER_DOWN, KeyCode.PAGE_DOWN);
        keys.add(layerDown);
    }

    public KeyCode getKey(KeyAction pause) {
        for (Key key : keys) {
            if (pause == key.getAction()) {
                return key.getCode();
            }
        }
        return null;
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

    public int getHorizontalResolution() {
        return horizontalResolution;
    }

    public void setResolutionWidth(int resolutionWidth, Controller controller) {
        setMeter(resolutionWidth, controller);
        this.horizontalResolution = resolutionWidth;
    }

    public void setMeter(int resolutionWidth, Controller controller) {
        Sizes.setReloadImages(true);
        Sizes.setMeter(resolutionWidth, controller);
    }

    public int getVerticalResolution() {
        return verticalResolution;
    }

    public void setResolutionHeight(int resolutionHeight, Controller controller) {
        setVerticalMeter(resolutionHeight, controller);
        this.verticalResolution = resolutionHeight;
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

    public List<Key> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<Key> keys) {
        this.keys = keys;
    }

    public double getWindowX() {
        return windowX;
    }

    public void setWindowX(double windowX) {
        this.windowX = windowX;
    }

    public double getWindowY() {
        return windowY;
    }

    public void setWindowY(double windowY) {
        this.windowY = windowY;
    }

    public double getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(double windowWidth) {
        this.windowWidth = windowWidth;
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(double windowHeight) {
        this.windowHeight = windowHeight;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(fontSize);
        out.writeDouble(gameScrollSpeed);
        out.writeDouble(dialogScrollSpeed);
        out.writeBoolean(isCenterOnPC);
        out.writeBoolean(isPauseOnInventory);
        out.writeInt(horizontalResolution);
        out.writeInt(verticalResolution);
        out.writeBoolean(isShowBar);
        out.writeObject(language);
        out.writeBoolean(isFullScreen);
        out.writeObject(keys);
        out.writeDouble(windowX);
        out.writeDouble(windowY);
        out.writeDouble(windowWidth);
        out.writeDouble(windowHeight);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        fontSize = (FontSize) in.readObject();
        gameScrollSpeed = in.readDouble();
        dialogScrollSpeed = in.readDouble();
        isCenterOnPC = in.readBoolean();
        isPauseOnInventory = in.readBoolean();
        horizontalResolution = in.readInt();
        verticalResolution = in.readInt();
        isShowBar = in.readBoolean();
        language = (String) in.readObject();
        isFullScreen = in.readBoolean();
        keys = (List<Key>) in.readObject();
        windowX = in.readDouble();
        windowY = in.readDouble();
        windowWidth = in.readDouble();
        windowHeight = in.readDouble();
    }
}
