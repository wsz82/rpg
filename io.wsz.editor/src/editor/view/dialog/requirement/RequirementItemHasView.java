package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.script.Not;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.has.BooleanCreatureHasInventoryPlace;
import io.wsz.model.script.bool.has.BooleanHas;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.HBox;

import java.util.List;
import java.util.Optional;

public class RequirementItemHasView extends SpecificRequirement{
    private final HBox elements = new HBox(5);
    private final ChoiceBox<Not> notCB = new ChoiceBox<>();
    private final ChoiceBox<InventoryPlaceType> placeTypeCB = new ChoiceBox<>();
    private final EditorController editorController;

    public RequirementItemHasView(EditorController editorController) {
        this.editorController = editorController;
        setUpNotCB();
        setUpPlaceTypeCB();
        fillElements();
    }

    private void fillElements() {
        elements.getChildren().addAll(notCB, placeTypeCB);
    }

    private void setUpNotCB() {
        Not[] notArr = Not.values();
        List<Not> not = List.of(notArr);
        ObservableList<Not> observableNot = FXCollections.observableArrayList(not);
        observableNot.add(null);
        notCB.setItems(observableNot);
    }

    private void setUpPlaceTypeCB() {
        ObservableList<InventoryPlaceType> placeTypes = editorController.getObservableInventoryPlacesTypes();
        placeTypeCB.setItems(placeTypes);
    }

    @Override
    public BooleanHas getExpression() {
        BooleanCreatureHasInventoryPlace expression = new BooleanCreatureHasInventoryPlace();
        InventoryPlaceType placeType = getInventoryPlaceType();
        if (placeType != null) {
            expression.setItemID(placeType.getId());
        }
        Not not = getNot();
        expression.setNot(not);
        return expression;
    }

    @Override
    public void populate(BooleanExpression expression) {
        if (!(expression instanceof BooleanCreatureHasInventoryPlace)) return;
        BooleanCreatureHasInventoryPlace specificExpression = (BooleanCreatureHasInventoryPlace) expression;
        setNot(specificExpression.getNot());
        Optional<InventoryPlaceType> optPlaceType = editorController.getObservableInventoryPlacesTypes().stream()
                .filter(a -> a.getId().equals(expression.getItemID()))
                .findFirst();
        setInventoryPlaceType(optPlaceType.orElse(null));
    }

    @Override
    public HBox getElements() {
        return elements;
    }

    public Not getNot() {
        return notCB.getValue();
    }

    public void setNot(Not not) {
        notCB.setValue(not);
    }

    public InventoryPlaceType getInventoryPlaceType() {
        return placeTypeCB.getValue();
    }

    public void setInventoryPlaceType(InventoryPlaceType placeType) {
        placeTypeCB.setValue(placeType);
    }
}
