package game.view.stage;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.content.ContentComparator;
import io.wsz.model.item.Creature;
import io.wsz.model.item.Item;
import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

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
                .filter(c -> c.isVisible())
                .filter(c -> c.getItem().getLevel() <= Controller.get().getCurrentLayer().getLevel())    //TODO
                .collect(Collectors.toList());
        for (Content content : contents) {
            final Item item = content.getItem();
            final Coords pos = item.getPos();
            final double x = pos.getX();
            final double y = pos.getY();

            if (content.isVisible()) {
                gc.drawImage(item.getAsset().getImage(), x, y);
            }
        }
    }

    private void hookupEvents() {
        setFocusTraversable(true);

        setOnMouseClicked(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                e.consume();
                Content content = lookForContent(e.getX(), e.getY());
                if (content == null) {
                    return;
                }
                ItemType type = content.getItem().getAsset().getType();
                switch (type) {
                    case CREATURE -> gainControl(content);
                }
            }
        });
    }

    private void gainControl(Content content) {
        final EventHandler<MouseEvent> creatureMoveTo = e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                e.consume();
                Creature creature = (Creature) content.getItem();
                int creatureWidth = (int) creature.getAsset().getImage().getWidth();
                int creatureHeight = (int) creature.getAsset().getImage().getHeight();
                double moveX = e.getX() - (double) creatureWidth/2;
                double moveY = e.getY() - creatureHeight;
                Coords dest = new Coords(moveX, moveY, creature.getPos().getZ());
                creature.setDest(dest);
            }
        };
        addEventHandler(MouseEvent.MOUSE_CLICKED, creatureMoveTo);
        addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                e.consume();
                removeEventHandler(MouseEvent.MOUSE_CLICKED, creatureMoveTo);
            }
        });
    }

    private Content lookForContent(double x, double y) {
        List<Content> contents = Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> c.isVisible())
                .filter(c -> c.getItem().getLevel() <= Controller.get().getCurrentLayer().getLevel())    //TODO
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
            double cX = c.getItem().getPos().getX();
            double cWidth = c.getItem().getAsset().getImage().getWidth();
            boolean fitX = x >= cX && x <= cX + cWidth;
            if (!fitX) {
                continue;
            }
            double cY = c.getItem().getPos().getY();
            double cHeight = c.getItem().getAsset().getImage().getHeight();
            boolean fitY = y >= cY && y <= cY + cHeight;
            if (!fitY) {
                continue;
            }
            return c;
        }
        return null;
    }

    private void setSize() {
        int width = Controller.get().getCurrentLocation().getCurrentWidth();
        int height = Controller.get().getCurrentLocation().getCurrentHeight();
        setWidth(width);
        setHeight(height);
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }
}
