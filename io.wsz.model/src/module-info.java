module io.wsz.model {
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.swing;
    requires java.desktop;

    exports io.wsz.model;
    exports io.wsz.model.animation;
    exports io.wsz.model.animation.creature;
    exports io.wsz.model.animation.door;
    exports io.wsz.model.animation.equipment;
    exports io.wsz.model.animation.equipment.container;
    exports io.wsz.model.animation.equipment.weapon;
    exports io.wsz.model.animation.openable;
    exports io.wsz.model.script;
    exports io.wsz.model.script.bool;
    exports io.wsz.model.script.bool.countable;
    exports io.wsz.model.script.bool.countable.item;
    exports io.wsz.model.script.bool.countable.variable;
    exports io.wsz.model.script.bool.equals;
    exports io.wsz.model.script.bool.equals.variable;
    exports io.wsz.model.script.bool.has;
    exports io.wsz.model.script.bool.has.item;
    exports io.wsz.model.script.variable;
    exports io.wsz.model.sizes;
    exports io.wsz.model.dialog;
    exports io.wsz.model.asset;
    exports io.wsz.model.item;
    exports io.wsz.model.layer;
    exports io.wsz.model.locale;
    exports io.wsz.model.location;
    exports io.wsz.model.plugin;
    exports io.wsz.model.textures;
    exports io.wsz.model.stage;
    exports io.wsz.model.world;
}