package io.wsz.model.item;

/*content comparator depends on this ordinal*/

public enum ItemType {
    COVER,
    OUTDOOR,
    INDOOR,
    CREATURE,
    CONTAINER,
    WEAPON,
    MISC,
    TELEPORT,
    LANDSCAPE;

    public static final ItemType[] EQUIPMENT_MAY_COUNTABLE_TYPES = new ItemType[]
            {WEAPON, MISC};
}
