package io.wsz.model.script.bool.has;

import io.wsz.model.item.Creature;
import io.wsz.model.script.Not;

public class BooleanCreatureHasInventoryPlace extends BooleanHas<Creature>{
    private static final long serialVersionUID = 1L;

    @Override
    protected boolean itemHas() {
        boolean expression = checkedItem.getInventoryPlaces().keySet().stream()
                .anyMatch(i -> i.getId().equals(itemID));
        if (not == Not.NOT) {
            return !expression;
        } else {
            return expression;
        }
    }
}
