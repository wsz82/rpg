package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.stage.Coords;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class AssetToContentConverter {

    public static List<Content> convert(List<Asset> assets, Coords pos) {
        List<Content> contents = new ArrayList<>(assets.size());
        for (Asset asset
                : assets) {
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
            case LANDSCAPE -> convertToLandscape(name, type, path, pos, level);
            case COVER -> convertToCover(name, type, path, pos, level);
            case MOVE_ZONE -> convertToMoveZone(name, type, path, pos, level);
            case FLY_ZONE -> convertToFlyZone(name, type, path, pos, level);
            case CREATURE -> convertToCreature(name, type, path, pos, level);
        };
    }

    private static Content convertToCreature(String name, ItemType type, String path, Coords pos, int level) {
        Creature creature = new Creature(name, type, path, pos, level);
        return new Content(creature);
    }

    private static Content convertToLandscape(String name, ItemType type, String path, Coords pos, int level) {
        Landscape landscape = new Landscape(name, type, path, pos, level);
        return new Content(landscape);
    }

    private static Content convertToCover(String name, ItemType type, String path, Coords pos, int level) {
        Cover cover = new Cover(name, type, path, pos, level);
        return new Content(cover);
    }

    private static Content convertToMoveZone(String name, ItemType type, String path, Coords pos, int level) {
        MoveZone moveZone = new MoveZone(name, type, path, pos, level);
        return new Content(moveZone);
    }

    private static Content convertToFlyZone(String name, ItemType type, String path, Coords pos, int level) {
        FlyZone flyZone = new FlyZone(name, type, path, pos, level);
        return new Content(flyZone);
    }
}
