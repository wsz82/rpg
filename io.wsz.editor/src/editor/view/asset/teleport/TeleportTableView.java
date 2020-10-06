package editor.view.asset.teleport;

import editor.model.EditorController;
import editor.view.asset.AssetsTableView;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Teleport;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class TeleportTableView extends AssetsTableView<Teleport> {

    public TeleportTableView(Stage parent, ObservableList<Teleport> assets,
                      EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, assets, editorCanvas, editorController);
        initTeleportTable();
    }

    private void initTeleportTable() {
        TableColumn<Teleport, String> toCol = new TableColumn<>("To");
        toCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                Location l = p.getValue().getExit().getLocation();
                if (l == null) {
                    return null;
                } else {
                    return l.getId();
                }
            }
        });
        toCol.setCellFactory(TextFieldTableCell.forTableColumn());
        toCol.setEditable(false);
        getColumns().add(toCol);

        TableColumn<Teleport, String> posCol = new TableColumn<>("To pos");
        posCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return p.getValue().getExit().toString();
            }
        });
        posCol.setCellFactory(TextFieldTableCell.forTableColumn());
        posCol.setEditable(false);
        getColumns().add(posCol);

        TableColumn<Teleport, String> levCol = new TableColumn<>("To level");
        levCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected String computeValue() {
                return String.valueOf(p.getValue().getExit().level);
            }
        });
        levCol.setCellFactory(TextFieldTableCell.forTableColumn());
        levCol.setEditable(false);
        getColumns().add(levCol);
    }

    @Override
    protected void editAsset() {
        Teleport t = getSelectionModel().getSelectedItem();
        if (t == null) {
            return;
        }
        TeleportAssetStage as = new TeleportAssetStage(parent, t, false, editorCanvas, controller);
        as.show();
        refreshTableOnStageHidden(as);
    }

    @Override
    protected void addAsset() {
        TeleportAssetStage as = new TeleportAssetStage(parent, editorCanvas, controller);
        as.show();
    }

    @Override
    protected List<Teleport> createItems(Coords rawPos) {
        List<Teleport> selectedAssets = getSelectionModel().getSelectedItems();
        List<Teleport> output = new ArrayList<>(1);
        for (Teleport p : selectedAssets) {
            Teleport t = new Teleport(p);
            clonePrototypePos(rawPos, p, t);
            output.add(t);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List<Teleport> assetsToRemove) {
        controller.getObservableAssets().getTeleports().removeAll(assetsToRemove);
    }

}
