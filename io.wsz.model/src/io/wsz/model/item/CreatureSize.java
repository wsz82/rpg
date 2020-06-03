package io.wsz.model.item;

public enum CreatureSize {
    XS (25, 11),
    S (25*2, 11*2),
    M (25*3, 11*3),
    L (25*4, 11*4),
    XL (25*5, 11*5);

    private int width;
    private int height;

    CreatureSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
