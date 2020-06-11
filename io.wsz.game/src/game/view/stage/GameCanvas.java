package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

public class GameCanvas extends Canvas {
    private static GameCanvas singleton;
    private final Board board = Controller.get().getBoard();

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

        List<PosItem> items = Controller.get().getCurrentLocation().getItems();
        Board.get().sortItems(items);
        items = items.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> pi.getLevel() <= Controller.get().getCurrentLayer().getLevel())    //TODO
                .collect(Collectors.toList());
        for (PosItem pi : items) {
            final ItemType type = pi.getType();
            final Coords pos = pi.getPos();
            final int x = pos.x;
            final int y = pos.y;

            if (pi.getVisible()) {
                switch (type) {
                    case CREATURE -> {
                        drawCreatureSize((Creature) pi, gc);
                    }
                }
                gc.drawImage(pi.getImage(), x, y);
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
        Coords centerBottomPos = cr.posToCenter();
        int x = centerBottomPos.x;
        int y = centerBottomPos.y;
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
                Coords pos = new Coords((int) e.getX(), (int) e.getY());
                Coords[] poss = new Coords[] {pos};
                ItemType[] types = new ItemType[] {ItemType.CREATURE};
                PosItem pi = Controller.get().getBoard().lookForContent(poss, types, true);
                if (pi != null) {
                    ItemType type = pi.getType();
                    switch (type) {
                        case CREATURE -> ((Creature) pi).interact();
                    }
                } else {
                    commandControllable(pos);
                }
            } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                board.getControlledCreatures()
                        .forEach(Creature::loseControl);
            }
        });
    }

    private void commandControllable(Coords pos) {
        board.getControlledCreatures()
                .forEach(c -> c.onInteractWith(pos));
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
