package game.view.stage;

import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;

public class DialogView extends BorderPane {

    public DialogView() {
        initWindow();
    }

    private void initWindow() {
        final ScrollPane center = new ScrollPane();
        setCenter(center);

        
    }
}
