package editor.view.stage;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

class CurrentLayerBox extends HBox {
    private final IntegerProperty levelProperty;

    public CurrentLayerBox(IntegerProperty levelProperty) {
        this.levelProperty = levelProperty;
    }

    public void initBox() {
        final Label layerText = new Label("Layer: ");
        final Label currentLayerNumber = new Label();

        getChildren().addAll(layerText, currentLayerNumber);
        setSpacing(5);
        Bindings.bindBidirectional(currentLayerNumber.textProperty(), this.levelProperty,
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
