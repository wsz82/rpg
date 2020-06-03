package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.content.ContentComparator;
import io.wsz.model.item.*;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GameCanvas extends Canvas {
    private static GameCanvas singleton;

    public static GameCanvas get() {
        if (singleton == null) {
            singleton = new GameCanvas();
        }
        return singleton;
    }

    private GameCanvas(){
        hookupEvents();
    }

    public void refresh() {
        setSize();
        GraphicsContext gc = getGraphicsContext2D();
        clear(gc);

        List<Content> contents = Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> c.getItem().getType() != ItemType.OBSTACLE)
                .filter(c -> c.isVisible())
                .filter(c -> c.getItem().getLevel() <= Controller.get().getCurrentLayer().getLevel())    //TODO
                .collect(Collectors.toList());
        for (Content content : contents) {
            final PosItem item = content.getItem();
            final ItemType type = content.getItem().getType();
            final Coords pos = item.getPos();
            final double x = pos.getX();
            final double y = pos.getY();

            if (content.isVisible()) {
                switch (type) {
                    case CREATURE -> {
                        checkPositionAvailability((Creature) item);
                        drawCreatureSize((Creature) item, gc);
                    }
                }
                gc.drawImage(item.getImage(), x, y);
            }
        }
    }

    private void checkPositionAvailability(Creature cr) {
        Coords[] poss = cr.getCorners();
        ItemType[] types = new ItemType[] {ItemType.OBSTACLE};
        Content c = lookForContent(poss, types, false);
        if (c != null) {
            cr.stop();
        }
    }

    private void drawCreatureSize(Creature cr, GraphicsContext gc) {
        CreatureControl control = cr.getControl();
        if (control != CreatureControl.CONTROL
                && control != CreatureControl.ENEMY) {
            return;
        }
        CreatureSize size = cr.getSize();
        Coords centerBottomPos = cr.getCenterBottomPos();
        double x = centerBottomPos.getX();
        double y = centerBottomPos.getY();
        switch (control) {
            case CONTROL -> gc.setStroke(Color.GREEN);
            case ENEMY -> gc.setStroke(Color.RED);
        }
        gc.setLineWidth(2);
        gc.strokeOval(x - size.getWidth()/2.0, y - size.getHeight()/2.0, size.getWidth(), size.getHeight());
    }

    private void hookupEvents() {
        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                e.consume();
                Coords pos = new Coords(e.getX(), e.getY());
                Coords[] poss = new Coords[] {pos};
                Content content = lookForContent(poss, null, true);
                if (content == null) {
                    return;
                }
                ItemType type = content.getItem().getType();
                switch (type) {
                    case CREATURE -> interactWith(content);
                }
            }
        });
    }

    private void interactWith(Content content) {
        Creature cr = (Creature) content.getItem();
        final EventHandler<MouseEvent> creatureMoveTo = e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                e.consume();
                CreatureControl control = cr.getControl();
                if (control == CreatureControl.NEUTRAL || control == CreatureControl.ENEMY) {
                    return;
                }
                if (control == CreatureControl.CONTROLABLE) {
                    cr.setControl(CreatureControl.CONTROL);
                }
                Coords rawPos = new Coords(e.getX(), e.getY());
                Coords dest = cr.calcCenterBottomPos(rawPos);
                boolean pointInObstacle = pointInObstacle(rawPos);
                if (!pointInObstacle) {
                    cr.setDest(dest);
                }
            }
        };
        addEventHandler(MouseEvent.MOUSE_CLICKED, creatureMoveTo);
        addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                e.consume();
                removeEventHandler(MouseEvent.MOUSE_CLICKED, creatureMoveTo);
                cr.setControl(CreatureControl.CONTROLABLE);
            }
        });
    }

    private boolean pointInObstacle(Coords dest) {
        Coords[] poss = new Coords[] {dest};
        ItemType[] types = new ItemType[] {ItemType.OBSTACLE};
        Content obstacle = lookForContent(poss, types, false);
        return obstacle != null;
    }

    private Content lookForContent(Coords[] poss, ItemType[] types, boolean includeLevelsBelow) {
        if (types == null) {
            types = new ItemType[] {ItemType.CREATURE};//TODO other types that need to be interact with
        }
        List<ItemType> typesList = new ArrayList<>(1);
        Collections.addAll(typesList, types);
        List<Content> contents = Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> typesList.contains(c.getItem().getType()))
                .filter(c -> c.isVisible())
                .filter(c -> {
                    int level = c.getItem().getLevel();
                    int actualLevel = Controller.get().getCurrentLayer().getLevel();
                    if (includeLevelsBelow) {
                        return level <= actualLevel;
                    } else {
                        return level == actualLevel;
                    }
                })
                .collect(Collectors.toList());
        if (contents.isEmpty()) {
            return null;
        }
        contents.sort(new ContentComparator() {
            @Override
            public int compare(Content o1, Content o2) {
                return super.compare(o2, o1);
            }
        });
        for (Content c : contents) {
            for (Coords pos : poss) {
                double x = pos.getX();
                double y = pos.getY();

                double cX = c.getItem().getPos().getX();
                double cWidth = c.getItem().getImage().getWidth();
                boolean fitX = x >= cX && x <= cX + cWidth;
                if (!fitX) {
                    continue;
                }

                double cY = c.getItem().getPos().getY();
                double cHeight = c.getItem().getImage().getHeight();
                boolean fitY = y >= cY && y <= cY + cHeight;
                if (!fitY) {
                    continue;
                }

                Image img = c.getItem().getImage();
                int imgX = (int) (x - cX);
                int imgY = (int) (y - cY);
                Color color = img.getPixelReader().getColor(imgX, imgY);    //TODO fix index ot of bounds exception
                boolean isPixelTransparent = color.equals(Color.TRANSPARENT);
                if (isPixelTransparent) {
                    continue;
                }
                return c;
            }
        }
        return null;
    }

    private void setSize() {
        int width = Controller.get().getCurrentLocation().getWidth();
        int height = Controller.get().getCurrentLocation().getHeight();
        setWidth(width);
        setHeight(height);
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
}
