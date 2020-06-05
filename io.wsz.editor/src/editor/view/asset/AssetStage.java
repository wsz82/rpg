package editor.view.asset;

import editor.view.stage.ChildStage;
import io.wsz.model.Controller;
import io.wsz.model.item.*;
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

public abstract class AssetStage extends ChildStage {
    protected Asset asset;
    protected final VBox container = new VBox(5);
    private final TextField nameInput = new TextField();
    private final Button imageButton = new Button("Image");
    private final Label imageLabel = new Label();
    private final Button ok = new Button("OK");
    private final Button create = new Button("Create");
    private final Button cancel = new Button("Cancel");
    private final ItemType type;
    private boolean isContent;
    private String path;

    public AssetStage(Stage parent, Asset asset, boolean isContent) {
        super(parent);
        this.type = asset.getType();
        this.asset = asset;
        this.isContent = isContent;
    }

    public AssetStage(Stage parent, ItemType type) {
        super(parent);
        this.type = type;
    }

    protected void initWindow() {
        setTitle(type.toString().toLowerCase() + " asset");
        final StackPane root = new StackPane();
        final VBox containerWithButtons = new VBox(5);
        containerWithButtons.setPadding(new Insets(10));
        final Scene scene = new Scene(root);
        initOwner(parent);
        setScene(scene);

        final HBox buttons = new HBox(10);
        buttons.getChildren().add(cancel);
        if (asset != null) {
            buttons.getChildren().add(ok);
            ok.setDefaultButton(true);
            ok.setOnAction(event -> {
                onEdit();
            });
            nameInput.setDisable(true);
        } else {
            buttons.getChildren().add(create);
            create.setDefaultButton(true);
            create.setOnAction(event -> {
                onCreate();
            });
        }

        nameInput.setPromptText("Name");
        HBox imageBox = new HBox(10);
        imageBox.getChildren().addAll(imageButton, imageLabel);
        container.getChildren().addAll(nameInput, imageBox);
        containerWithButtons.getChildren().addAll(container, buttons);
        root.getChildren().add(containerWithButtons);
        if (isContent) {
            nameInput.setDisable(true);
            imageButton.setDisable(true);
        }

        hookupEvents();
    }

    protected void fillInputs() {
        if (asset == null) {
            return;
        }
        nameInput.setText(asset.getName());
        path = asset.getRelativePath();
        imageLabel.setText(asset.getRelativePath());
    }

    private void hookupEvents() {
        imageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose image for asset");
            fileChooser.setInitialDirectory(Asset.createAssetTypeDir(type));
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(this);
            if (selectedFile == null || !selectedFile.isFile()) {
                return;
            }
            String selectedFilePath = selectedFile.getAbsolutePath();
            if (pathIsIncorrect(selectedFilePath)) return;
            path = Asset.convertToRelativeFilePath(selectedFilePath, type);
            imageLabel.setText(path);
        });
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> close());
    }

    protected void onEdit() {
        String name = nameInput.getText();
        boolean inputNameIsEmpty = name.equals("");
        boolean inputFileIsEmpty = path == null || path.isEmpty();
        if (inputNameIsEmpty || inputFileIsEmpty) {
            return;
        }
        editAsset();
    }

    private void editAsset() {
        asset.setName(nameInput.getText());
        asset.setRelativePath(path);
        close();
    }

    protected void onCreate() {
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
    }

    private boolean pathIsIncorrect(String path) {
        File selectedFile = new File(path);
        File parent = selectedFile.getParentFile();
        String actualPath = parent.getAbsolutePath();
        File required = new File(
                Controller.getProgramDir().getAbsolutePath() + Asset.getRelativeTypePath(type));
        String requiredPath = required.getAbsolutePath();
        if (!actualPath.equals(requiredPath)) {
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
        String relativePath = Asset.convertToRelativeFilePath(path, type);
        asset = switch (type) {
            case COVER -> new Cover(name, type, relativePath, null, 0);
            case CREATURE -> new Creature(name, type, relativePath, null, 0);
            case LANDSCAPE -> new Landscape(name, type, relativePath, null, 0);
            case FLY_ZONE -> new FlyZone(name, type, relativePath, null, 0);
            case OBSTACLE -> new Obstacle(name, type, relativePath, null, 0);
            case TELEPORT -> new Teleport(name, type, relativePath, null, 0);
        };
        Controller.get().getAssetsList().add(asset);
    }

    private void alertOfNameExisting() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, "This name already exists!", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }
}
