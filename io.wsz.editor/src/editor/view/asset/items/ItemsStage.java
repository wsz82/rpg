package editor.view.asset.items;

import editor.model.EditorController;
import editor.view.content.ContentEditDelegate;
import editor.view.stage.ChildStage;
import editor.view.stage.EditorCanvas;
import editor.view.utilities.SafeIntegerStringConverter;
import io.wsz.model.item.Containable;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.EquipmentMayCountable;
import io.wsz.model.item.list.EquipmentList;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ItemsStage<C extends Containable, I extends TableItem> extends ChildStage {
    protected final C containable;

    protected TableView<I> table;
    protected ObservableList<I> tableItems;

    private final ContentEditDelegate contentEditDelegate = new ContentEditDelegate();
    private final Button save = new Button("Save");
    private final Button cancel = new Button("Cancel");
    private final EditorCanvas editorCanvas;
    private final EditorController editorController;

    public ItemsStage(Stage parent, C containable, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent);
        this.containable = containable;
        this.editorCanvas = editorCanvas;
        this.editorController = editorController;
    }

    public void initWindow() {
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

        setUpContextMenu();
        hookupEvents();
    }

    private void hookupEvents() {
        cancel.setCancelButton(true);
        cancel.setOnAction(e -> close());
        save.setDefaultButton(true);
        save.setOnAction(e -> {
            save();
            close();
        });

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
                editorController.setItemsStageToAddItems(this);
                success = true;
            }
            e.setDropCompleted(success);

            e.consume();
        });

        table.setOnKeyPressed(e -> {
            KeyCode code = e.getCode();
            if (code.equals(KeyCode.DELETE)) {
                e.consume();
                removeItem();
            }
        });
    }

    private TableView<I> initTable() {
        ObservableList<I> items = getTableItems(containable.getEquipmentList().getMergedList());

        TableView<I> table = new TableView<>(items);
        table.setEditable(true);

        TableColumn<I, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                Equipment<?,?> equipment = p.getValue().getEquipment();
                if (equipment == null) {
                    return "";
                } else {
                    return equipment.getAssetId();
                }
            }
        });
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setEditable(false);
        table.getColumns().add(nameCol);

        TableColumn<I, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                Equipment<?,?> equipment = p.getValue().getEquipment();
                if (equipment == null) {
                    return "";
                } else {
                    return equipment.getType().toString();
                }
            }
        });
        typeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        typeCol.setEditable(false);
        table.getColumns().add(typeCol);

        TableColumn<I, Integer> countCol = new TableColumn<>("Count");
        countCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return p.getValue().getCount();
            }
        });
        countCol.setCellFactory(TextFieldTableCell.forTableColumn(new SafeIntegerStringConverter()));
        countCol.setEditable(true);
        countCol.setOnEditCommit(t -> {
            TableItem ti = table.getSelectionModel().getSelectedItem();
            Integer newValue = t.getNewValue();
            ti.setCount(newValue);
            table.refresh();
        });
        table.getColumns().add(countCol);
        return table;
    }

    private void setUpContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem edit = new MenuItem("Edit");
        final MenuItem remove = new MenuItem("Remove");
        contextMenu.getItems().addAll(edit);
        edit.setOnAction(event -> editItem());
        remove.setOnAction(event -> removeItem());
        table.setOnContextMenuRequested(e -> contextMenu.show(this, e.getScreenX(), e.getScreenY()));
    }

    private void editItem() {
        TableItem selTableItem = table.getSelectionModel().getSelectedItem();
        if (selTableItem == null) return;
        Equipment<?,?> selEquipment = selTableItem.getEquipment();
        if (selEquipment == null) return;
        contentEditDelegate.openEditWindow(this, selEquipment, editorCanvas, editorController);
    }

    private void removeItem() {
        TableItem selTableItem = table.getSelectionModel().getSelectedItem();
        if (selTableItem == null) return;
        Equipment<?,?> selEquipment = selTableItem.getEquipment();
        if (selEquipment == null) return;
        table.getItems().remove(selTableItem);
    }

    protected void save() {
        EquipmentList equipmentList = containable.getEquipmentList();
        equipmentList.clear();
        List<Equipment<?,?>> output = tableItemsToEquipment(table.getItems());
        equipmentList.addAll(output);
    }

    protected List<Equipment<?,?>> tableItemsToEquipment(ObservableList<I> items) {
        List<Equipment<?,?>> output = new ArrayList<>(0);
        for (TableItem ti : items) {
            Equipment<?,?> tiEquipment = ti.getEquipment();
            int count = ti.getCount();
            addCountableOrSingle(output, tiEquipment, count);
        }
        return output;
    }

    protected void addCountableOrSingle(List<Equipment<?,?>> output, Equipment<?,?> toClone, int count) {
        if (toClone.isCountable()) {
            toClone.setAmount(count);
            output.add(toClone);
        } else {
            for (int i = 0; i < count; i++) {
                Equipment<?,?> cloned = toClone.cloneEquipment(false);
                output.add(cloned);
            }
        }
    }

    protected ObservableList<I> getTableItems(List<Equipment<?,?>> items) {
        tableItems = FXCollections.observableArrayList();
        for (Equipment<?,?> e : items) {
            addEquipment(e);
        }
        return tableItems;
    }

    public void addEquipment(Equipment<?,?> toAdd) {
        boolean isInList = tableItems.stream()
                .anyMatch(t -> t.getEquipment().isUnitIdentical(toAdd));
        if (isInList) {
            Set<Equipment<?,?>> alreadyAdded = new HashSet<>(1);
            for (TableItem ti : tableItems) {
                Equipment<?,?> tiEquipment = ti.getEquipment();
                if (tiEquipment.isUnitIdentical(toAdd) && isEquipmentNotAddedYet(toAdd, alreadyAdded)) {
                    alreadyAdded.add(toAdd);
                    int count = 1;
                    if (tiEquipment.isCountable()) {
                        EquipmentMayCountable<?,?> countable = (EquipmentMayCountable<?,?>) toAdd; //TODO geenric
                        count = countable.getAmount();
                    }
                    ti.setCount(ti.getCount() + count);
                }
            }
        } else {
            I ti = getNewEquipment(toAdd);
            tableItems.add(ti);
        }
        if (table != null) {
            table.refresh();
        }
    }

    private boolean isEquipmentNotAddedYet(Equipment<?,?> e, Set<Equipment<?,?>> alreadyAdded) {
        return !alreadyAdded.contains(e);
    }

    protected abstract I getNewEquipment(Equipment<?,?> e) ;

    protected int getCount(Equipment<?,?> e) {
        int count = 1;
        if (e.isCountable()) {
            EquipmentMayCountable<?,?> countable = (EquipmentMayCountable<?,?>) e;
            count = countable.getAmount();
        }
        return count;
    }
}
