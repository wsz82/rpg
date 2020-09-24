package io.wsz.model.item;

/*content comparator depends on this ordinal*/

public enum ItemType {
    COVER ("Cover"),
    OUTDOOR ("OutDoor"),
    INDOOR ("InDoor"),
    CREATURE ("Creature"),
    CONTAINER ("Container"),
    WEAPON ("Weapon"),
    MISC ("Miscellaneous"),
    TELEPORT ("Teleport"),
    LANDSCAPE ("Landscape");

    public static final ItemType[] EQUIPMENT_MAY_COUNTABLE_TYPES = new ItemType[]
            {WEAPON, MISC};

    private String display;

    ItemType(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }

    public void setDisplay(String display) {
        this.display = display;
    }
}
