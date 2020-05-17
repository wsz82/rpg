package view.stage;

import javafx.application.Application;
import javafx.stage.Stage;
import model.Controller;

public class Main extends Application {
    public static Stage mainStage;

    @Override
    public void start(Stage stage) {
        mainStage = stage;
        initLocation();
        MainView view = new MainView(stage);
        view.show();
    }

    private void initLocation() {
        Controller.get().initNewPlugin();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getStage() {
        return mainStage;
    }
}