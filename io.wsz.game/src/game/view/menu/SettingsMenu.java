package game.view.menu;

import game.model.GameController;
import game.model.setting.Key;
import game.model.setting.Settings;
import io.wsz.model.Controller;
import io.wsz.model.locale.LocaleKeys;
import io.wsz.model.sizes.FontSize;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.util.StringConverter;

import java.io.File;
import java.util.Properties;

class SettingsMenu extends StackPane {
    private final GameController gameController;
    private final Settings settings;
    private final Controller controller;
    private final GameStage gameStage;
    private final SettingsParent parent;
    private final Button graphics;
    private final Button game;
    private final Button control;
    private final Button back;

    private BorderPane root;
    private StringConverter<Number> stringConverter;
    private Slider resHeightInput;
    private Slider resWidthInput;
    private Label mapScrollLabel;
    private Label dialogScrollLabel;
    private Label centerOnPcLabel;
    private Label stopOnInventoryLabel;
    private Label languageLabel;
    private Button backToSettings;

    public SettingsMenu(GameStage gameStage, GameController gameController, SettingsParent parent) {
        this.parent = parent;
        this.gameStage = gameStage;
        this.gameController = gameController;
        this.settings = gameController.getSettings();
        this.controller = gameController.getController();
        final VBox buttons = new VBox(10);
        buttons.setAlignment(Pos.CENTER);

        Properties locale = controller.getLocale();
        graphics = new Button(locale.getProperty(LocaleKeys.GRAPHICS));
        graphics.setOnAction(e -> openGraphicsSettings());
        game = new Button(locale.getProperty(LocaleKeys.GAME));
        game.setOnAction(e -> openGameSettings());
        control = new Button(locale.getProperty(LocaleKeys.CONTROL));
        control.setOnAction(e -> openControlSettings());
        back = new Button(locale.getProperty(LocaleKeys.BACK));
        back.setOnAction(e -> goBackToMenu());

        buttons.getChildren().addAll(graphics, game, control, back);
        getChildren().addAll(buttons);
    }

    private void openControlSettings() {
        StackPane controlSettingsContainer = getControlSettings();
        root.setCenter(controlSettingsContainer);
    }

    private StackPane getControlSettings() {
        final StackPane controlContainer = new StackPane();

        final VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);

        Properties locale = controller.getLocale();

        KeysControlTableView keysControls = new KeysControlTableView(gameController);
        keysControls.initTable();
        TableView<Key> keys = keysControls.getTable();
        keys.setMaxWidth(root.getWidth()/3);

        final Button backToSettings = new Button(locale.getProperty(LocaleKeys.BACK));
        backToSettings.setOnAction(event -> goBackToSettings());

