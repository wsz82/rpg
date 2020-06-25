package io.wsz.model.sizes;

public class Sizes {
    public static final int METER = 100;
    public static final int TURN_DURATION_MILLIS = 16;
    public static final double SECOND = 1000.0/TURN_DURATION_MILLIS;

    private static FontSize fontSize = FontSize.M;

    public static FontSize getFontSize() {
        return fontSize;
    }

    public static void setFontSize(FontSize fontSize) {
        Sizes.fontSize = fontSize;
    }
}
