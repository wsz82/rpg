package game.view.launcher;

import editor.view.stage.Main;
import game.model.GameController;
import game.view.stage.GameMenu;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Controller;
import model.plugin.Plugin;

import java.io.File;

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
        Button chooseGame = new Button("Choose game");
        chooseGame.setOnAction(event -> choosePlugin());
        Button exit = new Button("Exit");
        exit.setOnAction(event -> exit());
        menu.getChildren().addAll(play, chooseGame, exit);
        menu.setAlignment(Pos.CENTER);
        root.getChildren().addAll(menu);

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void play() {
        if (Controller.get().getActivePlugin() == null) {
            Controller.get().initNewPlugin();
        }
        GameMenu gameMenu = GameMenu.get();
        gameMenu.open();
        stage.close();
    }

    private void choosePlugin() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose plugin");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plugin file", "*.rpg"));
        File loadedFile = fileChooser.showOpenDialog(Main.getStage());

        if (loadedFile == null) {
            return;
        }
        Plugin plugin = Controller.get().loadPlugin(loadedFile);
        Controller.get().setActivePlugin(plugin);
        GameController.get().storeLastPlugin(plugin);
    }

    private void exit() {
        stage.close();
    }
}
