package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

class SettingsMenu extends StackPane {
    private final EventHandler<KeyEvent> settingsReturn = event -> {
        event.consume();
        if (event.getCode() == KeyCode.ESCAPE) {
            goBackToSettings();
        }
    };
    private final GameStage gameStage;
    private Scene scene;
    private EventHandler<KeyEvent> menuReturn;
    private StackPane graphicsRoot;

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
        menuReturn.handle(new KeyEvent(this, null, KeyEvent.KEY_RELEASED, "", "", KeyCode.ESCAPE,
                false, false, false, false));
    }

    private void openGraphicsSettings() {
        gameStage.removeEventHandler(KeyEvent.KEY_RELEASED, menuReturn);
        gameStage.addEventHandler(KeyEvent.KEY_RELEASED, settingsReturn);
        if (graphicsRoot == null) {
            initGraphicsSettings();
        }
        scene.setRoot(graphicsRoot);
    }

    private void initGraphicsSettings() {
        graphicsRoot = new StackPane();

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
        graphicsRoot.getChildren().addAll(settings);
    }

    private void changeFullScreenSetting(boolean isSelected) {
        gameStage.setFullScreen(isSelected);
        Coords current = Controller.get().getBoardPos();
        current.x = 0;
        current.y = 0;
    }

    void goBackToSettings(){
        gameStage.removeEventHandler(KeyEvent.KEY_RELEASED, settingsReturn);
        gameStage.addEventHandler(KeyEvent.KEY_RELEASED, menuReturn);
        scene.setRoot(this);
    }

    void open(Scene scene, EventHandler<KeyEvent> menuReturn){
        gameStage.addEventHandler(KeyEvent.KEY_RELEASED, menuReturn);
        this.scene = scene;
        this.menuReturn = menuReturn;
        scene.setRoot(this);
    }
}
