package editor.view.asset;

import editor.view.stage.ChildStage;
import editor.view.stage.Main;
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
import java.net.MalformedURLException;
import java.util.List;

class AssetStage extends ChildStage {
    protected Asset asset;
    protected final VBox container = new VBox(5);
    private final TextField nameInput = new TextField();
    private final Button imageButton = new Button("Image");
    private final Label imageLabel = new Label();
    private final Button ok = new Button("OK");
    private final Button create = new Button("Create");
    private final Button cancel = new Button("Cancel");
    private final ItemType itemType;
    private String path;

    AssetStage(Stage parent, Asset asset) {
        super(parent);
        this.itemType = asset.getType();
        this.asset = asset;
        initWindow();
    }

    AssetStage(Stage parent, ItemType itemType) {
        super(parent);
        this.itemType = itemType;
        initWindow();
    }

    private void initWindow() {
        setTitle(itemType.toString().toLowerCase() + " asset");
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

        hookupEvents();
        if (asset != null) {
            fillInputs();
        }
    }

    protected void fillInputs() {
        nameInput.setText(asset.getName());
        path = asset.getPath();
        imageLabel.setText(asset.getPath());
    }

    private void hookupEvents() {
        imageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose image for asset");
            fileChooser.setInitialDirectory(getAssetsTypeDir());
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(this);
            if (selectedFile != null && selectedFile.isFile()) {
                try {
                    path = selectedFile.toURI().toURL().toString();
                    imageLabel.setText(path);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
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
        close();
    }

    private void editAsset() {
        asset.setName(nameInput.getText());
        asset.setPath(path);
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

    private void addNewAsset() {
        String name = nameInput.getText();
        asset = switch (itemType) {
            case COVER -> new Cover(name, itemType, path, null, 0);
            case CREATURE -> new Creature(name, itemType, path, null, 0);
            case LANDSCAPE -> new Landscape(name, itemType, path, null, 0);
            case FLY_ZONE -> new FlyZone(name, itemType, path, null, 0);
            case MOVE_ZONE -> new MoveZone(name, itemType, path, null, 0);
        };
        Controller.get().getAssetsList().add(asset);
    }

    private File getAssetsTypeDir() {
        String path = File.separator + "assets" + File.separator + itemType.toString().toLowerCase();
        File dir = new File(Main.getDir() + path);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private void alertOfNameExisting() {
        final Alert alert = new Alert(Alert.AlertType.INFORMATION, "This name already exists!", ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }
}
