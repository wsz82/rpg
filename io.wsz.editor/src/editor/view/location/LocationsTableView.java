package editor.view.location;

import editor.model.EditorController;
import editor.view.stage.EditorCanvas;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class LocationsTableView extends TableView<Location> {
    private final EditorCanvas editorCanvas;
    private final EditorController controller;

    public LocationsTableView(EditorCanvas editorCanvas, EditorController controller) {
        super();
        this.editorCanvas = editorCanvas;
        this.controller = controller;
        initTable();
    }

    private void initTable() {
        setItems(controller.getObservableLocations());

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

        TableColumn<Location, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getId();
            }
        });
        nameCol.setEditable(true);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(t -> {
            Location location = getItems().get(t.getTablePosition().getRow());
            String newValue = t.getNewValue();
            if (isNameUnique(newValue)) {
                location.setId(newValue);
            } else {
                location.setId(t.getOldValue());
            }
            Location currentLocation = controller.getCurrentLocation().getLocation();
            if (location.equals(currentLocation)) {
                controller.getCurrentLocation().setName(newValue);
            }
            refresh();
        });
        nameCol.setPrefWidth(100);

        TableColumn<Location, Double> widthCol = new TableColumn<>("Width");
        widthCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return p.getValue().getWidth();
            }
        });
        widthCol.setEditable(true);
        widthCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        widthCol.setOnEditCommit(t -> {
            double newWidth = t.getNewValue();
            Location location = getItems().get(t.getTablePosition().getRow());
            location.setWidth(newWidth);
            Location currentLocation = controller.getCurrentLocation().getLocation();
            if (location.equals(currentLocation)) {
                controller.getCurrentLocation().setWidth(newWidth);
            }
            refresh();
        });

        TableColumn<Location, Double> heightCol = new TableColumn<>("Height");
        heightCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return p.getValue().getHeight();
            }
        });
        heightCol.setEditable(true);
        heightCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        heightCol.setOnEditCommit(t -> {
            double newHeight = t.getNewValue();
            Location location = getItems().get(t.getTablePosition().getRow());
            location.setHeight(t.getNewValue());
            Location currentLocation = controller.getCurrentLocation().getLocation();
            if (location.equals(currentLocation)) {
                controller.getCurrentLocation().setHeight(newHeight);
            }
            refresh();
        });

        ObservableList<TableColumn<Location, ?>> columns = this.getColumns();
        columns.add(0, nameCol);
        columns.add(1, widthCol);
        columns.add(2, heightCol);
    }

    private boolean isNameUnique(String newValue) {
        return controller.getLocations().stream()
                .noneMatch(layer -> layer.getId().equals(newValue));
    }

    void removeLocations() {
        List<Location> locationsToRemove = this.getSelectionModel().getSelectedItems();
        List<Location> locations = controller.getLocations();
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
        Location currentLocation = controller.getCurrentLocation().getLocation();
        if (!locations.contains(currentLocation)) {
            controller.getCurrentLocation().setLocation(locations.get(0));
        }
    }

    public void goTo() {
        Location location = getSelectionModel().getSelectedItem();
        controller.getCurrentLocation().setLocation(location);
        Coords curPos = controller.getBoard().getCurPos();
        curPos.x = 0;
        curPos.y = 0;
        curPos.level = 0;
        editorCanvas.refresh();
    }
}
