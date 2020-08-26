package io.wsz.model.sizes;

import java.io.File;

public class Paths {
    public static final String DIVIDER = "_";
    public static final String ASSETS_DIR = File.separator + "assets";
    public static final String TEXTURES_DIR = File.separator + "textures";
    public static final String IDLE = "idle";
    public static final String IDLE_DIR = File.separator + IDLE;
    public static final String BASIC = "basic";
    public static final String BASIC_DIR = File.separator + BASIC;
    public static final String MAIN = "main";
    public static final String MAIN_DIR = File.separator + MAIN;
    public static final String OPEN = "open";
    public static final String BASIC_OPEN = BASIC + DIVIDER + OPEN;
    public static final String BASIC_OPEN_DIR = File.separator + BASIC_OPEN;
    public static final String INVENTORY = "inventory";
    public static final String INVENTORY_DIR = File.separator + INVENTORY;
    public static final String INVENTORY_OPEN = INVENTORY + DIVIDER + OPEN;
    public static final String PORTRAIT = "portrait";
    public static final String PORTRAIT_DIR = File.separator + PORTRAIT;
    public static final String WALK = "walk";
    public static final String STOP = "stop";
    public static final String N = "N";
    public static final String NE = "NE";
    public static final String E = "E";
    public static final String SE = "SE";
    public static final String S = "S";
    public static final String SW = "SW";
    public static final String W = "W";
    public static final String NW = "NW";
    public static final String[] walks = new String[] {N, NE, E, SE, S, SW, W, NW};
    public static final String FOG_DIR = File.separator + "fog";
    public static final String FOG_BASE_NAME = "fog.png";
    public static final String FOG_BASE_NAME_DIR = File.separator + FOG_BASE_NAME;
    public static final String PNG = ".png";
    public static final String ARROW_RIGHT_PNG = "arrow_right" + PNG;
    public static final String ARROW_LEFT_PNG = "arrow_left" + PNG;
}
