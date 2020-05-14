package model.location;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

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

    public static Location getLocation(String name) {
        List<Location> listOfOneLocation = locations.stream()
                .filter(l -> l.getName().equals(name))
                .collect(Collectors.toList());
        if (!listOfOneLocation.isEmpty()) {
            return listOfOneLocation.get(0);
        } else {
            return null;
        }
    }

    public static boolean isLocationExisting(String name) {
        List<Location> listOfOneLocation = locations.stream()
                .filter(l -> l.getName().equals(name))
                .collect(Collectors.toList());
        return !listOfOneLocation.isEmpty();
    }
}
