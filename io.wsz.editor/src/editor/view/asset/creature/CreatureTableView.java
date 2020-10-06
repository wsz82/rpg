package editor.view.asset.creature;

import editor.model.EditorController;
import editor.view.asset.AssetsTableView;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Creature;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class CreatureTableView extends AssetsTableView<Creature> {

    public CreatureTableView(Stage parent, ObservableList<Creature> assets,
                      EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, assets, editorCanvas, editorController);
        initCreatureTable();
    }

    private void initCreatureTable() {
        TableColumn<Creature, String> controlCol = new TableColumn<>("Control");
        controlCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getControl().toString();
            }
        });
        controlCol.setCellFactory(TextFieldTableCell.forTableColumn());
        controlCol.setEditable(false);
        getColumns().add(controlCol);

        TableColumn<Creature, String> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getSize().toString();
            }
        });
        sizeCol.setCellFactory(TextFieldTableCell.forTableColumn());
        sizeCol.setEditable(false);
        getColumns().add(sizeCol);
    }

    @Override
    protected void editAsset() {
        Creature cr = getSelectionModel().getSelectedItem();
        if (cr == null) {
            return;
        }
        CreatureAssetStage as = new CreatureAssetStage(parent, cr, false, editorCanvas, controller);
        as.show();
        refreshTableOnStageHidden(as);
    }

    @Override
    protected void addAsset() {
        CreatureAssetStage as = new CreatureAssetStage(parent, editorCanvas, controller);
        as.show();
    }

    @Override
    protected List<Creature> createItems(Coords rawPos) {
        List<Creature> selectedAssets = getSelectionModel().getSelectedItems();
        List<Creature> output = new ArrayList<>(1);
        for (Creature p : selectedAssets) {
            Creature cr = new Creature(p);
            clonePrototypePos(rawPos, p, cr);
            output.add(cr);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<Creature> assetsToRemove) {
        controller.getObservableAssets().getCreatures().removeAll(assetsToRemove);
    }

}
