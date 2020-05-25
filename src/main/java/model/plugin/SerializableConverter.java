package model.plugin;

import javafx.collections.FXCollections;
import model.asset.Asset;
import model.content.Content;
import model.content.ContentList;
import model.item.*;
import model.layer.Layer;
import model.layer.LayersList;
import model.location.Location;
import model.stage.Coords;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

public class SerializableConverter {

    public static PluginSerializable toPluginSerializable(Plugin plugin) {
        List<LocationSerializable> locations = locationsToSerializable(plugin.getLocations());
        List<AssetSerializable> assets = assetsToSerializable(plugin.getAssets());
        return new PluginSerializable(locations, assets);
    }

    public static Plugin toPlugin(PluginSerializable ps) {
        List<Asset> assets = toAssetObjects(ps.getAssets());
        List<Location> locations = toLocationObjects(ps.getLocations(), assets);
        return new Plugin(ps.getFile(), locations, assets);
    }

    public static List<LocationSerializable> locationsToSerializable(List<Location> input) {
        List<LocationSerializable> output = new ArrayList<>(0);
        for (Location location : input) {
            String name = location.getName();
            int width = location.getWidth();
            int height = location.getHeight();
            List<LayerSerializable> layers = toSerializableLayers(location);
            List<ContentSerializable> contents = toSerializableContents(location);

            LocationSerializable ls = new LocationSerializable(name, width, height, layers, contents);
            output.add(ls);
        }
        return output;
    }

    private static List<LayerSerializable> toSerializableLayers(Location location) {
        List<LayerSerializable> output = new ArrayList<>(0);
        List<Layer> input = location.getLayers().get();
        for (Layer layer : input) {
            int level = layer.getLevel();
            String name = layer.getName();
            boolean visible = layer.getVisible();
            LayerSerializable ls = new LayerSerializable(level, name, visible);
            output.add(ls);
        }
        return output;
    }

    private static List<ContentSerializable> toSerializableContents(Location location) {
        List<ContentSerializable> output = new ArrayList<>(0);
        List<Content> input = location.getContents().get();
        for (Content content : input) {
            ItemSerializable item = toSerializableItem(content.getItem());
            boolean visible = content.isVisible();

            ContentSerializable cs = new ContentSerializable(item, visible);
            output.add(cs);
        }
        return output;
    }

    private static ItemSerializable toSerializableItem(Item item) {
        AssetSerializable asset = toSerializableAsset(item.getAsset());
        CoordinatesSerializable pos = toSerializableCoordinates(item.getPos());
        int level = item.getLevel();
        return new ItemSerializable(asset.getName(), pos, level);
    }

    private static CoordinatesSerializable toSerializableCoordinates(Coords pos) {
        double x = pos.getX();
        double y = pos.getY();
        int z = pos.getZ();
        return new CoordinatesSerializable(x, y, z);
    }

    public static List<Location> toLocationObjects(List<LocationSerializable> input, List<Asset> assets) {
        List<Location> output = FXCollections.observableArrayList();
        for (LocationSerializable ls : input) {
            Location location = new Location();
            location.setName(ls.getName());
            location.setWidth(ls.getWidth());
            location.setHeight(ls.getHeight());

            LayersList layersList = new LayersList();
            layersList.get().setAll(toLayers(ls.getLayers()));
            location.setLayers(layersList);

            ContentList contentList = new ContentList();
            contentList.get().setAll(toContents(ls.getContents(), assets));
            location.setContents(contentList);

            output.add(location);
        }
        return output;
    }

    private static List<Layer> toLayers(List<LayerSerializable> input) {
        List<Layer> output = FXCollections.observableArrayList();
        for (LayerSerializable ls : input) {
            Layer layer = new Layer();
            layer.setLevel(ls.getLevel());
            layer.setName(ls.getName());
            layer.setVisible(ls.isVisible());
            output.add(layer);
        }
        return output;
    }

    private static List<Content> toContents(List<ContentSerializable> input, List<Asset> assets) {
        if (assets.isEmpty()) {
            throw new NoSuchElementException("Assets list is empty");
        }
        List<Content> output = FXCollections.observableArrayList();
        for (ContentSerializable cs : input) {
            Content content = new Content();
            Item item = toItem(cs.getItem(), assets);
            content.setItem(item);
            content.setLevel(item.getLevel());
            content.setName(item.getAsset().getName());
            content.setType(item.getAsset().getType());
            content.setPos(item.getPos());
            content.setVisible(cs.isVisible());
            output.add(content);
        }
        return output;
    }

    private static Item toItem(ItemSerializable i, List<Asset> assets) {
        Item output;
        String name = i.getName();
        List<Asset> oneAsset = assets.stream()
                .filter(a -> a.getName().equals(name))
                .collect(Collectors.toList());
        Asset asset = oneAsset.get(0);
        Coords pos = toCoordinates(i.getPos());
        int level = i.getLevel();
        switch (asset.getType()) {
            case LANDSCAPE -> output = new Landscape(asset, pos, level);
            case COVER -> output = new Cover(asset, pos, level);
            case MOVE_ZONE -> output = new MoveZone(asset, pos, level);
            case FLY_ZONE -> output = new FlyZone(asset, pos, level);
            default -> output = new Landscape(asset, pos, level);
        }
        return output;
    }

    private static Coords toCoordinates(CoordinatesSerializable pos) {
        return new Coords(pos.getX(), pos.getY(), pos.getZ());
    }

    public static List<AssetSerializable> assetsToSerializable(List<Asset> input) {
        List<AssetSerializable> output = new ArrayList<>(0);
        for (Asset asset : input) {
            AssetSerializable as = toSerializableAsset(asset);
            output.add(as);
        }
        return output;
    }

    private static AssetSerializable toSerializableAsset(Asset asset) {
        String name = asset.getName();
        ItemType type = asset.getType();
        String path = asset.getPath();
        return new AssetSerializable(name, type, path);
    }

    static List<Asset> toAssetObjects(List<AssetSerializable> input) {
        List<Asset> output = FXCollections.observableArrayList();
        for (AssetSerializable as : input) {
            Asset asset = new Asset();
            asset.setName(as.getName());
            asset.setType(as.getType());
            asset.setPath(as.getPath());
            output.add(asset);
        }
        return output;
    }
}
