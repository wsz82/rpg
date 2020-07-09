package io.wsz.model.plugin;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class PluginCaretaker {

    public PluginCaretaker() {}

    public void save(Plugin plugin) {
        serializePlugin(plugin);
    }

    public void saveAs(Plugin plugin) {
        serializePlugin(plugin);
    }

    private void serializePlugin(Plugin plugin) {
        File file = Controller.getProgramDir();
        try (
                FileOutputStream fos = new FileOutputStream(file + File.separator + plugin.getName());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(plugin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Plugin load(String name) {
        Plugin deserialized = deserializeAll(name);

        retrievePrototypeReferences(deserialized);

        return deserializeAll(name);
    }

    private void retrievePrototypeReferences(Plugin deserialized) {
        List<Asset> assets = deserialized.getAssets();
        for (Location l : deserialized.getLocations()) {
            for (PosItem pi : l.getItems().get()) {
                PosItem prototype = pi.getPrototype();
                if (prototype != null) {
                    String prototypeName = prototype.getName();
                    List<Asset> singleAsset = assets.stream()
                            .filter(a -> a.getName().equals(prototypeName))
                            .collect(Collectors.toList());
                    pi.setPrototype((PosItem) singleAsset.get(0));
                }
            }
        }
    }

    private Plugin deserializeAll(String name) {
        File file = Controller.getProgramDir();
        Plugin plugin;
        try (
            FileInputStream fos = new FileInputStream(file + File.separator + name);
            ObjectInputStream oos = new ObjectInputStream(fos)
        ){
            plugin = (Plugin) oos.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return plugin;
    }

    public Plugin getPluginMetadata(String pluginName) {
        File file = Controller.getProgramDir();
        Plugin p;
        try (
                FileInputStream fos = new FileInputStream(file + File.separator + pluginName);
                ObjectInputStream oos = new ObjectInputStream(fos)
        ){
            p = (Plugin) oos.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return p;
    }
}
