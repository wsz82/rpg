package editor.view.asset.indoor;

import editor.view.asset.AssetStage;
import editor.view.asset.openable.OpenableAsset;
import io.wsz.model.item.InDoor;
import io.wsz.model.item.OpenableItem;
import javafx.scene.image.Image;

public class OpenableInDoor extends OpenableAsset<InDoor> {

    public OpenableInDoor(AssetStage<InDoor> assetStage, InDoor item, OpenableItem openableItem, boolean isContent) {
        super(assetStage, item, openableItem, isContent);
    }

    @Override
    protected Image getOpenImage() {
        return item.getOpenImage();
    }

    @Override
    protected void setOpen(boolean open) {
        item.setOpen(open);
    }

    @Override
    protected boolean isOpen() {
        return item.isOpen();
    }
}
