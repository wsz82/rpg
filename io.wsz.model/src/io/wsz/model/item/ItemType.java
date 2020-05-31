package io.wsz.model.item;

import java.io.Serializable;

/*content comparator depends on ordinal*/

public enum ItemType implements Serializable {
    COVER,
    CREATURE,
    LANDSCAPE,
    FLY_ZONE,
    MOVE_ZONE
}
