package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.stage.Coords;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
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

        VBox settings = new VBox(10);
        settings.setAlignment(Pos.CENTER);
        CheckBox fullScreen = new CheckBox("Full screen");
        fullScreen.setSelected(gameStage.isFullScreen());
        fullScreen.setOnAction(event -> {
            changeFullScreenSetting(fullScreen.isSelected()
            );
        });

        Button back = new Button("Back");
        back.setOnAction(event -> goBackToSettings());

        settings.getChildren().addAll(fullScreen, back);
        graphics.getChildren().addAll(settings);
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
