import javafx.application.Application;
import javafx.stage.Stage;

public class EditorMain extends Application {

    @Override
    public void start(Stage stage) {
        EditorMainView view = new EditorMainView(stage);
        view.show();
    }

    public static void main(String[] args) {
        launch();
    }

}