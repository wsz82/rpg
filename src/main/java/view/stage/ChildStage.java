package view.stage;

import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ChildStage extends Stage {
    protected final Stage parent;

    public ChildStage(Stage parent) {
        super(StageStyle.UTILITY);
        this.parent = parent;
    }
}
