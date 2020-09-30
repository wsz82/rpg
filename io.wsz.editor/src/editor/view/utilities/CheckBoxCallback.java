package editor.view.utilities;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

public class CheckBoxCallback<O> implements Callback<TableColumn.CellDataFeatures<O, CheckBox>, ObservableValue<CheckBox>> {
    private final BooleanSetter<O> propertySetter;
    private final BooleanGetter<O> propertyGetter;

    public CheckBoxCallback(BooleanGetter<O> getter, BooleanSetter<O> setter) {
        this.propertySetter = setter;
        this.propertyGetter = getter;
    }

    @Override
    public ObservableValue<CheckBox> call(TableColumn.CellDataFeatures<O, CheckBox> param) {
        O object = param.getValue();
        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().setValue(propertyGetter.get(object));
        checkBox.selectedProperty().addListener((ov, oldVal, newVal) -> propertySetter.set(object, newVal));
        return new SimpleObjectProperty<>(checkBox);
    }
}