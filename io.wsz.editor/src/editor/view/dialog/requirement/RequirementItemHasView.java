package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.script.Not;
import io.wsz.model.script.bool.BooleanObjectExpression;
import io.wsz.model.script.bool.has.item.BooleanCreatureHasInventoryPlace;
import io.wsz.model.script.bool.has.item.HasableCreatureInventoryPlace;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

import java.util.List;

public class RequirementItemHasView extends SpecificRequirement{
    private final ChoiceBox<Not> notCB = new ChoiceBox<>();
    private final ChoiceBox<InventoryPlaceType> placeTypeCB = new ChoiceBox<>();

    public RequirementItemHasView(EditorController editorController) {
        super(editorController);
        setUpNotCB();
        setUpPlaceTypeCB();
        fillElements();
    }

    @Override
    protected void fillElements() {
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
    public BooleanCreatureHasInventoryPlace getExpression() {
        HasableCreatureInventoryPlace hasable = new HasableCreatureInventoryPlace();
        BooleanCreatureHasInventoryPlace expression = new BooleanCreatureHasInventoryPlace(null, hasable);
        InventoryPlaceType placeType = getInventoryPlaceType();
        if (placeType != null) {
            hasable.setCheckedId(placeType.getId());
        }
        Not not = getNot();
        hasable.setNot(not);
        return expression;
    }

    @Override
    public void populate(BooleanObjectExpression expression) {
        if (!(expression instanceof BooleanCreatureHasInventoryPlace)) return;
        BooleanCreatureHasInventoryPlace specificExpression = (BooleanCreatureHasInventoryPlace) expression;
        HasableCreatureInventoryPlace hasable = specificExpression.getHasable();
        setNot(hasable.getNot());
        InventoryPlaceType placeType = editorController.getObservableInventoryPlacesTypes().stream()
                .filter(a -> a.getId().equals(hasable.getCheckedId()))
                .findFirst().orElse(null);
        setInventoryPlaceType(placeType);
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
