package io.wsz.model.item;

import io.wsz.model.content.Content;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PosItem extends Asset implements ItemUpdater {
    protected volatile Asset prototype;
    protected volatile Coords pos;
    protected volatile Integer level;
    protected volatile List<Coords> coverLine;
    protected volatile List<List<Coords>> collisionPolygons;

    public PosItem(Asset prototype, String name, ItemType type, String path, Coords pos, Integer level,
                   List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path);
        this.prototype = prototype;
        this.pos = pos;
        this.level = level;
        this.coverLine = coverLine;
        this.collisionPolygons = collisionPolygons;
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
        from.getContentToRemove().add(thisContent);
        target.getContentToAdd().add(thisContent);

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

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public List<Coords> getCoverLine() {
        return coverLine;
    }

    public void setCoverLine(List<Coords> coverLine) {
        this.coverLine = coverLine;
    }

    public List<List<Coords>> getCollisionPolygons() {
        return collisionPolygons;
    }

    public void setCollisionPolygons(List<List<Coords>> collisionPolygons) {
        this.collisionPolygons = collisionPolygons;
    }

    public Asset getPrototype() {
        return prototype;
    }

    public void setPrototype(Asset prototype) {
        this.prototype = prototype;
    }
}
