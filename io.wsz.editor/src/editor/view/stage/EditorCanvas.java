package editor.view.stage;

import editor.model.ActiveContent;
import editor.model.EditorController;
import editor.view.content.ContentTableView;
import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.content.ContentComparator;
import io.wsz.model.item.Asset;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
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

import java.util.ArrayList;
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
        refresh();
    }

    public void refresh() {
        setSize();
        GraphicsContext gc = getGraphicsContext2D();
        clear(gc);

        List<Content> contents = new ArrayList<>(Controller.get().getCurrentLocation().getContent());
        contents = contents.stream()
                .filter(c -> c.isVisible())
                .collect(Collectors.toList());
        contents.sort(new ContentComparator() {
            @Override
            public int compare(Content c1, Content c2) {
                return super.compare(c1, c2);
            }
        });
        boolean activeContentMarked = false;
        for (Content c : contents) {
            final PosItem item = c.getItem();
            final Coords pos = item.getPos();
            final double x = pos.getX();
            final double y = pos.getY();

            if (c.isVisible()) {
                gc.drawImage(item.getImage(), x, y);
                if (!activeContentMarked
                        && c.equals(EditorController.get().getActiveContent().getContent())) {
                    activeContentMarked = true;
                    drawActiveContentRectangle(gc, c);
                }
            }
        }

        if (pointer.isActive()) {
            Coords mark = pointer.getMark();
            Image marker = pointer.getMarkerImage();
            double x = mark.getX() - marker.getWidth()/2;
            double y = mark.getY() - marker.getHeight()/2;
            gc.drawImage(marker, x, y);
        }
    }

    private void drawActiveContentRectangle(GraphicsContext gc, Content c) {
        PosItem item = c.getItem();
        double x = item.getPos().getX();
        double y = item.getPos().getY();
        double width = item.getImage().getWidth();
        double height = item.getImage().getHeight();
        gc.setStroke(Color.RED);
        gc.setLineWidth(0.5);
        gc.setLineDashes(20);
        gc.strokeRect(x, y, width, height);
    }

    private void hookupEvents() {
        ListChangeListener<? super Content> locationListener = c -> {
            if (!c.next()) {
                return;
            }
            if (c.wasAdded() || c.wasRemoved()) {
                List<Content> addedContent = (List<Content>) c.getAddedSubList();
                hookupContentEvents(addedContent);
            }
        };
        Controller.get().getCurrentLocation().getContent().addListener(locationListener);
        Controller.get().getCurrentLocation().locationProperty().addListener((observable, oldValue, newValue) -> {
            hookupContentEvents(Controller.get().getCurrentLocation().getContent());
            Controller.get().getCurrentLocation().getContent().addListener(locationListener);
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
            ActiveContent ac = EditorController.get().getActiveContent();
            try {
                if (pointer.isActive()) {
                    ac.setContent(null);
                    attachArrowsEventToPointer();
                } else {
                    Content c = new Content();
                    if (e.getButton().equals(MouseButton.PRIMARY) || e.getButton().equals(MouseButton.SECONDARY)) {
                        Coords[] poss = new Coords[]{new Coords(e.getX(), e.getY())};
                        ItemType[] types = ItemType.values();
                        c = Controller.get().getBoard().lookForContent(poss, types, true);
                    }
                    if (c != null) {
                        if (e.getButton().equals(MouseButton.PRIMARY)) {
                            attachArrowsEventToContent(c);
                        } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                            openContextMenu(c, e);
                        }
                    } else {
                        ac.setContent(null);
                    }
                }
            } finally {
                if (((e.getButton().equals(MouseButton.SECONDARY)
                        || (ac.getContent()) == null) && !pointer.isActive())) {
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

    private void attachArrowsEventToContent(Content c) {
        EditorController.get().getActiveContent().setContent(c);
        removeArrowsEvent();
        arrowsEvent = e -> {
            e.consume();
            if (e.getCode() == KeyCode.DOWN
                    || e.getCode() == KeyCode.UP
                    || e.getCode() == KeyCode.RIGHT
                    || e.getCode() == KeyCode.LEFT) {
                Content active = EditorController.get().getActiveContent().getContent();
                moveContent(e.getCode(), active);
                ContentTableView.get().refresh();
                refresh();
            }
        };
        addEventHandler(KeyEvent.KEY_PRESSED, arrowsEvent);
    }

    private void hookupContentEvents(List<Content> addedContent) {
        for (Content con : addedContent) {
            con.visibleProperty().addListener((observable, oldValue, newValue) -> {
                refresh();
            });
            List<Asset> correspondingAsset = Controller.get().getAssetsList().stream()
                    .filter(a -> a.getName().equals(con.getItem().getName()))
                    .collect(Collectors.toList());
            correspondingAsset.get(0).relativePathProperty().addListener((observable, oldValue, newValue) -> {
                con.getItem().setRelativePath(newValue);
                con.getItem().setImage(correspondingAsset.get(0).loadImageFromPath());
                refresh();
            });
            correspondingAsset.get(0).nameProperty().addListener((observable, oldValue, newValue) -> {
                con.getItem().setName(newValue);
                ContentTableView.get().refresh();
            });
        }
        refresh();
    }

    private void openContextMenu(Content c, MouseEvent e) {
        final MenuItem remove = new MenuItem("Remove");
        remove.setOnAction(ev -> removeItem(c));
        final MenuItem moveToPointer = new MenuItem("Move to pointer");
        moveToPointer.setOnAction(ev -> moveToPointer(c));
        final MenuItem setInvisible = new MenuItem("Set invisible");
        setInvisible.setOnAction(ev -> setInvisible(c));
        final ContextMenu menu = new ContextMenu(remove, moveToPointer, setInvisible);
        menu.show(this, e.getScreenX(), e.getScreenY());
        addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> {
            ev.consume();
            menu.hide();
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

    private void moveContent(KeyCode keyCode, Content c){
        Coords pos = c.getItem().getPos();
        updatePos(keyCode, pos);
    }

    private void movePointer(KeyCode keyCode) {
        Coords mark = pointer.getMark();
        updatePos(keyCode, mark);
    }

    private void updatePos(KeyCode keyCode, Coords pos) {
        switch (keyCode) {
            case UP -> pos.setY(pos.getY() - 1);
            case LEFT -> pos.setX(pos.getX() - 1);
            case DOWN -> pos.setY(pos.getY() + 1);
            case RIGHT -> pos.setX(pos.getX() + 1);
        }
        refresh();
    }

    private void moveToPointer(Content c) {
        Coords pos = c.getItem().getPos();
        Coords newPos = pointer.getMark();
        pos.setX(newPos.getX());
        pos.setY(newPos.getY());
        ContentTableView.get().refresh();
        refresh();
    }

    private void setInvisible(Content c) {
        c.setVisible(false);
        refresh();
    }

    private void removeItem(Content c) {
        Controller.get().removeContent(c);
        refresh();
    }
}
