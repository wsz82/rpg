package editor.view.script.variable;

import io.wsz.model.script.variable.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObservableVariables {
    private final ObservableList<VariableString> strings = FXCollections.observableArrayList();
    private final ObservableList<VariableBoolean> booleans = FXCollections.observableArrayList();
    private final ObservableList<VariableInteger> integers = FXCollections.observableArrayList();
    private final ObservableList<VariableDecimal> decimals = FXCollections.observableArrayList();

    public ObservableVariables() {
    }

    public ObservableList<VariableString> getStrings() {
        return strings;
    }

    public ObservableList<VariableBoolean> getBooleans() {
        return booleans;
    }

    public ObservableList<VariableInteger> getIntegers() {
        return integers;
    }

    public ObservableList<VariableDecimal> getDecimals() {
        return decimals;
    }

    public void clear() {
        strings.clear();
        booleans.clear();
        integers.clear();
        decimals.clear();
    }

    public void fillLists(Variables variables) {
        strings.clear();
        strings.addAll(variables.getStrings());

        booleans.clear();
        booleans.addAll(variables.getBooleans());

        integers.clear();
        integers.addAll(variables.getIntegers());

        decimals.clear();
        decimals.addAll(variables.getDecimals());
    }

    public List<String> getIds() {
        List<String> ids = new ArrayList<>();
        ids.addAll(getStringsIds());
        ids.addAll(getBooleansIds());
        ids.addAll(getIntegersIds());
        ids.addAll(getDecimalsIds());
        return ids;
    }

    private List<String> getStringsIds() {
        return strings.stream()
                .map(Variable::getId)
                .collect(Collectors.toList());
    }

    private List<String> getBooleansIds() {
        return booleans.stream()
                .map(Variable::getId)
                .collect(Collectors.toList());
    }

    private List<String> getIntegersIds() {
        return integers.stream()
                .map(Variable::getId)
                .collect(Collectors.toList());
    }

    private List<String> getDecimalsIds() {
        return decimals.stream()
                .map(Variable::getId)
                .collect(Collectors.toList());
    }

    public ObservableList<Variable<?>> getMergedVariables() {
        ObservableList<Variable<?>> merged = FXCollections.observableArrayList();
        merged.addAll(strings);
        merged.addAll(booleans);
        merged.addAll(integers);
        merged.addAll(decimals);
        return merged;
    }

    public ObservableList<VariableNumber<?>> getMergedNumberVariables() {
        ObservableList<VariableNumber<?>> merged = FXCollections.observableArrayList();
        merged.addAll(integers);
        merged.addAll(decimals);
        return merged;
    }
}
