package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.location.Location;
import javafx.geometry.Bounds;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BarView {
    private static final double RIGHT_VIEW_PART = 0.08;
    private static final double PORTRAIT_PART = 0.9;
    private final Map<CreatureLocation, Double> portraitsPos = new HashMap<>(1);
    private final Canvas canvas;
    private final GraphicsContext gc;
    private CreatureLocation hoveredPortrait;
    private CreatureLocation activePortrait;

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

        double padding = (barWidth-portraitSize) / 2;

        drawHeroes(leftX, portraitSize, padding);

        checkPos(leftX, portraitSize, padding);

        updateHoveredPortrait(leftX, padding, portraitSize);

        updateActivePortrait(leftX, padding, portraitSize);
    }

    private void updateActivePortrait(double leftX, double padding, double portraitSize) {
        if (activePortrait == null) {
            return;
        }
        double portraitY = portraitsPos.get(activePortrait);
        gc.setStroke(Color.DARKGREEN);
        gc.setLineWidth(2);
        gc.strokeRect(leftX + padding, portraitY, portraitSize, portraitSize);
    }

    private void updateHoveredPortrait(double leftX, double padding, double portraitSize) {
        if (hoveredPortrait == null) {
            return;
        }
        double portraitY = portraitsPos.get(hoveredPortrait);
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(2);
        gc.strokeRect(leftX + padding, portraitY, portraitSize, portraitSize);
    }

    private void checkPos(double leftOfView, double portraitSize, double padding) {
        Bounds b = canvas.localToScreen(canvas.getBoundsInLocal());
        if (b == null) {
            return;
        }
        double left = b.getMinX() + leftOfView;
        double top = b.getMinY();
        double right = b.getMaxX();
        double bottom = b.getMaxY();

        Point p = MouseInfo.getPointerInfo().getLocation();
        int x = p.x;
        int y = p.y;

        if (x < left
                || x > right
                || y < top
                || y > bottom) {
            hoveredPortrait = null;
            return;
        }

        double portraitLeft = left + padding;
        double portraitRight = portraitLeft + portraitSize;

        if (x < portraitLeft || x > portraitRight) {
            hoveredPortrait = null;
            return;
        }

        if (!pointWithinPortraits(portraitSize, y, top)) {
            hoveredPortrait = null;
        }
    }

    private boolean pointWithinPortraits(double portraitSize, double y, double top) {
        for (CreatureLocation cl : portraitsPos.keySet()) {
            double portraitY = portraitsPos.get(cl);
            if (y > top + portraitY && y < top + portraitY + portraitSize) {
                if (hoveredPortrait != cl) {
                    hoveredPortrait = cl;
                }
                return true;
            }
        }
        return false;
    }

    private void drawHeroes(double leftX, double portraitSize, double padding) {
        Map<Creature, Location> heroes = Controller.get().getHeroes();

        double y = padding;
        double portraitX = leftX + padding;

        portraitsPos.clear();
        int i = 1;
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

            CreatureLocation cl = new CreatureLocation(cr, heroes.get(cr), i);
            portraitsPos.put(cl, y);

            gc.drawImage(img, portraitX, y);

            y += portraitSize + 2*padding;
            i++;
        }
    }

    private void drawBackground(double leftX, double barWidth) {
        gc.setFill(Color.SADDLEBROWN);
        gc.fillRect(leftX, 0, barWidth, canvas.getHeight());
    }

    private class CreatureLocation {
        private final Creature creature;
        private final Location location;
        private final int id;

        public CreatureLocation(Creature creature, Location location, int id) {
            this.creature = creature;
            this.location = location;
            this.id = id;
        }
    }
}
