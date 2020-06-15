package editor.view.asset;

import editor.view.stage.ChildStage;
import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

public abstract class AssetStage<A extends PosItem> extends ChildStage {
    protected final VBox container = new VBox(5);
    protected A item;
    private final TextField nameInput = new TextField();
    private final Button imageButton = new Button("Image");
    private final Label imageLabel = new Label();
    private final Button coverButton = new Button("Cover");
    private final Button collisionButton = new Button("Collision");
    private final Button ok = new Button("OK");
    private final Button create = new Button("Create");
    private final Button cancel = new Button("Cancel");
    private boolean isContent;
    private String path;

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
            coverButton.setDisable(true);
            collisionButton.setDisable(true);
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

        container.getChildren().addAll(nameInput, imageBox, coverButton, collisionButton);

        containerWithButtons.getChildren().addAll(container, buttons);
        root.getChildren().add(containerWithButtons);
        if (isContent) {
            nameInput.setDisable(true);
            imageButton.setDisable(true);
        }
        hookupEvents();
    }

    protected void fillInputs() {
        if (item == null) {
            return;
        }
        nameInput.setText(item.getName());
        path = item.getRelativePath();
        imageLabel.setText(item.getRelativePath());
    }

    protected abstract void defineAsset();

    private void onCreate() {
        String name = nameInput.getText();
        boolean inputNameIsEmpty = name.equals("");
        boolean inputFileIsEmpty = path == null || path.isEmpty();
        if (inputNameIsEmpty || inputFileIsEmpty) {
            return;
        }
        List<Asset> assets = Controller.get().getAssetsList();
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
        boolean inputFileIsEmpty = path == null || path.isEmpty();
        if (inputFileIsEmpty) {
            return;
        }
        editAsset();
        defineAsset();
    }

    private void hookupEvents() {
        imageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose image for asset");
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
            path = Asset.convertToRelativeFilePath(selectedFilePath, getType());
            imageLabel.setText(path);
        });
        coverButton.setOnAction(e -> openCoverEdit());
        collisionButton.setOnAction(e -> openCollisionEdit());
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> close());
    }

    private void openCollisionEdit() {
        if (item.getImage() == null) {
            return;
        }
        Stage coverEdit = new CollisionEditStage(this, item, isContent);
        coverEdit.show();
    }

    private void openCoverEdit() {
        if (item.getImage() == null) {
            return;
        }
        Stage coverEdit = new CoverEditStage(this, item, isContent);
        coverEdit.show();
    }

    private void editAsset() {
        item.setRelativePath(path);
        close();
    }

    private boolean pathIsIncorrect(String path) {
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
        String relativePath = Asset.convertToRelativeFilePath(path, getType());
        item = createNewAsset(name, relativePath);
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
