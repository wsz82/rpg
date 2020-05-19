package editor.view.stage;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import model.location.CurrentLocation;

class CurrentLocationBox extends HBox {

    CurrentLocationBox() {
        super();
        final Label layerText = new Label("Location: ");
        final Label currentLocationName = new Label();

        getChildren().addAll(layerText, currentLocationName);
        setSpacing(5);
        currentLocationName.textProperty().bind(CurrentLocation.get().currentNameProperty());
    }
}
