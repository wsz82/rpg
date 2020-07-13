package editor.view.asset;

import io.wsz.model.item.InDoor;
import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class InDoorAssetStage extends AssetStage<InDoor> {
    private static final String TITLE = "InDoor asset";

    private final Button openDoorButton = new Button("Open door");
    private final Label openDoorLabel = new Label();
    private final CheckBox openCB = new CheckBox("Open");
    private final Button openDoorCoverButton = new Button("Open door cover");
    private final Button openDoorCollisionButton = new Button("Open door collision");

    public InDoorAssetStage(Stage parent, InDoor item, boolean isContent){
        super(parent, item, isContent);
        initWindow();
    }

    public InDoorAssetStage(Stage parent){
        super(parent);
        initWindow();
    }

    @Override
    protected void initWindow() {
        super.initWindow();
        setTitle(TITLE);

        final HBox openDoorBox = new HBox(10);
        openDoorBox.getChildren().addAll(openDoorButton, openDoorLabel);

        if (item != null) {
            if (!isContent) {
                container.getChildren().addAll(openDoorBox, openDoorCoverButton, openDoorCollisionButton);
            }
        }

        container.getChildren().addAll(openCB);

        fillInputs();
        hookUpInDoorEvents();
    }

    private void hookUpInDoorEvents() {
        openDoorButton.setOnAction(e -> {
            String title = "Choose image for open door";
            setUpImageChooser(title, openDoorLabel);
        });
        openDoorCoverButton.setOnAction(e -> openOpenDoorCoverEdit());
        openDoorCollisionButton.setOnAction(e -> openOpenDoorCollisionEdit());
    }

    private void openOpenDoorCoverEdit() {
        Image background = item.getOpenImage();
        if (background == null) {
            return;
        }
        List<Coords> openDoorCoverLine = item.getOpenDoorCoverLine();
        if (openDoorCoverLine == null) {
            openDoorCoverLine = new ArrayList<>(0);
        }
        CoordsLineEditStage coverEdit = new CoordsLineEditStage(this, item, openDoorCoverLine, background);
        coverEdit.initWindow(isContent, "Open door cover edit");
        coverEdit.show();
    }

    private void openOpenDoorCollisionEdit() {
        Image background = item.getOpenImage();
        if (background == null) {
            return;
        }
        List<List<Coords>> openDoorCollisionPolygons = item.getOpenDoorCollisionPolygons();
        if (openDoorCollisionPolygons == null) {
            openDoorCollisionPolygons = new ArrayList<>(0);
        }
        CoordsPolygonsEditStage collisionEdit = new CoordsPolygonsEditStage(this, openDoorCollisionPolygons, item, background);
        collisionEdit.initWindow(isContent, "Open door collision edit");
        collisionEdit.show();
    }

    @Override
    protected void fillInputs() {
        super.fillInputs();
        if (item == null) {
            return;
        }

        String openImagePath = item.getOpenImagePath();
        if (openImagePath == null) {
            openDoorLabel.setText("");
        } else {
            openDoorLabel.setText(openImagePath);
        }

        boolean open = item.isOpen();
        openCB.setSelected(open);
    }

    @Override
    protected void defineAsset() {
        String openDoorPath = openDoorLabel.getText();
        if (!isContent && openDoorPath == null) {
            item.setOpenImagePath("");
        } else {
            item.setOpenImagePath(openDoorPath);
        }

        boolean open = openCB.isSelected();
        item.setOpen(open);
    }

    @Override
    protected void addAssetToList(InDoor asset) {
        ObservableAssets.get().getInDoors().add(asset);
    }

    @Override
    protected InDoor createNewAsset(String name, String relativePath) {
        InDoor i = new InDoor(
                null, name, getType(), relativePath,
                true, null);
        i.setOpenDoorCoverLine(new ArrayList<>(0));
        i.setOpenDoorCollisionPolygons(new ArrayList<>(0));
        return i;
    }

    @Override
    protected ItemType getType() {
        return ItemType.INDOOR;
    }
}