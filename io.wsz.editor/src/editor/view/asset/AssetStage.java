package editor.view.asset;

import editor.view.dialog.DialogEditStage;
import editor.view.stage.ChildStage;
import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public abstract class AssetStage<A extends PosItem> extends ChildStage {
    protected final VBox container = new VBox(5);
    protected final Button coverButton = new Button("Cover");
    protected final Button collisionButton = new Button("Collision");
    protected final Button dialogButton = new Button("Dialog");

    protected A item;
    protected boolean isContent;

    private final TextField nameInput = new TextField();
    private final Button imageButton = new Button("Image");
    private final Label imageLabel = new Label();
    private final Button ok = new Button("OK");
    private final Button create = new Button("Create");
    private final Button cancel = new Button("Cancel");

    public AssetStage(Stage parent, A item, boolean isContent) {
        super(parent);
        this.item = item;
        this.isContent = isContent;
    }

    public AssetStage(Stage parent) {
        super(parent);
    }

    protected void initWindow() {
        setTitle(getType().toString().toLowerCase() + " asset");
        final StackPane root = new StackPane();
        final VBox containerWithButtons = new VBox(5);
        containerWithButtons.setPadding(new Insets(10));
        final Scene scene = new Scene(root);
        setScene(scene);

        final HBox buttons = new HBox(10);
        buttons.getChildren().add(cancel);
        if (item == null) {
            buttons.getChildren().add(create);
            create.setDefaultButton(true);
            create.setOnAction(event -> {
                onCreate();
            });
        } else {
            buttons.getChildren().add(ok);
            ok.setDefaultButton(true);
            ok.setOnAction(event -> {
                onEdit();
            });
            nameInput.setDisable(true);
        }

        nameInput.setPromptText("Name");
        final HBox imageBox = new HBox(10);
        imageBox.getChildren().addAll(imageButton, imageLabel);
        container.getChildren().addAll(nameInput);

        if (!isContent) {
            container.getChildren().add(imageBox);
        }

        if (item != null) {
            if (!isContent) {
                container.getChildren().addAll(dialogButton, coverButton, collisionButton);
            }
        }

        containerWithButtons.getChildren().addAll(container, buttons);
        root.getChildren().add(containerWithButtons);
        if (isContent) {
            nameInput.setDisable(true);
        }
        hookupEvents();
    }

    protected void fillInputs() {
        if (item == null) {
            return;
        }
        nameInput.setText(item.getName());
        imageLabel.setText(item.getRelativePath());
    }

    protected abstract void defineAsset();

    private void onCreate() {
        String name = nameInput.getText();
        String path = imageLabel.getText();
        boolean inputNameIsEmpty = name.equals("");
        boolean inputFileIsEmpty = path == null || path.isEmpty();
        if (inputNameIsEmpty || inputFileIsEmpty) {
            return;
        }
        List<Asset> assets = ObservableAssets.get().merge();
        boolean assetNameAlreadyExists = assets.stream()
                .anyMatch(a -> a.getName().equals(name));
        if (assetNameAlreadyExists) {
            alertOfNameExisting();
            return;
        }
        close();
        addNewAsset();
        defineAsset();
    }

    private void onEdit() {
        String path = imageLabel.getText();
        boolean inputFileIsEmpty = path == null || path.isEmpty();
        if (inputFileIsEmpty) {
            return;
        }
        editAsset();
        defineAsset();
    }

    private void hookupEvents() {
        imageButton.setOnAction(e -> {
            String title = "Choose image for asset";
            setUpImageChooser(title, imageLabel);
        });
        coverButton.setOnAction(e -> openCoverEdit());
        collisionButton.setOnAction(e -> openCollisionEdit());
        dialogButton.setOnAction(e -> openDialogEdit());
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> close());
    }

    protected void setUpImageChooser(String title, Label label) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(Asset.createAssetTypeDir(getType()));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(this);
        if (selectedFile == null || !selectedFile.isFile()) {
            return;
        }
        String selectedFilePath = selectedFile.getAbsolutePath();
        if (pathIsIncorrect(selectedFilePath)) return;
        String path = Asset.convertToRelativePath(selectedFilePath);
        label.setText(path);
    }

    private void openDialogEdit() {
        Stage dialogEditStage = new DialogEditStage(this, item.getDialog());
        dialogEditStage.show();
    }

    private void openCoverEdit() {
        Image background = item.getInitialImage();
        if (background == null) {
            return;
        }
        List<Coords> coverLine = item.getCoverLine();
        CoordsLineEditStage coverEdit = new CoordsLineEditStage(this, item, coverLine, background);
        coverEdit.initWindow(isContent, "Cover edit");
        coverEdit.show();
    }

    private void openCollisionEdit() {
        Image background = item.getInitialImage();
        if (background == null) {
            return;
        }
        List<List<Coords>> collisionPolygons = item.getCollisionPolygons();
        CoordsPolygonsEditStage collisionEdit = new CoordsPolygonsEditStage(this, collisionPolygons, item, background);
        collisionEdit.initWindow(isContent, "Collision edit");
        collisionEdit.show();
    }

    private void editAsset() {
        String path = imageLabel.getText();
        item.setRelativePath(path);
        close();
    }

    protected boolean pathIsIncorrect(String path) {
        File selectedFile = new File(path);
        File parent = selectedFile.getParentFile();
        String actualPath = parent.getAbsolutePath().toLowerCase();
        File required = new File(
                Controller.getProgramDir() + Asset.getRelativeTypePath(getType()));
        String requiredPath = required.getAbsolutePath().toLowerCase();
        if (!parent.equals(required)) {
            alertWrongDirectory(actualPath, requiredPath);
            return true;
        }
        return false;
    }

    private void alertWrongDirectory(String actualPath, String requiredPath) {
        final Alert alert = new Alert(
                Alert.AlertType.ERROR, "Get image from directory: " + requiredPath
                + "\n" + "Actual: " + actualPath, ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }

    private void addNewAsset() {
        String name = nameInput.getText();
        String path = imageLabel.getText();
        String relativePath = Asset.convertToRelativePath(path);
        item = createNewAsset(name, relativePath);
        item.setDialog(new Dialog());
        addAssetToList(item);
    }

    protected abstract void addAssetToList(A asset);

    protected abstract A createNewAsset(String name, String relativePath);

    protected abstract ItemType getType();

    private void alertOfNameExisting() {
        final Alert alert = new Alert(
                Alert.AlertType.INFORMATION, "This name already exists!", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }
}
