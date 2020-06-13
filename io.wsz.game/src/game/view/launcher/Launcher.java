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

    Launcher(Stage stage) {
        this.stage = stage;
        initLauncher();
        restorePluginMemento();
    }

    private void restorePluginMemento() {
        GameController.get().restoreLastPlugin();
    }

    private void initLauncher() {
        StackPane root = new StackPane();

        VBox menu = new VBox(10);
        Button play = new Button("Play");
        play.setOnAction(event -> play());
        Button plugins = new Button("Plugins");
        plugins.setOnAction(event -> openPluginsTable());
        Button exit = new Button("Exit");
        exit.setOnAction(event -> exit());
        menu.getChildren().addAll(play, plugins, exit);
        menu.setAlignment(Pos.CENTER);
        root.getChildren().addAll(menu);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void play() {
        GameStage gameStage = new GameStage();
        gameStage.open();
        stage.close();
    }

    private void openPluginsTable() {
        Stage plugins = new GamePluginsTable();
        plugins.initOwner(stage);
        plugins.show();
    }

    private void exit() {
        stage.close();
    }
}
