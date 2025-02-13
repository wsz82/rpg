package editor.view.asset.indoor;

import editor.model.EditorController;
import editor.view.asset.AssetsTableView;
import editor.view.asset.lists.ObservableItemsList;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.InDoor;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class InDoorTableView extends AssetsTableView<InDoor> {

    public InDoorTableView(Stage parent, ObservableList<InDoor> assets,
                    EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, assets, editorCanvas, editorController);
    }

    @Override
    protected void editAsset() {
        InDoor id = getSelectionModel().getSelectedItem();
        if (id == null) {
            return;
        }
        InDoorAssetStage as = new InDoorAssetStage(parent, id, false, editorCanvas, controller);
        as.show();
        refreshTableOnStageHidden(as);
    }

    @Override
    protected void addAsset() {
        InDoorAssetStage as = new InDoorAssetStage(parent, editorCanvas, controller);
        as.show();
    }

    @Override
    protected List<InDoor> createItems(Coords rawPos) {
        List<InDoor> selectedAssets = getSelectionModel().getSelectedItems();
        List<InDoor> output = new ArrayList<>(1);
        for (InDoor p : selectedAssets) {
            InDoor id = new InDoor(p);
            clonePrototypePos(rawPos, p, id);
            output.add(id);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<InDoor> assetsToRemove) {
        controller.getObservableAssets().getInDoors().removeAll(assetsToRemove);
    }

    @Override
    protected List<InDoor> getConcreteObservableItems(ObservableItemsList itemsList) {
        return itemsList.getInDoors();
    }

}