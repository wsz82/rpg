package model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LayersList {
    private static ObservableList<Layer> layers ;

    public static ObservableList<Layer> get() {
        if (layers == null) {
            layers = FXCollections.observableArrayList();
        }
        return layers;
    }

    private LayersList() {
    }
}
