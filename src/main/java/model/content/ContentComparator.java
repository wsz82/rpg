package model.content;

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
            return o1z - o2z;
        } else {
            return 1;
        }
    }
}