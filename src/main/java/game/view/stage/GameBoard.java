package game.view.stage;

import board.Board;
import editor.view.stage.ContentWithImage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import model.content.Content;
import model.item.Item;
import model.location.CurrentLocation;
import model.stage.Coordinates;
import model.stage.CurrentLayer;

import java.util.List;

class GameBoard extends Board {
    private static GameBoard gameBoard;

    public static GameBoard get() {
        if (gameBoard == null) {
            gameBoard = new GameBoard();
        }
        return gameBoard;
    }

    private GameBoard(){
        addContentsToStage(CurrentLocation.get().getContent());
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

            clipImageX(iv, clipMask, x);
            clipImageY(iv, clipMask, y);
            pos.zProperty().addListener((observable, oldValue, newValue) -> {
                int z = newValue.intValue();
                int level = content.getLevel();
                iv.setViewOrder(-(level*1000 + (double) z/1000));
            });
            content.levelProperty().addListener((observable, oldValue, newValue) -> {
                int level = newValue.intValue();
                int z = content.getPos().getZ();
                iv.setViewOrder(-(level*1000 + (double) z/1000));
            });
            getChildren().add(iv);
            setLeftAnchor(iv, x);
            setTopAnchor(iv, y);

            content.setVisible(CurrentLayer.get().getCurrentLayer().getVisible());

            pos.xProperty().addListener((observable, oldValue, newValue) -> {
                setLeftAnchor(iv, newValue.doubleValue());
                clipImageX(iv, clipMask, newValue.doubleValue());
            });
            pos.yProperty().addListener((observable, oldValue, newValue) -> {
                setTopAnchor(iv, newValue.doubleValue());
                clipImageY(iv, clipMask, newValue.doubleValue());
            });
            item.getAsset().pathProperty().addListener((observable, oldValue, newValue) -> {
                iv.setImage(item.getAsset().getImage());
            });

            iv.visibleProperty().bindBidirectional(content.visibleProperty());

            ContentWithImage contentWithImage = new ContentWithImage(content, iv);
            boardContents.add(contentWithImage);
        }
    }
}
