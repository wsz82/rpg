package editor.view.stage;

import io.wsz.model.location.CurrentLocation;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

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
