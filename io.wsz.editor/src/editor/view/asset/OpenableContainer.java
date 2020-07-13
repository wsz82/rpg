package editor.view.asset;

import io.wsz.model.item.Container;
import io.wsz.model.stage.Coords;
import javafx.scene.image.Image;

import java.util.List;

public class OpenableContainer extends OpenableAsset<Container> {

    public OpenableContainer(AssetStage<Container> assetStage, Container item, boolean isContent) {
        super(assetStage, item, isContent);
    }

    @Override
    protected Image getOpenImage() {
        return item.getOpenImage();
    }

    @Override
    protected List<Coords> getOpenCoverLine() {
        return item.getOpenContainerCoverLine();
    }

    @Override
    protected List<List<Coords>> getOpenCollisionPolygons() {
        return item.getOpenContainerCollisionPolygons();
    }

    @Override
    protected void setOpen(boolean open) {
        item.setOpen(open);
    }

    @Override
    protected void setOpenImagePath(String openImagePath) {
        item.setOpenImagePath(openImagePath);
    }

    @Override
    protected boolean isOpen() {
        return item.isOpen();
    }

    @Override
    protected String getOpenImagePath() {
        return item.getOpenImagePath();
    }
}
