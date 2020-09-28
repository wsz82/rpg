package editor.view.script;

import editor.model.EditorController;
import io.wsz.model.script.variable.*;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ChoiceBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GlobalVariablesTableView extends TableView<GlobalVariablesTableView.VariableItem> {
    private final EditorController editorController;

    public GlobalVariablesTableView(EditorController editorController) {
        super();
        this.editorController = editorController;
    }

    public void initTable() {
        initTableView();
    }

    public void saveVariables() {
        List<Variable<?>> variables = variableItemsToVariables(getItems());
    }

    private void initTableView() {
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        setEditable(true);

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

    private ObservableList<VariableItem> variablesToVariableItems(ObservableList<Variable<?>> input) {
        if (input == null) return null;
        ObservableList<VariableItem> output = FXCollections.observableArrayList();
        for (Variable<?> variable : input) {
            VariableItem item = new VariableItem();
            item.setID(variable.getId());
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
            String id = item.getID();
            String value = item.getValue();
            Variable<?> variable = switch (item.getType()) {
                case BOOLEAN -> new VariableBoolean(id, Boolean.parseBoolean(value));
                case INTEGER -> new VariableInteger(id, Integer.parseInt(value));
                case DECIMAL -> new VariableDecimal(id, Double.parseDouble(value));
                case STRING -> new VariableString(id, value);
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
