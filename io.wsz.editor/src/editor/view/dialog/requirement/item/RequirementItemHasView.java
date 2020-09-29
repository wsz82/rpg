package editor.view.dialog.requirement.item;

import editor.model.EditorController;
import editor.view.dialog.requirement.SpecificRequirement;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.script.Method;
import io.wsz.model.script.Not;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.has.item.BooleanCreatureHasInventoryPlace;
import io.wsz.model.script.bool.has.item.HasableCreatureInventoryPlace;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;

import java.util.ArrayList;
import java.util.List;

public class RequirementItemHasView extends SpecificRequirement<BooleanCreatureHasInventoryPlace> {
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
    public void addExpressionTo(Requirements output, Method method) {
        BooleanCreatureHasInventoryPlace expression = getExpression();
        switch (method) {
            case PC_HAS -> addExpressionToPChas(output, expression);
            case NPC_HAS -> addExpressionToNPChas(output, expression);
        }
    }

    private void addExpressionToPChas(Requirements output, BooleanCreatureHasInventoryPlace expression) {
        List<BooleanExpression> expressions = output.getBooleanPChasExpressions();
        if (expressions == null) {
            expressions = new ArrayList<>(1);
            output.setBooleanPChasExpressions(expressions);
        }
        if (expression == null) return;
        expressions.add(expression);
    }

    private void addExpressionToNPChas(Requirements output, BooleanCreatureHasInventoryPlace expression) {
        List<BooleanExpression> expressions = output.getBooleanNPChasExpressions();
        if (expressions == null) {
            expressions = new ArrayList<>(1);
            output.setBooleanNPChasExpressions(expressions);
        }
        if (expression == null) return;
        expressions.add(expression);
    }

    @Override
    public void populate(BooleanCreatureHasInventoryPlace expression) {
        HasableCreatureInventoryPlace hasable = expression.getHasable();
        if (hasable == null) return;
        notCB.setValue(hasable.getNot());
        String checkedId = hasable.getCheckedId();
        editorController.getObservableInventoryPlacesTypes().stream()
                .filter(a -> a.getId().equals(checkedId))
                .findFirst().ifPresent(placeTypeCB::setValue);
    }
}
