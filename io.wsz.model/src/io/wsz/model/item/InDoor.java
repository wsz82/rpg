package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.item.list.AbstractItemsList;
import io.wsz.model.item.list.ItemsList;
import io.wsz.model.sizes.Paths;

import java.util.List;

public class InDoor extends Door<InDoor> {
    private static final long serialVersionUID = 1L;

    public InDoor() {}

    public InDoor(Controller controller) {
        super(ItemType.INDOOR, controller);
    }

    public InDoor(InDoor prototype) {
        super(prototype);
        setOpen(prototype.isOpen());
    }

    @Override
    public void addItemToList(AbstractItemsList list) {
        list.getInDoors().add(this);
    }

    @Override
    public void removeItemFromList(AbstractItemsList list) {
        list.getInDoors().remove(this);
    }

    @Override
    protected String getAssetDirName() {
        return Paths.INDOORS;
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        return creatureSecondaryInteract(cr);
    }

    @Override
    protected List<InDoor> getSpecificItemsList(ItemsList itemsList) {
        return itemsList.getInDoors();
    }

    @Override
    protected InDoor getNewItemFromPrototype() {
        return new InDoor(this);
    }
}
