package game.view.launcher;

import game.model.GameController;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        GameController gameController = new GameController();
        File programDir = getProgramDir();
        gameController.setProgramDir(programDir);
        gameController.initNewModel();
        gameController.restoreSettings();
        Launcher launcher = new Launcher(stage);
        launcher.launch(gameController);
    }

    public static void main(String[] args) {
        launch();
    }

    private File getProgramDir() {
        String path = System.getProperty("user.home");
        try {
            path = new File(
                    this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath().toLowerCase())
                    .getParent();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        new File(path).mkdir();
        return new File(path);
    }
}
