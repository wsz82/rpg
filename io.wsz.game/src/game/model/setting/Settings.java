package game.model.setting;

import io.wsz.model.Controller;
import io.wsz.model.sizes.Sizes;

public class Settings {
    private static double barPart = 0.08;
    private static double gameScrollSpeed = 0.2;
    private static double dialogScrollSpeed = 0.2;
    private static boolean centerOnPC = false;
    private static boolean pauseOnInventory = true;
    private static int resolutionWidth = 1600;
    private static int resolutionHeight = 900;
    private static boolean showBar = true;

    public static double getBarPart() {
        return barPart;
    }

    public static void setBarPart(double barPart) {
        Settings.barPart = barPart;
    }

    public static double getGameScrollSpeed() {
        return gameScrollSpeed;
    }

    public static void setGameScrollSpeed(double gameScrollSpeed) {
        Settings.gameScrollSpeed = gameScrollSpeed;
    }

    public static double getDialogScrollSpeed() {
        return dialogScrollSpeed;
    }

    public static void setDialogScrollSpeed(double dialogScrollSpeed) {
        Settings.dialogScrollSpeed = dialogScrollSpeed;
    }

    public static boolean isCenterOnPC() {
        return centerOnPC;
    }

    public static void setCenterOnPC(boolean centerOnPC) {
        Settings.centerOnPC = centerOnPC;
    }

    public static boolean isPauseOnInventory() {
        return pauseOnInventory;
    }

    public static void setPauseOnInventory(boolean pauseOnInventory) {
        Settings.pauseOnInventory = pauseOnInventory;
    }

    public static int getResolutionWidth() {
        return resolutionWidth;
    }

    public static void setResolutionWidth(int resolutionWidth, Controller controller) {
        Sizes.setReloadImages(true);
        Sizes.setMeter(resolutionWidth, controller);
        Settings.resolutionWidth = resolutionWidth;
    }

    public static int getResolutionHeight() {
        return resolutionHeight;
    }

    public static void setResolutionHeight(int resolutionHeight, Controller controller) {
        Sizes.setReloadImages(true);
        Sizes.setVerticalMeter(resolutionHeight, controller);
        Settings.resolutionHeight = resolutionHeight;
    }

    public static boolean isShowBar() {
        return showBar;
    }

    public static void setShowBar(boolean showBar) {
        Settings.showBar = showBar;
    }
}
