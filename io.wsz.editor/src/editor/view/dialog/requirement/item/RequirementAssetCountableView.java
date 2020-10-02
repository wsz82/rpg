package editor.view.dialog.requirement.item;

import editor.model.EditorController;
import io.wsz.model.dialog.Requirements;
import io.wsz.model.item.PosItem;
import io.wsz.model.script.bool.BooleanExpression;
import io.wsz.model.script.bool.countable.item.BooleanItemVsItem;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class RequirementAssetCountableView extends RequirementAssetOrItemCountableView<PosItem<?,?>> {

    public RequirementAssetCountableView(EditorController editorController) {
        super(editorController);
    }

    @Override
    protected ObservableList<PosItem<?,?>> getEquipmentAssetsOrItems() {
        return editorController.getObservableAssets().getEquipmentAssets();
    }

    protected void addExpressionToPChas(Requirements output, BooleanItemVsItem expression) {
        List<BooleanExpression> expressions = output.getPChasAssetExpressions();
        if (expressions == null) {
            expressions = new ArrayList<>(1);
            output.setPChasAssetExpressions(expressions);
        }
        if (expression == null) return;
        expressions.add(expression);
    }

    protected void addExpressionToNPChas(Requirements output, BooleanItemVsItem expression) {
        List<BooleanExpression> expressions = output.getNPChasAssetExpressions();
        if (expressions == null) {
            expressions = new ArrayList<>(1);
            output.setNPChasAssetExpressions(expressions);
        }
        if (expression == null) return;
        expressions.add(expression);
    }

    @Override
    protected String getAssetOrItemId(PosItem<?,?> item) {
        return item.getAssetId();
    }
}
