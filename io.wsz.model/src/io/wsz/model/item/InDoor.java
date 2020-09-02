package io.wsz.model.item;

import io.wsz.model.Controller;

public class InDoor extends Door<InDoor> {
    private static final long serialVersionUID = 1L;

    public InDoor() {}

    public InDoor(Controller controller) {
        super(ItemType.INDOOR, controller);
    }

    public InDoor(InDoor prototype, Boolean visible) {
        super(prototype, visible);
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        return creatureSecondaryInteract(cr);
    }
}
