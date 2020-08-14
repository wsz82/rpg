package editor.view.asset;

import editor.view.dialog.DialogEditStage;
import editor.view.stage.ChildStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
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
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public abstract class AssetStage<A extends PosItem> extends ChildStage {
    protected final EditorCanvas editorCanvas;
    protected final VBox container = new VBox(5);
    protected final Button interactionButton = new Button("Interaction point");
    protected final Button coverButton = new Button("Cover");
    protected final Button collisionButton = new Button("Collision");
    protected final Button dialogButton = new Button("Dialog");
    protected final HBox imageBox = new HBox(10);
    protected final Button imageButton = new Button("Image");
    protected final Label pathLabel = new Label();

    protected A item;
    protected boolean isContent;

    private final TextField nameInput = new TextField();
    private final Button ok = new Button("OK");
    private final Button create = new Button("Create");
    private final Button cancel = new Button("Cancel");

    public AssetStage(Stage parent, A item, boolean isContent, EditorCanvas editorCanvas) {
        super(parent);
        this.item = item;
        this.isContent = isContent;
        this.editorCanvas = editorCanvas;
    }

    public AssetStage(Stage parent, EditorCanvas editorCanvas) {
        super(parent);
        this.editorCanvas = editorCanvas;
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
        imageBox.getChildren().addAll(imageButton, pathLabel);
        container.getChildren().addAll(nameInput);

        if (!isContent) {
            container.getChildren().add(imageBox);
        }

        if (item != null) {
            if (!isContent) {
                container.getChildren().addAll(interactionButton, coverButton, collisionButton);
            }
        }

        if (!isContent) {
            container.getChildren().add(dialogButton);
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
        pathLabel.setText(item.getPath());
    }

    protected abstract void defineAsset();

    private void onCreate() {
        String name = nameInput.getText();
        String path = pathLabel.getText();
        boolean inputNameIsEmpty = name == null || name.isEmpty();
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
        String path = pathLabel.getText();
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
            setUpFileChooser(title, pathLabel);
        });
        interactionButton.setOnAction(e -> openInteractionPointEdit());
        coverButton.setOnAction(e -> openCoverEdit());
        collisionButton.setOnAction(e -> openCollisionEdit());
        dialogButton.setOnAction(e -> openDialogEdit());
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> close());
    }

    protected void setUpFileChooser(String title, Label label) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(Asset.createAssetTypeDir(getType()));
        File selectedFile = fileChooser.showOpenDialog(this);
        fillLabel(label, selectedFile);
    }

    protected void setUpDirChooser(String title, Label label) {
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(title);
        dirChooser.setInitialDirectory(Asset.createAssetTypeDir(getType()));
        File selectedFile = dirChooser.showDialog(this);
        fillLabel(label, selectedFile);
    }

    private void fillLabel(Label label, File selectedFile) {
        if (selectedFile == null) {
            return;
        }
        boolean isNotFileOrDirectory = !selectedFile.isFile() && !selectedFile.isDirectory();
        if (isNotFileOrDirectory) {
            return;
        }
        String selectedFilePath = selectedFile.getAbsolutePath();
        if (isPathIncorrect(selectedFilePath)) return;
        String path = Asset.convertToRelativePath(selectedFilePath);
        label.setText(path);
    }

    private void openDialogEdit() {
        Stage dialogEditStage = new DialogEditStage(this, item.getIndividualDialog());
        dialogEditStage.show();
    }

    protected void openInteractionPointEdit() {
        Image background = item.getInitialImage();
        if (background == null) {
            return;
        }
        Coords interactionCoords = item.getIndividualInteractionCoords();
        CoordsPointEditStage interactionEdit = new CoordsPointEditStage(this, item, interactionCoords, background);
        interactionEdit.initWindow(isContent, "Interaction point edit");
        interactionEdit.show();
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
        String path = pathLabel.getText();
        item.setPath(path);
        close();
    }

    protected boolean isPathIncorrect(String path) {
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
        String path = pathLabel.getText();
        String relativePath = Asset.convertToRelativePath(path);
        item.setName(name);
        item.setPath(relativePath);
        addAssetToList(item);
    }

    protected abstract void addAssetToList(A asset);

    protected abstract A createNewAsset();

    protected abstract ItemType getType();

    private void alertOfNameExisting() {
        final Alert alert = new Alert(
                Alert.AlertType.INFORMATION, "This name already exists!", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }
}
