package io.wsz.model.plugin;

import io.wsz.model.Controller;

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
        File file = Controller.getProgramDir();
        PluginSerializable ps = SerializableConverter.toPluginSerializable(plugin);
        try (
                FileOutputStream fos = new FileOutputStream(file + File.separator + plugin.getName());
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(ps);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Plugin load(String name) {
        return deserializeAll(name);
    }

    private Plugin deserializeAll(String name) {
        File file = Controller.getProgramDir();
        Plugin plugin;
        try (
            FileInputStream fos = new FileInputStream(file + File.separator + name);
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
