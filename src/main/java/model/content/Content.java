package model.content;

import javafx.beans.property.*;
import model.item.Item;
import model.item.ItemType;
import model.stage.Coordinates;

import java.util.Objects;

public class Content {
    private final ObjectProperty<Item> item = new SimpleObjectProperty<>(this, "item");
    private final StringProperty name = new SimpleStringProperty(this, "name");
    private final ObjectProperty<ItemType> type = new SimpleObjectProperty<>(this, "type");
    private final IntegerProperty level = new SimpleIntegerProperty(this, "level");
    private final BooleanProperty visible = new SimpleBooleanProperty(this, "visible");
    private final ObjectProperty<Coordinates> pos = new SimpleObjectProperty<>(this, "coords");

    public Content() {}

    public Content(Item item) {
        this.item.set(item);
        this.name.set(item.getName());
        this.type.set(item.getType());
        this.level.set(item.getLevel());
        this.visible.set(true);
        this.pos.set(item.getCoords());
    }

    public void setItem(Item item) {
        this.item.set(item);
    }

    public Item getItem() {
        return item.get();
    }

    public ObjectProperty<? extends Item> itemProperty() {
        return item;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setType(ItemType type) {
        this.type.set(type);
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

    public Coordinates getPos() {
        return pos.get();
    }

    public ObjectProperty<Coordinates> posProperty() {
        return pos;
    }

    public void setPos(Coordinates pos) {
        this.pos.set(pos);
        item.get().setCoords(pos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return item.equals(content.item) &&
                name.equals(content.name) &&
                type.equals(content.type) &&
                level.equals(content.level) &&
                visible.equals(content.visible) &&
                pos.equals(content.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, name, type, level, visible, pos);
    }
}
