package view.stage;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.asset.Asset;
import model.asset.AssetsList;
import model.content.Content;
import model.item.ImageItem;
import model.item.Item;
import model.location.CurrentLocation;
import model.location.Location;
import model.stage.Coordinates;
import model.stage.CurrentLayer;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

class Board extends AnchorPane {
    private static Board board;
    private final List<ContentWithImage> boardContents = new LinkedList<>();
    private final ListChangeListener<Content> locationContentListener = c -> {
        if (!c.next()) {
            return;
        }
        List<Content> addedContent = (List<Content>) c.getAddedSubList();
        addContentsToStage(addedContent);

        List<Content> removedContent = (List<Content>) c.getRemoved();
        for (Content content : removedContent) {
            List<ContentWithImage> contentsWithImages = boardContents.stream()
                    .filter(cwi -> cwi.getContent().equals(content))
                    .collect(Collectors.toList());

            ContentWithImage contentWithImage = contentsWithImages.get(0);
            ImageView imageToRemove = contentWithImage.getImageView();
            getChildren().remove(imageToRemove);
            boardContents.remove(contentWithImage);
        }
    };
    private final IntegerProperty zPos = new SimpleIntegerProperty();

    static Board get() {
        if (board == null) {
            board = new Board();
        }
        return board;
    }

    private Board() {
        bindWidthAndHeight();
        setBorder(getDefinedBorder());
        bindWithLocationAndContentChange();
    }

    private void bindWithLocationAndContentChange() {
        CurrentLocation.get().getContent().addListener(locationContentListener);
        CurrentLocation.get().locationProperty().addListener((observable, oldValue, newValue) -> {
            CurrentLocation.get().getContent().removeListener(locationContentListener);
            clearBoardAndInflateWithNewLocation(newValue);
            CurrentLocation.get().getContent().addListener(locationContentListener);
        });
    }

    private void clearBoardAndInflateWithNewLocation(Location newValue) {
        for (ContentWithImage contentWithImage : boardContents) {
            ImageView iv = contentWithImage.getImageView();
            getChildren().remove(iv);
        }
        boardContents.clear();
        List<Content> contents = newValue.getContents().get();
        addContentsToStage(contents);
    }

    private void addContentsToStage(List<Content> contents) {
        for (Content content : contents) {
            Item item = content.getItem();

            if (item instanceof ImageItem) {
                final ImageItem imageItem = (ImageItem) item;
                Image image = imageItem.getImage();
                if (image == null) {
                    for (Asset asset : AssetsList.get()) {
                        if (asset.getName().equals(imageItem.getName())) {
                            image = asset.getImage();
                        }
                    }
                }
                final ImageView iv = new ImageView(image);
                final Coordinates pos = content.getPos();
                final Rectangle clipMask = new Rectangle();
                double x = pos.getX();
                double y = pos.getY();

                clipImageX(iv, clipMask, x);
                clipImageY(iv, clipMask, y);
                getChildren().add(iv);
                setLeftAnchor(iv, x);
                setTopAnchor(iv, y);

                content.setVisible(CurrentLayer.get().getCurrentLayer().getVisible());

                prefWidthProperty().addListener((observable, oldValue, newValue) -> {
                    clipMask.setWidth(newValue.doubleValue() - x);
                    iv.setClip(clipMask);
                });
                pos.xProperty().addListener((observable, oldValue, newValue) -> {
                    setLeftAnchor(iv, newValue.doubleValue());
                    clipImageX(iv, clipMask, newValue.doubleValue());
                });
                prefHeightProperty().addListener((observable, oldValue, newValue) -> {
                    clipMask.setHeight(newValue.doubleValue() - y);
                    iv.setClip(clipMask);
                });
                pos.yProperty().addListener((observable, oldValue, newValue) -> {
                    setTopAnchor(iv, newValue.doubleValue());
                    clipImageY(iv, clipMask, newValue.doubleValue());
                });
                pos.zProperty().addListener((observable, oldValue, newValue) -> {
                    sortContent(boardContents);
                });
                content.levelProperty().addListener((observable, oldValue, newValue) -> {
                    sortContent(boardContents);
                });

                iv.visibleProperty().bindBidirectional(content.visibleProperty());

                ContentWithImage contentWithImage = new ContentWithImage(content, iv);
                boardContents.add(contentWithImage);

                iv.addEventHandler(MouseEvent.MOUSE_MOVED, event -> {
                    zPos.set(content.getPos().getZ());
                });

                sortContent(boardContents);
            }
        }
    }

    private void sortContent(List<ContentWithImage> sortedContent) {
        boardContents.sort(new ContentWithImageComparator());
        getChildren().clear();
        for (ContentWithImage ciw : sortedContent) {
            final ImageView iv = ciw.getImageView();
            final Content content = ciw.getContent();
            final Coordinates pos = content.getPos();
            final Rectangle clipMask = new Rectangle();
            double x = pos.getX();
            double y = pos.getY();

            clipImageX(iv, clipMask, x);
            clipImageY(iv, clipMask, y);
            getChildren().add(iv);
            setLeftAnchor(iv, x);
            setTopAnchor(iv, y);
        }
    }

    private void clipImageX(ImageView iv, Rectangle clipMask, double x) {
        clipMask.setWidth(CurrentLocation.get().getCurrentWidth() - x);
        iv.setClip(clipMask);
    }

    private void clipImageY(ImageView iv, Rectangle clipMask, double y) {
        clipMask.setHeight(CurrentLocation.get().getCurrentHeight() - y);
        iv.setClip(clipMask);
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

    public int getzPos() {
        return zPos.get();
    }

    public IntegerProperty zPosProperty() {
        return zPos;
    }
}
