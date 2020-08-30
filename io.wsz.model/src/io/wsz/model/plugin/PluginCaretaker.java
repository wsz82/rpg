package io.wsz.model.plugin;

import java.io.*;

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

    public Plugin load(String name) {
        return deserializeAll(name);
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
