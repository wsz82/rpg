package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.sizes.Paths;

public class InDoor extends Door<InDoor> {
    private static final long serialVersionUID = 1L;

    public InDoor() {}

    public InDoor(Controller controller) {
        super(ItemType.INDOOR, controller);
    }

    public InDoor(InDoor prototype, Boolean visible) {
        super(prototype, visible);
        setOpen(prototype.isOpen());
    }

    @Override
    protected String getAssetDirName() {
        return Paths.INDOORS;
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        return creatureSecondaryInteract(cr);
    }
}