        container.getChildren().addAll(keys, backToSettings);
        controlContainer.getChildren().add(container);
        return controlContainer;
    }

    void open(BorderPane root){
        this.root = root;
    }

    private void openGameSettings() {
        StackPane gameSettingsContainer = getGameSettings();
        root.setCenter(gameSettingsContainer);
    }

    private StackPane getGameSettings() {
        StackPane gameContainer = new StackPane();

        final VBox container = new VBox(10);
        container.setAlignment(Pos.CENTER);

        Properties locale = controller.getLocale();
        final HBox mapScrollBox = new HBox(5);
        mapScrollBox.setAlignment(Pos.CENTER);
        mapScrollLabel = new Label(locale.getProperty(LocaleKeys.MAP_SCROLL_SPEED));
        final Slider mapScrollSpeed = new Slider();
        mapScrollBox.getChildren().addAll(mapScrollLabel, mapScrollSpeed);
        mapScrollSpeed.setMaxWidth(getWidth()/10);
        hookUpGameScrollSpeedEvents(mapScrollSpeed);

        final HBox dialogScrollBox = new HBox(5);
        dialogScrollBox.setAlignment(Pos.CENTER);
        dialogScrollLabel = new Label(locale.getProperty(LocaleKeys.DIALOG_SCROLL_SPEED));
        final Slider dialogScrollSpeed = new Slider();
        dialogScrollBox.getChildren().addAll(dialogScrollLabel, dialogScrollSpeed);
        dialogScrollSpeed.setMaxWidth(getWidth()/10);
        hookUpDialogScrollSpeedEvents(dialogScrollSpeed);

        final HBox centerOnPcBox = new HBox(5);
        centerOnPcBox.setAlignment(Pos.CENTER);
        centerOnPcLabel = new Label(locale.getProperty(LocaleKeys.CENTER_ON_PC));
        final CheckBox centerOnPcCB = new CheckBox();
        centerOnPcBox.getChildren().addAll(centerOnPcLabel, centerOnPcCB);
        centerOnPcCB.setMaxWidth(getWidth()/10);
        hookUpCenterOnPCEvents(centerOnPcCB);

        final HBox stopOnInventoryBox = new HBox(5);
        stopOnInventoryBox.setAlignment(Pos.CENTER);
        stopOnInventoryLabel = new Label(locale.getProperty(LocaleKeys.PAUSE_ON_INVENTORY));
        final CheckBox stopOnInventoryCB = new CheckBox();
        stopOnInventoryBox.getChildren().addAll(stopOnInventoryLabel, stopOnInventoryCB);
        stopOnInventoryCB.setMaxWidth(getWidth()/10);
        hookUpStopOnInventoryEvents(stopOnInventoryCB);

        final HBox languageBox = new HBox(5);
        languageBox.setAlignment(Pos.CENTER);
        languageLabel = new Label(locale.getProperty(LocaleKeys.LANGUAGE));
        final ChoiceBox<String> languageCB = new ChoiceBox<>(getLanguages());
        String language = settings.getLanguage();
        if (language == null) {
            language = Paths.ENGLISH;
        }
        languageCB.setValue(language);
        languageBox.getChildren().addAll(languageLabel, languageCB);
        languageCB.setMaxWidth(getWidth()/10);
        hookUpLanguageEvents(languageCB);

        backToSettings = new Button(locale.getProperty(LocaleKeys.BACK));
        backToSettings.setOnAction(event -> goBackToSettings());

        container.getChildren().addAll(mapScrollBox, dialogScrollBox, centerOnPcBox, stopOnInventoryBox, languageBox, backToSettings);
        gameContainer.getChildren().addAll(container);
        return gameContainer;
    }

    private void hookUpLanguageEvents(ChoiceBox<String> languageCB) {
        languageCB.setOnAction(e -> {
            String language = languageCB.getValue();
            if (language == null) {
                language = Paths.ENGLISH;
            }
            settings.setLanguage(language);
            gameController.setLocale(language);
            updateNodesDisplayText();
        });
    }

    private void updateNodesDisplayText() {
        Properties locale = controller.getLocale();
        graphics.setText(locale.getProperty(LocaleKeys.GRAPHICS));
        game.setText(locale.getProperty(LocaleKeys.GAME));
        control.setText(locale.getProperty(LocaleKeys.CONTROL));
        back.setText(locale.getProperty(LocaleKeys.BACK));
        mapScrollLabel.setText(locale.getProperty(LocaleKeys.MAP_SCROLL_SPEED));
        dialogScrollLabel.setText(locale.getProperty(LocaleKeys.DIALOG_SCROLL_SPEED));
        centerOnPcLabel.setText(locale.getProperty(LocaleKeys.CENTER_ON_PC));
        stopOnInventoryLabel.setText(locale.getProperty(LocaleKeys.PAUSE_ON_INVENTORY));
        languageLabel.setText(locale.getProperty(LocaleKeys.LANGUAGE));
        backToSettings.setText(locale.getProperty(LocaleKeys.BACK));
    }

    private ObservableList<String> getLanguages() {
        ObservableList<String> languages = FXCollections.observableArrayList();
        File programDir = controller.getProgramDir();
        File locales = new File(programDir + Paths.LOCALE_DIR);
        File[] languagesPropertiesFiles = locales.listFiles();
        if (languagesPropertiesFiles == null) return languages;
        for (File languageFile : languagesPropertiesFiles) {
            if (languageFile.isDirectory()) continue;
            String name = languageFile.getName();
            String dotProperties = Paths.DOT_PROPERTIES;
            if (!name.endsWith(dotProperties)) continue;
            name = name.replace(dotProperties, "");
            languages.add(name);
        }
        return languages;
    }

    private void hookUpStopOnInventoryEvents(CheckBox cb) {
        cb.setSelected(settings.isPauseOnInventory());
        cb.setOnAction(e -> settings.setPauseOnInventory(cb.isSelected()));
    }

    private void hookUpCenterOnPCEvents(CheckBox cb) {
        cb.setSelected(settings.isCenterOnPC());
        cb.setOnAction(e -> settings.setCenterOnPC(cb.isSelected()));
    }

    private void hookUpDialogScrollSpeedEvents(Slider s) {
        s.setMin(0.01);
        s.setMax(1);
        s.setBlockIncrement(0.01);
        s.setValue(settings.getDialogScrollSpeed());
        s.valueProperty().addListener((observable, oldValue, newValue) -> settings.setDialogScrollSpeed(s.getValue()));
    }

    private void hookUpGameScrollSpeedEvents(Slider s) {
        s.setMin(0.01);
        s.setMax(1);
        s.setBlockIncrement(0.01);
        s.setValue(settings.getGameScrollSpeed());
        s.valueProperty().addListener((observable, oldValue, newValue) -> settings.setGameScrollSpeed(s.getValue()));
    }

    private void goBackToMenu() {
        if (parent == SettingsParent.MAIN_MENU) {
            gameStage.setMainMenuForCenter();
        } else {
            gameStage.setGameMenuForCenter();
        }
    }

    private void openGraphicsSettings() {
        StackPane graphicsContainer = getGraphicsSettings();
        root.setCenter(graphicsContainer);
    }

    private StackPane getGraphicsSettings() {
        StackPane graphicsContainer = new StackPane();

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

        Properties locale = controller.getLocale();
        final HBox fullScreenBox = new HBox(5);
        fullScreenBox.setAlignment(Pos.CENTER);
        final Label fullScreenLabel = new Label(locale.getProperty(LocaleKeys.FULL_SCREEN));
        final CheckBox fullScreenCB = new CheckBox();
        fullScreenBox.getChildren().addAll(fullScreenLabel, fullScreenCB);
        hookUpFullScreenEvents(fullScreenCB);

        final HBox resizeWithResolutionBox = new HBox(5);
        resizeWithResolutionBox.setAlignment(Pos.CENTER);
        final Label resizeWithResolutionLabel = new Label(locale.getProperty(LocaleKeys.RESIZE_WITH_RESOLUTION));
        final CheckBox resizeWithResolutionCB = new CheckBox();
        resizeWithResolutionBox.getChildren().addAll(resizeWithResolutionLabel, resizeWithResolutionCB);
        hookUpResizeWithResolutionEvents(resizeWithResolutionCB);

        final HBox resWidthBox = new HBox(5);
        resWidthBox.setAlignment(Pos.CENTER);
        final Label resWidthLabel = new Label(locale.getProperty(LocaleKeys.HORIZONTAL_RESOLUTION));
        final Label resWidthActual = new Label();
        resWidthActual.setMinWidth(getWidth()/20);
        resWidthInput = new Slider();
        resWidthBox.getChildren().addAll(resWidthLabel, resWidthActual, resWidthInput);
        hookUpResWidthEvents(resWidthInput, resWidthActual);

        final HBox resHeightBox = new HBox(5);
        resHeightBox.setAlignment(Pos.CENTER);
        final Label resHeightLabel = new Label(locale.getProperty(LocaleKeys.VERTICAL_RESOLUTION));
        final Label resHeightActual = new Label();
        resHeightActual.setMinWidth(getWidth()/20);
        resHeightInput = new Slider();
        resHeightBox.getChildren().addAll(resHeightLabel, resHeightActual, resHeightInput);
        hookUpResHeightEvents(resHeightInput, resHeightActual);

        final HBox fontBox = new HBox(5);
        fontBox.setAlignment(Pos.CENTER);
        final Label fontLabel = new Label(locale.getProperty(LocaleKeys.FONT_SIZE));
        final ChoiceBox<FontSize> fontSizeCB = new ChoiceBox<>();
        fontBox.getChildren().addAll(fontLabel, fontSizeCB);
        hookUpFontSizeEvents(fontSizeCB);

        final Button backToSettingsFromGraphics = new Button(locale.getProperty(LocaleKeys.BACK));
        backToSettingsFromGraphics.setOnAction(event -> goBackToSettings());

        settings.getChildren().addAll(fullScreenBox, resizeWithResolutionBox, resWidthBox, resHeightBox, fontBox, backToSettingsFromGraphics);
        graphicsContainer.getChildren().addAll(settings);
        return graphicsContainer;
    }

    private void hookUpResWidthEvents(Slider widthSlider, Label l) {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        int maxWidth = (int) bounds.getWidth();
        widthSlider.setMin(Sizes.MIN_RESOLUTION_WIDTH);
        widthSlider.setMax(maxWidth);
        widthSlider.setBlockIncrement(1);
        widthSlider.setValue(settings.getResolutionWidth());
        widthSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int newWidthResolution = newValue.intValue();
            updateWidthResolution(newWidthResolution);
        });
        l.setText(String.valueOf(widthSlider.getValue()));
        l.textProperty().bindBidirectional(widthSlider.valueProperty(), stringConverter);
    }

    private void hookUpResHeightEvents(Slider heightSlider, Label l) {
        Rectangle2D bounds = Screen.getPrimary().getBounds();
        int maxHeight = (int) bounds.getHeight();
        heightSlider.setMin(Sizes.MIN_RESOLUTION_HEIGHT);
        heightSlider.setMax(maxHeight);
        heightSlider.setBlockIncrement(1);
        heightSlider.setValue(settings.getResolutionHeight());
        heightSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            int newHeightResolution = newValue.intValue();
            updateHeightResolution(newHeightResolution);
        });
        l.setText(String.valueOf(heightSlider.getValue()));
        l.textProperty().bindBidirectional(heightSlider.valueProperty(), stringConverter);
    }

    private void updateWidthResolution(int newWidthResolution) {
        settings.setResolutionWidth(newWidthResolution, controller);
        if (Sizes.isResizeWithResolution()) {
            double ratio = Sizes.BASIC_RESOLUTION_RATIO;
            resHeightInput.setValue(newWidthResolution / ratio);
        }
    }

    private void updateHeightResolution(int newHeightResolution) {
        settings.setResolutionHeight(newHeightResolution, controller);
        if (Sizes.isResizeWithResolution()) {
            double ratio = Sizes.BASIC_RESOLUTION_RATIO;
            resWidthInput.setValue(newHeightResolution * ratio);
        }
    }

    private void hookUpResizeWithResolutionEvents(CheckBox cb) {
        cb.setSelected(Sizes.isResizeWithResolution());
        cb.setOnAction(event -> {
            boolean isSelected = cb.isSelected();
            Sizes.setResizeWithResolution(isSelected, controller);
            if (isSelected) {
                adjustHeightResolutionToPreserveRatio();
            }
        });
    }

    private void adjustHeightResolutionToPreserveRatio() {
        double resWidth = resWidthInput.getValue();
        double ratio = Sizes.BASIC_RESOLUTION_RATIO;
        resHeightInput.setValue(resWidth / ratio);
    }

    private void hookUpFontSizeEvents(ChoiceBox<FontSize> fontSizeCB) {
        ObservableList<FontSize> fontSizes = FXCollections.observableArrayList(FontSize.values());
        fontSizeCB.setItems(fontSizes);
        fontSizeCB.setValue(settings.getFontSize());
        fontSizeCB.setOnAction(e -> {
            FontSize value = fontSizeCB.getValue();
            settings.setFontSize(value);
        });
    }

    private void hookUpFullScreenEvents(CheckBox cb) {
        cb.setSelected(gameStage.isFullScreen());
        cb.setOnAction(event -> changeFullScreenSetting(cb.isSelected()));
    }

    private void changeFullScreenSetting(boolean isSelected) {
        settings.setFullScreen(isSelected);
        gameStage.setFullScreen(isSelected);
        Coords current = controller.getCurPos();
        current.x = 0;
        current.y = 0;
        controller.reloadInventoryPictures();
    }

    private void goBackToSettings(){
        root.setCenter(this);
    }
}
