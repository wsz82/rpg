package io.wsz.model.item;

import io.wsz.model.content.Content;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PosItem extends Asset implements ItemUpdater {
    protected Coords pos;
    protected int level;
    protected volatile boolean generic;

    public PosItem(String name, ItemType type, String path, Coords pos, int level, boolean generic,
                   List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path, coverLine, collisionPolygons);
        this.pos = pos;
        this.level = level;
        this.generic = generic;
    }

    public void changeLocation(Location from, Location target, Layer targetLayer, int targetX, int targetY) {
        List<Content> fromContent = from.getContents().get();
        List<Content> singleContent = fromContent.stream()
                .filter(c -> c.getItem().equals(this))
                .collect(Collectors.toList());
        if (singleContent.isEmpty()) {
            return;
        }
        Content thisContent = singleContent.get(0);
        fromContent.remove(thisContent);
        target.getContents().get().add(thisContent);

        pos.x = targetX;
        pos.y = targetY;
        level = targetLayer.getLevel();
    }

    public Coords getPos() {
        return pos;
    }

    public void setPos(Coords pos) {
        this.pos = pos;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isGeneric() {
        return generic;
    }

    public void setGeneric(boolean generic) {
        this.generic = generic;
    }
}
