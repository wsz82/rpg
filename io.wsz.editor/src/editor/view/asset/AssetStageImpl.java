package editor.view.asset;

import io.wsz.model.item.Asset;
import io.wsz.model.item.ItemType;
import javafx.stage.Stage;

class AssetStageImpl extends AssetStage {

    AssetStageImpl(Stage parent, Asset asset) {
        super(parent, asset);
        initWindow();
    }

    AssetStageImpl(Stage parent, ItemType itemType) {
        super(parent, itemType);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        fillInputs();
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();
    }
}
