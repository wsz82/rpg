package io.wsz.model.content;

import io.wsz.model.item.PosItem;
import io.wsz.model.location.CurrentLocation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class ItemList implements LevelValueListener, VisibleValueListener, Externalizable {
    private static final long serialVersionUID = 1L;

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

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        List<PosItem> ser = new ArrayList<>(contents);
        out.writeObject(ser);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        contents.addAll((List<PosItem>) in.readObject());
    }
}
