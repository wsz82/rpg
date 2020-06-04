package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.stage.Coords;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class AssetConverter {

    public static List<Content> convertToContent(List<Asset> assets, Coords pos) {
        List<Content> contents = new ArrayList<>(assets.size());
        for (Asset asset
                : assets) {
            Coords newPos = new Coords(pos.getX(), pos.getY());
            if (!pos.is0()) {
                double height = asset.getImage().getHeight();
                newPos.setY(pos.getY() - height);
            }
            Content content = convertToContent(asset, newPos, Controller.get().getCurrentLayer().getLevel());
            contents.add(content);
        }
        return contents;
    }

    public static Content convertToContent(Asset asset, Coords pos, int level) {
        if (asset == null) {
            throw new NoSuchElementException("Asset is null");
        }
        String name = asset.getName();
        ItemType type = asset.getType();
        String path = asset.getRelativePath();

        return switch (asset.getType()) {
            case LANDSCAPE -> new Content(convertToLandscape(name, type, path, pos, level));
            case COVER -> new Content(convertToCover(name, type, path, pos, level));
            case OBSTACLE -> new Content(convertObstacle(name, type, path, pos, level));
            case FLY_ZONE -> new Content(convertToFlyZone(name, type, path, pos, level));
            case CREATURE -> new Content(convertToCreature(name, type, path, pos, level, asset));
            case TELEPORT -> new Content(convertToTeleport(name, type, path, pos, level, asset));
        };
    }

    private static PosItem convertToTeleport(String name, ItemType type, String path, Coords pos, int level,
                                             Asset asset) {
        Teleport origin = (Teleport) asset;
        return new Teleport(name, type, path, pos, level,
                origin.getLocationName(), origin.getExit(), origin.getExitLevel());
    }

    public static Creature convertToCreature(String name, ItemType type, String path, Coords pos, int level,
                                             Asset asset) {
        Creature origin = (Creature) asset;
        return new Creature(name, type, path, pos, level,
                origin.getDest(), origin.getSize(), origin.getControl(), origin.getSpeed());
    }

    public static Landscape convertToLandscape(String name, ItemType type, String path, Coords pos, int level) {
        return new Landscape(name, type, path, pos, level);
    }

    public static Cover convertToCover(String name, ItemType type, String path, Coords pos, int level) {
        return new Cover(name, type, path, pos, level);
    }

    public static Obstacle convertObstacle(String name, ItemType type, String path, Coords pos, int level) {
        return new Obstacle(name, type, path, pos, level);
    }

    public static FlyZone convertToFlyZone(String name, ItemType type, String path, Coords pos, int level) {
        return new FlyZone(name, type, path, pos, level);
    }
}
