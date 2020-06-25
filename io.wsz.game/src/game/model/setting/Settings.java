package game.model.setting;

public class Settings {
    private static double gameScrollSpeed = 0.2;
    private static double dialogScrollSpeed = 0.2;
    private static boolean centerOnPC = false;
    private static boolean pauseOnInventory = true;

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
}
