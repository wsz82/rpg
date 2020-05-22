package editor.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
import model.content.Content;
import model.stage.ContentWithImage;

public class ActiveContent {
    private final ObjectProperty<ContentWithImage> ciw = new SimpleObjectProperty<>();
    private final ObjectProperty<Content> content = new SimpleObjectProperty<>();
    private final ObjectProperty<ImageView> image = new SimpleObjectProperty<>();
    private static ActiveContent singleton;

    public static ActiveContent get() {
        if (singleton == null) {
            singleton = new ActiveContent();
        }
        return singleton;
    }

    private ActiveContent(){}

    public Content getContent() {
        return content.get();
    }

    public ObjectProperty<Content> contentProperty() {
        return content;
    }

    public ImageView getImage() {
        return image.get();
    }

    public ObjectProperty<ImageView> imageProperty() {
        return image;
    }

    public ContentWithImage getContentWithImage() {
        return ciw.get();
    }

    public ObjectProperty<ContentWithImage> contentWithImageProperty() {
        return ciw;
    }

    public void setContentWithImage(ContentWithImage iv) {
        this.ciw.set(iv);
        this.content.set(ciw.get().getContent());
        this.image.set(ciw.get().getImageView());
    }
}
