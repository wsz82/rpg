package game.view.launcher;

import game.model.GameController;
import game.view.menu.GameStage;
import io.wsz.model.locale.LocaleKeys;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.Properties;

class Launcher {
    private final Stage stage;

    public Launcher(Stage stage) {
        this.stage = stage;
    }

    public void launch(GameController gameController) {
        initLauncher(gameController);
        restorePluginMemento(gameController);
    }

    private void restorePluginMemento(GameController gameController) {
        gameController.restoreLastPluginMetadata();
    }

    private void initLauncher(GameController gameController) {
        final StackPane root = new StackPane();
        root.setPadding(new Insets(10));

        Properties locale = gameController.getController().getLocale();
        final VBox menu = new VBox(10);
        final Button play = new Button(locale.getProperty(LocaleKeys.START_GAME));
        play.setOnAction(event -> play(gameController));
        final Button plugins = new Button(locale.getProperty(LocaleKeys.PLUGINS));
        plugins.setOnAction(event -> openPluginsTable(gameController));
        final Button exit = new Button(locale.getProperty(LocaleKeys.EXIT));
        exit.setOnAction(event -> exit());
        menu.getChildren().addAll(play, plugins, exit);
        menu.setAlignment(Pos.CENTER);
        root.getChildren().addAll(menu);

        final Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void play(GameController gameController) {
        final GameStage gameStage = new GameStage(gameController);
        gameStage.init();
        gameStage.show();
        stage.close();
    }

    private void openPluginsTable(GameController gameController) {
        final Stage plugins = new GamePluginsTable(gameController);
        plugins.initOwner(stage);
        plugins.show();
    }

    private void exit() {
        stage.close();
    }
}
