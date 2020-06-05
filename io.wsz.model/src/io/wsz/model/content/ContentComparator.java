package io.wsz.model.content;

import io.wsz.model.item.Cover;
import io.wsz.model.item.Creature;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;

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
            Coords pos1 = i1.getPos();
            Coords pos2 = i2.getPos();
            int o1y_bottom = (int) (pos1.getY() + i1.getImage().getHeight());
            int o2y_bottom = (int) (pos2.getY() + i2.getImage().getHeight());
            int o1y_top = (int) pos1.getY();
            int o2y_top = (int) pos2.getY();

            if (t1.equals(ItemType.CREATURE) && t2.equals(ItemType.COVER)) {
                Creature cr = (Creature) i1;
                Cover cover = (Cover) i2;
                if (o1y_bottom < o2y_top) {
                    return -1;
                } else if (o1y_bottom <= o2y_bottom) {
                    boolean isCovered = Board.get().isCovered(cr, cover);
                    if (isCovered) {
                        return -1;
                    } else {
                        return 1;
                    }
                } else {
                    return 1;
                }
            } else if (t1.equals(ItemType.COVER) && t2.equals(ItemType.CREATURE)) {
                Cover cover = (Cover) i1;
                Creature cr = (Creature) i2;
                if (o2y_bottom < o1y_top) {
                    return 1;
                } else if (o2y_bottom <= o1y_bottom) {
                    boolean isCovered = Board.get().isCovered(cr, cover);
                    if (isCovered) {
                        return 1;
                    } else {
                        return -1;
                    }
                } else {
                    return -1;
                }
            }
            int or1 = t1.ordinal();
            int or2 = t2.ordinal();
            if (or1 < or2) {
                return 1;
            } else if (t1 == t2) {
                if (t1 == ItemType.LANDSCAPE) {
                    return o2y_bottom - o1y_bottom;
                }
                return o1y_bottom - o2y_bottom;
            } else {
                return -1;
            }
        } else {
            return 1;
        }
    }
}