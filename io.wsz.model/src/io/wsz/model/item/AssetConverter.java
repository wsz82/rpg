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
            if (!pos.is0()) {
                double height = asset.getImage().getHeight();
                pos.setY(pos.getY() - height);
            }
            Content content = convertToContent(asset, pos, Controller.get().getCurrentLayer().getLevel());
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
        String path = asset.getPath();
        return switch (asset.getType()) {
            case LANDSCAPE -> new Content(convertToLandscape(name, type, path, pos, level));
            case COVER -> new Content(convertToCover(name, type, path, pos, level));
            case MOVE_ZONE -> new Content(convertToMoveZone(name, type, path, pos, level));
            case FLY_ZONE -> new Content(convertToFlyZone(name, type, path, pos, level));
            case CREATURE -> new Content(convertToCreature(name, type, path, pos, level, asset));
        };
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

    public static MoveZone convertToMoveZone(String name, ItemType type, String path, Coords pos, int level) {
        return new MoveZone(name, type, path, pos, level);
    }

    public static FlyZone convertToFlyZone(String name, ItemType type, String path, Coords pos, int level) {
        return new FlyZone(name, type, path, pos, level);
    }
}
