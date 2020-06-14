package io.wsz.model.plugin;

import io.wsz.model.asset.Asset;
import io.wsz.model.content.ItemList;
import io.wsz.model.item.*;
import io.wsz.model.layer.Layer;
import io.wsz.model.layer.LayersList;
import io.wsz.model.location.Location;
import io.wsz.model.stage.Coords;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SerializableConverter {

    public static PluginSerializable toPluginSerializable(Plugin plugin) {
        List<LocationSerializable> locations = toSerializableLocations(plugin.getLocations());
        List<AssetSerializable> assets = toSerializableAssets(plugin.getAssets());
        return new PluginSerializable(plugin.getName(), locations, assets, plugin.isActive(),
                plugin.isStartingLocation(), plugin.getStartLocation(),
                plugin.getStartX(), plugin.getStartY(), plugin.getStartLayer());
    }

    public static Plugin toPlugin(PluginSerializable ps) {
        List<Asset> assets = toAssets(ps.getAssets());
        List<Location> locations = toLocation(ps.getLocations(), assets);
        return new Plugin(ps.getName(), locations, assets, ps.isActive(),
                ps.isStartingLocation(), ps.getStartLocation(),
                ps.getStartX(), ps.getStartY(), ps.getStartLayer());
    }

    public static List<LocationSerializable> toSerializableLocations(List<Location> input) {
        List<LocationSerializable> output = new ArrayList<>(0);
        for (Location location : input) {
            String name = location.getName();
            double width = location.getWidth();
            double height = location.getHeight();
            List<LayerSerializable> layers = toSerializableLayers(location);
            List<PosItemSerializable> items = toSerializableItems(location);

            LocationSerializable ls = new LocationSerializable(name, width, height, layers, items);
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

    private static List<PosItemSerializable> toSerializableItems(Location location) {
        List<PosItemSerializable> output = new ArrayList<>(0);
        List<PosItem> input = location.getItems().get();
        for (PosItem pi : input) {
            PosItemSerializable pis = toPosItemSerializable(pi);
            output.add(pis);
        }
        return output;
    }

    private static PosItemSerializable toPosItemSerializable(PosItem pi) {
        return toConcreteItemSerializable(pi);
    }

    private static PosItemSerializable toConcreteItemSerializable(Asset a) {
        PosItem pi = (PosItem) a;
        Asset pr = pi.getPrototype();
        String prName = null;
        if (pr != null) {
            prName = pr.getName();
        }
        String name = a.getName();
        ItemType type = a.getType();
        String path = a.getRelativePath();
        Boolean visible = pi.getVisible();
        Coords pos = pi.getPos();
        Integer level = null;
        if (pi.getLevel() != null) {
            level = pi.getLevel();
        }
        return  switch (type) {
            case CREATURE -> toCreatureSerializable(
                    prName, name, type, path, visible, pos, level, a);
            case TELEPORT -> toTeleportSerializable(
                    prName, name, type, path, visible, pos, level, a);
            default -> toPosItemSerializable(
                    prName, name, type, path, visible, pos, level, a);
        };
    }

    private static TeleportSerializable toTeleportSerializable(
            String prototype, String name, ItemType type, String path,
            Boolean visible, Coords pos, Integer level,
            Asset a) {
        Teleport t = (Teleport) a;
        return new TeleportSerializable(
                prototype, name, type, path,
                visible, pos, level,
                t.getCoverLine(), t.getCollisionPolygons(),
                t.getLocationName(), t.getExit(), t.getExitLevel());
    }

    private static PosItemSerializable toPosItemSerializable(
            String prototype, String name, ItemType type, String path,
            Boolean visible, Coords pos, Integer level,
            Asset a) {
        PosItem pi = (PosItem) a;
        return new PosItemSerializable(
                prototype, name, type, path,
                visible, pos, level,
                pi.getCoverLine(), pi.getCollisionPolygons());
    }

    private static CreatureSerializable toCreatureSerializable(
            String prototype, String name, ItemType type, String path, Boolean visible, Coords pos, Integer level,
            Asset a) {
        Creature cr = (Creature) a;
        Coords dest = cr.getDest();
        return new CreatureSerializable(
                prototype, name, type, path,
                visible, pos, level,
                cr.getCoverLine(), cr.getCollisionPolygons(),
                dest, cr.getSize(), cr.getControl(), cr.getSpeed());
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

            ItemList itemList = new ItemList();
            itemList.get().setAll(toItems(ls.getItems(), assets));
            location.setItems(itemList);

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

    private static List<PosItem> toItems(List<PosItemSerializable> input, List<Asset> assets) {
        if (input.isEmpty()) {
            return new ArrayList<>(0);
        }
        List<PosItem> output = FXCollections.observableArrayList();
        for (PosItemSerializable pis : input) {
            PosItem pi = toPosItem(pis, assets);

            pi.setVisible(pis.getVisible());
            output.add(pi);
        }
        return output;
    }

    private static PosItem toPosItem(PosItemSerializable is, List<Asset> assets) {
        return toConcreteItem(is, assets);
    }

    private static PosItem toConcreteItem(AssetSerializable as, List<Asset> assets) {
        ItemType type = as.getType();
        String name = as.getName();
        String path = as.getPath();
        PosItemSerializable pis = (PosItemSerializable) as;
        String prototypeName = pis.getPrototype();
        Boolean visible = pis.getVisible();
        Coords pos = pis.getPos();
        Integer level = pis.getLevel();
        Asset prototype = null;
        if (prototypeName != null) {
            List<Asset> singleAsset = assets.stream()
                    .filter(a -> a.getName().equals(prototypeName))
                    .collect(Collectors.toList());
            prototype = singleAsset.get(0);
        }
        return switch (type) {
            case CREATURE -> toCreature(
                    (Creature) prototype, name, type, path,
                    visible, pos, level, as);
            case COVER -> new Cover(
                    (Cover) prototype, name, type, path,
                    visible, pos, level,
                    as.getCoverLine(), as.getCollisionPolygons());
            case LANDSCAPE -> new Landscape(
                    (Landscape) prototype, name, type, path,
                    visible, pos, level,
                    as.getCoverLine(), as.getCollisionPolygons());
            case TELEPORT -> toTeleport(
                    (Teleport) prototype, name, type, path,
                    visible, pos, level, as);
        };
    }

    private static Teleport toTeleport(Teleport prototype, String name, ItemType type, String path,
                                       Boolean visible, Coords pos, Integer level,
                                       AssetSerializable as) {
        TeleportSerializable ts = (TeleportSerializable) as;
        return new Teleport(
                prototype, name, type, path,
                visible, pos, level,
                as.getCoverLine(), as.getCollisionPolygons(),
                ts.getLocationName(), ts.getExit(), ts.getExitLevel());
    }

    private static Creature toCreature(Creature prototype, String name, ItemType type, String path,
                                       Boolean visible, Coords pos, Integer level,
                                       AssetSerializable as) {
        CreatureSerializable cs = (CreatureSerializable) as;
        return new Creature(
                prototype, name, type, path,
                visible, pos, level,
                as.getCoverLine(), as.getCollisionPolygons(),
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

    private static AssetSerializable toAssetSerializable(Asset a) {
        return toConcreteItemSerializable(a);
    }

    private static List<Asset> toAssets(List<AssetSerializable> input) {
        List<Asset> output = FXCollections.observableArrayList();
        for (AssetSerializable as : input) {
            Asset a = toConcreteItem(as, null);
            output.add(a);
        }
        return output;
    }
}
