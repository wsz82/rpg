package editor.model;

import io.wsz.model.item.PosItem;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ActiveItem {
    private final ObjectProperty<PosItem> items = new SimpleObjectProperty<>();
    private static ActiveItem singleton;

    public static ActiveItem get() {
        if (singleton == null) {
            singleton = new ActiveItem();
        }
        return singleton;
    }

    private ActiveItem(){}

    public PosItem getItems() {
        return items.get();
    }

    public void setItems(PosItem items) {
        this.items.set(items);
    }
}
