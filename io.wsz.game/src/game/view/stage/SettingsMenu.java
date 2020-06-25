package game.view.stage;

import game.model.setting.Settings;
import io.wsz.model.Controller;
import io.wsz.model.sizes.FontSize;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

class SettingsMenu extends StackPane {
    private StackPane graphics;
    private StackPane game;
    private Node parentToReturn;
    private BorderPane root;
    private final GameStage gameStage;

    public SettingsMenu(GameStage gameStage) {
        this.gameStage = gameStage;
        final VBox buttons = new VBox(10);
        buttons.setAlignment(Pos.CENTER);

        final Button graphics = new Button("Graphics");
        graphics.setOnAction(event -> openGraphicsSettings());
        final Button game = new Button("Game");
        game.setOnAction(event -> openGameSettings());
        final Button back = new Button("Back");
        back.setOnAction(event -> goBackToMenu());

        buttons.getChildren().addAll(graphics, game, back);
        getChildren().addAll(buttons);
    }

    private void openGameSettings() {
        if (game == null) {
            initGameSettings();
        }
        root.setCenter(game);
    }

    private void initGameSettings() {
        game = new StackPane();

        final VBox settings = new VBox(10);
        settings.setAlignment(Pos.CENTER);

        final HBox gameScrollBox = new HBox(5);
        gameScrollBox.setAlignment(Pos.CENTER);
        final Label gameScrollLabel = new Label("Game scroll speed");
        final Slider gameScrollSpeed = new Slider();
        gameScrollBox.getChildren().addAll(gameScrollLabel, gameScrollSpeed);
        gameScrollSpeed.setMaxWidth(getWidth()/10);
        hookUpGameScrollSpeedEvents(gameScrollSpeed);

        final HBox dialogScrollBox = new HBox(5);
        dialogScrollBox.setAlignment(Pos.CENTER);
        final Label dialogScrollLabel = new Label("Dialog scroll speed");
        final Slider dialogScrollSpeed = new Slider();
        dialogScrollBox.getChildren().addAll(dialogScrollLabel, dialogScrollSpeed);
        dialogScrollSpeed.setMaxWidth(getWidth()/10);
        hookUpDialogScrollSpeedEvents(dialogScrollSpeed);

        final HBox centerOnPcBox = new HBox(5);
        centerOnPcBox.setAlignment(Pos.CENTER);
        final Label centerOnPcLabel = new Label("Center on PC");
        final CheckBox centerOnPcCB = new CheckBox();
        centerOnPcBox.getChildren().addAll(centerOnPcLabel, centerOnPcCB);
        centerOnPcCB.setMaxWidth(getWidth()/10);
        hookUpCenterOnPCEvents(centerOnPcCB);

        final HBox stopOnInventoryBox = new HBox(5);
        stopOnInventoryBox.setAlignment(Pos.CENTER);
        final Label stopOnInventoryLabel = new Label("Pause on inventory");
        final CheckBox stopOnInventoryCB = new CheckBox();
        stopOnInventoryBox.getChildren().addAll(stopOnInventoryLabel, stopOnInventoryCB);
        stopOnInventoryCB.setMaxWidth(getWidth()/10);
        hookUpStopOnInventoryEvents(stopOnInventoryCB);

        final Button back = new Button("Back");
        back.setOnAction(event -> goBackToSettings());

        settings.getChildren().addAll(gameScrollBox, dialogScrollBox, centerOnPcBox, stopOnInventoryBox, back);
        game.getChildren().addAll(settings);
    }

    private void hookUpStopOnInventoryEvents(CheckBox cb) {
        cb.setSelected(Settings.isPauseOnInventory());
        cb.setOnAction(e -> {
            Settings.setPauseOnInventory(cb.isSelected());
        });
    }

    private void hookUpCenterOnPCEvents(CheckBox cb) {
        cb.setSelected(Settings.isCenterOnPC());
        cb.setOnAction(e -> {
            Settings.setCenterOnPC(cb.isSelected());
        });
    }

    private void hookUpDialogScrollSpeedEvents(Slider s) {
        s.setMin(0.01);
        s.setMax(1);
        s.setBlockIncrement(0.01);
        s.setValue(Settings.getDialogScrollSpeed());
        s.valueProperty().addListener((observable, oldValue, newValue) -> {
            Settings.setDialogScrollSpeed(s.getValue());
        });
    }

    private void hookUpGameScrollSpeedEvents(Slider s) {
        s.setMin(0.01);
        s.setMax(1);
        s.setBlockIncrement(0.01);
        s.setValue(Settings.getGameScrollSpeed());
        s.valueProperty().addListener((observable, oldValue, newValue) -> {
            Settings.setGameScrollSpeed(s.getValue());
        });
    }

    private void goBackToMenu() {
        root.setCenter(parentToReturn);
    }

    private void openGraphicsSettings() {
        if (graphics == null) {
            initGraphicsSettings();
        }
        root.setCenter(graphics);
    }

    private void initGraphicsSettings() {
        graphics = new StackPane();

        final VBox settings = new VBox(10);
        settings.setAlignment(Pos.CENTER);

        final CheckBox fullScreen = new CheckBox("Full screen");
        hookUpFullScreenEvents(fullScreen);

        final HBox fontBox = new HBox(5);
        fontBox.setAlignment(Pos.CENTER);
        final Label fontLabel = new Label("Font size");
        final ChoiceBox<FontSize> fontSizeCB = new ChoiceBox<>();
        fontBox.getChildren().addAll(fontLabel, fontSizeCB);
        hookUpFontSizeEvents(fontSizeCB);

        final Button back = new Button("Back");
        back.setOnAction(event -> goBackToSettings());

        settings.getChildren().addAll(fullScreen, fontBox, back);
        graphics.getChildren().addAll(settings);
    }

    private void hookUpFontSizeEvents(ChoiceBox<FontSize> fontSizeCB) {
        ObservableList<FontSize> fontSizes = FXCollections.observableArrayList(FontSize.values());
        fontSizeCB.setItems(fontSizes);
        fontSizeCB.setValue(Sizes.getFontSize());
        fontSizeCB.setOnAction(e -> {
            FontSize value = fontSizeCB.getValue();
            Sizes.setFontSize(value);
        });
    }

    private void hookUpFullScreenEvents(CheckBox fullScreen) {
        fullScreen.setSelected(gameStage.isFullScreen());
        fullScreen.setOnAction(event -> {
            changeFullScreenSetting(fullScreen.isSelected()
            );
        });
    }

    private void changeFullScreenSetting(boolean isSelected) {
        gameStage.setFullScreen(isSelected);
        Coords current = Controller.get().getBoardPos();
        current.x = 0;
        current.y = 0;
    }

    private void goBackToSettings(){
        root.setCenter(this);
    }

    void open(BorderPane root, Node parentToReturn){
        this.root = root;
        this.parentToReturn = parentToReturn;
    }
}
