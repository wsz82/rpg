package io.wsz.model.location;

import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.ResolutionImage;
import io.wsz.model.textures.Fog;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static io.wsz.model.location.FogStatus.UNVISITED;

public class Location implements Externalizable {
    private static final long serialVersionUID = 1L;

    private String id;
    private double width;
    private double height;
    private List<Layer> layers;
    private List<PosItem> items;
    private List<PosItem> itemsToRemove;
    private List<PosItem> itemsToAdd;
    private List<List<FogStatusWithImage>> discoveredFog;

    public Location() {}

    public Location(String id) {
        this.id = id;
        initLists();
    }

    public Location(String id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
        initLists();
    }

    private void initLists() {
        this.layers = new ArrayList<>(0);
        this.items = new ArrayList<>(0);
        this.itemsToRemove = new ArrayList<>(0);
        this.itemsToAdd = new ArrayList<>(0);
    }

    public void initDiscoveredFog(Fog fog, double halfFogSize) {
        if (discoveredFog == null) {
            int maxPiecesHeight = (int) Math.ceil(getHeight() / halfFogSize) + 2;
            int maxPiecesWidth = (int) Math.ceil(getWidth() / halfFogSize) + 2;
            List<List<FogStatusWithImage>> discoveredFog = new ArrayList<>(maxPiecesHeight);
            for (int i = 0; i < maxPiecesHeight; i++) {
                ArrayList<FogStatusWithImage> horList = new ArrayList<>(maxPiecesWidth);
                for (int j = 0; j < maxPiecesWidth; j++) {
                    ResolutionImage randomFog = fog.getRandomFog();
                    FogStatusWithImage statusWithImage = new FogStatusWithImage(UNVISITED, randomFog);
                    horList.add(statusWithImage);
                }
                discoveredFog.add(i, horList);
            }
            this.discoveredFog = discoveredFog;
        }
        List<FogStatusWithImage> firstRow = discoveredFog.get(0);
        int widthPieces = firstRow.size();
        if (firstRow.get(0).getImage() == null) {
            initFogPiecesImages(discoveredFog, fog, widthPieces);
        }
    }

    private void initFogPiecesImages(List<List<FogStatusWithImage>> discoveredFog, Fog fog, int widthPieces) {
        for (List<FogStatusWithImage> fogStatusesWithImages : discoveredFog) {
            for (int j = 0; j < widthPieces; j++) {
                ResolutionImage randomFog = fog.getRandomFog();
                fogStatusesWithImages.get(j).setImage(randomFog);
            }
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }

    public List<PosItem> getItems() {
        return items;
    }

    public void setItems(List<PosItem> items) {
        this.items = items;
    }

    public List<PosItem> getItemsToRemove() {
        return itemsToRemove;
    }

    public List<PosItem> getItemsToAdd() {
        return itemsToAdd;
    }
    public List<List<FogStatusWithImage>> getDiscoveredFog() {
        return discoveredFog;
    }

    @Override
    public String toString() {
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Objects.equals(getId(), location.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(id);

        out.writeDouble(width);

        out.writeDouble(height);

        out.writeObject(layers);

        out.writeObject(items);

        out.writeObject(itemsToRemove);

        out.writeObject(itemsToAdd);

        out.writeObject(discoveredFog);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        id = in.readUTF();

        width = in.readDouble();

        height = in.readDouble();

        layers = (List<Layer>) in.readObject();

        items = (List<PosItem>) in.readObject();

        itemsToRemove = (List<PosItem>) in.readObject();

        itemsToAdd = (List<PosItem>) in.readObject();

        discoveredFog = (List<List<FogStatusWithImage>>) in.readObject();
    }
}
