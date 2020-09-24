package editor.view.script;

import editor.model.EditorController;
import io.wsz.model.script.variable.Variable;
import io.wsz.model.script.variable.VariableType;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class GlobalVariablesTableView extends TableView<GlobalVariablesTableView.VariableItem> {
    private final EditorController editorController;
    private final ObservableList<Variable<?>> observableGlobalVariables;

    public GlobalVariablesTableView(EditorController editorController) {
        super();
        this.editorController = editorController;
        observableGlobalVariables = editorController.getObservableGlobalVariables();
    }

    public void initTable() {
        initTableView();
        setUpContextMenu();
    }

    public void saveVariables() {
        List<Variable<?>> variables = variableItemsToVariables(getItems());
        ObservableList<Variable<?>> observableGlobalVariables = editorController.getObservableGlobalVariables();
        observableGlobalVariables.clear();
        observableGlobalVariables.addAll(variables);
    }

    private void initTableView() {
        setItems(variablesToVariableItems(observableGlobalVariables));
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

        TableColumn<VariableItem, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getID();
            }
        });
        idCol.setEditable(true);
        idCol.setCellFactory(TextFieldTableCell.forTableColumn());
        idCol.setOnEditCommit(t -> {
            VariableItem item = getSelectionModel().getSelectedItem();
            String newID = t.getNewValue();
            String oldID = t.getOldValue();
            if (newID.equals(oldID)) return;
            List<String> IDs = getIds();
            newID = getUniqueID(newID, IDs);
            //TODO update new name wherever it is used
            item.setID(newID);
            refresh();
        });

        TableColumn<VariableItem, VariableType> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected VariableType computeValue() {
                return param.getValue().getType();
            }
        });
        typeCol.setEditable(true);
        ObservableList<VariableType> variableTypes = FXCollections.observableArrayList(List.of(VariableType.values()));
        typeCol.setCellFactory(ChoiceBoxTableCell.forTableColumn(variableTypes));
        typeCol.setOnEditCommit(t -> {
            VariableItem item = getSelectionModel().getSelectedItem();
            VariableType newType = t.getNewValue();
            item.setType(newType);
            String correctValue = getCorrectValue(item.getValue(), newType);
            item.setValue(correctValue);
            refresh();
        });

        TableColumn<VariableItem, String> valueCol = new TableColumn<>("Value");
        valueCol.setCellValueFactory(param -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return param.getValue().getValue();
            }
        });
        valueCol.setEditable(true);
        valueCol.setCellFactory(TextFieldTableCell.forTableColumn());
        valueCol.setOnEditCommit(t -> {
            VariableItem item = getSelectionModel().getSelectedItem();
            String newValue = t.getNewValue();
            VariableType type = item.getType();
            newValue = getCorrectValue(newValue, type);
            item.setValue(newValue);
            refresh();
        });

        getColumns().addAll(idCol, typeCol, valueCol);
    }

    private String getCorrectValue(String value, VariableType type) {
        value = switch (type) {
            case BOOLEAN -> getBooleanValue(value);
            case INTEGER -> getIntegerValue(value);
            case DECIMAL -> getDoubleValue(value);
            case STRING -> value;
        };
        return value;
    }

    private String getBooleanValue(String newValue) {
        if (newValue.equals("true")) {
            return "true";
        } else {
            return "false";
        }
    }

    private String getDoubleValue(String newValue) {
        try {
            Double.parseDouble(newValue);
        } catch (NumberFormatException e) {
            return "0";
        }
        return newValue;
    }

    private String getIntegerValue(String newValue) {
        try {
            Integer.parseInt(newValue);
        } catch (NumberFormatException e) {
            return "0";
        }
        return newValue;
    }

    private List<String> getIds() {
        return getItems().stream()
                .map(VariableItem::getID)
                .collect(Collectors.toList());
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
        VariableItem item = new VariableItem();
        item.setID(getUniqueID("new", getIds()));
        item.setType(VariableType.INTEGER);
        item.setValue("0");
        getItems().add(item);
    }

    private void removeVariables() {
        ObservableList<VariableItem> selectedItems = getSelectionModel().getSelectedItems();
        getItems().remove(selectedItems);
    }

    private ObservableList<VariableItem> variablesToVariableItems(ObservableList<Variable<?>> input) {
        if (input == null) return null;
        ObservableList<VariableItem> output = FXCollections.observableArrayList();
        for (Variable<?> variable : input) {
            VariableItem item = new VariableItem();
            item.setID(variable.getID());
            VariableType type = getVariableType(variable.getValue());
            item.setType(type);
            item.setValue(String.valueOf(variable.getValue()));
            output.add(item);
        }
        return output;
    }

    private List<Variable<?>> variableItemsToVariables(List<VariableItem> input) {
        if (input == null) return null;
        List<Variable<?>> output = new ArrayList<>(input.size());
        for (VariableItem item : input) {
            String ID = item.getID();
            String value = item.getValue();
            Variable<?> variable = switch (item.getType()) {
                case BOOLEAN -> new Variable<>(ID, Boolean.parseBoolean(value));
                case INTEGER -> new Variable<>(ID, Integer.parseInt(value));
                case DECIMAL -> new Variable<>(ID, Double.parseDouble(value));
                case STRING -> new Variable<>(ID, value);
            };
            output.add(variable);
        }
        return output;
    }

    private VariableType getVariableType(Object value) {
        if (value instanceof Boolean) {
            return VariableType.BOOLEAN;
        } else if (value instanceof Integer) {
            return VariableType.INTEGER;
        } else if (value instanceof Double) {
            return VariableType.DECIMAL;
        } else if (value instanceof String) {
            return VariableType.STRING;
        } else {
            return null;
        }
    }

    private String getUniqueID(String newID, List<String> IDs) {
        boolean IDsContainsNewID = IDs.contains(newID);
        if (IDsContainsNewID) {
            newID += "New";
            return getUniqueID(newID, IDs);
        } else {
            return newID;
        }
    }

    public class VariableItem {
        private String ID;
        private VariableType type;
        private String value;

        public String getID() {
            return ID;
        }

        public void setID(String ID) {
            this.ID = ID;
        }

        public VariableType getType() {
            return type;
        }

        public void setType(VariableType type) {
            this.type = type;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return ID;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof VariableItem)) return false;
            VariableItem that = (VariableItem) o;
            return Objects.equals(getID(), that.getID()) &&
                    getType() == that.getType() &&
                    Objects.equals(getValue(), that.getValue());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getID(), getType(), getValue());
        }
    }
}
