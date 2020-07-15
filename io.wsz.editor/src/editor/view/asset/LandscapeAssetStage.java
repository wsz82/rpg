package editor.view.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.Landscape;
import javafx.stage.Stage;

public class LandscapeAssetStage extends AssetStage<Landscape> {

    public LandscapeAssetStage(Stage parent, Landscape asset, boolean isContent) {
        super(parent, asset, isContent);
        initWindow();
    }

    public LandscapeAssetStage(Stage parent) {
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
    protected void addAssetToList(Landscape asset) {
        ObservableAssets.get().getLandscapes().add(asset);
    }

    @Override
    protected Landscape createNewAsset() {
        return new Landscape(getType());
    }

    @Override
    protected ItemType getType() {
        return ItemType.LANDSCAPE;
    }
}