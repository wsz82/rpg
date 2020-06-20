package editor.view.asset;

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

import static io.wsz.model.Constants.METER;

public class ContainerTableView extends AssetsTableView<Container> {

    public ContainerTableView(Stage parent, ObservableList<Container> assets) {
        super(parent, assets);
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
        ContainerAssetStage as = new ContainerAssetStage(parent, c, false);
        as.show();
    }

    @Override
    protected void addAsset() {
        ContainerAssetStage as = new ContainerAssetStage(parent);
        as.show();
    }

    @Override
    protected List<Container> createItems(Coords rawPos, int level) {
        List<Container> selectedAssets = getSelectionModel().getSelectedItems();
        List<Container> output = new ArrayList<>(1);
        for (Container p
                : selectedAssets) {
            Coords pos = rawPos.clone();
            if (!pos.is0()) {
                double height = p.getImage().getHeight() / METER;
                pos.y = pos.y - height;
            }

            String name = p.getName();
            ItemType type = p.getType();
            String path = p.getRelativePath();

            Coords clonePos = pos.clone();
            List<Coords> coverLine = new ArrayList<>();
            if (p.getCoverLine() != null) {
                coverLine.addAll(p.getCoverLine());
            }
            List<List<Coords>> collisionPolygons = new ArrayList<>();
            if (p.getCollisionPolygons() != null) {
                collisionPolygons.addAll(p.getCollisionPolygons());
            }

            Container c = new Container(
                    p, name, type, path,
                    true, clonePos, level,
                    coverLine, collisionPolygons);
            output.add(c);

            c.getItems().addAll(p.getItems());

        }
        return output;
    }

    @Override
    protected void removeAssetFromList(List assetsToRemove) {
        ObservableAssets.get().getContainers().removeAll(assetsToRemove);
    }

    @Override
    protected ItemType getType() {
        return ItemType.CONTAINER;
    }
}
