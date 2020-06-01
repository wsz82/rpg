package io.wsz.model.content;

import io.wsz.model.item.PosItem;
import javafx.beans.property.*;

import java.util.Objects;

public class Content {
    private final ObjectProperty<PosItem> item = new SimpleObjectProperty<>(this, "item");
    private final IntegerProperty level = new SimpleIntegerProperty(this, "level");
    private final BooleanProperty visible = new SimpleBooleanProperty(this, "visible");

    public Content() {}

    public Content(PosItem item) {
        this.item.set(item);
        this.level.set(item.getLevel());
        this.visible.set(true);
    }

    public void setItem(PosItem item) {
        this.item.set(item);
    }

    public PosItem getItem() {
        return item.get();
    }

    public ObjectProperty<PosItem> itemProperty() {
        return item;
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

    public IntegerProperty levelProperty() {
        return level;
    }

    public void setLevel(int level) {
        this.level.set(level);
        item.get().setLevel(level);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Content content = (Content) o;
        return item.equals(content.item) &&
                level.equals(content.level) &&
                visible.equals(content.visible);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, level, visible);
    }
}
