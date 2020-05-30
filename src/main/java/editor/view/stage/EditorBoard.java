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
            final Coords pos = item.getPos();
            final double x = pos.getX();
            final double y = pos.getY();
            final int z = pos.getZ();
            final int level = item.getLevel();

            final ImageView iv = new ImageView();
            setItemsImageForImageView(item, iv);

            iv.setViewOrder(-(level*1000 + (double) z/1000));
            getChildren().add(iv);
            setLeftAnchor(iv, x);
            setTopAnchor(iv, y);
            content.setVisible(Controller.get().getCurrentLayer().getLayer().getVisible());

            ContentWithImage cwi = new ContentWithImage(content, iv);
            boardContents.add(cwi);

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
            pos.zProperty().addListener((observable, oldValue, newValue) -> {
                int zNew = newValue.intValue();
                int levelNew = item.getLevel();
                iv.setViewOrder(-(levelNew*1000 + (double) zNew/1000));
            });
            content.levelProperty().addListener((observable, oldValue, newValue) -> {
                int levelNew = newValue.intValue();
                int zNew = item.getPos().getZ();
                iv.setViewOrder(-(levelNew*1000 + (double) zNew/1000));
            });

            item.getAsset().pathProperty().addListener((observable, oldValue, newValue) -> {
                setItemsImageForImageView(item, iv);
            });

            iv.visibleProperty().bindBidirectional(content.visibleProperty());

            iv.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                zPos.set(item.getPos().getZ());
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

    private void setItemsImageForImageView(Item item, ImageView iv) {
        Image img = item.getAsset().getImage();
        int width = Controller.get().getCurrentLocation().getCurrentWidth();
        int height = Controller.get().getCurrentLocation().getCurrentHeight();
        int resizeWidth = width - (int) item.getPos().getX();
        int resizeHeight = height - (int) item.getPos().getY();
        if (resizeWidth > img.getWidth()) {
            resizeWidth = (int) img.getWidth();
        }
        if (resizeHeight > img.getHeight()) {
            resizeHeight = (int) img.getHeight();
        }
        final Image changedImage = new WritableImage(
                img.getPixelReader(), resizeWidth, resizeHeight);
        iv.setImage(changedImage);
    }

    void moveContent(KeyCode keyCode, Content content){
        Coords pos = content.getItem().getPos();
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
