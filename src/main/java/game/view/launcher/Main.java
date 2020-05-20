package game.view.launcher;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;

public class Main extends Application {
    private static File programDir;

    @Override
    public void start(Stage stage) {
        programDir = getProgramDir();
        new Launcher(stage);
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

    public static File getDir() {
        return programDir;
    }
}
