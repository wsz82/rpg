package game.view.launcher;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        File programDir = getProgramDir();
        new Launcher(stage, programDir);
    }

    public static void main(String[] args) {
        launch();
    }

    private File getProgramDir() {
        String path = System.getProperty("user.home");
        try {
            path = new File(
                    this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                    .getParent();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        new File(path).mkdir();
        return new File(path);
    }
}
