package editor.view.location;

import io.wsz.model.Controller;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class LocationsTableView extends TableView<Location> {

    LocationsTableView() {
        super();
        initTable();
    }

    private void initTable() {
        setItems(LocationsList.get());

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

        TableColumn<Location, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setEditable(true);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(t -> {
            Location location = getItems().get(t.getTablePosition().getRow());
            String newValue = t.getNewValue();
            if (isNameUnique(newValue)) {
                location.setName(newValue);
            } else {
                location.setName(t.getOldValue());
            }
            Location currentLocation = CurrentLocation.get().getLocation();
            if (location.equals(currentLocation)) {
                Controller.get().getCurrentLocation().setCurrentName(newValue);
            }
            refresh();
        });

        TableColumn<Location, Integer> widthCol = new TableColumn<>("Width");
        widthCol.setCellValueFactory(new PropertyValueFactory<>("width"));
        widthCol.setEditable(true);
        widthCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        widthCol.setOnEditCommit(t -> {
            int newWidth = t.getNewValue();
            Location location = getItems().get(t.getTablePosition().getRow());
            location.setWidth(newWidth);
            Location currentLocation = CurrentLocation.get().getLocation();
            if (location.equals(currentLocation)) {
                Controller.get().getCurrentLocation().setCurrentWidth(newWidth);
            }
            refresh();
        });

        TableColumn<Location, Integer> heightCol = new TableColumn<>("Height");
        heightCol.setCellValueFactory(new PropertyValueFactory<>("height"));
        heightCol.setEditable(true);
        heightCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        heightCol.setOnEditCommit(t -> {
            int newHeight = t.getNewValue();
            Location location = getItems().get(t.getTablePosition().getRow());
            location.setHeight(t.getNewValue());
            Location currentLocation = CurrentLocation.get().getLocation();
            if (location.equals(currentLocation)) {
                Controller.get().getCurrentLocation().setCurrentHeight(newHeight);
            }
            refresh();
        });

        ObservableList<TableColumn<Location, ?>> columns = this.getColumns();
        columns.add(0, nameCol);
        columns.add(1, widthCol);
        columns.add(2, heightCol);
    }

    private boolean isNameUnique(String newValue) {
        return Controller.get().getLocationsList().stream()
                .noneMatch(layer -> layer.getName().equals(newValue));
    }

    void removeLocations() {
        List<Location> locationsToRemove = this.getSelectionModel().getSelectedItems();
        List<Location> locations = Controller.get().getLocationsList();
        boolean listSizesAreEqual = locations.size() == locationsToRemove.size();
        if (listSizesAreEqual) {
            locationsToRemove = locationsToRemove.stream()
                    .limit(locations.size() - 1)
                    .collect(Collectors.toList());
        }
        locations.removeAll(locationsToRemove);
    }

    public void goTo() {
        Location location = getSelectionModel().getSelectedItem();
        Controller.get().setCurrentLocation(location);
    }
}
