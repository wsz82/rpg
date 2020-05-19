package model.plugin;

import javafx.stage.FileChooser;
import model.asset.AssetsList;
import model.location.CurrentLocation;
import model.location.Location;
import model.location.LocationsList;
import model.stage.CurrentLayer;
import editor.view.stage.Main;

import java.io.*;
import java.util.List;

public class Plugin {
    private File file;

    public Plugin() {
    }

    public void save() {
        serializeAll();
    }

    public void saveAs() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save plugin");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plugin file", "*.rpg")
        );
        file = fileChooser.showSaveDialog(Main.getStage());
        serializeAll();
        ActivePlugin.get().setActivePlugin(this);
    }

    private void serializeAll() {
        List<LocationSerializable> locations = SerializableConverter.locationsToSerializable(LocationsList.get());
        List<AssetSerializable> assets = SerializableConverter.assetsToSerializable(AssetsList.get());
        All all = new All(locations, assets);
        try (
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
        ) {
            oos.writeObject(all);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void load() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose plugin");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Plugin file", "*.rpg")
        );
        file = fileChooser.showOpenDialog(Main.getStage());
        if (file == null) {
            return;
        }
        LocationsList.get().clear();
        AssetsList.get().clear();
        deserializeAll();
        Location first = LocationsList.get().get(0);
        CurrentLocation.get().setLocation(first);
        CurrentLayer.get().setCurrentLayer(first.getLayers().get().get(0));
        ActivePlugin.get().setActivePlugin(this);
    }

    private void deserializeAll() {
        try (
            FileInputStream fos = new FileInputStream(file);
            ObjectInputStream oos = new ObjectInputStream(fos);
        ){
            All all = (All) oos.readObject();
            List<AssetSerializable> assets = all.getAssets();
            AssetsList.get().setAll(SerializableConverter.toAssetObjects(assets));
            List<LocationSerializable> locations = all.getLocations();
            LocationsList.get().setAll(SerializableConverter.toLocationObjects(locations, AssetsList.get()));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
