package model.plugin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.asset.Asset;
import model.content.Content;
import model.content.ContentList;
import model.item.*;
import model.layer.Layer;
import model.layer.LayersList;
import model.location.Location;
import model.stage.Coordinates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SerializableConverter {

    static List<LocationSerializable> locationsToSerializable(List<Location> input) {
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
        String name = item.getName();
        ItemType type = item.getType();
        CoordinatesSerializable pos = toSerializableCoordinates(item.getCoords());
        int level = item.getLevel();
        return new ItemSerializable(name, type, pos, level);
    }

    private static CoordinatesSerializable toSerializableCoordinates(Coordinates pos) {
        double x = pos.getX();
        double y = pos.getY();
        int z = pos.getZ();
        return new CoordinatesSerializable(x, y, z);
    }

    public static List<Location> toLocationObjects(List<LocationSerializable> input) {
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
            contentList.get().setAll(toContents(ls.getContents()));
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

    private static List<Content> toContents(List<ContentSerializable> input) {
        List<Content> output = FXCollections.observableArrayList();
        for (ContentSerializable cs : input) {
            Content content = new Content();
            Item item = toItem(cs.getItem());
            content.setItem(item);
            content.setLevel(item.getLevel());
            content.setName(item.getName());
            content.setType(item.getType());
            content.setPos(item.getCoords());
            content.setVisible(cs.isVisible());
            output.add(content);
        }
        return output;
    }

    private static Item toItem(ItemSerializable i) {
        Item output;
        String name = i.getName();
        ItemType type = i.getType();
        Coordinates pos = toCoordinates(i.getPos());
        int level = i.getLevel();
        switch (type) {
            case LANDSCAPE -> output = new Landscape(name, type, pos, level, null);
            case COVER -> output = new Cover(name, type, pos, level, null);
            case MOVE_ZONE -> output = new MoveZone(name, type, pos, level, null);
            case FLY_ZONE -> output = new FlyZone(name, type, pos, level, null);
            default -> output = new Landscape(name, type, pos, level, null);
        }
        return output;
    }

    private static Coordinates toCoordinates(CoordinatesSerializable pos) {
        return new Coordinates(pos.getX(), pos.getY(), pos.getZ());
    }

    public static List<AssetSerializable> assetsToSerializable(ObservableList<Asset> input) {
        List<AssetSerializable> output = new ArrayList<>(0);
        for (Asset asset : input) {
            String name = asset.getName();
            ItemType type = asset.getType();
            String path = asset.getPath();
            AssetSerializable as = new AssetSerializable(name, type, path);
            output.add(as);
        }
        return output;
    }

    public static Collection<Asset> toAssetObjects(List<AssetSerializable> input) {
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
