package game.model.world;

import model.item.Creature;

public class ActivePC {
    private static ActivePC singleton;
    private Creature creature;

    public static ActivePC get() {
        if (singleton == null) {
            singleton = new ActivePC();
        }
        return singleton;
    }

    private ActivePC(){}

    public Creature getCreature() {
        return creature;
    }

    public void setCreature(Creature creature) {
        this.creature = creature;
    }
}
