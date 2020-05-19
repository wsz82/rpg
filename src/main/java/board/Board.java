package board;

import editor.view.stage.ContentWithImage;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import model.content.Content;
import model.location.CurrentLocation;
import model.location.Location;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Board extends AnchorPane {
    protected final List<ContentWithImage> boardContents = new LinkedList<>();
    protected final IntegerProperty zPos = new SimpleIntegerProperty();
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

    protected Board(){
        bindWithLocationAndContentChange();
    }

    protected abstract void addContentsToStage(List<Content> contents);

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

    protected void clipImageX(ImageView iv, Rectangle clipMask, double x) {
        clipMask.setWidth(CurrentLocation.get().getCurrentWidth() - x);
        iv.setClip(clipMask);
    }

    protected void clipImageY(ImageView iv, Rectangle clipMask, double y) {
        clipMask.setHeight(CurrentLocation.get().getCurrentHeight() - y);
        iv.setClip(clipMask);
    }

    public int getzPos() {
        return zPos.get();
    }

    public IntegerProperty zPosProperty() {
        return zPos;
    }
}
