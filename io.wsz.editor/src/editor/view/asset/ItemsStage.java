package editor.view.asset;

import editor.model.EditorController;
import editor.view.SafeIntegerStringConverter;
import editor.view.stage.ChildStage;
import io.wsz.model.item.Containable;
import io.wsz.model.item.Equipment;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class ItemsStage<A extends Containable> extends ChildStage {
    private final A containable;
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private TableView<TableItem> table;
    private ObservableList<TableItem> tableItems;

    public ItemsStage(Stage parent, A containable) {
        super(parent);
        this.containable = containable;
        initWindow();
    }

    private void initWindow() {
        final StackPane root = new StackPane();
        final VBox container = new VBox(5);
        final Scene scene = new Scene(root);
        setScene(scene);

        table = initTable();
        final HBox buttons = new HBox(5);
        buttons.setAlignment(Pos.CENTER);
        cancel.setAlignment(Pos.CENTER);
        save.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(cancel, save);

        container.getChildren().addAll(table, buttons);
        scene.setRoot(container);

        hookupEvents();
    }

    private void hookupEvents() {
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> close());
        save.setDefaultButton(true);
        save.setOnAction(e -> save());

        table.setOnDragOver(e -> {
            if (e.getGestureSource() != this
                    && e.getDragboard().hasImage()) {
                e.acceptTransferModes(TransferMode.COPY);
            }

            e.consume();
        });

        table.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasImage()) {
                EditorController.get().setItemsStageToAddItems(this);
                success = true;
            }
            e.setDropCompleted(success);

            e.consume();
        });
    }

    private void save() {
        List<Equipment> items = containable.getItems();

        items.clear();
        List<Equipment> input = tableItemsToEquipment(table.getItems());
        items.addAll(input);
        close();
    }

    private List<Equipment> tableItemsToEquipment(ObservableList<TableItem> items) {
        List<Equipment> output = new ArrayList<>(0);
        for (TableItem ti : items) {
            int count = ti.count;
            for (int i = 0; i < count; i++) {
                Equipment e = ti.equipment;
                output.add(e.cloneEquipment());
            }
        }
        return output;
    }

    private TableView<TableItem> initTable() {
        ObservableList<TableItem> items = getTableItems(containable.getItems());

        TableView<TableItem> table = new TableView<>(items);
        table.setEditable(true);

        TableColumn<TableItem, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().equipment.getName();
            }
        });
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setEditable(false);
        table.getColumns().add(nameCol);

        TableColumn<TableItem, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().equipment.getType().toString();
            }
        });
        typeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        typeCol.setEditable(false);
        table.getColumns().add(typeCol);

        TableColumn<TableItem, Integer> countCol = new TableColumn<>("Count");
        countCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return p.getValue().count;
            }
        });
        countCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        countCol.setEditable(true);
        countCol.setOnEditCommit(t -> {
            TableItem ti = t.getTableView().getItems().get(t.getTablePosition().getRow());
            ti.count = t.getNewValue();
            table.refresh();
        });
        table.getColumns().add(countCol);
        return table;
    }

    private ObservableList<TableItem> getTableItems(List<Equipment> items) {
        tableItems = FXCollections.observableArrayList();
        for (Equipment e : items) {
            addEquipment(e);
        }
        return tableItems;
    }

    public void addEquipment(Equipment e) {
        boolean isInList = tableItems.stream()
                .anyMatch(t -> t.equipment.equals(e));
        if (isInList) {
            tableItems.stream()
                    .filter(t -> t.equipment.equals(e))
                    .forEach(t -> t.count += 1);
        } else {
            TableItem ti = new TableItem(e, 1);
            tableItems.add(ti);
        }
        if (table != null) {
            table.refresh();
        }
    }

    private static class TableItem {
        private final Equipment equipment;
        private int count;

        private TableItem(Equipment equipment, int count) {
            this.equipment = equipment;
            this.count = count;
        }
    }
}
