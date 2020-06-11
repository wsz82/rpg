package io.wsz.model.item;

import io.wsz.model.content.Content;
import io.wsz.model.stage.Coords;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class AssetConverter {

    public static Content convertToContent(Asset a, Coords pos, int level) {
        if (a == null) {
            throw new NoSuchElementException("Asset is null");
        }
        PosItem pi = (PosItem) a;
        Coords clonePos = new Coords(pos.x, pos.y);
        List<Coords> coverLine = new ArrayList<>();
        if (pi.getCoverLine() != null) {
            coverLine.addAll(pi.getCoverLine());
        }
        List<List<Coords>> collisionPolygons = new ArrayList<>();
        if (pi.getCollisionPolygons() != null) {
            collisionPolygons.addAll(pi.getCollisionPolygons());
        }

        String name = a.getName();
        ItemType type = a.getType();
        String path = a.getRelativePath();

        return switch (a.getType()) {
            case LANDSCAPE -> new Content(convertToLandscape(
                    a, name, type, path, clonePos, level, coverLine, collisionPolygons));
            case COVER -> new Content(convertToCover(
                    a, name, type, path, clonePos, level, coverLine, collisionPolygons));
            case CREATURE -> new Content(convertToCreature(
                    a, name, type, path, clonePos, level, coverLine, collisionPolygons));
            case TELEPORT -> new Content(convertToTeleport(
                    a, name, type, path, clonePos, level, coverLine, collisionPolygons));
        };
    }

    private static PosItem convertToTeleport(
            Asset prototype, String name, ItemType type, String path, Coords pos, Integer level,
            List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        Teleport o = (Teleport) prototype;
        Coords exit = o.getExit();
        Coords cloneExit = null;
        if (exit != null) {
            cloneExit = new Coords(exit.x, exit.y);
        }
        return new Teleport(
                prototype, name, type, path, pos, level,
                coverLine, collisionPolygons,
                o.getLocationName(), cloneExit, o.getExitLevel());
    }

    public static Creature convertToCreature(
            Asset prototype, String name, ItemType type, String path, Coords pos, Integer level,
            List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        Creature o = (Creature) prototype;
        Coords dest = o.getDest();
        Coords cloneDest = null;
        if (dest != null) {
            cloneDest = new Coords(dest.x, dest.y);
        }
        return new Creature(
                prototype, name, type, path, pos, level,
                coverLine, collisionPolygons,
                cloneDest, o.getSize(), o.getControl(), o.getSpeed());
    }

    public static Landscape convertToLandscape(
            Asset prototype,String name, ItemType type, String path, Coords pos, Integer level,
            List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        return new Landscape(
                prototype, name, type, path, pos, level,
                coverLine, collisionPolygons);
    }

    public static Cover convertToCover(
            Asset prototype, String name, ItemType type, String path, Coords pos, Integer level,
            List<Coords> coverLine, List<List<Coords>> collisionPolygons) {
        return new Cover(
                prototype, name, type, path, pos, level,
                coverLine, collisionPolygons);
    }
}
