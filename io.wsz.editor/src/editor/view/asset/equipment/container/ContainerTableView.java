package editor.view.asset.equipment.container;

import editor.model.EditorController;
import editor.view.asset.AssetsTableView;
import editor.view.stage.EditorCanvas;
import io.wsz.model.item.Container;
import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.util.ArrayList;
import java.util.List;

public class ContainerTableView extends AssetsTableView<Container> {

    public ContainerTableView(Stage parent, ObservableList<Container> assets,
                              EditorCanvas editorCanvas, EditorController editorController) {
        super(parent, assets, editorCanvas, editorController);
        initContainersTable();
    }

    private void initContainersTable() {
        TableColumn<Container, Integer> sizeCol = new TableColumn<>("Size");
        sizeCol.setCellValueFactory(p -> new ObjectBinding<>() {
            @Override
            protected Integer computeValue() {
                return p.getValue().getSize();
            }
        });
        sizeCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        sizeCol.setEditable(false);
        getColumns().add(sizeCol);
    }

    @Override
    protected void editAsset() {
        Container c = getSelectionModel().getSelectedItem();
        if (c == null) {
            return;
        }
        ContainerAssetStage as = new ContainerAssetStage(parent, c, false, editorCanvas, editorController);
        as.show();
        refreshTableOnStageHidden(as);
    }

    @Override
    protected void addAsset() {
        ContainerAssetStage as = new ContainerAssetStage(parent, editorCanvas, editorController);
        as.show();
    }

    @Override
    protected List<Container> createItems(Coords rawPos) {
        List<Container> selectedAssets = getSelectionModel().getSelectedItems();
        List<Container> output = new ArrayList<>(1);
        for (Container p
                : selectedAssets) {
            Container c = new Container(p, true);
            clonePrototypePos(rawPos, p, c);
            c.getItems().addAll(p.getItems());
            c.setOpen(p.isOpen());

            output.add(c);
        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List assetsToRemove) {
        editorController.getObservableAssets().getContainers().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.CONTAINER;
    }
}
