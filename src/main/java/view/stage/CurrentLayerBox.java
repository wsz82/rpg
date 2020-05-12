package view.stage;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class CurrentLayerBox extends HBox {
    private final Label layerText = new Label("Layer: ");
    private static final Label currentLayerNumber = new Label();

    CurrentLayerBox() {
        super();
        this.getChildren().addAll(layerText, currentLayerNumber);
        this.setSpacing(5);
        currentLayerNumber.setText("" + 0);
    }

    public static void setCurrentLayerNumber(int number) {
        currentLayerNumber.setText("" + number);
    }
}
