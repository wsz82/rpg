package model.asset;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class AssetsList {
    private static ObservableList<Asset> assets;

    public static ObservableList<Asset> get() {
        if (assets == null) {
            assets = FXCollections.observableArrayList();
        }
        return assets;
    }

    private AssetsList() {
    }
}
