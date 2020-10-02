package editor.view.stage;

import editor.model.EditorController;
import editor.view.content.ContentTableView;
import editor.view.location.CurrentObservableLocation;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import io.wsz.model.stage.ResolutionImage;
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
    private final EditorController controller;
    private final Coords curPos;
    private final Coords draggedItemMousePos = new Coords();

    private EventHandler<KeyEvent> arrowsEvent;
    private ContentTableView contentTableView;
    private PosItem<?,?> draggedItem;

    public EditorCanvas(Stage stage, EditorController controller, Pane parent, Pointer pointer){
        this.stage = stage;
        this.controller = controller;
        this.curPos = controller.getCurPos();
        this.pointer = pointer;
        this.parent = parent;
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

        List<PosItem<?,?>> items = controller.getCurrentObservableLocation().getItems();
        items = items.stream()
                .filter(PosItem::isVisible)
                .filter(pi -> {
                    double piLeft = pi.getLeft();
                    double piRight = pi.getRight();
                    double piTop = pi.getTop();
                    double piBottom = pi.getBottom();
                    return Geometry.doOverlap(
                    left, top, right, bottom,
                    piLeft, piTop, piRight, piBottom);
                })
                .collect(Collectors.toList());

        controller.getBoard().sortPosItems(items);

        boolean activeContentMarked = false;
        for (PosItem<?,?> pi : items) {
            double itemsOpacity = EditorToolBar.getItemsOpacity();
            if (itemsOpacity != 1) {
                if (EditorToolBar.isLayerOpacity()) {
                    int level = controller.getCurrentObservableLayer().getLevel();
                    if (pi.getPos().level == level) {
                        gc.setGlobalAlpha(itemsOpacity);
                    }
                } else {
                    if (pi == controller.getActiveItem()) {
                        gc.setGlobalAlpha(itemsOpacity);
                    }
                }
            }

            Coords pos = pi.getPos();
            Coords translated = pos.clonePos();
            translated.subtract(curPos);
            double x = (translated.x * meter);
            double y = (translated.y * meter);

            ResolutionImage resolutionImage = pi.getImage();
            if (resolutionImage == null) continue;
            double width = resolutionImage.getWidth();
            double height = resolutionImage.getHeight();

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
            Image img = resolutionImage.getFxImage();
            gc.drawImage(img, startX, startY, width, height, destX, destY, width, height);

            if (!activeContentMarked
                    && pi.equals(controller.getActiveItem())) {
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
            translated.setLocation(controller.getCurrentObservableLocation().getLocation());
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

    private void drawActiveContentRectangle(GraphicsContext gc, PosItem<?,?> pi) {
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
        hookUpMoveItemWithArrowsEvent();
    }

    private void hookUpMoveItemWithArrowsEvent() {
        arrowsEvent = e -> {
            if (pointer.isActive()) {
                return;
            }
            e.consume();
            if (e.getCode() == KeyCode.DOWN
                    || e.getCode() == KeyCode.UP
                    || e.getCode() == KeyCode.RIGHT
                    || e.getCode() == KeyCode.LEFT) {
                PosItem<?,?> active = controller.getActiveItem();
                if (active != null) {
                    moveContent(e.getCode(), active);
                    contentTableView.refresh();
                    refresh();
                }
            }
        };
        addEventHandler(KeyEvent.KEY_PRESSED, arrowsEvent);
    }

    private void hookUpClickEvents() {
        setOnMouseClicked(e -> {
            e.consume();
            setFocusTraversable(true);
            requestFocus();
            selectActiveItem(e);
            refresh();
        });
    }

    private void selectActiveItem(MouseEvent e) {
        PosItem<?,?> selectedItem = null;
        MouseButton button = e.getButton();
        boolean isPrimaryButton = button.equals(MouseButton.PRIMARY);
        boolean isSecondaryButton = button.equals(MouseButton.SECONDARY);
        if (isPrimaryButton || isSecondaryButton) {
            int meter = Sizes.getMeter();
            double xPos = e.getX() / meter;
            double yPos = e.getY() / meter;
            selectedItem = getItemAtPos(xPos, yPos);
        }
        if (selectedItem != null && !selectedItem.isBlocked()) {
            controller.setActiveItem(selectedItem);
            if (isSecondaryButton) {
                openContextMenu(selectedItem, e);
            }
        } else {
            controller.setActiveItem(null);
        }
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
        controller.getCurrentObservableLocation().locationProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
        controller.getCurrentObservableLocation().getWidthProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
        controller.getCurrentObservableLocation().getHeightProperty().addListener((observable, oldValue, newValue) -> {
            refresh();
        });
    }

    private void hookUpItemsPermutationEvents() {
        ListChangeListener<? super PosItem<?,?>> locationListener = c -> {
            if (!c.next()) {
                return;
            }
            refresh();
        };
        CurrentObservableLocation currentObservableLocation = controller.getCurrentObservableLocation();
        currentObservableLocation.getItems().addListener(locationListener);
    }

    private void hookUpItemDragEvents() {
        int meter = Sizes.getMeter();
        setOnDragDetected(e -> {
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                e.consume();
                double xPos = e.getX() / meter;
                double yPos = e.getY() / meter;
                PosItem<?,?> selectedItem = getItemAtPos(xPos, yPos);
                if (selectedItem == null || selectedItem.isBlocked()) {
                    return;
                }
                draggedItem = selectedItem;
                Coords itemPos = selectedItem.getPos();
                draggedItemMousePos.x = xPos - itemPos.x;
                draggedItemMousePos.y = yPos - itemPos.y;

                Dragboard db = startDragAndDrop(TransferMode.MOVE);

                ClipboardContent content = new ClipboardContent();
                content.putImage(selectedItem.getImage().getFxImage());
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
                        controller.getCurrentObservableLayer().getLevel(),
                        controller.getCurrentObservableLocation().getLocation());
                if (draggedItem == null) {
                    dragPos.add(controller.getCurPos());
                    controller.setDragPos(dragPos);
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

    private PosItem<?,?> getItemAtPos(double xPos, double yPos) {
        int level = controller.getCurrentObservableLayer().getLevel();
        CurrentObservableLocation currentObservableLocation = controller.getCurrentObservableLocation();
        Location location = currentObservableLocation.getLocation();
        Coords pos = new Coords(xPos, yPos, level, location);
        Coords translated = pos.clonePos();
        translated.add(curPos);
        double x = translated.x;
        double y = translated.y;
        ItemType[] types = ItemType.values();
        List<PosItem<?,?>> items = currentObservableLocation.getItems();
        return controller.getBoard().lookForItem(items, x, y, level, types, true);
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
            double locWidth = controller.getCurrentObservableLocation().getWidth() * Sizes.getMeter();
            double locHeight = controller.getCurrentObservableLocation().getHeight() * Sizes.getMeter();

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

    private void openContextMenu(PosItem<?,?> pi, MouseEvent e) {
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

    private void edit(PosItem<?,?> pi) {
        contentTableView.openEditWindow(stage, pi);
    }

    private void setSize() {
        double locWidth = controller.getCurrentObservableLocation().getWidth() * Sizes.getMeter();
        double locHeight = controller.getCurrentObservableLocation().getHeight() * Sizes.getMeter();
        double maxWidth = parent.getWidth();
        double maxHeight = parent.getHeight();
        setWidth(Math.min(locWidth, maxWidth));
        setHeight(Math.min(locHeight, maxHeight));
    }

    private void clear(GraphicsContext gc) {
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, getWidth(), getHeight());
    }

    private void moveContent(KeyCode keyCode, PosItem<?,?> pi){
        Coords pos = pi.getPos();
        updatePos(keyCode, pos);
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

    private void moveToPointer(PosItem<?,?> pi) {
        Coords pos = pi.getPos();
        Coords newPos = pointer.getMark();
        pos.x = newPos.x;
        double y = 0;
        if (newPos.y != 0) {
            y = newPos.y - pi.getImage().getHeight()/Sizes.getMeter();
        }
        pos.y = y;
        pos.setLocation(controller.getCurrentObservableLocation().getLocation());
        contentTableView.refresh();
        refresh();
    }

    private void setInvisible(PosItem<?,?> pi) {
        pi.setVisible(false);
        refresh();
    }

    private void removeItem(PosItem<?,?> pi) {
        controller.getCurrentObservableLocation().getItems().remove(pi);
        refresh();
    }

    public void setContentTableView(ContentTableView table) {
        this.contentTableView = table;
    }
}
