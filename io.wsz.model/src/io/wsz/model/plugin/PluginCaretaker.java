package io.wsz.model.plugin;

import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.PosItem;
import io.wsz.model.location.Location;
import io.wsz.model.world.World;

import java.io.*;
import java.util.List;
import java.util.Optional;

public class PluginCaretaker {
    private final File programDir;

    public PluginCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void save(Plugin plugin) {
        serializePlugin(plugin);
    }

    public void saveAs(Plugin plugin) {
        serializePlugin(plugin);
    }

    private void serializePlugin(Plugin plugin) {
        try (
                FileOutputStream fos = new FileOutputStream(programDir + File.separator + plugin.getName());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(plugin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Plugin load(String name, Controller controller) {
        Plugin deserialized = deserializeAll(name);
        if (deserialized == null) return null;
        World world = deserialized.getWorld();
        restorePrototypesReferences(world);
        assignControllerToPrototypes(world, controller);
        return deserialized;
    }

    private void assignControllerToPrototypes(World world, Controller controller) {
        List<Asset> assets = world.getAssets();
        for (Asset asset : assets) {
            ((PosItem) asset).setController(controller);
        }
    }

    private void restorePrototypesReferences(World world) {
        List<Asset> assets = world.getAssets();
        for (Location l : world.getLocations()) {
            for (PosItem pi : l.getItems()) {
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
        Plugin p;
        try (
            FileInputStream fos = new FileInputStream(programDir + File.separator + name);
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
        Plugin p;
        try (
                FileInputStream fos = new FileInputStream(programDir + File.separator + name);
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
