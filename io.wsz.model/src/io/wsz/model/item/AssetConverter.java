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
            Coords newPos = new Coords(pos.x, pos.y);
            if (!pos.is0()) {
                double height = asset.getImage().getHeight();
                newPos.y = pos.y - (int) height;
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
            case LANDSCAPE -> new Content(convertToLandscape(name, type, path, pos, level, asset));
            case COVER -> new Content(convertToCover(name, type, path, pos, level, asset));
            case CREATURE -> new Content(convertToCreature(name, type, path, pos, level, asset));
            case TELEPORT -> new Content(convertToTeleport(name, type, path, pos, level, asset));
        };
    }

    private static PosItem convertToTeleport(String name, ItemType type, String path, Coords pos, int level,
                                             Asset asset) {
        Teleport o = (Teleport) asset;
        return new Teleport(name, type, path, pos, level, o.getCoverLine(), o.getCollisionPolygons(),
                o.getLocationName(), o.getExit(), o.getExitLevel());
    }

    public static Creature convertToCreature(String name, ItemType type, String path, Coords pos, int level,
                                             Asset asset) {
        Creature o = (Creature) asset;
        return new Creature(name, type, path, pos, level, o.getCoverLine(), o.getCollisionPolygons(),
                o.getDest(), o.getSize(), o.getControl(), o.getSpeed());
    }

    public static Landscape convertToLandscape(String name, ItemType type, String path, Coords pos, int level,
                                               Asset asset) {
        Landscape o = (Landscape) asset;
        return new Landscape(name, type, path, pos, level, o.getCoverLine(), o.getCollisionPolygons());
    }

    public static Cover convertToCover(String name, ItemType type, String path, Coords pos, int level,
                                       Asset asset) {
        Cover o = (Cover) asset;
        return new Cover(name, type, path, pos, level, o.getCoverLine(), o.getCollisionPolygons());
    }
}
