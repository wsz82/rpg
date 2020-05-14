package view.stage;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.location.CurrentLocation;

public class Board extends AnchorPane {

    Board() {
        this.prefWidthProperty().bindBidirectional(
                CurrentLocation.get().currentWidthProperty());
        this.prefHeightProperty().bindBidirectional(
                CurrentLocation.get().currentHeightProperty());
        BorderStroke[] strokes = new BorderStroke[]{
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)};
        this.setBorder(new Border(strokes));
    }
}
