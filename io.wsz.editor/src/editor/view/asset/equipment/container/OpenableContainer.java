package editor.view.asset.equipment.container;

import editor.view.asset.AssetStage;
import editor.view.asset.openable.OpenableAsset;
import io.wsz.model.item.Container;
import io.wsz.model.item.OpenableItem;
import javafx.scene.image.Image;

public class OpenableContainer extends OpenableAsset<Container> {

    public OpenableContainer(AssetStage<Container> assetStage, Container item, OpenableItem openableItem, boolean isContent) {
        super(assetStage, item, openableItem, isContent);
    }

    @Override
    protected Image getOpenImage() {
        return item.getOpenImage().getFxImage();
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
