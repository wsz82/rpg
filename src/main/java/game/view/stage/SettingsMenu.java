package game.view.stage;

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
    private static SettingsMenu singleton;
    private final EventHandler<KeyEvent> settingsReturn = event -> {
        event.consume();
        if (event.getCode() == KeyCode.ESCAPE) {
            goBackToSettings();
        }
    };
    private Scene scene;
    private EventHandler<KeyEvent> menuReturn;
    private StackPane graphicsRoot;

    static SettingsMenu get() {
        if (singleton == null) {
            singleton = new SettingsMenu();
        }
        return singleton;
    }

    private SettingsMenu(){
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
        Game.get().removeEventHandler(KeyEvent.KEY_RELEASED, menuReturn);
        Game.get().addEventHandler(KeyEvent.KEY_RELEASED, settingsReturn);
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
        fullScreen.setSelected(Game.get().isFullScreen());
        fullScreen.setOnAction(event -> {
            Game.get().setFullScreen(fullScreen.isSelected());
        });

        Button back = new Button("Back");
        back.setOnAction(event -> goBackToSettings());

        settings.getChildren().addAll(fullScreen, back);
        graphicsRoot.getChildren().addAll(settings);
    }

    void goBackToSettings(){
        Game.get().removeEventHandler(KeyEvent.KEY_RELEASED, settingsReturn);
        Game.get().addEventHandler(KeyEvent.KEY_RELEASED, menuReturn);
        scene.setRoot(this);
    }

    void open(Scene scene, EventHandler<KeyEvent> menuReturn){
        Game.get().addEventHandler(KeyEvent.KEY_RELEASED, menuReturn);
        this.scene = scene;
        this.menuReturn = menuReturn;
        scene.setRoot(this);
    }
}
