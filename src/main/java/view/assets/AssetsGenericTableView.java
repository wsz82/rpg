package view.assets;

import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;
import model.assets.Asset;

abstract class AssetsGenericTableView extends TableView<Asset> {

    AssetsGenericTableView() {
        this.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}
