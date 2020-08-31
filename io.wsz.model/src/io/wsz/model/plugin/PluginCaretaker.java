package io.wsz.model.plugin;

import io.wsz.model.sizes.Paths;

import java.io.*;

public class PluginCaretaker {
    private final File programDir;

    public PluginCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void serialize(Plugin plugin, String pluginName) {
        String pluginDir = File.separator + pluginName;
        try (
                FileOutputStream fos = new FileOutputStream(programDir + Paths.PLUGINS_DIR + pluginDir + Paths.PLUGIN_DIR);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(plugin);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Plugin deserialize(String pluginName) {
        String pluginDir = File.separator + pluginName;
        Plugin p;
        try (
            FileInputStream fos = new FileInputStream(programDir + Paths.PLUGINS_DIR + pluginDir + Paths.PLUGIN_DIR);
            ObjectInputStream oos = new ObjectInputStream(fos)
        ){
            p = (Plugin) oos.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return p;
    }
}
