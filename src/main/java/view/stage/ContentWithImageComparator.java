package view.stage;

import java.util.Comparator;

public class ContentWithImageComparator implements Comparator<ContentWithImage> {

    @Override
    public int compare(ContentWithImage o1, ContentWithImage o2) {
        int o1level = o1.getContent().getLevel();
        int o2level = o2.getContent().getLevel();
        int o1z = o1.getContent().getPos().getZ();
        int o2z = o2.getContent().getPos().getZ();
        if (o1level < o2level) {
            return -1;
        } else if (o1level == o2level){
            return o1z - o2z;
        } else {
            return 1;
        }
    }
}
