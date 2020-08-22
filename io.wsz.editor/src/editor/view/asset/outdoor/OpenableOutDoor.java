package editor.view.asset.outdoor;

import editor.view.asset.AssetStage;
import editor.view.asset.openable.OpenableAsset;
import io.wsz.model.item.OpenableItem;
import io.wsz.model.item.OutDoor;
import javafx.scene.image.Image;

public class OpenableOutDoor extends OpenableAsset<OutDoor> {

    public OpenableOutDoor(AssetStage<OutDoor> assetStage, OutDoor item, OpenableItem openableItem, boolean isContent) {
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
