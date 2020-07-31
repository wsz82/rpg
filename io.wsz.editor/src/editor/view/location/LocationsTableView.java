package editor.view.location;

import editor.view.stage.EditorCanvas;
import io.wsz.model.Controller;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class LocationsTableView extends TableView<Location> {
    private final EditorCanvas editorCanvas;

    LocationsTableView(EditorCanvas editorCanvas) {
        super();
        this.editorCanvas = editorCanvas;
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
                Controller.get().getCurrentLocation().setName(newValue);
            }
            refresh();
        });
        nameCol.setPrefWidth(100);

        TableColumn<Location, Double> widthCol = new TableColumn<>("Width");
        widthCol.setCellValueFactory(new PropertyValueFactory<>("width"));
        widthCol.setEditable(true);
        widthCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        widthCol.setOnEditCommit(t -> {
            double newWidth = t.getNewValue();
            Location location = getItems().get(t.getTablePosition().getRow());
            location.setWidth(newWidth);
            Location currentLocation = CurrentLocation.get().getLocation();
            if (location.equals(currentLocation)) {
                Controller.get().getCurrentLocation().setWidth(newWidth);
            }
            refresh();
        });

        TableColumn<Location, Double> heightCol = new TableColumn<>("Height");
        heightCol.setCellValueFactory(new PropertyValueFactory<>("height"));
        heightCol.setEditable(true);
        heightCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        heightCol.setOnEditCommit(t -> {
            double newHeight = t.getNewValue();
            Location location = getItems().get(t.getTablePosition().getRow());
            location.setHeight(t.getNewValue());
            Location currentLocation = CurrentLocation.get().getLocation();
            if (location.equals(currentLocation)) {
                Controller.get().getCurrentLocation().setHeight(newHeight);
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

        changeCurrentLocationIfWasRemoved(locations);
    }

    private void changeCurrentLocationIfWasRemoved(List<Location> locations) {
        Location currentLocation = Controller.get().getCurrentLocation().getLocation();
        if (!locations.contains(currentLocation)) {
            Controller.get().getCurrentLocation().setLocation(locations.get(0));
        }
    }

    public void goTo() {
        Location location = getSelectionModel().getSelectedItem();
        Controller controller = Controller.get();
        controller.getCurrentLocation().setLocation(location);
        Coords curPos = controller.getBoard().getCurPos();
        curPos.x = 0;
        curPos.y = 0;
        curPos.level = 0;
        editorCanvas.refresh();
    }
}
