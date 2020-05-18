package model.asset;

import model.content.Content;
import model.item.Cover;
import model.item.FlyZone;
import model.item.Landscape;
import model.item.MoveZone;
import model.stage.Coordinates;
import model.stage.CurrentLayer;

import java.util.ArrayList;
import java.util.List;

public class AssetToContentConverter {

    public static List<Content> convert(List<Asset> assets, Coordinates pos) {
        List<Content> contents = new ArrayList<>(assets.size());
        for (Asset asset
                : assets) {
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
                asset, pos, CurrentLayer.get().getCurrentLevel());
        return new Content(landscape);
    }

    private static Content convertToCover(Asset asset, Coordinates pos) {
        Cover cover = new Cover(
                asset, pos, CurrentLayer.get().getCurrentLevel());
        return new Content(cover);
    }

    private static Content convertToMoveZone(Asset asset, Coordinates pos) {
        MoveZone moveZone = new MoveZone(
                asset, pos, CurrentLayer.get().getCurrentLevel());
        return new Content(moveZone);
    }

    private static Content convertToFlyZone(Asset asset, Coordinates pos) {
        FlyZone flyZone = new FlyZone(
                asset, pos, CurrentLayer.get().getCurrentLevel());
        return new Content(flyZone);
    }
}
