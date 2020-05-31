package io.wsz.model.content;

import java.util.Comparator;

abstract public class ContentComparator implements Comparator<Content> {

    @Override
    public int compare(Content o1, Content o2) {
        int o1level = o1.getItem().getLevel();
        int o2level = o2.getItem().getLevel();
        int o1z = o1.getItem().getPos().getZ();
        int o2z = o2.getItem().getPos().getZ();
        if (o1level < o2level) {
            return -1;
        } else if (o1level == o2level){
            int t1 = o1.getItem().getAsset().getType().ordinal();
            int t2 = o2.getItem().getAsset().getType().ordinal();
            if (t1 < t2) {
                return 1;
            } else if (t1 == t2) {
                return o1z - o2z;
            } else {
                return -1;
            }
        } else {
            return 1;
        }
    }
}