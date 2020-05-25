package editor.view.stage;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Controller;

import java.io.File;
import java.net.URISyntaxException;

public class Main extends Application {
    private static Stage mainStage;
    private static File programDir;

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        Controller.get().initNewPlugin();

        setProgramDir();

        MainView view = new MainView(stage);
        view.show();
    }

    private void setProgramDir() {
        String path = System.getProperty("user.home");
        try {
            path = new File(
                    this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath())
                    .getParent();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        new File(path).mkdir();
        programDir = new File(path);
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getStage() {
        return mainStage;
    }

    public static File getDir() {
        return programDir;
    }
}