package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.sizes.FontSize;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

class SettingsMenu extends StackPane {
    private StackPane graphics;
    private Node parentToReturn;
    private BorderPane root;
    private final GameStage gameStage;

    public SettingsMenu(GameStage gameStage){
        this.gameStage = gameStage;
        VBox buttons = new VBox(10);
        buttons.setAlignment(Pos.CENTER);

        Button graphics = new Button("Graphics");
        graphics.setOnAction(event -> openGraphicsSettings());
        Button back = new Button("Back");
        back.setOnAction(event -> goBackToMenu());

        buttons.getChildren().addAll(graphics, back);
        getChildren().addAll(buttons);
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

        final ChoiceBox<FontSize> fontSizeCB = new ChoiceBox<>();
        hookUpFontSizeEvents(fontSizeCB);

        Button back = new Button("Back");
        back.setOnAction(event -> goBackToSettings());

        settings.getChildren().addAll(fullScreen, fontSizeCB, back);
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
