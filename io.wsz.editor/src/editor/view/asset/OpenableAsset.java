package editor.view.asset;

import io.wsz.model.item.Openable;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public abstract class OpenableAsset<A extends PosItem> {
    protected final A item;
    protected final boolean isContent;
    protected final AssetStage<A> assetStage;

    protected final Button openButton = new Button("Open image");
    protected final Label openLabel = new Label();
    protected final CheckBox openCB = new CheckBox("Is open");
    protected final Button openCoverButton = new Button("Open cover");
    protected final Button openCollisionButton = new Button("Open collision");

    public OpenableAsset(AssetStage<A> assetStage, A item, boolean isContent) {
        this.item = item;
        this.isContent = isContent;
        this.assetStage = assetStage;
    }

    public void initOpenable(VBox container) {
        final HBox openDoorBox = new HBox(10);
        openDoorBox.getChildren().addAll(openButton, openLabel);

        if (!isContent) {
            container.getChildren().add(openDoorBox);
        }

        if (item != null && ((Openable) item).getOpenImage() != null) {
            if (!isContent) {
                container.getChildren().addAll(openCoverButton, openCollisionButton);
            }
        }

        container.getChildren().addAll(openCB);

        hookUpOpenableEvents();
    }

    private void hookUpOpenableEvents() {
        openButton.setOnAction(e -> {
            String title = "Choose image for open";
            assetStage.setUpFileChooser(title, openLabel);
        });
        openCoverButton.setOnAction(e -> openOpenDoorCoverEdit());
        openCollisionButton.setOnAction(e -> openOpenDoorCollisionEdit());
    }

    private void openOpenDoorCoverEdit() {
        Image background = getOpenImage();
        if (background == null) {
            return;
        }
        List<Coords> openDoorCoverLine = getOpenCoverLine();
        CoordsLineEditStage coverEdit = new CoordsLineEditStage(assetStage, item, openDoorCoverLine, background);
        coverEdit.initWindow(isContent, "Open cover edit");
        coverEdit.show();
    }

    protected abstract Image getOpenImage();

    protected abstract List<Coords> getOpenCoverLine();

    private void openOpenDoorCollisionEdit() {
        Image background = getOpenImage();
        if (background == null) {
            return;
        }
        List<List<Coords>> openDoorCollisionPolygons = getOpenCollisionPolygons();
        CoordsPolygonsEditStage collisionEdit = new CoordsPolygonsEditStage(assetStage, openDoorCollisionPolygons, item, background);
        collisionEdit.initWindow(isContent, "Open collision edit");
        collisionEdit.show();
    }

    protected abstract List<List<Coords>> getOpenCollisionPolygons();

    public void fillOpenableInputs() {
        if (item == null) {
            return;
        }

        String openImagePath = getOpenImagePath();
        if (openImagePath.isEmpty()) {
            openLabel.setText("");
        } else {
            openLabel.setText(openImagePath);
        }

        boolean open = isOpen();
        openCB.setSelected(open);
    }

    public void defineOpenable() {
        String openDoorPath = openLabel.getText();
        if (!isContent && openDoorPath.isEmpty()) {
            setOpenImagePath("");
        } else {
            setOpenImagePath(openDoorPath);
        }

        boolean open = openCB.isSelected();
        setOpen(open);
    }

    protected abstract void setOpen(boolean open);

    protected abstract void setOpenImagePath(String s);

    protected abstract boolean isOpen();

    protected abstract String getOpenImagePath();
}