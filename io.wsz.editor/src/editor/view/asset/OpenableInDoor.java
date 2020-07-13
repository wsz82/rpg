package editor.view.asset;

import io.wsz.model.item.InDoor;
import io.wsz.model.stage.Coords;
import javafx.scene.image.Image;

import java.util.List;

public class OpenableInDoor extends OpenableAsset<InDoor>{

    public OpenableInDoor(AssetStage<InDoor> assetStage, InDoor item, boolean isContent) {
        super(assetStage, item, isContent);
    }

    @Override
    protected Image getOpenImage() {
        return item.getOpenImage();
    }

    @Override
    protected List<Coords> getOpenCoverLine() {
        return item.getOpenDoorCoverLine();
    }

    @Override
    protected List<List<Coords>> getOpenCollisionPolygons() {
        return item.getOpenDoorCollisionPolygons();
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
