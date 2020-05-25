package model.plugin;

import model.asset.Asset;
import model.location.Location;

import java.io.*;
import java.util.List;

public class PluginCaretaker {

    public PluginCaretaker() {
    }

    public void save(Plugin plugin) {
        serializePlugin(plugin);
    }

    public void saveAs(Plugin plugin) {
        serializePlugin(plugin);
    }

    private void serializePlugin(Plugin plugin) {
        PluginSerializable ps = SerializableConverter.toPluginSerializable(plugin);
        try (
            FileOutputStream fos = new FileOutputStream(plugin.getFile());
            ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(ps);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Plugin load(File file) {
        return deserializeAll(file);
    }

    private Plugin deserializeAll(File file) {
        Plugin plugin = new Plugin();
        try (
            FileInputStream fos = new FileInputStream(file);
            ObjectInputStream oos = new ObjectInputStream(fos)
        ){
            PluginSerializable ps = (PluginSerializable) oos.readObject();

            List<Asset> assets = SerializableConverter.toAssetObjects(ps.getAssets());
            List<Location> locations = SerializableConverter.toLocationObjects(
                    ps.getLocations(), assets);

            plugin.setFile(ps.getFile());
            plugin.setAssets(assets);
            plugin.setLocations(locations);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return plugin;
    }
}
