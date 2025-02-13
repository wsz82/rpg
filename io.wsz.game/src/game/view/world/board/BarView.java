package game.view.world.board;

import game.model.GameController;
import game.model.world.GameRunner;
import io.wsz.model.animation.creature.PortraitAnimation;
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
    private final GameController controller;
    private final GraphicsContext gc;
    private final LinkedList<Portrait> portraits = new LinkedList<>();
    private final List<Creature> creatures = new ArrayList<>(6);

    private double width;
    private int hoveredPortrait;
    private double lastPortraitSize;

    public BarView(Canvas canvas, GameController controller) {
        this.canvas = canvas;
        this.controller = controller;
        this.gc = canvas.getGraphicsContext2D();
        hookUpEvents();
    }

    public void refresh() {
        if (!controller.getSettings().isShowBar()) {
            return;
        }
        forceRefresh();
    }

    public void forceRefresh() {
        double canvasWidth = canvas.getWidth();
        if (canvasWidth == 0) {
            return;
        }

        double canvasHeight = canvas.getHeight();
        double portraitSize = 0.08*canvasWidth*PORTRAIT_PART;
        if (portraitSize*9 > canvasHeight) {
            portraitSize = canvasHeight/9;
        }

        width = portraitSize * 1.2;
        double leftX = canvasWidth - width;
        drawBackground(leftX, width);

        if (lastPortraitSize != portraitSize) {
            lastPortraitSize = portraitSize;
            Sizes.setPortraitSize((int) portraitSize);
            controller.reloadHeroesPortraits();
        }
        double padding = portraitSize / 10;

        drawHeroes(leftX, padding, portraitSize);

        checkPos(leftX, padding, portraitSize);

        updateHoveredPortrait(leftX, padding, portraitSize);

        updateActivePortrait(leftX, padding, portraitSize);
    }

    private void hookUpEvents() {
        EventHandler<MouseEvent> clickEvent = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.PRIMARY)) {
                e.consume();
                if (hoveredPortrait != -1 && controller.getDragged() == null) {
                    int hoveredPortraitIndex = hoveredPortrait;
                    GameRunner.runLater(() -> resolveHeroControlAndLocation(e.isShiftDown(), hoveredPortraitIndex));
                }
            }
        };
        canvas.addEventHandler(MouseEvent.MOUSE_RELEASED, clickEvent);

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
            Location location = controller.getCurrentLocation();
            controller.getBoard().looseCreaturesControl(location);
        }
        resolveCreatureControlAndLocation(portraits.get(i));
    }

    private void resolveHeroControlAndLocation(boolean multiple, int hoveredPortrait) {
        if (!multiple){
            Location location = controller.getCurrentLocation();
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
        Location current = controller.getCurrentLocation();
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
            if (controller.getHoveredHero() != null) {
                controller.setHoveredHero(null);
            }
            return;
        }
        Portrait cl = portraits.get(hoveredPortrait);
        double portraitY = cl.y;
        if (portraitY == 0) {
            return;
        }

        Creature hero = cl.creature;
        Creature hoveredHero = controller.getHoveredHero();
        if (hoveredHero != hero) {
            controller.setHoveredHero(hero);
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

        if (!isPointWithinPortraits(portraitSize, y, top)) {
            hoveredPortrait = -1;
        }
    }

    private boolean isPointWithinPortraits(double portraitSize, double y, double top) {
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
            PortraitAnimation animation = cr.getPortraitAnimation();
            animation.play(cr);
            ResolutionImage animationPortrait = cr.getPortrait();
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
        gc.setFill(Color.GREY);
        gc.fillRect(leftX, 0, barWidth, canvas.getHeight());
    }

    public double getLeft() {
        double canvasWidth = canvas.getWidth();
        return canvasWidth - width;
    }

    public double getWidth() {
        return width;
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
