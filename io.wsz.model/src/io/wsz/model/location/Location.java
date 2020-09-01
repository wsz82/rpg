package io.wsz.model.location;

import io.wsz.model.item.PosItem;
import io.wsz.model.layer.Layer;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.ResolutionImage;
import io.wsz.model.textures.Fog;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

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

    private String name;
    private double width;
    private double height;
    private ObservableList<Layer> layers;
    private ObservableList<PosItem> items;
    private List<PosItem> itemsToRemove;
    private List<PosItem> itemsToAdd;
    private List<List<FogStatusWithImage>> discoveredFog;

    public Location() {}

    public Location(String name) {
        this.name = name;
        this.layers = FXCollections.observableArrayList();
        this.items = FXCollections.observableArrayList();
        this.itemsToRemove = new ArrayList<>(0);
        this.itemsToAdd = new ArrayList<>(0);
    }

    public Location(String name, int width, int height) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.layers = FXCollections.observableArrayList();
        this.items = FXCollections.observableArrayList();
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public ObservableList<Layer> getLayers() {
        return layers;
    }

    public ObservableList<PosItem> getItems() {
        return items;
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
        return getName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        Location location = (Location) o;
        return Objects.equals(getName(), location.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeUTF(name);

        out.writeDouble(width);

        out.writeDouble(height);

        out.writeObject(new ArrayList<>(layers));

        out.writeObject(new ArrayList<>(items));

        out.writeObject(itemsToRemove);

        out.writeObject(itemsToAdd);

        out.writeObject(discoveredFog);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        name = in.readUTF();

        width = in.readDouble();

        height = in.readDouble();

        List<Layer> serLayers = (List<Layer>) in.readObject();
        layers = FXCollections.observableArrayList(serLayers);

        List<PosItem> serItems = (List<PosItem>) in.readObject();
        items = FXCollections.observableArrayList(serItems);

        itemsToRemove = (List<PosItem>) in.readObject();

        itemsToAdd = (List<PosItem>) in.readObject();

        discoveredFog = (List<List<FogStatusWithImage>>) in.readObject();
    }
}
