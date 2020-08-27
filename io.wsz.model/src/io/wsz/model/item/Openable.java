package io.wsz.model.item;

import io.wsz.model.stage.ResolutionImage;

public interface Openable {

    void open();

    void close();

    ResolutionImage getOpenImage();
}
