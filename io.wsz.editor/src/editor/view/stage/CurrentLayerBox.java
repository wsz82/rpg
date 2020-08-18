package editor.view.stage;

import io.wsz.model.Controller;
import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

class CurrentLayerBox extends HBox {

    CurrentLayerBox(Controller controller) {
        super();
        final Label layerText = new Label("Layer: ");
        final Label currentLayerNumber = new Label();

        getChildren().addAll(layerText, currentLayerNumber);
        setSpacing(5);
        Bindings.bindBidirectional(currentLayerNumber.textProperty(), controller.getCurrentLayer().levelProperty(),
                new StringConverter<>() {
                    @Override
                    public String toString(Number object) {
                        return "" + object.intValue();
                    }

                    @Override
                    public Number fromString(String string) {
                        return 0;
                    }
                });
    }
}
