package editor.view.content;

import editor.model.EditorController;
import editor.view.SafeIntegerStringConverter;
import editor.view.location.CurrentObservableLocation;
import editor.view.stage.EditorCanvas;
import editor.view.stage.Pointer;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class ContentTableView extends TableView<PosItem> {
    private final ContentEditDelegate contentEditDelegate = new ContentEditDelegate();
    private final EditorCanvas editorCanvas;
    private final EditorController controller;

    private Pointer pointer;

    public ContentTableView(EditorCanvas editorCanvas, EditorController controller) {
        this.editorCanvas = editorCanvas;
        this.controller = controller;
        hookUpOnClickCanvasRefreshEvent();
    }

    private void hookUpOnClickCanvasRefreshEvent() {
        addEventFilter(MouseEvent.MOUSE_CLICKED, e -> {
            editorCanvas.refresh();
        });
    }

    public void initTable() {
        CurrentObservableLocation currentObservableLocation = controller.getCurrentObservableLocation();
        ObservableList<PosItem> items = currentObservableLocation.getItems();
        setItems(items);

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

        TableColumn<PosItem, String> assetIdCol = new TableColumn<>("Asset ID");
        assetIdCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getAssetId();
            }
        });
        TableColumn<PosItem, String> itemIdCol = new TableColumn<>("Item ID");
        itemIdCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getItemId();
            }
        });
        TableColumn<PosItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getIndividualName();
            }
        });
        nameCol.setEditable(false);
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());

        TableColumn<PosItem, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getType().toString();
            }
        });
        TableColumn<PosItem, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return param.getValue().getPos().level;
            }
        });
        levelCol.setEditable(true);
        levelCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        levelCol.setOnEditCommit(t -> {
            int level = t.getNewValue();
            PosItem pi = getSelectionModel().getSelectedItem();

            List<Integer> levels = currentObservableLocation.getLayers().stream()
                    .map(Layer::getLevel)
                    .collect(Collectors.toList());
            if (!levels.contains(level)) {
                alertLayerNotExisting(level);
                pi.getPos().level = t.getOldValue();
            } else {
                pi.getPos().level = level;
                editorCanvas.refresh();
            }
            refresh();
        });

        TableColumn<PosItem, CheckBox> visibilityCol = new TableColumn<>("Visibility");
        visibilityCol.setCellValueFactory(f -> {
            PosItem pi = f.getValue();
            CheckBox checkBox = new CheckBox();
            checkBox.selectedProperty().setValue(pi.isVisible());
            checkBox.selectedProperty().addListener((ov, oldVal, newVal) -> pi.setVisible(newVal));
            return new SimpleObjectProperty<>(checkBox);
        });
        visibilityCol.setEditable(true);

        TableColumn<PosItem, Boolean> blockedCol = new TableColumn<>("Block");
        blockedCol.setCellValueFactory(new PropertyValueFactory<>("isBlocked"));
        blockedCol.setCellFactory(CheckBoxTableCell.forTableColumn(blockedCol));
        blockedCol.setEditable(true);

        TableColumn<PosItem, Double> posCol = new TableColumn<>("Position");
        posCol.setEditable(true);

        TableColumn<PosItem, Double> xCol = new TableColumn<>("X");
        xCol.setEditable(true);
        xCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return param.getValue().getPos().x;
            }
        });
        xCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        xCol.setOnEditCommit(t -> {
            PosItem pi = getSelectionModel().getSelectedItem();
            pi.getPos().x = t.getNewValue();
            refresh();
            editorCanvas.refresh();
        });

        TableColumn<PosItem, Double> yCol = new TableColumn<>("Y");
        yCol.setEditable(true);
        yCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Double computeValue() {
                return param.getValue().getPos().y;
            }
        });
        yCol.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        yCol.setOnEditCommit(t -> {
            PosItem pi = getSelectionModel().getSelectedItem();
            pi.getPos().y = t.getNewValue();
            refresh();
            editorCanvas.refresh();
        });
        posCol.getColumns().addAll(xCol, yCol);

        ObservableList<TableColumn<PosItem, ?>> columns = getColumns();
        columns.addAll(assetIdCol, itemIdCol, nameCol, typeCol, levelCol, visibilityCol, blockedCol, posCol);
    }

    private void alertLayerNotExisting(int level) {
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION, "Layer " + level + " does not exist!", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }

    void removeContents() {
        ObservableList<PosItem> itemsToRemove = getSelectionModel().getSelectedItems();
        controller.getCurrentObservableLocation().getItems().removeAll(itemsToRemove);
    }

    public void changeVisibility() {
        List<PosItem> itemsToChange = getSelectionModel().getSelectedItems();
        for (PosItem pi : itemsToChange) {
            pi.setVisible(!pi.isVisible());
        }
    }

    public void moveToPointer() {
        List<PosItem> itemsToMove = getSelectionModel().getSelectedItems();
        Coords newPos = pointer.getMark();
        Location location = controller.getCurrentObservableLocation().getLocation();
        for (PosItem pi : itemsToMove) {
            Coords pos = pi.getPos();
            pos.x = newPos.x;
            double y = 0;
            if (newPos.y != 0) {
                y = newPos.y - pi.getImage().getHeight() / Sizes.getMeter();
            }
            pos.y = y;
            pos.setLocation(location);
        }
        refresh();
        editorCanvas.refresh();
    }

    public void editItem(Stage parent) {
        PosItem pi = getSelectionModel().getSelectedItem();
        openEditWindow(parent, pi);
    }

    public void openEditWindow(Stage parent, PosItem pi) {
        contentEditDelegate.openEditWindow(parent, pi, editorCanvas, controller);
    }

    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }
}
