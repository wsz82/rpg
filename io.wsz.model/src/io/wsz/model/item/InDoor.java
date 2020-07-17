package io.wsz.model.item;

public class InDoor extends Door<InDoor> {
    private static final long serialVersionUID = 1L;

    public InDoor() {}

    public InDoor(ItemType type) {
        super(type);
    }

    public InDoor(InDoor prototype, String name, ItemType type, String path, Boolean visible) {
        super(prototype, name, type, path, visible);
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        return creatureSecondaryInteract(cr);
    }
}
