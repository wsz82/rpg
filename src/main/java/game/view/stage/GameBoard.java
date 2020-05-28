package game.view.stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import model.Controller;
import model.content.Content;
import model.item.Creature;
import model.item.Item;
import model.stage.Coords;

public class GameBoard extends AnchorPane {
    private static GameBoard singleton;

    public static GameBoard get() {
        if (singleton == null) {
            singleton = new GameBoard();
        }
        return singleton;
    }

    private GameBoard(){
    }

    public void refresh() {
        getChildren().clear();

        for (Content content : Controller.get().getCurrentLocation().getContent()) {
            final Item item = content.getItem();
            final Coords pos = item.getPos();
            final double x = pos.getX();
            final double y = pos.getY();
            final int z = pos.getZ();
            final int level = item.getLevel();

            final Image origin = item.getAsset().getImage();
            final int width = Controller.get().getCurrentLocation().getCurrentWidth();
            final int height = Controller.get().getCurrentLocation().getCurrentHeight();
            int resizeWidth = width - (int) x;
            int resizeHeight = height - (int) y;
            if (resizeWidth > origin.getWidth()) {
                resizeWidth = (int) origin.getWidth();
            }
            if (resizeHeight > origin.getHeight()) {
                resizeHeight = (int) origin.getHeight();
            }
            final Image resizedImage = new WritableImage(
                    origin.getPixelReader(), resizeWidth, resizeHeight);
            final ImageView iv = new ImageView(resizedImage);

            iv.setViewOrder(-(level * 1000 + (double) z / 1000));
            getChildren().add(iv);
            setLeftAnchor(iv, x);
            setTopAnchor(iv, y);
            content.setVisible(Controller.get().getCurrentLayer().getVisible());

            pos.zProperty().addListener((observable, oldValue, newValue) -> {
                int zNew = newValue.intValue();
                int levelNew = item.getLevel();
                iv.setViewOrder(-(levelNew * 1000 + (double) zNew / 1000));
            });
            content.levelProperty().addListener((observable, oldValue, newValue) -> {
                int levelNew = newValue.intValue();
                int zNew = item.getPos().getZ();
                iv.setViewOrder(-(levelNew * 1000 + (double) zNew / 1000));
            });

            item.getAsset().pathProperty().addListener((observable, oldValue, newValue) -> {
                iv.setImage(item.getAsset().getImage());
            });

            iv.visibleProperty().bindBidirectional(content.visibleProperty());

            switch (item.getAsset().getType()) {
                case CREATURE -> {
                    boolean[] isActive = new boolean[]{false};
                    iv.setOnMouseClicked(e -> {
                        e.consume();
                        if (e.getButton().equals(MouseButton.PRIMARY)) {
                            isActive[0] = true;
                            setOnMouseClicked(ev -> {
                                if (ev.getButton().equals(MouseButton.PRIMARY)
                                        && isActive[0]) {
                                    Creature creature = (Creature) item;
                                    int creatureWidth = (int) creature.getAsset().getImage().getWidth();
                                    int creatureHeight = (int) creature.getAsset().getImage().getHeight();
                                    double moveX = ev.getX() - (double) creatureWidth/2;
                                    double moveY = ev.getY() - creatureHeight;
                                    Coords dest = new Coords(moveX, moveY, creature.getPos().getZ());
                                    creature.setDest(dest);
                                } else if (ev.getButton().equals(MouseButton.SECONDARY)) {
                                    isActive[0] = false;
                                }
                            });
                        }
                    });
                }
            }
        }
    }
}
