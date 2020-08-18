package editor.view.stage;

import editor.model.EditorController;
import editor.view.content.ContentTableView;
import io.wsz.model.Controller;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
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
    private final Controller controller;
    private final EditorController editorController;
    private final Coords curPos;
    private final Coords draggedItemMousePos = new Coords();

    private EventHandler<KeyEvent> arrowsEvent;
    private ContentTableView contentTableView;
    private PosItem draggedItem;

    public EditorCanvas(Stage stage, EditorController editorController, Pane parent, Pointer pointer){
        this.stage = stage;
        this.editorController = editorController;
        this.controller = editorController.getController();
        this.curPos = controller.getCurPos();
        this.pointer = pointer;
        this.parent = parent;
        setSize();
        hookUpEvents();
    }

    public void refresh() {
        setSize();
        GraphicsContext gc = getGraphicsContext2D();
        clear(gc);

        double left = curPos.x;
        double canvasWidth = getWidth();
        int meter = Sizes.getMeter();
        double right = left + canvasWidth/ meter;
        double top = curPos.y;
        double canvasHeight = getHeight();
        double bottom = top + canvasHeight/ meter;

        List<PosItem> items = controller.getCurrentLocation().getItems();
        items = items.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> {
                    double piLeft = pi.getLeft();
                    double piRight = pi.getRight();
                    double piTop = pi.getTop();
                    double piBottom = pi.getBottom();
                    return Coords.doOverlap(
                    left, top, right, bottom,
                    piLeft, piTop, piRight, piBottom);
                })
                .collect(Collectors.toList());

        controller.getBoard().sortPosItems(items);

        boolean activeContentMarked = false;
        for (PosItem pi : items) {

            double itemsOpacity = EditorToolBar.getItemsOpacity();
            if (itemsOpacity != 1) {
                if (EditorToolBar.isLayerOpacity()) {
                    int level = controller.getCurrentLayer().getLevel();
                    if (pi.getPos().level == level) {
                        gc.setGlobalAlpha(itemsOpacity);
                    }
                } else {
                    if (pi == editorController.getActiveItem()) {
                        gc.setGlobalAlpha(itemsOpacity);
                    }
                }
            }

            Coords pos = pi.getPos();
            Coords translated = pos.clonePos();
            translated.subtract(curPos);
            double x = (translated.x * meter);
            double y = (translated.y * meter);

            Image img = pi.getImage();
            double width = img.getWidth();
            double height = img.getHeight();

            double startX = 0;
            if (x < 0) {
                startX = -x;
                width = x + width;
            }
            if (x + width > canvasWidth) {
                width = canvasWidth - x;
            } else if (width > canvasWidth) {
                width = canvasWidth;
            }

            double startY = 0;
            if (y < 0) {
                startY = -y;
                height = y + height;
            }
            if (y + height > canvasHeight) {
                height = canvasHeight - y;
            } else if (height > canvasHeight) {
                height = canvasHeight;
            }

            double destX = 0;
            if (x > 0) {
                destX = x;
            }
            double destY = 0;
            if (y > 0) {
                destY = y;
            }
            gc.drawImage(img, startX, startY, width, height, destX, destY, width, height);

            if (!activeContentMarked
                    && pi.equals(editorController.getActiveItem())) {
                activeContentMarked = true;
                drawActiveContentRectangle(gc, pi);
            }

            gc.setGlobalAlpha(1);
        }

        if (pointer.isActive()) {
            Coords mark = pointer.getMark();
            if (mark == null) {
                return;
            }
            Coords translated = mark.clonePos();
            translated.setLocation(controller.getCurrentLocation().getLocation());
            translated.subtract(curPos);
            Image marker = pointer.getMarkerImage();
            if (marker == null) {
                return;
            }
            double x = translated.x * meter - marker.getWidth()/2;
            double y = translated.y * meter - marker.getHeight()/2;
            gc.drawImage(marker, x, y);
        }
    }

    private void drawActiveContentRectangle(GraphicsContext gc, PosItem pi) {
        Coords translated = pi.getPos().clonePos();
        translated.subtract(curPos);
        double width = pi.getImage().getWidth();
        double height = pi.getImage().getHeight();
        gc.setStroke(Color.RED);
        gc.setLineWidth(0.5);
        gc.setLineDashes(20);
        gc.strokeRect(translated.x * Sizes.getMeter(), translated.y * Sizes.getMeter(), width, height);
    }

    private void hookUpEvents() {
        hookUpScreenDragEvents();
        hookUpItemsPermutationEvents();
        hookUpLocationSizeEvents();
        hookUpClickEvents();
        hookUpItemDragEvents();
    }

    private void hookUpClickEvents() {
        int meter = Sizes.getMeter();
        setOnMouseClicked(e -> {
            e.consume();
            setFocusTraversable(true);
            requestFocus();
            try {
                if (pointer.isActive()) {
                    attachArrowsEventToPointer();
                }
                PosItem pi = null;
                if (e.getButton().equals(MouseButton.PRIMARY) || e.getButton().equals(MouseButton.SECONDARY)) {
                    double xPos = e.getX() / meter;
                    double yPos = e.getY() / meter;
                    pi = getPosItem(xPos, yPos);
                }
                if (pi != null) {
                    if (e.getButton().equals(MouseButton.PRIMARY)) {
                        attachArrowsEventToContent(pi);
                    } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                        editorController.setActiveItem(pi);
                        openContextMenu(pi, e);
                    }
                } else {
                    editorController.setActiveItem(null);
                }
            } finally {
                if (((e.getButton().equals(MouseButton.SECONDARY)
                        || editorController.getActiveItem() == null) && !pointer.isActive())) {
                    removeArrowsEvent();
                }
                refresh();
            }
        });
    }

    private void hookUpLocationSizeEvents() {
        stage.widthProperty().addListener((observable, oldValue, newValue) -> {
            curPos.x = 0;
            refresh();
        });
        stage.heightProperty().addListener((observable, oldValue, newValue) -> {
            curPos.y = 0;
            refresh();
        });
        controller.getCurrentLocation().locationProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
        controller.getCurrentLocation().getWidthProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
        controller.getCurrentLocation().getHeightProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
    }

    private void hookUpItemsPermutationEvents() {
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
        controller.getCurrentLocation().getItems().addListener(locationListener);
        controller.getCurrentLocation().locationProperty().addListener((observable, oldValue, newValue) -> {
            hookupItemsEvents(controller.getCurrentLocation().getItems());
            controller.getCurrentLocation().getItems().addListener(locationListener);
        });
    }

    private void hookUpItemDragEvents() {
        int meter = Sizes.getMeter();
        setOnDragDetected(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                e.consume();
                double xPos = e.getX() / meter;
                double yPos = e.getY() / meter;
                PosItem selectedItem = getPosItem(xPos, yPos);
                if (selectedItem == null) {
                    return;
                }
                draggedItem = selectedItem;
                Coords itemPos = selectedItem.getPos();
                draggedItemMousePos.x = xPos - itemPos.x;
                draggedItemMousePos.y = yPos - itemPos.y;

                Dragboard db = startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();
                content.putImage(selectedItem.getImage());
                db.setContent(content);
            }
        });

        setOnDragOver(e -> {
            e.consume();
            if (e.getDragboard().hasImage()) {
                e.acceptTransferModes(TransferMode.COPY, TransferMode.MOVE);
            }
        });

        setOnDragDropped(e -> {
            e.consume();
            Dragboard db = e.getDragboard();
            boolean success = false;
            if (db.hasImage()) {
                Coords dragPos = new Coords(
                        e.getX() / meter, e.getY() / meter,
                        controller.getCurrentLayer().getLevel(),
                        controller.getCurrentLocation().getLocation());
                if (draggedItem == null) {
                    dragPos.add(controller.getCurPos());
                    editorController.setDragPos(dragPos);
                } else {
                    dragPos.x -= draggedItemMousePos.x;
                    dragPos.y -= draggedItemMousePos.y;
                    draggedItem.setPos(dragPos);
                    refresh();
                    draggedItem = null;
                }
                success = true;
            }
            e.setDropCompleted(success);
        });
    }

    private PosItem getPosItem(double xPos, double yPos) {
        PosItem pi;
        int level = controller.getCurrentLayer().getLevel();
        Coords pos = new Coords(
                xPos, yPos,
                level,
                controller.getCurrentLocation().getLocation());
        Coords translated = pos.clonePos();
        translated.add(curPos);
        double x = translated.x;
        double y = translated.y;
        ItemType[] types = ItemType.values();
        Location location = controller.getCurrentLocation().getLocation();
        pi = controller.getBoard().lookForItem(location, x, y, level, types, true);
        return pi;
    }

    private void hookUpScreenDragEvents() {
        double[] dx = new double[2];
        double[] dy = new double[2];

        EventHandler<MouseEvent> startScreenDrag = e -> {
            MouseButton button = e.getButton();
            if (button.equals(MouseButton.MIDDLE)) {
                e.consume();
                this.startFullDrag();
                dx[0] = e.getX();
                dy[0] = e.getY();
            }
        };
        EventHandler<MouseEvent> progressScreenDrag = e -> {
            e.consume();
            dx[1] = e.getX();
            dy[1] = e.getY();
            double dX = dx[1] - dx[0];
            double dY = dy[1] - dy[0];
            double newX = curPos.x * Sizes.getMeter() - dX;
            double newY = curPos.y * Sizes.getMeter() - dY;
            double locWidth = controller.getCurrentLocation().getWidth() * Sizes.getMeter();
            double locHeight = controller.getCurrentLocation().getHeight() * Sizes.getMeter();

            if (newX + getWidth() <= locWidth) {
                curPos.x = Math.max(newX, 0) / Sizes.getMeter();
            } else {
                curPos.x = (locWidth - getWidth()) / Sizes.getMeter();
            }

            if (newY + getHeight() <= locHeight) {
                curPos.y = Math.max(newY, 0) / Sizes.getMeter();
            } else {
                curPos.y = (locHeight - getHeight()) / Sizes.getMeter();
            }

            refresh();
        };
        addEventHandler(MouseDragEvent.DRAG_DETECTED, startScreenDrag);
        addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, progressScreenDrag);
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
        editorController.setActiveItem(pi);
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
                PosItem active = editorController.getActiveItem();
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
        double locWidth = controller.getCurrentLocation().getWidth() * Sizes.getMeter();
        double locHeight = controller.getCurrentLocation().getHeight() * Sizes.getMeter();
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
        pos.setLocation(controller.getCurrentLocation().getLocation());
        contentTableView.refresh();
        refresh();
    }

    private void setInvisible(PosItem pi) {
        pi.setVisible(false);
        refresh();
    }

    private void removeItem(PosItem pi) {
        controller.getModel().getCurrentLocation().getLocation().getItems().remove(pi);
        refresh();
    }

    public void setContentTableView(ContentTableView table) {
        this.contentTableView = table;
    }
}
