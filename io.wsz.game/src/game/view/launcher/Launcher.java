package game.view.launcher;

import game.model.GameController;
import game.view.stage.GameStage;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
        gameController.restoreLastPlugin();
    }

    private void initLauncher(GameController gameController) {
        StackPane root = new StackPane();

        VBox menu = new VBox(10);
        Button play = new Button("Play");
        play.setOnAction(event -> play(gameController));
        Button plugins = new Button("Plugins");
        plugins.setOnAction(event -> openPluginsTable(gameController));
        Button exit = new Button("Exit");
        exit.setOnAction(event -> exit());
        menu.getChildren().addAll(play, plugins, exit);
        menu.setAlignment(Pos.CENTER);
        root.getChildren().addAll(menu);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void play(GameController gameController) {
        GameStage gameStage = new GameStage(gameController);
        gameStage.open();
        stage.close();
    }

    private void openPluginsTable(GameController gameController) {
        Stage plugins = new GamePluginsTable(gameController);
        plugins.initOwner(stage);
        plugins.show();
    }

    private void exit() {
        stage.close();
    }
}
