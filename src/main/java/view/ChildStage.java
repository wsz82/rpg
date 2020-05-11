package view;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

class ChildStage extends Stage {
    ChildStage(Stage parent, String title) {
        super(StageStyle.UTILITY);
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 100, 100);
        this.initOwner(parent);
        this.setTitle(title);
        this.setAlwaysOnTop(true);
        this.setScene(scene);
    }
}
