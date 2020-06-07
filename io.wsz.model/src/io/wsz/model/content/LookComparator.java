package io.wsz.model.content;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;

import java.util.Comparator;

import static io.wsz.model.item.ItemType.LANDSCAPE;

abstract public class LookComparator implements Comparator<Content> {

    @Override
    public int compare(Content c1, Content c2) {
        int o1level = c1.getItem().getLevel();
        int o2level = c2.getItem().getLevel();

        if (o1level != o2level) {
            return o1level - o2level;
        } else {
            PosItem i1 = c1.getItem();
            PosItem i2 = c2.getItem();
            ItemType t1 = i1.getType();
            ItemType t2 = i2.getType();
            Coords pos1 = i1.getPos();
            Coords pos2 = i2.getPos();

            if (t1 == t2) {
                int o1y_bottom = pos1.getY() + (int) i1.getImage().getHeight();
                int o2y_bottom = pos2.getY() + (int) i2.getImage().getHeight();

                if (t1.equals(LANDSCAPE)) {
                    return o2y_bottom - o1y_bottom;
                }

                else {
                    return o1y_bottom - o2y_bottom;
                }
            }
            int or1 = t1.ordinal();
            int or2 = t2.ordinal();
            return or2 - or1;
        }
    }
}