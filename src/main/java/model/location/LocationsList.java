package model.location;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LocationsList {
    private static ObservableList<Location> locations;

    public static ObservableList<Location> get() {
        if (locations == null) {
            locations = FXCollections.observableArrayList();
        }
        return locations;
    }

    private LocationsList() {
    }
}
