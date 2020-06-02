package editor.model;

import io.wsz.model.content.Content;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ActiveContent {
    private final ObjectProperty<Content> content = new SimpleObjectProperty<>();
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

    public void setContent(Content content) {
        this.content.set(content);
    }
}
