package editor.view.asset;

import io.wsz.model.item.Equipment;
import io.wsz.model.stage.Coords;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public abstract class OpenableEquipmentAssetStage<A extends Equipment> extends EquipmentAssetStage<A> {
    protected final Button openButton = new Button("Open image");
    protected final Label openLabel = new Label();
    protected final CheckBox openCB = new CheckBox("Is open");
    protected final Button openCoverButton = new Button("Open cover");
    protected final Button openCollisionButton = new Button("Open collision");

    public OpenableEquipmentAssetStage(Stage parent, A item, boolean isContent) {
        super(parent, item, isContent);
    }

    public OpenableEquipmentAssetStage(Stage parent) {
        super(parent);
    }

    @Override
    protected void initWindow() {
        super.initWindow();

        final HBox openDoorBox = new HBox(10);
        openDoorBox.getChildren().addAll(openButton, openLabel);

        container.getChildren().addAll(openDoorBox, openCB);

        if (item != null) {
            container.getChildren().addAll(openCoverButton, openCollisionButton);
        }

        hookUpOpenableEvents();
    }

    private void hookUpOpenableEvents() {
        openButton.setOnAction(e -> {
            String title = "Choose image for open";
            setUpImageChooser(title, openLabel);
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
        if (openDoorCoverLine == null) {
            openDoorCoverLine = new ArrayList<>(0);
        }
        CoordsLineEditStage coverEdit = new CoordsLineEditStage(this, item, openDoorCoverLine, background);
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
        if (openDoorCollisionPolygons == null) {
            openDoorCollisionPolygons = new ArrayList<>(0);
        }
        CoordsPolygonsEditStage collisionEdit = new CoordsPolygonsEditStage(this, openDoorCollisionPolygons, item, background);
        collisionEdit.initWindow(isContent, "Open collision edit");
        collisionEdit.show();
    }

    protected abstract List<List<Coords>> getOpenCollisionPolygons();

    @Override
    protected void fillInputs() {
        super.fillInputs();
        if (item == null) {
            return;
        }

        String openImagePath = getOpenImagePath();
        if (openImagePath == null) {
            openLabel.setText("");
        } else {
            openLabel.setText(openImagePath);
        }

        boolean open = isOpen();
        openCB.setSelected(open);
    }

    @Override
    protected void defineAsset() {
        String openDoorPath = openLabel.getText();
        if (!isContent && openDoorPath == null) {
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
