package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.location.Location;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.Map;

public class BarView {
    private static final double RIGHT_VIEW_PART = 0.08;
    private static final double PORTRAIT_PART = 0.9;
    private final Canvas canvas;
    private final GraphicsContext gc;

    public BarView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
    }

    public void refresh() {
        double canvasWidth = canvas.getWidth();
        double barWidth = canvasWidth*RIGHT_VIEW_PART;
        double leftX = canvasWidth - barWidth;
        double portraitSize = barWidth*PORTRAIT_PART;

        drawBackground(leftX, barWidth);

        drawHeroes(leftX, barWidth, portraitSize);
    }

    private void drawHeroes(double leftX, double barWidth, double portraitSize) {
        Map<Creature, Location> heroes = Controller.get().getHeroes();

        double padding = (barWidth-portraitSize) / 2;
        double y = padding;
        double portraitX = leftX + padding;

        for (Creature cr : heroes.keySet()) {
            gc.setFill(Color.DARKVIOLET);
            gc.fillRect(portraitX, y, portraitSize, portraitSize);

            Image raw = cr.getPortrait();
            ImageView iv = new ImageView(raw);
            iv.setFitWidth(portraitSize);
            iv.setFitHeight(portraitSize);

            SnapshotParameters sp = new SnapshotParameters();
            sp.setFill(Color.TRANSPARENT);
            Image img = iv.snapshot(sp, null);

            gc.drawImage(img, portraitX, y);

            y += portraitSize + 2*padding;
        }
    }

    private void drawBackground(double leftX, double barWidth) {
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(leftX, 0, barWidth, canvas.getHeight());
    }
}
