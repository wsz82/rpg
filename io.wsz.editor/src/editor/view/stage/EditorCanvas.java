package editor.view.stage;

import editor.model.ActiveItem;
import editor.model.EditorController;
import editor.view.content.ContentTableView;
import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.stream.Collectors;

public class EditorCanvas extends Canvas {
    private static EditorCanvas singleton;
    private final Pointer pointer = Pointer.get();
    private EventHandler<KeyEvent> arrowsEvent;

    public static EditorCanvas get() {
        if (singleton == null) {
            singleton = new EditorCanvas();
        }
        return singleton;
    }

    private EditorCanvas(){
        setSize();
        hookupEvents();
    }

    public void refresh() {
        setSize();
        GraphicsContext gc = getGraphicsContext2D();
        clear(gc);

        List<PosItem> items = Controller.get().getCurrentLocation().getItems();
        items = items.stream()
                .filter(pi -> pi.getVisible())
                .collect(Collectors.toList());
        Board.get().sortItems(items);

        boolean activeContentMarked = false;
        for (PosItem pi : items) {
            final Coords pos = pi.getPos();
            final int x = pos.x;
            final int y = pos.y;

            if (pi.getVisible()) {
                gc.drawImage(pi.getImage(), x, y);
                if (!activeContentMarked
                        && pi.equals(EditorController.get().getActiveContent().getItems())) {
                    activeContentMarked = true;
                    drawActiveContentRectangle(gc, pi);
                }
            }
        }

        if (pointer.isActive()) {
            Coords mark = pointer.getMark();
            if (mark == null) {
                return;
            }
            Image marker = pointer.getMarkerImage();
            if (marker == null) {
                return;
            }
            int x = mark.x - (int) marker.getWidth()/2;
            int y = mark.y - (int) marker.getHeight()/2;
            gc.drawImage(marker, x, y);
        }
    }

    private void drawActiveContentRectangle(GraphicsContext gc, PosItem pi) {
        int x = pi.getPos().x;
        int y = pi.getPos().y;
        double width = pi.getImage().getWidth();
        double height = pi.getImage().getHeight();
        gc.setStroke(Color.RED);
        gc.setLineWidth(0.5);
        gc.setLineDashes(20);
        gc.strokeRect(x, y, width, height);
    }

