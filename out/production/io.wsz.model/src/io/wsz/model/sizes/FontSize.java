package io.wsz.model.sizes;

public enum FontSize {
    XS (90),
    S (70),
    M (50),
    L (40),
    XL (30);

    FontSize(int size) {
        this.size = size;
    }

    private final int size;

    public int getSize() {
        return size;
    }
}
