package model.assets;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AssetsList {
    private static ObservableList<Assets> assets;

    public static ObservableList<Assets> get() {
        if (assets == null) {
            assets = FXCollections.observableArrayList();
        }
        return assets;
    }

    private AssetsList() {
    }
}
