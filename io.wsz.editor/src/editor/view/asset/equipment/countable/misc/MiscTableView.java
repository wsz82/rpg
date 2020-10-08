package editor.view.asset.equipment.countable.misc;

import editor.model.EditorController;
import editor.view.asset.equipment.EquipmentTableView;
import editor.view.asset.lists.ObservableItemsList;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Misc;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class MiscTableView extends EquipmentTableView<Misc> {

    public MiscTableView(Stage parent, ObservableList<Misc> assets,
                            EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, assets, editorCanvas, editorController);
    }

    @Override
    protected void editAsset() {
        Misc m = getSelectionModel().getSelectedItem();
        if (m == null) {
            return;
        }
        MiscAssetStage as = new MiscAssetStage(parent, m, false, editorCanvas, controller);
        as.show();
        refreshTableOnStageHidden(as);
    }

    @Override
    protected void addAsset() {
        MiscAssetStage as = new MiscAssetStage(parent, editorCanvas, controller);
        as.show();
    }

    @Override
    protected List<Misc> createItems(Coords rawPos) {
        List<Misc> selectedAssets = getSelectionModel().getSelectedItems();
        List<Misc> output = new ArrayList<>(1);
        for (Misc p : selectedAssets) {
            Misc m = new Misc(p);
            clonePrototypePos(rawPos, p, m);
            output.add(m);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<Misc> assetsToRemove) {
        controller.getObservableAssets().getMiscs().removeAll(assetsToRemove);
    }

    @Override
    protected List<Misc> getConcreteObservableItems(ObservableItemsList itemsList) {
        return itemsList.getMiscs();
    }

}