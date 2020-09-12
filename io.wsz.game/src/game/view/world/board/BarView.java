package game.view.world.board;

import game.model.GameController;
import game.model.world.GameRunner;
import io.wsz.model.Controller;
import io.wsz.model.animation.creature.CreatureAnimation;
import io.wsz.model.item.Creature;
import io.wsz.model.item.CreatureControl;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.ResolutionImage;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class BarView {
    private static final double PORTRAIT_PART = 9.0/10;

    private final Canvas canvas;
    private final GameController gameController;
    private final Controller controller;
    private final GraphicsContext gc;
    private final LinkedList<Portrait> portraits = new LinkedList<>();
    private final List<Creature> creatures = new ArrayList<>(6);
    private int hoveredPortrait;

    public BarView(Canvas canvas, GameController gameController) {
        this.canvas = canvas;
        this.gameController = gameController;
        controller = gameController.getController();
        this.gc = canvas.getGraphicsContext2D();
        hookupEvents();
    }

    public void refresh() {
        double canvasWidth = canvas.getWidth();
        if (canvasWidth == 0) {
            return;
        }
        double barWidth = getBarWidth(canvasWidth);
        double leftX = canvasWidth - barWidth;

        drawBackground(leftX, barWidth);

        double canvasHeight = canvas.getHeight();
        double portraitSize = barWidth*PORTRAIT_PART;
        if (portraitSize*9 > canvasHeight) {
            portraitSize = canvasHeight/9;
        }
        Sizes.setPortraitSize((int) portraitSize);
        double padding = portraitSize / 10;

        drawHeroes(leftX, padding, portraitSize);

        checkPos(leftX, padding, portraitSize);

        updateHoveredPortrait(leftX, padding, portraitSize);

        updateActivePortrait(leftX, padding, portraitSize);
    }

    private void hookupEvents() {
        EventHandler<MouseEvent> clickEvent = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                e.consume();
                if (hoveredPortrait != -1) {
                    int hoveredPortraitIndex = hoveredPortrait;
                    GameRunner.runLater(() -> resolveHeroControlAndLocation(e.isShiftDown(), hoveredPortraitIndex));
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
            Location location = controller.getCurrentLocation().getLocation();
            controller.getBoard().looseCreaturesControl(location);
        }
        resolveCreatureControlAndLocation(portraits.get(i));
    }

    private void resolveHeroControlAndLocation(boolean multiple, int hoveredPortrait) {
        if (!multiple){
            Location location = controller.getCurrentLocation().getLocation();
            controller.getBoard().looseCreaturesControl(location);
        }
        if (portraits.isEmpty()) return;
        Portrait cl = portraits.get(hoveredPortrait);
        resolveCreatureControlAndLocation(cl);
    }

    private void resolveCreatureControlAndLocation(Portrait cl) {
        if (cl == null) {
            return;
        }
        Creature cr = cl.creature;
        if (cr == null) {
            return;
        }

        controller.setCreatureToOpenInventory(cr);

        CreatureControl control = cr.getControl();
        if (control == CreatureControl.CONTROLLABLE) {
            controller.getCreaturesToControl().add(cr);
        } else {
            controller.getCreaturesToLooseControl().add(cr);
        }
        Location current = controller.getCurrentLocation().getLocation();
        Location heroLocation = cr.getPos().getLocation();
        if (current != heroLocation) {
            controller.setLocationToUpdate(heroLocation);
        }
        controller.setPosToCenter(cr.getCenter());
    }

    private void updateActivePortrait(double leftX, double padding, double portraitSize) {
        for (Portrait cl : portraits) {
            Creature hero = cl.creature;
            if (hero == null) {
                return;
            }
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
            if (gameController.getHoveredHero() != null) {
                gameController.setHoveredHero(null);
            }
            return;
        }
        Portrait cl = portraits.get(hoveredPortrait);
        double portraitY = cl.y;
        if (portraitY == 0) {
            return;
        }

        Creature hero = cl.creature;
        Creature hoveredHero = gameController.getHoveredHero();
        if (hoveredHero != hero) {
            gameController.setHoveredHero(hero);
        }

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
            Portrait cl = portraits.get(i);
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
        double y = padding;
        double portraitX = leftX + padding;

        clearPortraits(padding, portraitSize, y, portraitX);

        creatures.clear();
        creatures.addAll(controller.getHeroes());
        int dif = creatures.size() - portraits.size();
        if (dif > 0) {
            for (int i = 0; i < dif; i++) {
                portraits.add(i, new Portrait(null, null, 0));
            }
        }
        for (int i = 0; i < creatures.size(); i++) {
            Portrait portrait = portraits.get(i);
            Creature cr = creatures.get(i);

            portrait.creature = cr;
            CreatureAnimation animation = cr.getAnimation();
            ResolutionImage animationPortrait = animation.getPortrait(cr);
            if (animationPortrait != null) {
                Image fxImage = animationPortrait.getFxImage();
                if (fxImage != null) {
                    portrait.image = fxImage;
                }
            }
            portrait.y = y;
            gc.drawImage(portrait.image, portraitX, y);

            y += portraitSize + 2*padding;
        }
    }

    private void clearPortraits(double padding, double portraitSize, double y, double portraitX) {
        for (int i = 0; i < portraits.size(); i++) {
            gc.setFill(Color.DARKVIOLET);
            gc.fillRect(portraitX, y, portraitSize, portraitSize);
            y += portraitSize + 2*padding;
        }
    }

    private void drawBackground(double leftX, double barWidth) {
        gc.setFill(Color.DARKGRAY);
        gc.fillRect(leftX, 0, barWidth, canvas.getHeight());
    }

    public double getLeft() {
        double canvasWidth = canvas.getWidth();
        double barWidth = getBarWidth(canvasWidth);
        return canvasWidth - barWidth;
    }

    public double getBarWidth(double canvasWidth) {
        return canvasWidth * gameController.getSettings().getBarPart();
    }

    private class Portrait {
        private Creature creature;
        private Image image;
        private double y;

        public Portrait(Creature creature, Image image, double y) {
            this.creature = creature;
            this.image = image;
            this.y = y;
        }
    }
}
