package io.wsz.model.plugin;

import io.wsz.model.content.Content;
import io.wsz.model.content.ContentList;
import io.wsz.model.item.*;
import io.wsz.model.layer.Layer;
import io.wsz.model.layer.LayersList;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class SerializableConverter {

    public static PluginSerializable toPluginSerializable(Plugin plugin) {
        List<LocationSerializable> locations = toSerializableLocations(plugin.getLocations());
        List<AssetSerializable> assets = toSerializableAssets(plugin.getAssets());
        return new PluginSerializable(plugin.getFile(), locations, assets, plugin.isActive(),
                plugin.isStartingLocation(), plugin.getStartLocation(),
                plugin.getStartX(), plugin.getStartY(), plugin.getStartLayer());
    }

    public static Plugin toPlugin(PluginSerializable ps) {
        List<Asset> assets = toAssets(ps.getAssets());
        List<Location> locations = toLocation(ps.getLocations(), assets);
        return new Plugin(ps.getFile(), locations, assets, ps.isActive(),
                ps.isStartingLocation(), ps.getStartLocation(),
                ps.getStartX(), ps.getStartY(), ps.getStartLayer());
    }

    public static List<LocationSerializable> toSerializableLocations(List<Location> input) {
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
            PosItemSerializable item = toPosItemSerializable(content.getItem());
            boolean visible = content.isVisible();

            ContentSerializable cs = new ContentSerializable(item, visible);
            output.add(cs);
        }
        return output;
    }

    private static PosItemSerializable toPosItemSerializable(PosItem item) {
        String name = item.getName();
        ItemType type = item.getType();
        String path = item.getRelativePath();
        Coords pos = item.getPos();
        int level = item.getLevel();
        return toConcreteItemSerializable(item, name, type, path, pos, level);
    }

    private static PosItemSerializable toConcreteItemSerializable(
            Asset asset, String name, ItemType type, String path, Coords pos, int level) {
        return  switch (type) {
            case CREATURE -> toCreatureSerializable(name, type, path, pos, level, asset);
            default -> toPosItemSerializable(name, type, path, pos, level);
        };
    }

    private static PosItemSerializable toPosItemSerializable(
            String name, ItemType type, String path, Coords pos, int level) {
        return new PosItemSerializable(name, type, path, pos, level);
    }

    private static CreatureSerializable toCreatureSerializable(
            String name, ItemType type, String path, Coords pos, int level, Asset asset) {
        Creature cr = (Creature) asset;
        Coords dest = cr.getDest();
        return new CreatureSerializable(
                name, type, path, pos, level, dest, cr.getSize(), cr.getControl(), cr.getSpeed());
    }

    public static List<Location> toLocation(List<LocationSerializable> input, List<Asset> assets) {
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
        if (input.isEmpty()) {
            return new ArrayList<>(0);
        }
        if (assets.isEmpty()) {
            throw new NoSuchElementException("Assets list is empty");
        }
        List<Content> output = FXCollections.observableArrayList();
        for (ContentSerializable cs : input) {
            PosItemSerializable is = cs.getItem();
            PosItem item = toPosItem(is);

            Content content = AssetConverter.convertToContent(item, item.getPos(), item.getLevel());
            content.setVisible(cs.isVisible());
            output.add(content);
        }
        return output;
    }

    private static PosItem toPosItem(PosItemSerializable is) {
        ItemType type = is.getType();
        String name = is.getName();
        String path = is.getPath();
        Coords pos = is.getPos();
        int level = is.getLevel();
        return toConcreteItem(is, type, name, path, pos, level);
    }

    private static PosItem toConcreteItem(AssetSerializable as, ItemType type, String name, String path,
                                          Coords pos, int level) {
        return switch (type) {
            case CREATURE -> toCreature(name, type, path, pos, level, as);
            case COVER -> new Cover(name, type, path, pos, level);
            case FLY_ZONE -> new FlyZone(name, type, path, pos, level);
            case LANDSCAPE -> new Landscape(name, type, path, pos, level);
            case OBSTACLE -> new Obstacle(name, type, path, pos, level);
            case TELEPORT -> toTeleport(name, type, path, pos, level, as);
        };
    }

    private static PosItem toTeleport(String name, ItemType type, String path, Coords pos, int level,
                                      AssetSerializable as) {
        TeleportSerializable ts = (TeleportSerializable) as;
        return new Teleport(name, type, path, pos, level,
                ts.getLocationName(), ts.getExit(), ts.getExitLevel());
    }

    private static Creature toCreature(String name, ItemType type, String path, Coords pos, int level,
                                      AssetSerializable as) {
        CreatureSerializable cs = (CreatureSerializable) as;
        return new Creature(name, type, path, pos, level,
                cs.getDest(), cs.getSize(), cs.getControl(), cs.getSpeed());
    }

    private static List<AssetSerializable> toSerializableAssets(List<Asset> input) {
        List<AssetSerializable> output = new ArrayList<>(0);
        for (Asset asset : input) {
            AssetSerializable as = toAssetSerializable(asset);
            output.add(as);
        }
        return output;
    }

    private static AssetSerializable toAssetSerializable(Asset asset) {
        String name = asset.getName();
        ItemType type = asset.getType();
        String path = asset.getRelativePath();
        return toConcreteItemSerializable(asset, name, type, path, null, 0);
    }

    private static List<Asset> toAssets(List<AssetSerializable> input) {
        List<Asset> output = FXCollections.observableArrayList();
        for (AssetSerializable as : input) {
            Asset asset = toAsset(as);
            output.add(asset);
        }
        return output;
    }

    private static Asset toAsset(AssetSerializable as) {
        ItemType type = as.getType();
        String name = as.getName();
        String path = as.getPath();
        return toConcreteItem(as, type, name, path, null, 0);
    }
}
