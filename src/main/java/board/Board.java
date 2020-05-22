package board;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import model.Controller;
import model.content.Content;
import model.location.CurrentLocation;
import model.location.Location;
import model.stage.ContentWithImage;

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
        bindWidthAndHeight();
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

    protected void resizeImageWithChangedBoard(ContentWithImage cwi, Image origin, double width, double height) {
        resizeImage(cwi, origin, (int) width, (int) height);
    }

    protected void resizeRelocatedImage(ContentWithImage cwi, Image origin, double x, double y) {
        int width = (int) (getWidth() - x);
        int height = (int) (getHeight() - y);
        resizeImage(cwi, origin, width, height);
    }

    private void resizeImage(ContentWithImage cwi, Image origin, int width, int height) {
        if (width < 1 || height < 1) {
            Controller.get().removeContent(cwi.getContent());
            return;
        }
        Image wi = new WritableImage(
                origin.getPixelReader(), width, height);
        cwi.getImageView().setImage(wi);
    }

    private void bindWidthAndHeight() {
        prefWidthProperty().bindBidirectional(
                CurrentLocation.get().currentWidthProperty());
        prefHeightProperty().bindBidirectional(
                CurrentLocation.get().currentHeightProperty());
    }

    public int getzPos() {
        return zPos.get();
    }

    public IntegerProperty zPosProperty() {
        return zPos;
    }

}
