package io.wsz.model.plugin;

import java.io.*;

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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Plugin load(File file) throws ClassCastException {
        return deserializeAll(file);
    }

    private Plugin deserializeAll(File file) throws ClassCastException {
        Plugin plugin;
        try (
            FileInputStream fos = new FileInputStream(file);
            ObjectInputStream oos = new ObjectInputStream(fos)
        ){
            PluginSerializable ps = (PluginSerializable) oos.readObject();
            plugin = SerializableConverter.toPlugin(ps);
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null;
        }
        return plugin;
    }
}
