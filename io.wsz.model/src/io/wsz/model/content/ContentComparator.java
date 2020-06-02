package io.wsz.model.content;

import io.wsz.model.item.ItemType;

import java.util.Comparator;

abstract public class ContentComparator implements Comparator<Content> {

    @Override
    public int compare(Content o1, Content o2) {
        int o1level = o1.getItem().getLevel();
        int o2level = o2.getItem().getLevel();
        int o1y_fixed = (int) (o1.getItem().getPos().getY() + o1.getItem().getImage().getHeight());
        int o2y_fixed = (int) (o2.getItem().getPos().getY() + o2.getItem().getImage().getHeight());
        if (o1level < o2level) {
            return -1;
        } else if (o1level == o2level) {
            ItemType t1 = o1.getItem().getType();
            if (t1 == ItemType.LANDSCAPE) {
                return -1;
            } else {
                return o1y_fixed - o2y_fixed;
            }
        } else {
            return 1;
        }
    }
}