package editor.view.dialog.requirement;

import io.wsz.model.script.bool.BooleanExpression;
import javafx.scene.layout.HBox;

public abstract class SpecificRequirement {

    public abstract BooleanExpression<?> getExpression();

    public abstract void populate(BooleanExpression<?> expression);

    public abstract HBox getElements();
}
