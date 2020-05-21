package editor.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;

public class ActiveContentImage {
    private final ObjectProperty<ImageView> image = new SimpleObjectProperty<>();
    private static ActiveContentImage singleton;

    public static ActiveContentImage get() {
        if (singleton == null) {
            singleton = new ActiveContentImage();
        }
        return singleton;
    }

    private ActiveContentImage(){}

    public ImageView getImage() {
        return image.get();
    }

    public ObjectProperty<ImageView> imageProperty() {
        return image;
    }

    public void setImage(ImageView iv) {
        this.image.set(iv);
    }
}
