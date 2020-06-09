package editor.view.content;

import editor.view.SafeIntegerStringConverter;
import editor.view.asset.AssetStage;
import editor.view.asset.CreatureAssetStage;
import editor.view.asset.TeleportAssetStage;
import editor.view.stage.EditorCanvas;
import editor.view.stage.Pointer;
import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.item.Asset;
import io.wsz.model.item.ItemType;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;
import java.util.stream.Collectors;

public class ContentTableView extends TableView<Content> {
    private static ContentTableView singleton;

    public static ContentTableView get() {
        if (singleton == null) {
            singleton = new ContentTableView();
        }
        return singleton;
    }

    private ContentTableView() {
        super();
        initTable();
    }

    private void initTable() {
        setItems(CurrentLocation.get().getContent());
        CurrentLocation.get().locationProperty().addListener((observable, oldValue, newValue) -> {
            setItems(newValue.getContents().get());
        });

        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

        TableColumn<Content, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getItem().getName();
            }
        });
        TableColumn<Content, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getItem().getType().toString();
            }
        });
        TableColumn<Content, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return param.getValue().getItem().getLevel();
            }
        });
        levelCol.setEditable(true);
        levelCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        levelCol.setOnEditCommit(t -> {
            int level = t.getNewValue();
            Content c = t.getTableView().getItems().get(t.getTablePosition().getRow());

            List<Integer> levels = Controller.get().getCurrentLocation().getLayers().stream()
                    .map(l -> l.getLevel())
                    .collect(Collectors.toList());
            if (!levels.contains(level)) {
                alertLayerNotExisting(level);
                c.getItem().setLevel(t.getOldValue());
            } else {
                c.getItem().setLevel(level);
                EditorCanvas.get().refresh();
            }
            refresh();
        });

        TableColumn<Content, Boolean> visibilityCol = new TableColumn<>("Visibility");
        visibilityCol.setCellValueFactory(param -> param.getValue().visibleProperty());
        visibilityCol.setEditable(true);
        visibilityCol.setCellFactory(CheckBoxTableCell.forTableColumn(visibilityCol));

        TableColumn<Content, Integer> posCol = new TableColumn<>("Position");
        posCol.setEditable(true);

        TableColumn<Content, Integer> xCol = new TableColumn<>("X");
        xCol.setEditable(true);
        xCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return param.getValue().getItem().getPos().x;
            }
        });
        xCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        xCol.setOnEditCommit(t -> {
            Content c = t.getTableView().getItems().get(t.getTablePosition().getRow());
            c.getItem().getPos().x = t.getNewValue();
            refresh();
            EditorCanvas.get().refresh();
        });

        TableColumn<Content, Integer> yCol = new TableColumn<>("Y");
        yCol.setEditable(true);
        yCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return param.getValue().getItem().getPos().y;
            }
        });
        yCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        yCol.setOnEditCommit(t -> {
            Content c = t.getTableView().getItems().get(t.getTablePosition().getRow());
            c.getItem().getPos().y = t.getNewValue();
            refresh();
            EditorCanvas.get().refresh();
        });
        posCol.getColumns().addAll(xCol, yCol);

        ObservableList<TableColumn<Content, ?>> columns = getColumns();
        columns.add(0, nameCol);
        columns.add(1, typeCol);
        columns.add(2, levelCol);
        columns.add(3, visibilityCol);
        columns.add(4, posCol);
    }

    private void alertLayerNotExisting(int level) {
        Alert alert = new Alert(
                Alert.AlertType.INFORMATION, "Layer " + level + " does not exist!", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }

    void removeContents() {
        ObservableList<Content> contentsToRemove = getSelectionModel().getSelectedItems();
        CurrentLocation.get().getContent().removeAll(contentsToRemove);
    }

    public void changeVisibility() {
        List<Content> contentsToChange = getSelectionModel().getSelectedItems();
        for (Content content : contentsToChange) {
            content.setVisible(!content.isVisible());
        }
    }

    public void moveToPointer() {
        List<Content> contentsToMove = getSelectionModel().getSelectedItems();
        Coords newPos = Pointer.get().getMark();
        for (Content c : contentsToMove) {
            c.getItem().getPos().x = newPos.x;
            int y = 0;
            if (newPos.y != 0) {
                y = newPos.y - (int) c.getItem().getImage().getHeight();
            }
            c.getItem().getPos().y = y;
        }
        refresh();
        EditorCanvas.get().refresh();
    }

    public void editContent(Stage parent) {
        Content c = getSelectionModel().getSelectedItem();
        openEditWindow(parent, c);
    }

    public void openEditWindow(Stage parent, Content c) {
        if (c == null) {
            return;
        }
        Asset item = c.getItem();
        ItemType type = item.getType();
        AssetStage itemStage = switch (type) {
            case CREATURE -> new CreatureAssetStage(parent, item, true);
            case TELEPORT -> new TeleportAssetStage(parent, item, true);
            default -> null;
        };
        if (itemStage == null) {
            return;
        }
        itemStage.show();
    }
}
