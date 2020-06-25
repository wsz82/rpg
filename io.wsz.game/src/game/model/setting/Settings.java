package game.model.setting;

public class Settings {
    private static double gameScrollSpeed = 0.2;
    private static double dialogScrollSpeed = 0.2;
    private static boolean centerOnPC = false;

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
}
