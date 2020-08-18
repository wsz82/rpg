module io.wsz.model {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.desktop;

    exports io.wsz.model;
    exports io.wsz.model.animation;
    exports io.wsz.model.sizes;
    exports io.wsz.model.dialog;
    exports io.wsz.model.asset;
    exports io.wsz.model.item;
    exports io.wsz.model.layer;
    exports io.wsz.model.location;
    exports io.wsz.model.plugin;
    exports io.wsz.model.stage;
    exports io.wsz.model.world;
}