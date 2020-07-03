package editor.view.stage;

import editor.model.ActiveItem;
import editor.model.EditorController;
import editor.view.content.ContentTableView;
import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
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

        double leftX = currentPos.x;
        double rightX = leftX + getWidth()/Sizes.getMeter();
        double topY = currentPos.y;
        double bottomY = topY + getHeight()/Sizes.getMeter();

        List<PosItem> items = Controller.get().getCurrentLocation().getItems();
        items = items.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> {
                    Coords pos = pi.getPos();
                    Image img = pi.getImage();
                    double piLeftX = pos.x;
                    double piRightX = piLeftX + img.getWidth()/Sizes.getMeter();
                    double piTopY = pos.y;
                    double piBottomY = piTopY + img.getHeight()/Sizes.getMeter();
                    return Comparator.doOverlap(
                    leftX, topY, rightX, bottomY,
                    piLeftX, piTopY, piRightX, piBottomY);
                })
                .collect(Collectors.toList());

        Board.get().sortItems(items);

        boolean activeContentMarked = false;
        for (PosItem pi : items) {
            Coords pos = pi.getPos();
            Coords translated = pos.clonePos();
            translated.subtract(currentPos);
            int x = (int) (translated.x * Sizes.getMeter());
            int y = (int) (translated.y * Sizes.getMeter());

            Image img = pi.getImage();
            double width = img.getWidth();
            double height = img.getHeight();

            int startX = 0;
            if (x < 0) {
                startX = -x;
                width = x + width;
            }
            int startY = 0;
            if (y < 0) {
                startY = -y;
                height = y + height;
            }

            int destX = 0;
            if (x > 0) {
                destX = x;
            }
            int destY = 0;
            if (y > 0) {
                destY = y;
            }
            gc.drawImage(img, startX, startY, width, height, destX, destY, width, height);

            if (!activeContentMarked
                    && pi.equals(EditorController.get().getActiveContent().getItems())) {
                activeContentMarked = true;
                drawActiveContentRectangle(gc, pi);
            }
        }

        if (pointer.isActive()) {
            Coords mark = pointer.getMark();
            if (mark == null) {
                return;
            }
            Coords translated = mark.clonePos();
            translated.setLocation(Controller.get().getCurrentLocation().getLocation());
            translated.subtract(currentPos);
            Image marker = pointer.getMarkerImage();
            if (marker == null) {
                return;
            }
            double x = translated.x * Sizes.getMeter() - marker.getWidth()/2;
            double y = translated.y * Sizes.getMeter() - marker.getHeight()/2;
            gc.drawImage(marker, x, y);
        }
    }

    private void drawActiveContentRectangle(GraphicsContext gc, PosItem pi) {
        Coords translated = pi.getPos().clonePos();
        translated.subtract(currentPos);
        double width = pi.getImage().getWidth();
        double height = pi.getImage().getHeight();
        gc.setStroke(Color.RED);
        gc.setLineWidth(0.5);
        gc.setLineDashes(20);
        gc.strokeRect(translated.x * Sizes.getMeter(), translated.y * Sizes.getMeter(), width, height);
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
            double dX = dx[1] - dx[0];
            double dY = dy[1] - dy[0];
            double newX = currentPos.x * Sizes.getMeter() - dX;
            double newY = currentPos.y * Sizes.getMeter() - dY;
            double locWidth = Controller.get().getCurrentLocation().getWidth() * Sizes.getMeter();
            double locHeight = Controller.get().getCurrentLocation().getHeight() * Sizes.getMeter();

            if (newX + getWidth() <= locWidth) {
                currentPos.x = Math.max(newX, 0) / Sizes.getMeter();
            } else {
                currentPos.x = (locWidth - getWidth()) / Sizes.getMeter();
            }

            if (newY + getHeight() <= locHeight) {
                currentPos.y = Math.max(newY, 0) / Sizes.getMeter();
            } else {
                currentPos.y = (locHeight - getHeight()) / Sizes.getMeter();
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
            refresh();
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
                    Coords pos = new Coords(e.getX() / Sizes.getMeter(), e.getY() / Sizes.getMeter(),
                            Controller.get().getCurrentLocation().getLocation());
                    Coords translated = pos.clonePos();
                    translated.add(currentPos);
                    Coords[] poss = new Coords[]{translated};
                    ItemType[] types = ItemType.values();
                    Location location = Controller.get().getCurrentLocation().getLocation();
                    pi = Controller.get().getBoard().lookForContent(location, poss, types, true);
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

        setOnDragOver(e -> {
            if (e.getGestureSource() != this
                    && e.getDragboard().hasImage()) {
                e.acceptTransferModes(TransferMode.COPY);
            }

            e.consume();
        });

        setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasImage()) {
                Coords dragPos = new Coords(
                        e.getX() / Sizes.getMeter(), e.getY() / Sizes.getMeter(),
                        Controller.get().getCurrentLocation().getLocation());
                dragPos.add(Controller.get().getBoardPos());
                EditorController.get().setDragPos(dragPos);
                success = true;
            }
            e.setDropCompleted(success);

            e.consume();
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
        double locWidth = Controller.get().getCurrentLocation().getWidth() * Sizes.getMeter();
        double locHeight = Controller.get().getCurrentLocation().getHeight() * Sizes.getMeter();
        double maxWidth = parent.getWidth();
        double maxHeight = parent.getHeight();
        if (locWidth >= maxWidth) {
            setWidth(maxWidth);
        } else {
            setWidth(locWidth);
        }
        if (locHeight >= maxHeight) {
            setHeight(maxHeight);
        } else {
            setHeight(locHeight);
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
        double dif = 1.0 / Sizes.getMeter();
        switch (keyCode) {
            case UP -> pos.y = pos.y - dif;
            case LEFT -> pos.x = pos.x - dif;
            case DOWN -> pos.y = pos.y + dif;
            case RIGHT -> pos.x = pos.x + dif;
        }
    }

    private void moveToPointer(PosItem pi) {
        Coords pos = pi.getPos();
        Coords newPos = pointer.getMark();
        pos.x = newPos.x;
        double y = 0;
        if (newPos.y != 0) {
            y = newPos.y - pi.getImage().getHeight()/Sizes.getMeter();
        }
        pos.y = y;
        pos.setLocation(Controller.get().getCurrentLocation().getLocation());
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
