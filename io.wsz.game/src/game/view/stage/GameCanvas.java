package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.item.*;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GameCanvas extends Canvas {
    private static GameCanvas singleton;
    private Creature activeCreature;    //TODO find different solution for keeping control
    private final EventHandler<MouseEvent> creatureMoveTo = e -> {
        if (e.getButton().equals(MouseButton.PRIMARY)) {
            e.consume();
            Coords pos = new Coords(e.getX(), e.getY());
            if (activeCreature.onInteractWith(pos)) return;
            activeCreature.setDest(activeCreature.calcDest(pos));
        }
    };

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

        List<Content> contents = new ArrayList<>(Controller.get().getCurrentLocation().getContent());
        contents = contents.stream()
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
                        if (((Creature) item).getControl().equals(CreatureControl.CONTROL)) {
                            activeCreature = (Creature) item;
                            hookupCreatureEvents((Creature) item);
                        }
                        drawCreatureSize((Creature) item, gc);
                    }
                }
                gc.drawImage(item.getImage(), x, y);
            }
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
        gc.setLineWidth(1.5);
        gc.strokeOval(x - size.getWidth()/2.0, y - size.getHeight()/2.0, size.getWidth(), size.getHeight());
    }

    private void hookupEvents() {
        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                e.consume();
                Coords pos = new Coords(e.getX(), e.getY());
                Coords[] poss = new Coords[] {pos};
                ItemType[] types = new ItemType[] {ItemType.CREATURE};
                Content c = Controller.get().getBoard().lookForContent(poss, types, true);
                if (c == null) {
                    return;
                }
                PosItem item = c.getItem();
                ItemType type = item.getType();
                switch (type) {
                    case CREATURE -> interactWithCreature((Creature) item);
                }
            }
        });
    }

    private void interactWithCreature(Creature cr) {
        boolean isInteracted = cr.onInteraction();
        if (!isInteracted) {
            return;
        }
        if (activeCreature != null && activeCreature != cr) {
            activeCreature.onStopInteraction();
        }
        activeCreature = cr;
        hookupCreatureEvents(cr);
    }

    private void hookupCreatureEvents(Creature cr) {
        addEventHandler(MouseEvent.MOUSE_CLICKED, creatureMoveTo);
        addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                e.consume();
                removeEventHandler(MouseEvent.MOUSE_CLICKED, creatureMoveTo);
                cr.setControl(CreatureControl.CONTROLABLE);
            }
        });
    }

    private void setSize() {
        int width = Controller.get().getCurrentLocation().getWidth();
        int height = Controller.get().getCurrentLocation().getHeight();
        setWidth(width);
        setHeight(height);
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
}
