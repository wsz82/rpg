package io.wsz.model.asset;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.item.*;
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
        return switch (asset.getType()) {
            case LANDSCAPE -> convertToLandscape(asset, pos, level);
            case COVER -> convertToCover(asset, pos, level);
            case MOVE_ZONE -> convertToMoveZone(asset, pos, level);
            case FLY_ZONE -> convertToFlyZone(asset, pos, level);
            case CREATURE -> convertToCreature(asset, pos, level);
        };
    }

    private static Content convertToCreature(Asset asset, Coords pos, int level) {
        Creature creature = new Creature(
                asset, pos, level);
        return new Content(creature);
    }

    private static Content convertToLandscape(Asset asset, Coords pos, int level) {
        Landscape landscape = new Landscape(
                asset, pos, level);
        return new Content(landscape);
    }

    private static Content convertToCover(Asset asset, Coords pos, int level) {
        Cover cover = new Cover(
                asset, pos, level);
        return new Content(cover);
    }

    private static Content convertToMoveZone(Asset asset, Coords pos, int level) {
        MoveZone moveZone = new MoveZone(
                asset, pos, level);
        return new Content(moveZone);
    }

    private static Content convertToFlyZone(Asset asset, Coords pos, int level) {
        FlyZone flyZone = new FlyZone(
                asset, pos, level);
        return new Content(flyZone);
    }
}
