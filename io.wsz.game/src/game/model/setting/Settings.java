package game.model.setting;

import io.wsz.model.sizes.Sizes;

public class Settings {
    private static double gameScrollSpeed = 0.2;
    private static double dialogScrollSpeed = 0.2;
    private static boolean centerOnPC = false;
    private static boolean pauseOnInventory = true;
    private static int resolutionWidth = 1600;
    private static int resolutionHeight = 900;

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

    public static void setResolutionWidth(int resolutionWidth) {
        Sizes.setReloadImages(true);
        Sizes.setMeter(resolutionWidth);
        Settings.resolutionWidth = resolutionWidth;
    }

    public static int getResolutionHeight() {
        return resolutionHeight;
    }

    public static void setResolutionHeight(int resolutionHeight) {
        Settings.resolutionHeight = resolutionHeight;
    }
}
