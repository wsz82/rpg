package io.wsz.model.sizes;

import io.wsz.model.Controller;
import io.wsz.model.stage.Coords;

import java.io.File;

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
    public static final String ASSETS_DIR = File.separator + "assets";
    public static final String TEXTURES_DIR = File.separator + "textures";

    private static int meter = CONSTANT_METER;
    private static FontSize fontSize = FontSize.M;
    private static int portraitSize;

    private static boolean resizeWithResolution;
    private static boolean reloadImages;
    private static boolean reloadDialogImages;

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

    public static void setMeter(int resWidth, Controller controller) {
        Sizes.meter = CONSTANT_METER * resWidth / ORIGIN_RESOLUTION_WIDTH;
        controller.clearResizablePictures();
    }

    public static FontSize getFontSize() {
        return fontSize;
    }

    public static void setFontSize(FontSize fontSize) {
        setReloadDialogImages(true);
        Sizes.fontSize = fontSize;
    }

    public static boolean isResizeWithResolution() {
        return resizeWithResolution;
    }

    public static void setResizeWithResolution(boolean resizeWithResolution, Controller controller) {
        setReloadImages(true);
        Coords screenPos = controller.getCurPos();
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

    public static boolean isReloadDialogImages() {
        return reloadDialogImages;
    }

    public static void setReloadDialogImages(boolean reloadDialogImages) {
        Sizes.reloadDialogImages = reloadDialogImages;
    }
}
