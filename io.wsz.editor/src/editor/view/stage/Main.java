package editor.view.stage;

import editor.model.EditorController;
import io.wsz.model.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;

public class Main extends Application {
    private static Stage mainStage;
    private static File programDir;

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        EditorController.get().initNewPlugin();

        setProgramDir();
        Controller.setProgramDir(programDir);

        MainView view = new MainView(stage);
        view.show();
    }

    private void setProgramDir() {
        String path = System.getProperty("user.home");
        try {
            path = new File(
                    this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath().toLowerCase())
                    .getParent();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        new File(path).mkdir();
        programDir = new File(path);
    }

    public static void main(String[] args) {
        Application.launch();
    }

    public static Stage getStage() {
        return mainStage;
    }

    public static File getDir() {
        return programDir;
    }
}