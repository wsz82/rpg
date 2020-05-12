package view.assets;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.assets.Asset;
import model.assets.AssetsList;
import model.items.ItemType;

import java.io.File;

class NewAssetStage extends Stage {
    private static final String TITLE = "New asset";
    private final AnchorPane root = new AnchorPane();
    private final TextField nameInput = new TextField();
    private final Button imageButton = new Button("Image");
    private final Button create = new Button("Create");
    private final Button cancel = new Button("Cancel");
    private final Stage parent;
    private final ItemType itemType;
    private String path;

    NewAssetStage(Stage parent, ItemType itemType) {
        super(StageStyle.UTILITY);
        this.parent = parent;
        this.itemType = itemType;
        initWindow();
    }

    private void initWindow() {
        Scene scene = new Scene(root);
        this.initOwner(parent);
        this.setTitle(TITLE);
        this.setScene(scene);

        FlowPane flowPane = new FlowPane();
        HBox createCancel = new HBox();
        createCancel.getChildren().addAll(create, cancel);
        createCancel.setSpacing(10);
        flowPane.setVgap(8);
        flowPane.setHgap(4);
        nameInput.setPromptText("Name");
        flowPane.getChildren().addAll(nameInput, imageButton);
        AnchorPane.setTopAnchor(flowPane, 10.0);
        AnchorPane.setLeftAnchor(flowPane, 10.0);
        AnchorPane.setBottomAnchor(createCancel, 10.0);
        AnchorPane.setRightAnchor(createCancel, 10.0);
        root.getChildren().addAll(flowPane, createCancel);

        imageButton.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose image for asset");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image files", "*.png", "*.jpg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(this);
            if (selectedFile != null && selectedFile.isFile()) {
                path = selectedFile.toURI().getPath();
            }
        });

        cancel.setOnAction(event -> this.close());
        create.setOnAction(event -> {
            boolean inputNameIsEmpty = nameInput.getText().equals("");
            boolean inputFileIsEmpty = path == null || path.isEmpty();
            if (inputNameIsEmpty || inputFileIsEmpty) {
                return;
            }
            addNewAsset();
            this.close();
        });
    }

    private void addNewAsset() {
        String name = nameInput.getText();
        Asset asset = new Asset(name, itemType, path);
        AssetsList.get().add(asset);
    }
}
