package view.stage;

import javafx.collections.ListChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.content.Content;
import model.items.ImageItem;
import model.items.Item;
import model.location.CurrentLocation;
import model.location.Location;
import model.stage.Coordinates;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Board extends AnchorPane {
    private final Map<Content, ImageView> contentImageMap = new HashMap<>(0);
    private final ListChangeListener<? super Content> locationContentListener = c -> {
        if (!c.next()) {
            return;
        }
        List<Content> addedContent = (List<Content>) c.getAddedSubList();
        addContentsToStage(addedContent);

        List<Content> removedContent = (List<Content>) c.getRemoved();
        for (Content content : removedContent) {
            ImageView imageToRemove = contentImageMap.get(content);
            getChildren().remove(imageToRemove);
            contentImageMap.remove(content);
        }
    };

    Board() {
        bindWidthAndHeight();
        setBorder();
        bindWithLocationAndContentChange();
    }

    private void bindWithLocationAndContentChange() {
        listenToContentChanges();
        CurrentLocation.get().locationProperty().addListener((observable, oldValue, newValue) -> {
            boolean locationIsChanged = !newValue.getName().equals(oldValue.getName());
            if (locationIsChanged) {
                CurrentLocation.get().getContent().removeListener(locationContentListener);
                clearBoardAndInflateWithNewLocation(newValue);
                listenToContentChanges();
            }
        });
    }

    private void listenToContentChanges() {
        CurrentLocation.get().getContent().addListener(locationContentListener);
    }

    private void clearBoardAndInflateWithNewLocation(Location newValue) {
        for (ImageView iv : contentImageMap.values()) {
            getChildren().remove(iv);
        }
        contentImageMap.clear();
        List<Content> contents = newValue.getContents().get();
        addContentsToStage(contents);
    }

    private void addContentsToStage(List<Content> contents) {
        for (Content content : contents) {
            Item item = content.getItem();

            if (item instanceof ImageItem) {
                ImageItem imageItem = (ImageItem) item;
                Image image = imageItem.getImage();
                ImageView iv = new ImageView(image);
                Coordinates pos = content.getCoords();
                double x = pos.getX();
                double y = pos.getY();
                this.getChildren().add(iv);
                setLeftAnchor(iv, x);
                setTopAnchor(iv, y);
                pos.xProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() != oldValue.doubleValue()) {
                        setLeftAnchor(iv, newValue.doubleValue());
                    }
                });
                pos.yProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() != oldValue.doubleValue()) {
                        setTopAnchor(iv, newValue.doubleValue());
                    }
                });

                iv.visibleProperty().bindBidirectional(content.visibleProperty());

                contentImageMap.put(content, iv);
            }
        }
    }

    private void bindWidthAndHeight() {
        prefWidthProperty().bindBidirectional(
                CurrentLocation.get().currentWidthProperty());
        prefHeightProperty().bindBidirectional(
                CurrentLocation.get().currentHeightProperty());
    }

    private void setBorder() {
        BorderStroke[] strokes = new BorderStroke[]{
                new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderStroke.THIN)};
        setBorder(new Border(strokes));
    }
}
