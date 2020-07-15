package io.wsz.model.item;

import javafx.scene.image.Image;

public interface Openable {

    void open();

    void close();

    Image getOpenImage();

    void setOpenImage(Image image);
}
