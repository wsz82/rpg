package editor.model;

import io.wsz.model.item.PosItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ActiveItem {
    private final ObjectProperty<PosItem> item = new SimpleObjectProperty<>();
    private static ActiveItem singleton;

    public static ActiveItem get() {
        if (singleton == null) {
            singleton = new ActiveItem();
        }
        return singleton;
    }

    private ActiveItem(){}

    public PosItem getItem() {
        return item.get();
    }

    public void setItem(PosItem items) {
        this.item.set(items);
    }
}
