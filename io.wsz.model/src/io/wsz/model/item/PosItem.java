package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;

import java.util.List;
import java.util.stream.Collectors;

public abstract class PosItem extends Asset implements ItemUpdater {
    protected volatile boolean generic;
    protected Coords pos;
    protected int level;
    protected volatile List<Coords> coverLine;
    protected volatile List<List<Coords>> collisionPolygons;

    public PosItem(String name, ItemType type, String path, Coords pos, int level, boolean generic,
                   List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        super(name, type, path);
        this.pos = pos;
        this.level = level;
        this.generic = generic;
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

    public List<Coords> getCoverLine() {
        return coverLine;
    }

    public void setCoverLine(List<Coords> coverLine) {
        this.coverLine = coverLine;
        if (this instanceof PosItem) {
            if (this.getPos() != null) {
                return;
            }
            Controller.get().getLocationsList().forEach(l -> {
                l.getContents().get().stream()
                        .filter(c -> c.getItem().getName().equals(getName()))
                        .filter(c -> c.getItem().isGeneric())
                        .forEach(c -> c.getItem().setCoverLine(coverLine));
            });
        }
    }

    public List<List<Coords>> getCollisionPolygons() {
        return collisionPolygons;
    }

    public void setCollisionPolygons(List<List<Coords>> collisionPolygons) {
        this.collisionPolygons = collisionPolygons;
        if (this instanceof PosItem) {
            if (this.getPos() != null) {
                return;
            }
            Controller.get().getLocationsList().forEach(l -> {
                l.getContents().get().stream()
                        .filter(c -> c.getItem().getName().equals(getName()))
                        .filter(c -> c.getItem().isGeneric())
                        .forEach(c -> c.getItem().setCollisionPolygons(collisionPolygons));
            });
        }
    }
}
