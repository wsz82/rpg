package editor.view.stage;

import board.Board;
import editor.model.EditorController;
import editor.view.content.ContentTableView;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import model.Controller;
import model.content.Content;
import model.item.Item;
import model.layer.CurrentLayer;
import model.stage.ContentWithImage;
import model.stage.Coords;

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
    }

    @Override
    protected void addContentsToStage(List<Content> contents) {
        for (Content content : contents) {
            final Item item = content.getItem();
            final Coords pos = content.getPos();
            final double x = pos.getX();
            final double y = pos.getY();
            final int z = pos.getZ();
            final int level = content.getLevel();
            final Image originImage = item.getAsset().getImage();
            final Image resizedImage = new WritableImage(
                    originImage.getPixelReader(), (int) (getWidth() - x), (int) (getHeight() - y));
            final ImageView iv = new ImageView(resizedImage);

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
                resizeImageWithChangedBoard(cwi, item.getAsset().getImage(), newValue.doubleValue(), getHeight());
            });
            prefHeightProperty().addListener((observable, oldValue, newValue) -> {
                resizeImageWithChangedBoard(cwi, item.getAsset().getImage(), getWidth(), newValue.doubleValue());
            });
            pos.xProperty().addListener((observable, oldValue, newValue) -> {
                resizeRelocatedImage(cwi, item.getAsset().getImage(), newValue.doubleValue(), pos.getY());
                setLeftAnchor(iv, newValue.doubleValue());
            });
            pos.yProperty().addListener((observable, oldValue, newValue) -> {
                resizeRelocatedImage(cwi, item.getAsset().getImage(), pos.getX(), newValue.doubleValue());
                setTopAnchor(iv, newValue.doubleValue());
            });
            item.getAsset().pathProperty().addListener((observable, oldValue, newValue) -> {
                final Image changedImage = new WritableImage(
                        item.getAsset().getImage().getPixelReader(),
                        (int) (getWidth() - pos.getX()), (int) (getHeight() - pos.getY()));
                iv.setImage(changedImage);
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
        Coords pos = content.getPos();
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
}
