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

    public static List<Content> convert(List<Asset> assets, Coordinates pos) {
        List<Content> contents = new ArrayList<>(assets.size());
        for (Asset asset
                : assets) {
            asset.setImageFromPath();
            convertAndAddItem(asset, contents, pos);
        }
        return contents;
    }

    private static void convertAndAddItem(Asset asset, List<Content> contents, Coordinates pos) {
        switch (asset.getType()) {
            case LANDSCAPE -> contents.add(convertToLandscape(asset, pos));
            case COVER -> contents.add(convertToCover(asset, pos));
            case MOVE_ZONE -> contents.add(convertToMoveZone(asset, pos));
            case FLY_ZONE -> contents.add(convertToFlyZone(asset, pos));
        }
    }

    private static Content convertToLandscape(Asset asset, Coordinates pos) {
        Landscape landscape = new Landscape(
                asset.getName(), asset.getType(), pos, CurrentLayer.getCurrentLayer(), asset.getImage());
        return new Content(landscape);
    }

    private static Content convertToCover(Asset asset, Coordinates pos) {
        Cover cover = new Cover(
                asset.getName(), asset.getType(), pos, CurrentLayer.getCurrentLayer(), asset.getImage());
        return new Content(cover);
    }

    private static Content convertToMoveZone(Asset asset, Coordinates pos) {
        MoveZone moveZone = new MoveZone(
                asset.getName(), asset.getType(), pos, CurrentLayer.getCurrentLayer(), asset.getImage());
        return new Content(moveZone);
    }

    private static Content convertToFlyZone(Asset asset, Coordinates pos) {
        FlyZone flyZone = new FlyZone(
                asset.getName(), asset.getType(), pos, CurrentLayer.getCurrentLayer(), asset.getImage());
        return new Content(flyZone);
    }
}
