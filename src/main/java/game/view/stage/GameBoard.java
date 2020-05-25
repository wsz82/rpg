package game.view.stage;

import javafx.scene.layout.AnchorPane;
import model.location.CurrentLocation;

public class GameBoard extends AnchorPane {
    private static GameBoard singleton;

    public GameBoard(){
        setPrefHeight(CurrentLocation.get().getCurrentHeight());
        setPrefWidth(CurrentLocation.get().getCurrentWidth());
    }

    public static GameBoard get() {
        if (singleton == null) {
            singleton = new GameBoard();
        }
        return singleton;
    }

    public void redraw() {

    }

//    protected void addContentsToStage(List<Content> contents) {
//        for (Content content : contents) {
//            final Item item = content.getItem();
//            final Coords pos = content.getPos();
//            final double x = pos.getX();
//            final double y = pos.getY();
//            final int z = pos.getZ();
//            final int level = content.getLevel();
//
//            final Image originImage = item.getAsset().getImage();
//            final int width = Controller.get().getCurrentLocation().getCurrentWidth();
//            final int height = Controller.get().getCurrentLocation().getCurrentHeight();
//            final Image resizedImage = new WritableImage(
//                    originImage.getPixelReader(), width - (int) x, height - (int) y);
//            final ImageView iv = new ImageView(resizedImage);
//
//            iv.setViewOrder(-(level*1000 + (double) z/1000));
//            getChildren().add(iv);
//            setLeftAnchor(iv, x);
//            setTopAnchor(iv, y);
//            content.setVisible(CurrentLayer.get().getCurrentLayer().getVisible());
//
//            ContentWithImage cwi = new ContentWithImage(content, iv);
//            boardContents.add(cwi);
//
//            pos.zProperty().addListener((observable, oldValue, newValue) -> {
//                int zNew = newValue.intValue();
//                int levelNew = content.getLevel();
//                iv.setViewOrder(-(levelNew*1000 + (double) zNew/1000));
//            });
//            content.levelProperty().addListener((observable, oldValue, newValue) -> {
//                int levelNew = newValue.intValue();
//                int zNew = content.getPos().getZ();
//                iv.setViewOrder(-(levelNew*1000 + (double) zNew/1000));
//            });
//
//            pos.xProperty().addListener((observable, oldValue, newValue) -> {
//                resizeRelocatedImage(cwi, item.getAsset().getImage(), newValue.doubleValue(), pos.getY());
//                setLeftAnchor(iv, newValue.doubleValue());
//            });
//            pos.yProperty().addListener((observable, oldValue, newValue) -> {
//                resizeRelocatedImage(cwi, item.getAsset().getImage(), pos.getX(), newValue.doubleValue());
//                setTopAnchor(iv, newValue.doubleValue());
//            });
//            item.getAsset().pathProperty().addListener((observable, oldValue, newValue) -> {
//                iv.setImage(item.getAsset().getImage());
//            });
//
//            iv.visibleProperty().bindBidirectional(content.visibleProperty());
//        }
//    }
}
