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
            plugin = SerializableConverter.toPlugin(ps);
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
