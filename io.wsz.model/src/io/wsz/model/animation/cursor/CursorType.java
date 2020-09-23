package io.wsz.model.animation.cursor;

public enum CursorType {
    MAIN,
    UP,
    RIGHT,
    DOWN,
    LEFT,
    LEFT_UP,
    RIGHT_UP,
    RIGHT_DOWN,
    LEFT_DOWN,
    DOOR_OPEN,
    DOOR_CLOSED,
    CONTAINER_OPEN,
    CONTAINER_CLOSED,
    PICK,
    GO,
    NOT_GO,
    TALK,
    ATTACK;

    private boolean showAmount;
    private int amount;

    public boolean isShowAmount() {
        return showAmount;
    }

    public void setShowAmount(boolean showAmount) {
        this.showAmount = showAmount;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
