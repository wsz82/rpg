package game.view.menu;

import game.model.setting.Settings;
import io.wsz.model.Controller;
import io.wsz.model.sizes.FontSize;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.util.StringConverter;

class SettingsMenu extends StackPane {
    private final Controller controller;
    private final GameStage gameStage;

    private StackPane graphics;
    private StackPane game;
    private Node parentToReturn;
    private BorderPane root;
    private StringConverter<Number> stringConverter;

    public SettingsMenu(GameStage gameStage, Controller controller) {
        this.gameStage = gameStage;
        this.controller = controller;
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

        stringConverter = new StringConverter<>() {
            @Override
            public String toString(Number n) {
                return String.valueOf(n.intValue());
            }

            @Override
            public Number fromString(String s) {
                return Integer.parseInt(s);
            }
        };

        final VBox settings = new VBox(10);
        settings.setAlignment(Pos.CENTER);

        final HBox fullScreenBox = new HBox(5);
        fullScreenBox.setAlignment(Pos.CENTER);
        final Label fullScreenLabel = new Label("Full screen");
        final CheckBox fullScreenCB = new CheckBox();
        fullScreenBox.getChildren().addAll(fullScreenLabel, fullScreenCB);
        hookUpFullScreenEvents(fullScreenCB);

        final HBox resizeWithResolutionBox = new HBox(5);
        resizeWithResolutionBox.setAlignment(Pos.CENTER);
        final Label resizeWithResolutionLabel = new Label("Resize with resolution");
        final CheckBox resizeWithResolutionCB = new CheckBox();
        resizeWithResolutionBox.getChildren().addAll(resizeWithResolutionLabel, resizeWithResolutionCB);
        hookUpResizeWithResolutionEvents(resizeWithResolutionCB);

        final HBox resWidthBox = new HBox(5);
        resWidthBox.setAlignment(Pos.CENTER);
        final Label resWidthLabel = new Label("Resolution width");
        final Label resWidthActual = new Label();
        resWidthActual.setMinWidth(getWidth()/20);
        final Slider resWidthInput = new Slider();
        resWidthBox.getChildren().addAll(resWidthLabel, resWidthActual, resWidthInput);
        hookUpResWidthEvents(resWidthInput, resWidthActual);

        final HBox resHeightBox = new HBox(5);
        resHeightBox.setAlignment(Pos.CENTER);
        final Label resHeightLabel = new Label("Resolution height");
        final Label resHeightActual = new Label();
        resHeightActual.setMinWidth(getWidth()/20);
        final Slider resHeightInput = new Slider();
        resHeightBox.getChildren().addAll(resHeightLabel, resHeightActual, resHeightInput);
        hookUpResHeightEvents(resHeightInput, resHeightActual);

        final HBox fontBox = new HBox(5);
        fontBox.setAlignment(Pos.CENTER);
        final Label fontLabel = new Label("Font size");
        final ChoiceBox<FontSize> fontSizeCB = new ChoiceBox<>();
        fontBox.getChildren().addAll(fontLabel, fontSizeCB);
        hookUpFontSizeEvents(fontSizeCB);

        final Button back = new Button("Back");
        back.setOnAction(event -> goBackToSettings());

        settings.getChildren().addAll(fullScreenBox, resizeWithResolutionBox, resWidthBox, resHeightBox, fontBox, back);
        graphics.getChildren().addAll(settings);
    }

    private void hookUpResHeightEvents(Slider s, Label l) {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        int maxHeight = (int) bounds.getHeight();
        s.setMin(Sizes.MIN_RESOLUTION_HEIGHT);
        s.setMax(maxHeight);
        s.setBlockIncrement(1);
        s.setValue(Settings.getResolutionHeight());
        s.valueProperty().addListener((observable, oldValue, newValue) -> {
            Settings.setResolutionHeight((int) s.getValue());
        });
        l.setText(String.valueOf(s.getValue()));
        l.textProperty().bindBidirectional(s.valueProperty(), stringConverter);
    }

    private void hookUpResWidthEvents(Slider s, Label l) {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        int maxWidth = (int) bounds.getWidth();
        s.setMin(Sizes.MIN_RESOLUTION_WIDTH);
        s.setMax(maxWidth);
        s.setBlockIncrement(1);
        s.setValue(Settings.getResolutionWidth());
        s.valueProperty().addListener((observable, oldValue, newValue) -> {
            Settings.setResolutionWidth((int) s.getValue(), controller);
        });
        l.setText(String.valueOf(s.getValue()));
        l.textProperty().bindBidirectional(s.valueProperty(), stringConverter);
    }

    private void hookUpResizeWithResolutionEvents(CheckBox cb) {
        cb.setSelected(Sizes.isResizeWithResolution());
        cb.setOnAction(event -> {
            Sizes.setResizeWithResolution(cb.isSelected(), controller);
        });
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

    private void hookUpFullScreenEvents(CheckBox cb) {
        cb.setSelected(gameStage.isFullScreen());
        cb.setOnAction(event -> {
            changeFullScreenSetting(cb.isSelected());
        });
    }

    private void changeFullScreenSetting(boolean isSelected) {
        gameStage.setFullScreen(isSelected);
        Coords current = controller.getCurPos();
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
