package io.wsz.model.stage;

import io.wsz.model.content.Content;
import javafx.scene.image.ImageView;

public class ContentWithImage {
    private final Content content;
    private final ImageView imageView;

    public ContentWithImage(Content content, ImageView imageView) {
        this.content = content;
        this.imageView = imageView;
    }

    public Content getContent() {
        return content;
    }

    public ImageView getImageView() {
        return imageView;
    }
}
