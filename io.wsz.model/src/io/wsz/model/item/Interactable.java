package io.wsz.model.item;

public interface Interactable {

    boolean creaturePrimaryInteract(Creature cr);

    boolean creatureSecondaryInteract(Creature cr);
}
