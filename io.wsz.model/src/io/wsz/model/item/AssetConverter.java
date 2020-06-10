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
                    name, type, path, clonePos, level, coverLine, collisionPolygons, a));
            case COVER -> new Content(convertToCover(
                    name, type, path, clonePos, level, coverLine, collisionPolygons, a));
            case CREATURE -> new Content(convertToCreature(
                    name, type, path, clonePos, level, coverLine, collisionPolygons, a));
            case TELEPORT -> new Content(convertToTeleport(
                    name, type, path, clonePos, level, coverLine, collisionPolygons, a));
        };
    }

    private static PosItem convertToTeleport(
            String name, ItemType type, String path, Coords pos, int level,
            List<Coords> coverLine, List<List<Coords>> collisionPolygons,
            Asset asset) {
        Teleport o = (Teleport) asset;
        Coords exit = o.getExit();
        Coords cloneExit = null;
        if (exit != null) {
            cloneExit = new Coords(exit.x, exit.y);
        }
        return new Teleport(
                name, type, path, pos, level, true,
                coverLine, collisionPolygons,
                o.getLocationName(), cloneExit, o.getExitLevel());
    }

    public static Creature convertToCreature(
            String name, ItemType type, String path, Coords pos, int level,
            List<Coords> coverLine, List<List<Coords>> collisionPolygons,
            Asset asset) {
        Creature o = (Creature) asset;
        Coords dest = o.getDest();
        Coords cloneDest = null;
        if (dest != null) {
            cloneDest = new Coords(dest.x, dest.y);
        }
        return new Creature(
                name, type, path, pos, level, true,
                coverLine, collisionPolygons,
                cloneDest, o.getSize(), o.getControl(), o.getSpeed());
    }

    public static Landscape convertToLandscape(
            String name, ItemType type, String path, Coords pos, int level,
            List<Coords> coverLine, List<List<Coords>> collisionPolygons,
            Asset asset) {
        Landscape o = (Landscape) asset;
        return new Landscape(
                name, type, path, pos, level, true,
                coverLine, collisionPolygons);
    }

    public static Cover convertToCover(
            String name, ItemType type, String path, Coords pos, int level,
            List<Coords> coverLine, List<List<Coords>> collisionPolygons,
            Asset asset) {
        Cover o = (Cover) asset;
        return new Cover(
                name, type, path, pos, level, true,
                coverLine, collisionPolygons);
    }
}
