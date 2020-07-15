package editor.view.asset;

import io.wsz.model.item.Cover;
import io.wsz.model.item.ItemType;
import javafx.stage.Stage;

public class CoverAssetStage extends AssetStage<Cover> {

    public CoverAssetStage(Stage parent, Cover asset, boolean isContent) {
        super(parent, asset, isContent);
        initWindow();
    }

    public CoverAssetStage(Stage parent) {
        super(parent);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();

        fillInputs();
    }

    @Override
    protected void fillInputs() {
        if (item == null) {
            item = createNewAsset();
        }
        super.fillInputs();
    }

    @Override
    protected void defineAsset() {
    }

    @Override
    protected void addAssetToList(Cover asset) {
        ObservableAssets.get().getCovers().add(asset);
    }

    @Override
    protected Cover createNewAsset() {
        return new Cover(getType());
    }

    @Override
    protected ItemType getType() {
        return ItemType.COVER;
    }
}
