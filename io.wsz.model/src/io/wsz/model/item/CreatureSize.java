package io.wsz.model.item;

public enum CreatureSize {
    XS (0.3, 0.2),
    S (0.3*2, 0.2*2),
    M (0.3*3, 0.2*3),
    L (0.3*4, 0.2*4),
    XL (0.3*5, 0.2*5);

    private static final long serialVersionUID = 1L;

    private final double width;
    private final double height;

    CreatureSize(double width, double height) {
        this.width = width;
        this.height = height;
    }

    public static CreatureSize getDefault() {
        return M;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }
}
