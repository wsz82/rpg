package editor.view.asset.openable;

import editor.view.asset.AssetStage;
import editor.view.asset.coords.CoordsLineEditStage;
import editor.view.asset.coords.CoordsPolygonsEditStage;
import io.wsz.model.item.Openable;
import io.wsz.model.item.OpenableItem;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import java.util.List;

public abstract class OpenableAsset<A extends PosItem> {
    protected final AssetStage<A> assetStage;
    protected final A item;
    protected final OpenableItem openableItem;
    protected final boolean isContent;

    protected final CheckBox openCB = new CheckBox("Is open");
    protected final Button openCoverButton = new Button("Open cover");
    protected final Button openCollisionButton = new Button("Open collision");

    public OpenableAsset(AssetStage<A> assetStage, A item, OpenableItem openableItem, boolean isContent) {
        this.assetStage = assetStage;
        this.item = item;
        this.openableItem = openableItem;
        this.isContent = isContent;
    }

    public void initOpenable(VBox container) {
        if (item != null && ((Openable) item).getOpenImage() != null) {
            if (!isContent) {
                container.getChildren().addAll(openCoverButton, openCollisionButton);
            }
        }

        container.getChildren().addAll(openCB);

        hookUpOpenableEvents();
    }

    private void hookUpOpenableEvents() {
        openCoverButton.setOnAction(e -> openOpenDoorCoverEdit());
        openCollisionButton.setOnAction(e -> openOpenDoorCollisionEdit());
    }

    private void openOpenDoorCoverEdit() {
        Image background = getOpenImage();
        if (background == null) {
            return;
        }
        List<Coords> openDoorCoverLine = openableItem.getOpenCoverLine();
        CoordsLineEditStage coverEdit = new CoordsLineEditStage(assetStage, item, openDoorCoverLine, background);
        coverEdit.initWindow(isContent, "Open cover edit");
        coverEdit.show();
    }

    protected abstract Image getOpenImage();

    private void openOpenDoorCollisionEdit() {
        Image background = getOpenImage();
        if (background == null) {
            return;
        }
        List<List<Coords>> openDoorCollisionPolygons = openableItem.getOpenCollisionPolygons();
        CoordsPolygonsEditStage collisionEdit = new CoordsPolygonsEditStage(assetStage, openDoorCollisionPolygons, item, background);
        collisionEdit.initWindow(isContent, "Open collision edit");
        collisionEdit.show();
    }

    public void fillOpenableInputs() {
        if (item == null) {
            return;
        }

        boolean open = isOpen();
        openCB.setSelected(open);
    }

    public void defineOpenable() {
        boolean open = openCB.isSelected();
        setOpen(open);
    }

    protected abstract void setOpen(boolean open);

    protected abstract boolean isOpen();
}