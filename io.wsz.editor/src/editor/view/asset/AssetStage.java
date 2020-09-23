package editor.view.asset;

import editor.model.EditorController;
import editor.view.DoubleField;
import editor.view.asset.coords.*;
import editor.view.stage.ChildStage;
import editor.view.stage.EditorCanvas;
import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;
import java.util.Optional;

public abstract class AssetStage<A extends PosItem<?,?>> extends ChildStage {
    protected final EditorCanvas editorCanvas;
    protected final EditorController editorController;
    protected final Controller controller;
    protected final VBox container = new VBox(5);
    protected final Button interactionButton = new Button("Interaction point");
    protected final Button coverButton = new Button("Cover");
    protected final Button collisionButton = new Button("Collision");

    protected A item;
    protected boolean isContent;

    private final TextField assetIdInput = new TextField();
    private final TextField itemIdInput = new TextField();
    private final TextField nameInput = new TextField();
    private final Button ok = new Button("OK");
    private final Button create = new Button("Create");
    private final Button cancel = new Button("Cancel");
    private final DoubleField animationSpeedInput = new DoubleField(true);
    private final Button animationButton = new Button("Animation");
    private final Label pathLabel = new Label();
    private final ChoiceBox<Dialog> dialogsCB = new ChoiceBox<>();

