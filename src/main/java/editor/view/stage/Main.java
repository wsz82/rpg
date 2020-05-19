package editor.view.stage;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Controller;

import java.io.File;
import java.net.URISyntaxException;

public class Main extends Application {
    public static Stage mainStage;

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        Controller.get().initNewPlugin();

        File programDir = getProgramDir();

        MainView view = new MainView(stage);
        view.show(programDir);
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

    public static void main(String[] args) {
        launch();
    }

    public static Stage getStage() {
        return mainStage;
    }
}