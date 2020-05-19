package game.view.launcher;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        new Launcher(stage);
    }

    public static void main(String[] args) {
        launch();
    }
}
