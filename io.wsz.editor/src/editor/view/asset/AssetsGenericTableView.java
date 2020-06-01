package editor.view.asset;

import io.wsz.model.item.Asset;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableView;

abstract class AssetsGenericTableView extends TableView<Asset> {

    AssetsGenericTableView() {
        getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }
}