    public AssetStage(Stage parent, A item, boolean isContent, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent);
        this.item = item;
        this.isContent = isContent;
        this.editorCanvas = editorCanvas;
        this.editorController = editorController;
        controller = editorController.getController();
    }

    public AssetStage(Stage parent, EditorCanvas editorCanvas, EditorController editorController) {
        super(parent);
        this.editorCanvas = editorCanvas;
        this.editorController = editorController;
        this.controller = editorController.getController();
        this.item = getNewAsset();
    }

    protected void initWindow() {
        setTitle(item.getType().toString().toLowerCase() + " asset");

        final StackPane root = new StackPane();
        final VBox containerWithButtons = new VBox(5);
        containerWithButtons.setPadding(new Insets(10));
        final Scene scene = new Scene(root);
        setScene(scene);

        final HBox buttons = new HBox(10);
        buttons.getChildren().add(cancel);
        if (isItemBeingInitialized()) {
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
            assetIdInput.setDisable(true);
        }

        assetIdInput.setPromptText("Asset ID");
        nameInput.setPromptText("Name");
        container.getChildren().addAll(assetIdInput, nameInput);

        if (isContent) {
            itemIdInput.setPromptText("Item Id");
            container.getChildren().add(itemIdInput);
        }

        if (!isContent) {
            final HBox animationBox = new HBox(10);
            animationBox.getChildren().addAll(animationButton, pathLabel);
            container.getChildren().add(animationBox);
        }

        final HBox animationSpeedBox = new HBox(10);
        final Label animationSpeedLabel = new Label("Animation speed");
        animationSpeedBox.getChildren().addAll(animationSpeedLabel, animationSpeedInput);

        container.getChildren().addAll(animationSpeedBox, interactionButton);

        if (!isContent) {
            container.getChildren().addAll(coverButton, collisionButton);
        }

        final HBox dialogBox = new HBox(10);
        final Label dialogLabel = new Label("Dialog");
        dialogBox.getChildren().addAll(dialogLabel, dialogsCB);
        setUpDialogCB();
        container.getChildren().add(dialogBox);

        containerWithButtons.getChildren().addAll(container, buttons);
        root.getChildren().add(containerWithButtons);
        if (isContent) {
            assetIdInput.setDisable(true);
        }
        hookUpAssetEvents();
    }

    private boolean isItemBeingInitialized() {
        return item.getPath() == null;
    }

    private void setUpDialogCB() {
        ObservableList<Dialog> dialogs = editorController.getObservableDialogs();
        ObservableList<Dialog> dialogsWithNull = FXCollections.observableArrayList(dialogs);
        dialogsWithNull.add(null);
        dialogsCB.setItems(dialogsWithNull);
        dialogsCB.setConverter(new StringConverter<>() {
            @Override
            public String toString(Dialog dialog) {
                if (dialog == null) {
                    return "";
                } else {
                    return dialog.getID();
                }
            }

            @Override
            public Dialog fromString(String name) {
                Optional<Dialog> optDialog = editorController.getObservableDialogs().stream()
                        .filter(t -> t.getID().equals(name))
                        .findFirst();
                return optDialog.orElse(null);
            }
        });
    }

    protected void fillInputs() {
        if (item == null) {
            return;
        }
        assetIdInput.setText(item.getAssetId());
        nameInput.setText(item.getIndividualName());
        pathLabel.setText(item.getPath());

        if (isContent) {
            String itemId = item.getItemId();
            itemIdInput.setText(itemId);
        }

        Double animationSpeed = item.getIndividualAnimationSpeed();
        if (animationSpeed == null) {
            animationSpeedInput.setText("");
        } else {
            animationSpeedInput.setText(String.valueOf(animationSpeed));
        }

        Dialog dialog = item.getIndividualDialog();
        dialogsCB.setValue(dialog);
    }

    private void onCreate() {
        String assetId = assetIdInput.getText();
        boolean inputNameIsEmpty = assetId == null || assetId.isEmpty();
        if (inputNameIsEmpty) {
            return;
        }
        String path = pathLabel.getText();
        boolean inputFileIsEmpty = path == null || path.isEmpty();
        if (inputFileIsEmpty) {
            return;
        }
        List<Asset> assets = editorController.getObservableAssets().getMergedAssets();
        boolean doesAssetIdAlreadyExist = assets.stream()
                .anyMatch(a -> a.getAssetId().equals(assetId));
        if (doesAssetIdAlreadyExist) {
            alertOfIdExisting(assetId);
            return;
        }
        addNewAsset();
        defineAsset();
        close();
    }

    protected void defineAsset() {
        String path = pathLabel.getText();
        item.setPath(path);

        String name = nameInput.getText();
        if (name != null && !name.isEmpty()) {
            item.setName(name);
        }

        if (isContent) {
            String itemId = itemIdInput.getText();
            if (doesItemIdAlreadyExist(itemId)) {
                alertOfIdExisting(itemId);
                return;
            }
            item.setItemId(itemId);
        }

        String animationSpeed = animationSpeedInput.getText();
        if (animationSpeed.isEmpty()) {
            item.setAnimationSpeed(null);
        } else {
            item.setAnimationSpeed(Double.parseDouble(animationSpeed));
        }

        Dialog dialog = dialogsCB.getValue();
        item.setDialog(dialog);
    }

    private boolean doesItemIdAlreadyExist(String itemId) {
        return controller.getLocations().stream()
                .anyMatch(l -> l.getItems().stream().anyMatch(i -> {
                    if (item == i) return false;
                    String id = i.getItemId();
                    if (id == null) {
                        return false;
                    } else {
                        return id.equals(itemId);
                    }
                }));
    }

    private void onEdit() {
        String path = pathLabel.getText();
        boolean inputFileIsEmpty = path == null || path.isEmpty();
        if (inputFileIsEmpty) {
            return;
        }
        defineAsset();
        close();
    }

    private void hookUpAssetEvents() {
        animationButton.setOnAction(e -> {
            String title = "Choose animation for asset";
            setUpDirChooser(title, pathLabel);
        });
        interactionButton.setOnAction(e -> openInteractionPointEdit());
        coverButton.setOnAction(e -> openCoverEdit());
        collisionButton.setOnAction(e -> openCollisionEdit());
        cancel.setCancelButton(true);
        cancel.setOnAction(event -> close());
    }

    public void setUpDirChooser(String title, Label label) {
        final DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle(title);
        dirChooser.setInitialDirectory(item.getTypeDir(controller));
        File selectedFile = dirChooser.showDialog(this);
        fillPath(label, selectedFile);
    }

    private void fillPath(Label label, File selectedFile) {
        if (selectedFile == null) {
            return;
        }
        boolean isNotDirectory = !selectedFile.isDirectory();
        if (isNotDirectory) {
            return;
        }
        String selectedFilePath = selectedFile.getAbsolutePath();
        if (isPathIncorrect(selectedFilePath)) return;
        String path = Asset.convertToRelativePath(selectedFilePath);
        label.setText(path);
        item.setPath(path);
    }

    protected void openInteractionPointEdit() {
        ResolutionImage initialImage = item.getInitialImage();
        if (initialImage == null) {
            return;
        }
        Image background = initialImage.getFxImage();
        if (background == null) {
            return;
        }
        Coords interactionPoint = item.getIndividualInteractionPoint();
        PointSetter pointSetter = item::setInteractionPoint;
        CoordsPointEditStage<A> interactionEdit = new CoordsPointEditStage<>(this, item, interactionPoint, background, pointSetter);
        interactionEdit.initWindow(false, "Interaction point edit");
        interactionEdit.show();
    }

    private void openCoverEdit() {
        ResolutionImage initialImage = item.getInitialImage();
        if (initialImage == null) {
            return;
        }
        Image background = initialImage.getFxImage();
        if (background == null) {
            return;
        }
        List<Coords> coverLine = item.getCoverLine();
        CoordsLineEditStage<A> coverEdit = new CoordsLineEditStage<>(this, item, coverLine, background);
        coverEdit.initWindow(isContent, "Cover edit");
        coverEdit.show();
    }

    private void openCollisionEdit() {
        ResolutionImage initialImage = item.getInitialImage();
        if (initialImage == null) {
            return;
        }
        Image background = initialImage.getFxImage();
        if (background == null) {
            return;
        }
        List<List<Coords>> collisionPolygons = item.getCollisionPolygons();
        PolygonsSetter collisionPolygonsSetter = item::setCollisionPolygons;
        CoordsPolygonsEditStage<A> collisionEdit = new CoordsPolygonsEditStage<>(this, collisionPolygons, item, background, collisionPolygonsSetter);
        collisionEdit.initWindow(isContent, "Collision edit");
        collisionEdit.show();
    }

    protected boolean isPathIncorrect(String path) {
        File selectedFile = new File(path);
        File parent = selectedFile.getParentFile();
        String actualPath = parent.getAbsolutePath().toLowerCase();
        File required = new File(
                controller.getProgramDir() + item.getRelativeTypePath());
        String requiredPath = required.getAbsolutePath().toLowerCase();
        if (!parent.equals(required)) {
            alertWrongDirectory(actualPath, requiredPath);
            return true;
        }
        return false;
    }

    private void alertWrongDirectory(String actualPath, String requiredPath) {
        final Alert alert = new Alert(
                Alert.AlertType.ERROR, "Get animation from directory: " + requiredPath
                + "\n" + "Actual: " + actualPath, ButtonType.CANCEL);
        alert.showAndWait()
                .filter(r -> r == ButtonType.CANCEL)
                .ifPresent(r -> alert.close());
    }

    private void addNewAsset() {
        String id = assetIdInput.getText();
        String path = pathLabel.getText();
        String relativePath = Asset.convertToRelativePath(path);
        item.setAssetId(id);
        item.setPath(relativePath);
        addAssetToList(item);
    }

    protected abstract void addAssetToList(A asset);

    protected abstract A getNewAsset();

    private void alertOfIdExisting(String id) {
        final Alert alert = new Alert(
                Alert.AlertType.INFORMATION,  id + " ID already exists!", ButtonType.OK);
        alert.showAndWait()
                .filter(r -> r == ButtonType.OK)
                .ifPresent(r -> alert.close());
    }
}
