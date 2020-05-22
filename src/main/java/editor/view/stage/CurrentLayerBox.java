package editor.view.stage;

import javafx.beans.binding.Bindings;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import model.layer.CurrentLayer;

class CurrentLayerBox extends HBox {

    CurrentLayerBox() {
        super();
        final Label layerText = new Label("Layer: ");
        final Label currentLayerNumber = new Label();

        getChildren().addAll(layerText, currentLayerNumber);
        setSpacing(5);
        Bindings.bindBidirectional(currentLayerNumber.textProperty(), CurrentLayer.get().currentLevelProperty(),
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
