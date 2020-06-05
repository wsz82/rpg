package io.wsz.model.item;

public enum CreatureSize {
    XS (25, 11),
    S (25*2, 11*2),
    M (25*3, 11*3),
    L (25*4, 11*4),
    XL (25*5, 11*5);

    private final int width;
    private final int height;

    CreatureSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
