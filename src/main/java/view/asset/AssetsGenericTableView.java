package view.asset;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import model.asset.Asset;

abstract class AssetsGenericTableView extends TableView<Asset> {

    AssetsGenericTableView() {
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}
