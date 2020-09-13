package io.wsz.model.item;

import io.wsz.model.stage.ResolutionImage;

public interface Openable {

    boolean isOpen();

    void open();

    void close();

    ResolutionImage getOpenImage();
}
