package editor.view.dialog.requirement;

import editor.model.EditorController;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.script.Not;
import io.wsz.model.script.bool.has.item.BooleanCreatureHasInventoryPlace;
import io.wsz.model.script.bool.has.item.HasableCreatureInventoryPlace;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

import java.util.List;

public class RequirementItemHasView extends SpecificRequirement<BooleanCreatureHasInventoryPlace>{
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
        String placeId = null;
        InventoryPlaceType placeType = placeTypeCB.getValue();
        if (placeType != null) {
            placeId = placeType.getId();
        }
        Not not = notCB.getValue();
        HasableCreatureInventoryPlace hasable = new HasableCreatureInventoryPlace(placeId, not);
        return new BooleanCreatureHasInventoryPlace(null, hasable);
    }

    @Override
    public void populate(BooleanCreatureHasInventoryPlace expression) {
        HasableCreatureInventoryPlace hasable = expression.getHasable();
        if (hasable == null) return;
        notCB.setValue(hasable.getNot());
        editorController.getObservableInventoryPlacesTypes().stream()
                .filter(a -> a.getId().equals(hasable.getCheckedId()))
                .findFirst().ifPresent(placeTypeCB::setValue);
    }
}
