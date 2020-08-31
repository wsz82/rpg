package io.wsz.model.plugin;

import io.wsz.model.sizes.Paths;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PluginMetadataCaretaker {
    private final File programDir;

    public PluginMetadataCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void serialize(PluginMetadata metadata, String pluginName) {
        String pluginDir = File.separator + pluginName;
        try (
                FileOutputStream fos = new FileOutputStream(programDir + Paths.PLUGINS_DIR + pluginDir + Paths.METADATA_DIR);
                ObjectOutputStream oos = new ObjectOutputStream(fos)
        ) {
            oos.writeObject(metadata);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public PluginMetadata deserialize(String pluginName) {
        String pluginDir = File.separator + pluginName;
        PluginMetadata m;
        try (
                FileInputStream fos = new FileInputStream(programDir + Paths.PLUGINS_DIR + pluginDir + Paths.METADATA_DIR);
                ObjectInputStream oos = new ObjectInputStream(fos)
        ){
            m = (PluginMetadata) oos.readObject();
            m.setPluginName(pluginName);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return m;
    }

    public List<PluginMetadata> getMetadatas() {
        String pluginsPath = programDir + Paths.PLUGINS_DIR;
        File pluginsDir = new File(pluginsPath);
        File[] pluginsFiles = pluginsDir.listFiles();
        if (pluginsFiles == null) return null;
        List<PluginMetadata> metadatas = new ArrayList<>(0);
        for (File file : pluginsFiles) {
            if (!file.isDirectory()) continue;
            String pluginName = file.getName();
            PluginMetadata metadata = deserialize(pluginName);
            if (metadata == null) continue;
            metadatas.add(metadata);
        }
        return metadatas;
    }
}
