package editor.model;

import javafx.beans.property.ObjectProperty;
import javafx.scene.image.ImageView;

public class EditorController {
    private static EditorController singleton;

    public static EditorController get() {
        if (singleton == null) {
            singleton = new EditorController();
        }
        return singleton;
    }

    private EditorController(){}

    public ImageView getActiveImage() {
        return ActiveContentImage.get().getImage();
    }

    public ObjectProperty<ImageView> activeImageProperty() {
        return ActiveContentImage.get().imageProperty();
    }

    public void setActiveImage(ImageView iv) {
        ActiveContentImage.get().setImage(iv);
    }
}
