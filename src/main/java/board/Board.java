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
        for (ContentWithImage cwi : boardContents) {
            ImageView iv = cwi.getImageView();
            getChildren().remove(iv);
        }
        boardContents.clear();
        List<Content> contents = newValue.getContents().get();
        addContentsToStage(contents);
    }

    private void bindWidthAndHeight() {
        prefWidthProperty().bind(
                CurrentLocation.get().currentWidthProperty());
        prefHeightProperty().bind(
                CurrentLocation.get().currentHeightProperty());
    }

    protected void resizeImageWithChangedBoard(ContentWithImage cwi, Image origin, double width, double height) {
        double resizeWidth = width - cwi.getContent().getItem().getPos().getX();
        double resizeHeight = height - cwi.getContent().getItem().getPos().getY();
        if (resizeWidth > origin.getWidth()) {
            resizeWidth = origin.getWidth();
        }
        if (resizeHeight > origin.getHeight()) {
            resizeHeight = origin.getHeight();
        }
        resizeImage(cwi, origin, (int) resizeWidth, (int) resizeHeight);
    }

    protected void resizeRelocatedImage(ContentWithImage cwi, Image origin, double x, double y) {
        int width = (int) (getWidth() - x);
        int height = (int) (getHeight() - y);
        if (width > origin.getWidth()) {
            width = (int) origin.getWidth();
        }
        if (height > origin.getHeight()) {
            height = (int) origin.getHeight();
        }
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

    public int getzPos() {
        return zPos.get();
    }

    public IntegerProperty zPosProperty() {
        return zPos;
    }

    public List<ContentWithImage> getBoardContents() {
        return boardContents;
    }
}
