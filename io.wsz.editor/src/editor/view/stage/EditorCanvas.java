package editor.view.stage;

import editor.model.ActiveItem;
import editor.model.EditorController;
import editor.view.content.ContentTableView;
import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Comparator;
import io.wsz.model.stage.Coords;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.Collectors;

public class EditorCanvas extends Canvas {
    private final Stage stage;
    private final Pointer pointer;
    private final Pane parent;
    private final Coords currentPos = Controller.get().getBoardPos();
    private EventHandler<KeyEvent> arrowsEvent;
    private ContentTableView contentTableView;

    public EditorCanvas(Stage stage, Pane parent, Pointer pointer){
        this.stage = stage;
        this.pointer = pointer;
        this.parent = parent;
        setSize();
        hookupEvents();
    }

    public void refresh() {
        setSize();
        GraphicsContext gc = getGraphicsContext2D();
        clear(gc);

        int leftX = currentPos.x;
        int rightX = leftX + (int) getWidth();
        int topY = currentPos.y;
        int bottomY = topY + (int) getHeight();

        List<PosItem> items = Controller.get().getCurrentLocation().getItems();
        items = items.stream()
                .filter(pi -> {
                    Coords pos = pi.getPos();
                    Image img = pi.getImage();
                    int piLeftX = pos.x;
                    int piRightX = piLeftX + (int) img.getWidth();
                    int piTopY = pos.y;
                    int piBottomY = piTopY + (int) img.getHeight();
                    return Comparator.doOverlap(
                            leftX, topY, rightX, bottomY,
                            piLeftX, piTopY, piRightX, piBottomY);
                })
                .filter(PosItem::getVisible)
                .collect(Collectors.toList());
        Board.get().sortItems(items);

        boolean activeContentMarked = false;
        for (PosItem pi : items) {
            final Coords pos = pi.getPos();
            Coords translated = pos.subtract(currentPos);
            final int x = translated.x;
            final int y = translated.y;

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
            Coords translated = mark.subtract(currentPos);
            Image marker = pointer.getMarkerImage();
            if (marker == null) {
                return;
            }
            int x = translated.x - (int) marker.getWidth()/2;
            int y = translated.y - (int) marker.getHeight()/2;
            gc.drawImage(marker, x, y);
        }
    }

    private void drawActiveContentRectangle(GraphicsContext gc, PosItem pi) {
        int x = pi.getPos().x;
        int y = pi.getPos().y;
        Coords translated = new Coords(x, y).subtract(currentPos);
        double width = pi.getImage().getWidth();
        double height = pi.getImage().getHeight();
        gc.setStroke(Color.RED);
        gc.setLineWidth(0.5);
        gc.setLineDashes(20);
        gc.strokeRect(translated.x, translated.y, width, height);
    }

    private void hookupEvents() {
        double[] dx = new double[2];
        double[] dy = new double[2];

        EventHandler<MouseEvent> startDrag = e -> {
            e.consume();
            this.startFullDrag();
            dx[0] = e.getX();
            dy[0] = e.getY();
        };
        EventHandler<MouseEvent> progressDrag = e -> {
            e.consume();
            dx[1] = e.getX();
            dy[1] = e.getY();
            int dX = (int) (dx[1] - dx[0]);
            int dY = (int) (dy[1] - dy[0]);
            int newX = currentPos.x - dX;
            int newY = currentPos.y - dY;
            int locWidth = Controller.get().getCurrentLocation().getWidth();
            int locHeight = Controller.get().getCurrentLocation().getHeight();

            if (newX + (int) getWidth() <= locWidth) {
                currentPos.x = Math.max(newX, 0);
            } else {
                currentPos.x = locWidth - (int) getWidth();
            }

            if (newY + (int) getHeight() <= locHeight) {
                currentPos.y = Math.max(newY, 0);
            } else {
                currentPos.y = locHeight - (int) getHeight();
            }

            refresh();
        };
        addEventHandler(MouseDragEvent.DRAG_DETECTED, startDrag);
        addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, progressDrag);

        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            currentPos.x = 0;
            refresh();
        });
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            currentPos.y = 0;
            refresh();
        });
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
                    Coords pos = new Coords((int) e.getX(), (int) e.getY());
                    Coords translated = pos.add(currentPos);
                    Coords[] poss = new Coords[]{translated};
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
                contentTableView.refresh();
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
        contentTableView.openEditWindow(stage, pi);
    }

    private void setSize() {
        int locWidth = Controller.get().getCurrentLocation().getWidth();
        int locHeight = Controller.get().getCurrentLocation().getHeight();
        double maxWidth = parent.getWidth();
        double maxHeight = parent.getHeight();
        if (locWidth >= maxWidth) {
            setWidth(maxWidth);
        } else {
            if (currentPos.x > 0) {
                setWidth(locWidth + currentPos.x);
            } else {
                setWidth(locWidth);
            }
        }
        if (locHeight >= maxHeight) {
            setHeight(maxHeight);
        } else {
            if (currentPos.y > 0) {
                setHeight(locHeight + currentPos.y);
            } else {
                setHeight(locHeight);
            }
        }
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, getWidth(), getHeight());
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
        contentTableView.refresh();
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

    public void setContentTableView(ContentTableView table) {
        this.contentTableView = table;
    }
}
