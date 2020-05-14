package model.layer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LayersList {
    private final ObservableList<Layer> layers;

    public LayersList() {
        this.layers = FXCollections.observableArrayList();
    }

    public ObservableList<Layer> get() {
        return layers;
    }
}
