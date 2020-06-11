package io.wsz.model.content;

import io.wsz.model.item.PosItem;
import io.wsz.model.location.CurrentLocation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ItemList implements LevelValueListener, VisibleValueListener {
    private final ObservableList<PosItem> contents;

    public ItemList() {
        this.contents = FXCollections.observableArrayList();
    }

    public ObservableList<PosItem> get() {
        return contents;
    }

    @Override
    public void onLevelValueChanged(int oldValue, int newValue) {
        for (PosItem pi:
                CurrentLocation.get().getItems()) {
            if (pi.getLevel() == oldValue) {
                pi.setLevel(newValue);
            }
        }
    }

    @Override
    public void onVisibleValueChanged(int level, boolean newValue) {
        for (PosItem pi:
                CurrentLocation.get().getItems()) {
            if (pi.getLevel() == level) {
                pi.setVisible(newValue);
            }
        }
    }
}
