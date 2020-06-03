package io.wsz.model.content;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;

import java.util.Comparator;

abstract public class ContentComparator implements Comparator<Content> {

    @Override
    public int compare(Content c1, Content c2) {
        int o1level = c1.getItem().getLevel();
        int o2level = c2.getItem().getLevel();

        if (o1level < o2level) {
            return -1;
        } else if (o1level == o2level) {
            PosItem i1 = c1.getItem();
            PosItem i2 = c2.getItem();
            ItemType t1 = i1.getType();
            ItemType t2 = i2.getType();
            int o1y_fixed = (int) (i1.getPos().getY() + i1.getImage().getHeight());
            int o2y_fixed = (int) (i2.getPos().getY() + i2.getImage().getHeight());

            if ((t1.equals(ItemType.CREATURE) && t2.equals(ItemType.COVER))
                    || (t1.equals(ItemType.COVER) && t2.equals(ItemType.CREATURE))) {
                return o1y_fixed - o2y_fixed;
            }
            int or1 = t1.ordinal();
            int or2 = t2.ordinal();
            if (or1 < or2) {
                return 1;
            } else if (t1 == t2) {
                if (t1 == ItemType.LANDSCAPE) {
                    return o2y_fixed - o1y_fixed;
                }
                return o1y_fixed - o2y_fixed;
            } else {
                return -1;
            }
        } else {
            return 1;
        }
    }
}