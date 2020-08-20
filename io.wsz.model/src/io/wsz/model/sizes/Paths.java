package io.wsz.model.sizes;

import java.io.File;

public class Paths {
    public static final String WALK_N = "walk_N";
    public static final String WALK_NE = "walk_NE";
    public static final String WALK_E = "walk_E";
    public static final String WALK_SE = "walk_SE";
    public static final String WALK_S = "walk_S";
    public static final String WALK_SW = "walk_SW";
    public static final String WALK_W = "walk_W";
    public static final String WALK_NW = "walk_NW";
    public static final String[] walks = new String[] {WALK_N, WALK_NE, WALK_E, WALK_SE,
            WALK_S, WALK_SW, WALK_W, WALK_NW};
    public static final String IDLE = "idle";
    public static final String MAIN = "main";
    public static final String IDLE_DIR = File.separator + IDLE;
    public static final String PORTRAIT = "portrait";
    public static final String PORTRAIT_DIR = File.separator + PORTRAIT;
    public static final String INVENTORY = "inventory";
    public static final String INVENTORY_EMPTY = "empty";
    public static final String INVENTORY_EMPTY_DIR = File.separator + INVENTORY_EMPTY;
    public static final String ASSETS_DIR = File.separator + "assets";
    public static final String TEXTURES_DIR = File.separator + "textures";
}
