package model.content;

import javafx.beans.property.*;
import model.items.Item;
import model.items.ItemType;
import model.stage.Coordinates;

public class Content {
    private final ObjectProperty<Item> item;
    private final StringProperty name;
    private final ObjectProperty<ItemType> type;
    private final IntegerProperty level;
    private final BooleanProperty visible;
    private final ObjectProperty<Coordinates> coords;

    public Content() {
        this.item = new SimpleObjectProperty<>(this, "item");
        this.name = new SimpleStringProperty(this, "name");
        this.type = new SimpleObjectProperty<>(this, "type");
        this.level = new SimpleIntegerProperty(this, "level");
        this.visible = new SimpleBooleanProperty(this, "visible");
        this.coords = new SimpleObjectProperty<>(this, "coords");
    }

    public Content(Item item) {
        this.item = new SimpleObjectProperty<>(this, "item");
        this.item.set(item);
        this.name = new SimpleStringProperty(this, "name");
        this.name.set(item.getName());
        this.type = new SimpleObjectProperty<>(this, "type");
        this.type.set(item.getType());
        this.level = new SimpleIntegerProperty(this, "level");
        setLevel(item.getLevel());
        this.visible = new SimpleBooleanProperty(this, "visible");
        setVisible(true);
        this.coords = new SimpleObjectProperty<>(this, "coords");
        setCoords(item.getCoords());
    }

    public Item getItem() {
        return item.get();
    }

    public ObjectProperty<? extends Item> itemProperty() {
        return item;
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public ItemType getType() {
        return type.get();
    }

    public ObjectProperty<ItemType> typeProperty() {
        return type;
    }

    public boolean isVisible() {
        return visible.get();
    }

    public BooleanProperty visibleProperty() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible.set(visible);
    }

    public int getLevel() {
        return level.get();
    }

    public IntegerProperty levelProperty() {
        return level;
    }

    public void setLevel(int level) {
        this.level.set(level);
        item.get().setLevel(level);
    }

    public Coordinates getCoords() {
        return coords.get();
    }

    public ObjectProperty<Coordinates> coordsProperty() {
        return coords;
    }

    public void setCoords(Coordinates coords) {
        this.coords.set(coords);
        item.get().setCoords(coords);
    }
}
