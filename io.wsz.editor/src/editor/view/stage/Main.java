package editor.view.stage;

import editor.model.EditorController;
import io.wsz.model.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.File;
import java.net.URISyntaxException;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        Controller controller = new Controller();
        controller.initNewModel();
        EditorController editorController = new EditorController(controller);
        editorController.initNewPlugin();

        File programDir = getProgramDir();
        controller.setProgramDir(programDir);

        MainView view = new MainView(stage, editorController);
        view.show();
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

    public static void main(String[] args) {
        Application.launch();
    }
}