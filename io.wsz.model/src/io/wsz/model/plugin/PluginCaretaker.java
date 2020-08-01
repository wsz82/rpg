package io.wsz.model.plugin;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;

import java.io.*;
import java.util.List;
import java.util.Optional;

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
        if (deserialized == null) return null;
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
                    Optional<Asset> optAsset = assets.stream()
                            .filter(a -> a.getName().equals(prototypeName))
                            .findFirst();
                    Asset p = optAsset.orElse(null);
                    if (p == null) {
                        throw new NullPointerException(prototypeName + " reference is not found");
                    }
                    pi.setPrototype((PosItem) p);
                }
            }
        }
    }

    private Plugin deserializeAll(String name) {
        File file = Controller.getProgramDir();
        Plugin p;
        try (
            FileInputStream fos = new FileInputStream(file + File.separator + name);
            ObjectInputStream oos = new ObjectInputStream(fos)
        ){
            p = (Plugin) oos.readObject();
            p.setName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return p;
    }

    public Plugin getPluginMetadata(String name) {
        File file = Controller.getProgramDir();
        Plugin p;
        try (
                FileInputStream fos = new FileInputStream(file + File.separator + name);
                ObjectInputStream oos = new ObjectInputStream(fos)
        ){
            p = (Plugin) oos.readObject();
            p.setName(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return p;
    }
}
