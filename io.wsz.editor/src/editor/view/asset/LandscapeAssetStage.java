package editor.view.asset;

import io.wsz.model.item.ItemType;
import io.wsz.model.item.Landscape;
import javafx.stage.Stage;

import java.util.ArrayList;

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
    protected Landscape createNewAsset(String name, String relativePath) {
        Landscape l = new Landscape(null, name, getType(), relativePath,
                true, null);
        l.setCoverLine(new ArrayList<>(0));
        l.setCollisionPolygons(new ArrayList<>(0));
        return l;
    }

    @Override
    protected ItemType getType() {
        return ItemType.LANDSCAPE;
    }
}