    private void hookupEvents() {
        ListChangeListener<? super PosItem> locationListener = c -> {
            if (!c.next()) {
                return;
            }
            if (c.wasAdded() || c.wasRemoved()) {
                List<PosItem> addedContent = (List<PosItem>) c.getAddedSubList();
                hookupItemsEvents(addedContent);
            }
        };
        Controller.get().getCurrentLocation().getItems().addListener(locationListener);
        Controller.get().getCurrentLocation().locationProperty().addListener((observable, oldValue, newValue) -> {
            hookupItemsEvents(Controller.get().getCurrentLocation().getItems());
            Controller.get().getCurrentLocation().getItems().addListener(locationListener);
        });

        Controller.get().getCurrentLocation().locationProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
        Controller.get().getCurrentLocation().widthProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
        Controller.get().getCurrentLocation().heightProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });

        setOnMouseClicked(e -> {
            e.consume();
            setFocusTraversable(true);
            requestFocus();
            ActiveItem ac = EditorController.get().getActiveContent();
            try {
                if (pointer.isActive()) {
                    attachArrowsEventToPointer();
                }
                PosItem pi = null;
                if (e.getButton().equals(MouseButton.PRIMARY) || e.getButton().equals(MouseButton.SECONDARY)) {
                    Coords[] poss = new Coords[]{new Coords((int) e.getX(), (int) e.getY())};
                    ItemType[] types = ItemType.values();
                    pi = Controller.get().getBoard().lookForContent(poss, types, true);
                }
                if (pi != null) {
                    if (e.getButton().equals(MouseButton.PRIMARY)) {
                        attachArrowsEventToContent(pi);
                    } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                        EditorController.get().getActiveContent().setItems(pi);
                        openContextMenu(pi, e);
                    }
                } else {
                    ac.setItems(null);
                }
            } finally {
                if (((e.getButton().equals(MouseButton.SECONDARY)
                        || (ac.getItems()) == null) && !pointer.isActive())) {
                    removeArrowsEvent();
                }
                refresh();
            }
        });
    }

    private void attachArrowsEventToPointer() {
        removeArrowsEvent();
        arrowsEvent = e -> {
            e.consume();
            if (e.getCode() == KeyCode.DOWN
                    || e.getCode() == KeyCode.UP
                    || e.getCode() == KeyCode.RIGHT
                    || e.getCode() == KeyCode.LEFT) {
                movePointer(e.getCode());
                refresh();
            }
        };
        addEventHandler(KeyEvent.KEY_PRESSED, arrowsEvent);
    }

    private void removeArrowsEvent() {
        if (arrowsEvent != null) {
            removeEventHandler(KeyEvent.KEY_PRESSED, arrowsEvent);
        }
    }

    private void attachArrowsEventToContent(PosItem pi) {
        EditorController.get().getActiveContent().setItems(pi);
        removeArrowsEvent();
        arrowsEvent = e -> {
            if (pointer.isActive()) {
                return;
            }
            e.consume();
            if (e.getCode() == KeyCode.DOWN
                    || e.getCode() == KeyCode.UP
                    || e.getCode() == KeyCode.RIGHT
                    || e.getCode() == KeyCode.LEFT) {
                PosItem active = EditorController.get().getActiveContent().getItems();
                moveContent(e.getCode(), active);
                ContentTableView.get().refresh();
                refresh();
            }
        };
        addEventHandler(KeyEvent.KEY_PRESSED, arrowsEvent);
    }

    private void hookupItemsEvents(List<PosItem> addedItems) {
        for (PosItem pi : addedItems) {
            pi.visibleProperty().addListener((observable, oldValue, newValue) -> {
                refresh();
            });
        }
        refresh();
    }

    private void openContextMenu(PosItem pi, MouseEvent e) {
        final MenuItem edit = new MenuItem("Edit item");
        edit.setOnAction(event -> edit(pi));
        final MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(ev -> removeItem(pi));
        final MenuItem moveToPointer = new MenuItem("Move to pointer");
        moveToPointer.setOnAction(ev -> moveToPointer(pi));
        final MenuItem setInvisible = new MenuItem("Set invisible");
        setInvisible.setOnAction(ev -> setInvisible(pi));
        final ContextMenu menu = new ContextMenu(edit, remove, moveToPointer, setInvisible);
        menu.show(this, e.getScreenX(), e.getScreenY());
        addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            ev.consume();
            menu.hide();
        });
    }

    private void edit(PosItem pi) {
        ContentTableView.get().openEditWindow(Main.getStage(), pi);
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

    private void moveContent(KeyCode keyCode, PosItem pi){
        Coords pos = pi.getPos();
        updatePos(keyCode, pos);
    }

    private void movePointer(KeyCode keyCode) {
        Coords mark = pointer.getMark();
        updatePos(keyCode, mark);
    }

    private void updatePos(KeyCode keyCode, Coords pos) {
        switch (keyCode) {
            case UP -> pos.y = pos.y - 1;
            case LEFT -> pos.x = pos.x - 1;
            case DOWN -> pos.y = pos.y + 1;
            case RIGHT -> pos.x = pos.x + 1;
        }
        refresh();
    }

    private void moveToPointer(PosItem pi) {
        Coords pos = pi.getPos();
        Coords newPos = pointer.getMark();
        pos.x = newPos.x;
        int y = 0;
        if (newPos.y != 0) {
            y = newPos.y - (int) pi.getImage().getHeight();
        }
        pos.y = y;
        ContentTableView.get().refresh();
        refresh();
    }

    private void setInvisible(PosItem pi) {
        pi.setVisible(false);
        refresh();
    }

    private void removeItem(PosItem pi) {
        Controller.get().removeItem(pi);
        refresh();
    }
}
