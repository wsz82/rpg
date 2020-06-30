package game.view.stage;

import game.model.GameController;
import io.wsz.model.Controller;
import io.wsz.model.item.Creature;
import io.wsz.model.item.CreatureControl;
import io.wsz.model.location.Location;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BarView {
    private static final double RIGHT_VIEW_PART = 0.08;
    private static final double PORTRAIT_PART = 0.9;
    private final LinkedList<CreatureLocation> portraits = new LinkedList<>();
    private final Canvas canvas;
    private final GraphicsContext gc;
    private int hoveredPortrait;

    public BarView(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        hookupEvents();
    }

    private void hookupEvents() {
        EventHandler<MouseEvent> clickEvent = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                e.consume();
                if (hoveredPortrait != -1) {
                    synchronized (GameController.get().getGameRunner()) {
                        resolveHeroControlAndLocation(e.isShiftDown());
                    }
                }
            }
        };
        canvas.addEventHandler(MouseEvent.MOUSE_CLICKED, clickEvent);

        EventHandler<KeyEvent> keyboardEvent = e -> {
            KeyCode key = e.getCode();
            boolean multiple = e.isShiftDown();
            switch (key) {
                case DIGIT1 -> handleHeroEventAndConsume(e, 0, multiple);
                case DIGIT2 -> handleHeroEventAndConsume(e, 1, multiple);
                case DIGIT3 -> handleHeroEventAndConsume(e, 2, multiple);
                case DIGIT4 -> handleHeroEventAndConsume(e, 3, multiple);
                case DIGIT5 -> handleHeroEventAndConsume(e, 4, multiple);
                case DIGIT6 -> handleHeroEventAndConsume(e, 5, multiple);
            }
        };
        canvas.addEventHandler(KeyEvent.KEY_RELEASED, keyboardEvent);
    }

    private void handleHeroEventAndConsume(KeyEvent e, int i, boolean multiple) {
        e.consume();
        if (i >= portraits.size()) {
            return;
        }
        if (!multiple) {
            Controller.get().getBoard().looseCreaturesControl();
        }
        resolveCreatureControlAndLocation(portraits.get(i));
    }

    private void resolveHeroControlAndLocation(boolean multiple) {
        if (!multiple){
            Controller.get().getBoard().looseCreaturesControl();
        }
        CreatureLocation cl = portraits.get(hoveredPortrait);
        resolveCreatureControlAndLocation(cl);
    }

    private void resolveCreatureControlAndLocation(CreatureLocation cl) {
        if (cl == null) {
            return;
        }
        Creature cr = cl.creature;
        CreatureControl control = cr.getControl();
        if (control == CreatureControl.CONTROLLABLE) {
            Controller.get().getCreaturesToControl().add(cr);
        } else {
            Controller.get().getCreaturesToLooseControl().add(cr);
        }
        Location current = Controller.get().getCurrentLocation().getLocation();
        Location heroLocation = cl.location;
        if (current != heroLocation) {
            Controller.get().setLocationToUpdate(heroLocation);
        }
    }

    public void refresh() {
        double canvasWidth = canvas.getWidth();
        double barWidth = canvasWidth*RIGHT_VIEW_PART;
        double leftX = canvasWidth - barWidth;

        drawBackground(leftX, barWidth);

        double portraitSize = barWidth*PORTRAIT_PART;
        double padding = (barWidth-portraitSize) / 2;

        drawHeroes(leftX, padding, portraitSize);

        checkPos(leftX, padding, portraitSize);

        updateHoveredPortrait(leftX, padding, portraitSize);

        updateActivePortrait(leftX, padding, portraitSize);
    }

    private void updateActivePortrait(double leftX, double padding, double portraitSize) {
        for (CreatureLocation cl : portraits) {
            Creature hero = cl.creature;
            if (hero.getControl().equals(CreatureControl.CONTROL)) {
                double portraitY = cl.y;
                gc.setStroke(Color.GREEN);
                gc.setLineWidth(2);
                gc.strokeRect(leftX + padding, portraitY, portraitSize, portraitSize);
            }
        }
    }

    private void updateHoveredPortrait(double leftX, double padding, double portraitSize) {
        if (hoveredPortrait == -1) {
            return;
        }
        CreatureLocation cl = portraits.get(hoveredPortrait);
        double portraitY = cl.y;
        gc.setStroke(Color.LIGHTGREY);
        gc.setLineWidth(2);
        gc.strokeRect(leftX + padding, portraitY, portraitSize, portraitSize);
    }

    private void checkPos(double leftOfView, double padding, double portraitSize) {
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
            hoveredPortrait = -1;
            return;
        }

        double portraitLeft = left + padding;
        double portraitRight = portraitLeft + portraitSize;

        if (x < portraitLeft || x > portraitRight) {
            hoveredPortrait = -1;
            return;
        }

        if (!pointWithinPortraits(portraitSize, y, top)) {
            hoveredPortrait = -1;
        }
    }

    private boolean pointWithinPortraits(double portraitSize, double y, double top) {
        for (int i = 0; i < portraits.size(); i++) {
            CreatureLocation cl = portraits.get(i);
            double portraitY = cl.y;
            double screenY = top + portraitY;
            if (y > screenY && y < screenY + portraitSize) {
                if (hoveredPortrait != i) {
                    hoveredPortrait = i;
                }
                return true;
            }
        }
        return false;
    }

    private void drawHeroes(double leftX, double padding, double portraitSize) {
        Map<Creature, Location> heroes = Controller.get().getHeroes();

        double y = padding;
        double portraitX = leftX + padding;

        portraits.clear();
        List<Creature> creatures = new ArrayList<>(heroes.keySet());
        for (int i = 0; i < creatures.size(); i++) {
            Creature cr = creatures.get(i);
            gc.setFill(Color.DARKVIOLET);
            gc.fillRect(portraitX, y, portraitSize, portraitSize);

            if (cr != null) {
                Image raw = cr.getPortrait();
                ImageView iv = new ImageView(raw);
                iv.setFitWidth(portraitSize);
                iv.setFitHeight(portraitSize);

                SnapshotParameters sp = new SnapshotParameters();
                sp.setFill(Color.TRANSPARENT);
                Image img = iv.snapshot(sp, null);

                CreatureLocation cl = new CreatureLocation(cr, heroes.get(cr), y);
                portraits.add(i, cl);

                gc.drawImage(img, portraitX, y);
            }

            y += portraitSize + 2*padding;
        }
    }

    private void drawBackground(double leftX, double barWidth) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(leftX, 0, barWidth, canvas.getHeight());
    }

    public double getLeft() {
        double canvasWidth = canvas.getWidth();
        double barWidth = canvasWidth*RIGHT_VIEW_PART;
        return canvasWidth - barWidth;
    }

    private class CreatureLocation {
        private final Creature creature;
        private final Location location;
        private final double y;

        public CreatureLocation(Creature creature, Location location, double y) {
            this.creature = creature;
            this.location = location;
            this.y = y;
        }
    }
}
