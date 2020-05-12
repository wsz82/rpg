package model.assets;

import model.content.Content;
import model.items.Cover;
import model.items.FlyZone;
import model.items.Landscape;
import model.items.MoveZone;
import model.stage.Coordinates;
import model.stage.CurrentLayer;

import java.util.ArrayList;
import java.util.List;

public class AssetToContentConverter {

    public static List<Content> convert(List<Asset> assets) {
        List<Content> contents = new ArrayList<>(assets.size());
        for (Asset asset
                : assets) {
            asset.setImageFromPath();
            convertAndAddItem(asset, contents);
        }
        return contents;
    }

    private static void convertAndAddItem(Asset asset, List<Content> contents) {
        switch (asset.getType()) {
            case LANDSCAPE -> contents.add(convertToLandscape(asset));
            case COVER -> contents.add(convertToCover(asset));
            case MOVE_ZONE -> contents.add(convertToMoveZone(asset));
            case FLY_ZONE -> contents.add(convertToFlyZone(asset));
        }
    }

    private static Content convertToLandscape(Asset asset) {
        Landscape landscape = new Landscape(
                asset.getName(), asset.getType(), new Coordinates(0, 0), CurrentLayer.getCurrentLayer(), asset.getImage());
        return new Content(landscape);
    }

    private static Content convertToCover(Asset asset) {
        Cover cover = new Cover(
                asset.getName(), asset.getType(), new Coordinates(0, 0), CurrentLayer.getCurrentLayer(), asset.getImage());
        return new Content(cover);
    }

    private static Content convertToMoveZone(Asset asset) {
        MoveZone moveZone = new MoveZone(
                asset.getName(), asset.getType(), new Coordinates(0, 0), CurrentLayer.getCurrentLayer(), asset.getImage());
        return new Content(moveZone);
    }

    private static Content convertToFlyZone(Asset asset) {
        FlyZone flyZone = new FlyZone(
                asset.getName(), asset.getType(), new Coordinates(0, 0), CurrentLayer.getCurrentLayer(), asset.getImage());
        return new Content(flyZone);
    }
}
