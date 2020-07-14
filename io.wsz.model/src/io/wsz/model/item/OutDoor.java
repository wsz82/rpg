package io.wsz.model.item;

public class OutDoor extends InDoor {

    public OutDoor() {}

    public OutDoor(InDoor prototype, String name, ItemType type, String path, Boolean visible) {
        super(prototype, name, type, path, visible);
    }
}
