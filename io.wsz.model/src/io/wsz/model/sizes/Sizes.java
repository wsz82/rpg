package io.wsz.model.sizes;

import io.wsz.model.Controller;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;

public class Sizes {
    public static final long VERSION = 1L;
    public static final int MIN_RESOLUTION_WIDTH = 100;
    public static final int MIN_RESOLUTION_HEIGHT = 100;
    public static final int ORIGIN_RESOLUTION_WIDTH = 1600;
    public static final int CONSTANT_METER = 100;
    public static final int FPS = 60;
    public static final int TURN_DURATION_MILLIS = 1000/FPS;
    public static final double SECOND = 1000.0/TURN_DURATION_MILLIS;
    public static final double COVER_OPACITY = 0.4;

    private static int meter = CONSTANT_METER;
    private static double fogSize = 0.20;
    private static FontSize fontSize = FontSize.M;
    private static int portraitSize;

    private static boolean resizeWithResolution;
    private static boolean reloadImages;

    public static int getMeter() {
        if (resizeWithResolution) {
            return meter;
        } else {
            return CONSTANT_METER;
        }
    }

    public static int getTrueMeter() {
        return meter;
    }

    public static void setMeter(int resWidth) {
        Sizes.meter = CONSTANT_METER * resWidth / ORIGIN_RESOLUTION_WIDTH;
        Controller.get().clearHeroesPortraits();
    }

    public static double getFogSize() {
        return fogSize;
    }

    public static void setFogSize(double fogSize) {
        Sizes.fogSize = fogSize;
    }

    public static FontSize getFontSize() {
        return fontSize;
    }

    public static void setFontSize(FontSize fontSize) {
        Sizes.fontSize = fontSize;
    }

    public static boolean isResizeWithResolution() {
        return resizeWithResolution;
    }

    public static void setResizeWithResolution(boolean resizeWithResolution) {
        setReloadImages(true);
        Coords screenPos = Board.get().getBoardPos();
        screenPos.x = 0;
        screenPos.y = 0;
        Sizes.resizeWithResolution = resizeWithResolution;
    }

    public static boolean isReloadImages() {
        return reloadImages;
    }

    public static void setReloadImages(boolean reloadImages) {
        Sizes.reloadImages = reloadImages;
    }

    public static int getPortraitSize() {
        return portraitSize;
    }

    public static void setPortraitSize(int portraitSize) {
        Sizes.portraitSize = portraitSize;
    }
}
