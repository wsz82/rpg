package editor.view.asset;

import io.wsz.model.Controller;
import io.wsz.model.item.Asset;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class AssetStageImpl extends AssetStage {

    public AssetStageImpl(Stage parent, Asset asset, boolean isContent) {
        super(parent, asset, isContent);
        initWindow();
    }

    public AssetStageImpl(Stage parent, ItemType itemType) {
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

    @Override
    protected void defineAsset() {
        PosItem pi = (PosItem) asset;
        if (pi.isGeneric()) {
            List<Asset> correspondingAsset = Controller.get().getAssetsList().stream()
                    .filter(a -> a.getName().equals(asset.getName()))
                    .collect(Collectors.toList());
            PosItem prototype = (PosItem) correspondingAsset.get(0);
            pi.setCoverLine(prototype.getCoverLine());
            pi.setCollisionPolygons(prototype.getCollisionPolygons());
        }
    }
}
