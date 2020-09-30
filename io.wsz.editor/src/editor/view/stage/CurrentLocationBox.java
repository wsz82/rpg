package editor.view.stage;

import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

class CurrentLocationBox extends HBox {

    private final StringProperty idProperty;

    public CurrentLocationBox(StringProperty idProperty) {
        this.idProperty = idProperty;
    }

    public void initBox() {
        final Label layerText = new Label("Location: ");
        final Label currentLocationName = new Label();

        getChildren().addAll(layerText, currentLocationName);
        setSpacing(5);
        currentLocationName.textProperty().bind(this.idProperty);
    }
}
