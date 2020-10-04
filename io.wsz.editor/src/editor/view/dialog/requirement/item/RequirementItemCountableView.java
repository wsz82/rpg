package editor.view.dialog.requirement.item;

import editor.model.EditorController;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RequirementItemCountableView extends RequirementAssetOrItemCountableView<PosItem<?,?>> {

    public RequirementItemCountableView(EditorController editorController) {
        super(editorController);
    }

    @Override
    protected ObservableList<PosItem<?,?>> getEquipmentAssetsOrItems() {
        List<Location> locations = editorController.getObservableLocations();
        Set<PosItem<?,?>> itemSet = new HashSet<>(0);
        locations.forEach(l -> l.getItemsList().getEquipment().forEach(e -> {
            if (e.getItemId() != null) {
                itemSet.add(e);
            }
        }));
        return FXCollections.observableArrayList(itemSet);
    }

    protected void addExpressionToPChas(Requirements output, BooleanItemVsItem expression) {
        List<BooleanExpression> expressions = output.getPChasItemExpressions();
        if (expressions == null) {
            expressions = new ArrayList<>(1);
            output.setPChasItemExpressions(expressions);
        }
        if (expression == null) return;
        expressions.add(expression);
    }

    protected void addExpressionToNPChas(Requirements output, BooleanItemVsItem expression) {
        List<BooleanExpression> expressions = output.getNPChasItemExpressions();
        if (expressions == null) {
            expressions = new ArrayList<>(1);
            output.setNPChasItemExpressions(expressions);
        }
        if (expression == null) return;
        expressions.add(expression);
    }

    @Override
    protected String getAssetOrItemId(PosItem<?,?> item) {
        return item.getItemId();
    }
}
