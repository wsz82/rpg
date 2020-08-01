package io.wsz.model.item;

public class InDoor extends Door<InDoor> {
    private static final long serialVersionUID = 1L;

    public InDoor() {}

    public InDoor(ItemType type) {
        super(type);
    }

    public InDoor(InDoor prototype, Boolean visible) {
        super(prototype, visible);
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        return creatureSecondaryInteract(cr);
    }
}
