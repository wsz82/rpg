package io.wsz.model.layer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class LayersList implements Externalizable {
    private final ObservableList<Layer> layers;

    public LayersList() {
        this.layers = FXCollections.observableArrayList();
    }

    public ObservableList<Layer> get() {
        return layers;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        List<Layer> ser = new ArrayList<>(layers);
        out.writeObject(new ArrayList<>(ser));
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        layers.addAll((List<Layer>) in.readObject());
    }
}
