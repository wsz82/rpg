package editor.view.stage;

import editor.model.EditorController;
import editor.view.content.ContentTableView;
import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.content.ContentComparator;
import io.wsz.model.item.Asset;
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

import java.util.List;
import java.util.stream.Collectors;

public class EditorCanvas extends Canvas {
    private static EditorCanvas singleton;
    private boolean drawMarker;
    private Image markerImage;
    private double markerX, markerY;

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

        List<Content> contents = Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> c.isVisible())
                .collect(Collectors.toList());
        for (Content c : contents) {
            final PosItem item = c.getItem();
            final Coords pos = item.getPos();
            final double x = pos.getX();
            final double y = pos.getY();

            if (c.isVisible()) {
                gc.drawImage(item.getImage(), x, y);
            }
        }

        if (drawMarker) {
            gc.drawImage(markerImage, markerX, markerY);
        }
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
            if (e.getButton().equals(MouseButton.PRIMARY)) {
                e.consume();
                setFocusTraversable(true);
                requestFocus();
                Content c = lookForContent(e.getX(), e.getY());
                if (c == null) {
                    return;
                }
                makeActive(c);
            } else if (e.getButton().equals(MouseButton.SECONDARY)) {
                e.consume();
                Content c = lookForContent(e.getX(), e.getY());
                if (c == null) {
                    return;
                }
                openContextMenu(c, e);
            }
        });
    }

    private void hookupContentEvents(List<Content> addedContent) {
        for (Content con : addedContent) {
            con.visibleProperty().addListener((observable, oldValue, newValue) -> {
                refresh();
            });
            List<Asset> correspondingAsset = Controller.get().getAssetsList().stream()
                    .filter(a -> a.getName().equals(con.getItem().getName()))
                    .collect(Collectors.toList());
            correspondingAsset.get(0).pathProperty().addListener((observable, oldValue, newValue) -> {
                con.getItem().setImage(correspondingAsset.get(0).getImage());
                refresh();
            });
            correspondingAsset.get(0).nameProperty().addListener((observable, oldValue, newValue) -> {
                con.getItem().setName(newValue);
                ContentTableView.get().refresh();
            });
        }
        refresh();
    }

    private void makeActive(Content c) {
        EditorController.get().getActiveContent().setContent(c);
        final EventHandler<KeyEvent> onActive = e -> {
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
        addEventHandler(KeyEvent.KEY_PRESSED, onActive);
        addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton().equals(MouseButton.SECONDARY)) {
                e.consume();
                removeEventHandler(KeyEvent.KEY_PRESSED, onActive);
            }
        });
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
    }

    private Content lookForContent(double x, double y) {
        List<Content> contents = Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> c.isVisible())
                .filter(c -> c.getItem().getLevel() <= Controller.get().getCurrentLayer().getLevel()) //TODO change
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
            double cWidth = c.getItem().getImage().getWidth();
            boolean fitX = x >= cX && x <= cX + cWidth; //TODO transparent looking
            if (!fitX) {
                continue;
            }
            double cY = c.getItem().getPos().getY();
            double cHeight = c.getItem().getImage().getHeight();
            boolean fitY = y >= cY && y <= cY + cHeight;
            if (!fitY) {
                continue;
            }
            return c;
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
        gc.setFill(Color.LIGHTGREY);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    void moveContent(KeyCode keyCode, Content c){
        Coords pos = c.getItem().getPos();
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
        Coords newPos = Pointer.getMark();
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

    public void drawMarker(Image markerImage, double x, double y) {
        this.markerX = x;
        this.markerY = y;
        this.drawMarker = true;
        this.markerImage = markerImage;
        refresh();
    }

    public void removeMarker() {
        this.drawMarker = false;
        refresh();
    }
}
