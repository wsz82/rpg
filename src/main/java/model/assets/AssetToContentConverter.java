package model.assets;

import model.content.Content;
import model.items.Landscape;
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
        }
    }

    private static Content convertToLandscape(Asset asset) {
        Landscape landscape = new Landscape(
                asset.getName(), asset.getType(), new Coordinates(0, 0), CurrentLayer.getCurrentLayer(), asset.getImage());
        return new Content(landscape);
    }
}
