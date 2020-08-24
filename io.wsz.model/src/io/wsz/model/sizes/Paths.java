package io.wsz.model.sizes;

import java.io.File;

public class Paths {
    public static final String ASSETS_DIR = File.separator + "assets";
    public static final String TEXTURES_DIR = File.separator + "textures";
    public static final String IDLE = "idle";
    public static final String IDLE_DIR = File.separator + IDLE;
    public static final String BASIC = "basic";
    public static final String BASIC_DIR = File.separator + BASIC;
    public static final String MAIN = "main";
    public static final String MAIN_DIR = File.separator + MAIN;
    public static final String OPEN = "open";
    public static final String OPEN_DIR = File.separator + OPEN;
    public static final String BASIC_OPEN = BASIC + "_" + OPEN;
    public static final String BASIC_OPEN_DIR = File.separator + BASIC_OPEN;
    public static final String INVENTORY = "inventory";
    public static final String INVENTORY_DIR = File.separator + INVENTORY;
    public static final String INVENTORY_OPEN = INVENTORY + "_" + OPEN;
    public static final String INVENTORY_OPEN_DIR = File.separator + INVENTORY_OPEN;
    public static final String PORTRAIT = "portrait";
    public static final String PORTRAIT_DIR = File.separator + PORTRAIT;
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
    public static final String FOG_DIR = File.separator + "fog";
    public static final String FOG_BASE_NAME = "fog.png";
    public static final String FOG_BASE_NAME_DIR = File.separator + FOG_BASE_NAME;
    public static final String PNG = ".png";
    public static final String ARROW_RIGHT_PNG = "arrow_right" + PNG;
    public static final String ARROW_LEFT_PNG = "arrow_left" + PNG;
}
