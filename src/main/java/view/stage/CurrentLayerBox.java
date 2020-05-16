package view.stage;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CurrentLayerBox extends HBox {
    private static final Label currentLayerNumber = new Label();

    CurrentLayerBox() {
        super();
        final Label layerText = new Label("Layer: ");

        getChildren().addAll(layerText, currentLayerNumber);
        setSpacing(5);
        currentLayerNumber.setText("" + 0);
    }

    public static void setCurrentLayerNumber(int number) {
        currentLayerNumber.setText("" + number);
    }
}
