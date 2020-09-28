package editor.view.script;

import editor.model.EditorController;
import io.wsz.model.script.variable.Variable;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.List;

public abstract class GlobalsTableView<A extends Variable<?>> extends TableView<A> {
    protected final EditorController editorController;
    protected final Stage parent;

    public GlobalsTableView(Stage parent, EditorController editorController) {
        super();
        this.parent = parent;
        this.editorController = editorController;
    }

    public void initGlobalsTable(ObservableList<A> globals) {
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setItems(globals);
        setEditable(true);
        setUpContextMenu();

        TableColumn<A, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getId();
            }
        });
        idCol.setEditable(true);
        idCol.setCellFactory(TextFieldTableCell.forTableColumn());
        idCol.setOnEditCommit(t -> {
            A global = getSelectionModel().getSelectedItem();
            String newId = t.getNewValue();
            String oldId = t.getOldValue();
            if (newId.equals(oldId)) return;
            List<String> IDs = getIds();
            newId = getUniqueId(newId, IDs);
            //TODO update new name wherever it is used
            global.setId(newId);
            refresh();
        });
        getColumns().add(idCol);

        TableColumn<A, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getValue().toString();
            }
        });
        valueCol.setEditable(true);
        valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
        valueCol.setOnEditCommit(t -> {
            A item = getSelectionModel().getSelectedItem();
            String newValue = t.getNewValue();
            item.setValue(newValue);
            refresh();
        });
        getColumns().add(valueCol);
    }

    private void setUpContextMenu() {
        final ContextMenu contextMenu = new ContextMenu();
        final MenuItem addVariable = new MenuItem("Add variable");
        final MenuItem removeVariables = new MenuItem("Remove variable/s");
        addVariable.setOnAction(event -> addVariable());
        removeVariables.setOnAction(event -> removeVariables());
        contextMenu.getItems().addAll(addVariable, removeVariables);
        setOnContextMenuRequested(event -> {
            contextMenu.show(this, event.getScreenX(), event.getScreenY());
        });
    }

    private void addVariable() {
        A varaible = getNewVariable();
        varaible.setId(getUniqueId("new", getIds()));
        getItems().add(varaible);
    }

    protected abstract A getNewVariable();

    private void removeVariables() {
        ObservableList<A> selectedItems = getSelectionModel().getSelectedItems();
        getItems().remove(selectedItems);
    }


    private String getUniqueId(String newId, List<String> ids) {
        boolean IDsContainsNewID = ids.contains(newId);
        if (IDsContainsNewID) {
            newId += "New";
            return getUniqueId(newId, ids);
        } else {
            return newId;
        }
    }

    private List<String> getIds() {
        return editorController.getObservableGlobals().getIds();
    }
}
