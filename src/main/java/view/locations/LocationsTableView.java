package view.locations;

import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import model.location.CurrentLocation;
import model.location.Location;
import model.location.LocationsList;

import java.util.List;

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
            nameCol.setVisible(false);
            nameCol.setVisible(true);
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
                CurrentLocation.get().setCurrentWidth(newWidth);
            }
            widthCol.setVisible(false);
            widthCol.setVisible(true);
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
                CurrentLocation.get().setCurrentHeight(newHeight);
            }
            heightCol.setVisible(false);
            heightCol.setVisible(true);
        });

        ObservableList<TableColumn<Location, ?>> columns = this.getColumns();
        columns.add(0, nameCol);
        columns.add(1, widthCol);
        columns.add(2, heightCol);
    }

    private boolean isNameUnique(String newValue) {
        return getItems().stream()
                .noneMatch(layer -> layer.getName().equals(newValue));
    }

    void removeLocations() {
        List<Location> locationsToRemove = this.getSelectionModel().getSelectedItems();
        getItems().removeAll(locationsToRemove);
    }

    public void goTo() {
        Location location = getSelectionModel().getSelectedItem();
        CurrentLocation.get().setLocation(location);
    }
}
