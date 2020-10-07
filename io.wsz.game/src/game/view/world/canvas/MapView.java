package game.view.world.canvas;

import game.model.GameController;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class MapView {
    protected final Canvas canvas;
    protected final GraphicsContext gc;
    protected final GameController controller;

    public MapView(Canvas canvas, GameController controller) {
        this.canvas = canvas;
        this.controller = controller;
        gc = canvas.getGraphicsContext2D();
    }

    
}
