package editor.view.stage;

import board.Board;
import editor.model.EditorController;
import editor.view.content.ContentTableView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.Controller;
import model.content.Content;
import model.item.Item;
import model.location.CurrentLocation;
import model.stage.Coordinates;
import model.stage.CurrentLayer;

import java.util.List;

class EditorBoard extends Board {
    private static EditorBoard editorBoard;

    static EditorBoard get() {
        if (editorBoard == null) {
            editorBoard = new EditorBoard();
        }
        return editorBoard;
    }

    private EditorBoard() {
        bindWidthAndHeight();
        setBorder(getDefinedBorder());
    }

    @Override
    protected void addContentsToStage(List<Content> contents) {
        for (Content content : contents) {
            final Item item = content.getItem();
            final Image image = item.getAsset().getImage();
            final ImageView iv = new ImageView(image);
            final Coordinates pos = content.getPos();
            final Rectangle clipMask = new Rectangle();
            final double x = pos.getX();
            final double y = pos.getY();
            final int z = pos.getZ();
            final int level = content.getLevel();

            clipImageX(iv, clipMask, x);
            clipImageY(iv, clipMask, y);
            iv.setViewOrder(-(level*1000 + (double) z/1000));
            getChildren().add(iv);
            setLeftAnchor(iv, x);
            setTopAnchor(iv, y);
            content.setVisible(CurrentLayer.get().getCurrentLayer().getVisible());

            ContentWithImage cwi = new ContentWithImage(content, iv);
            boardContents.add(cwi);

            pos.zProperty().addListener((observable, oldValue, newValue) -> {
                int zNew = newValue.intValue();
                int levelNew = content.getLevel();
                iv.setViewOrder(-(levelNew*1000 + (double) zNew/1000));
            });
            content.levelProperty().addListener((observable, oldValue, newValue) -> {
                int levelNew = newValue.intValue();
                int zNew = content.getPos().getZ();
                iv.setViewOrder(-(levelNew*1000 + (double) zNew/1000));
            });

            prefWidthProperty().addListener((observable, oldValue, newValue) -> {
                clipMask.setWidth(newValue.doubleValue() - pos.getX());
                iv.setClip(clipMask);
            });
            pos.xProperty().addListener((observable, oldValue, newValue) -> {
                setLeftAnchor(iv, newValue.doubleValue());
                clipImageX(iv, clipMask, newValue.doubleValue());
            });
            prefHeightProperty().addListener((observable, oldValue, newValue) -> {
                clipMask.setHeight(newValue.doubleValue() - pos.getY());
                iv.setClip(clipMask);
            });
            pos.yProperty().addListener((observable, oldValue, newValue) -> {
                setTopAnchor(iv, newValue.doubleValue());
                clipImageY(iv, clipMask, newValue.doubleValue());
            });
            item.getAsset().pathProperty().addListener((observable, oldValue, newValue) -> {
                iv.setImage(item.getAsset().getImage());
            });

            iv.visibleProperty().bindBidirectional(content.visibleProperty());

            iv.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                zPos.set(content.getPos().getZ());
            });

            iv.setOnContextMenuRequested(event -> {
                MenuItem remove = new MenuItem("Remove");
                remove.setOnAction(ev -> removeItem(content));
                MenuItem setInvisible = new MenuItem("Set invisible");
                setInvisible.setOnAction(e -> setInvisible(content));
                ContextMenu menu = new ContextMenu(remove, setInvisible);
                menu.show(this, event.getScreenX(), event.getScreenY());

                this.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
                    menu.hide();
                });
            });

            iv.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    EditorController.get().getActiveContent().setContentWithImage(cwi);
                }
            });
            iv.setOnMouseClicked(e -> {
                if (e.getButton().equals(MouseButton.PRIMARY)) {
                    iv.setFocusTraversable(true);
                    iv.requestFocus();
                    iv.setOnKeyPressed(ev -> {
                        ev.consume();
                        if (ev.getCode() == KeyCode.DOWN
                                || ev.getCode() == KeyCode.UP
                                || ev.getCode() == KeyCode.RIGHT
                                || ev.getCode() == KeyCode.LEFT) {
                            moveContent(ev.getCode(), content);
                            ContentTableView.get().refresh();
                        }
                    });
                }
            });
        }
    }

    void moveContent(KeyCode keyCode, Content content){
        Coordinates pos = content.getPos();
        switch (keyCode) {
            case UP -> {
                int y = (int) pos.getY();
                if (y > 0) pos.setY(pos.getY() - 1);
            }
            case LEFT -> {
                int x = (int) pos.getX();
                if (x > 0) pos.setX(pos.getX() - 1);
            }
            case DOWN -> {
                pos.setY(pos.getY() + 1);
            }
            case RIGHT -> {
                pos.setX(pos.getX() + 1);
            }
        }
    }

    private void setInvisible(Content content) {
        content.setVisible(false);
    }

    private void removeItem(Content content) {
        Controller.get().removeContent(content);
    }

    private void bindWidthAndHeight() {
        prefWidthProperty().bindBidirectional(
                CurrentLocation.get().currentWidthProperty());
        prefHeightProperty().bindBidirectional(
                CurrentLocation.get().currentHeightProperty());
    }

    private Border getDefinedBorder() {
        BorderStroke[] strokes = new BorderStroke[]{
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)};
        return new Border(strokes);
    }
}
