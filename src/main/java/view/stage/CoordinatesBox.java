package view.stage;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import model.stage.Coordinates;

class CoordinatesBox extends HBox {
    private final Label mouseX = new Label();
    private final Label mouseY = new Label();
    private final Region root;

    CoordinatesBox(Region region) {
        this.root = region;
        this.create();
    }

    private void create() {
        root.setOnMouseMoved(event -> {
            Coordinates coordinates = new Coordinates(root, event);
            mouseX.setText("X: " + coordinates.getX());
            mouseY.setText("Y: " + coordinates.getY());
        });

        this.getChildren().addAll(mouseX, mouseY);
        this.setAlignment(Pos.BOTTOM_LEFT);
        this.setSpacing(20);
    }
}
