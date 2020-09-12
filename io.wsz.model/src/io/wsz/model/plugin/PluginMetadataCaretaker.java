package io.wsz.model.plugin;

import io.wsz.model.sizes.Paths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class PluginMetadataCaretaker {
    private static final String TEMP_DIR = File.separator + "temp_metadata";
    private final File programDir;

    public PluginMetadataCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void serialize(PluginMetadata metadata, String pluginName) {
        String metadataDir = File.separator + pluginName;
        String metadataProgramDir = programDir + Paths.PLUGINS_DIR + metadataDir;
        String tempMetadataProgramDir = metadataProgramDir + TEMP_DIR;
        File tempFile = new File(tempMetadataProgramDir);
        try (
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(tempFile))
        ) {
            os.writeObject(metadata);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String path = metadataProgramDir + Paths.METADATA_DIR;
        File targetFile = new File(path);
        Path source = Path.of(tempFile.toURI());
        Path target = Path.of(targetFile.toURI());
        try {
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        tempFile.delete();
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
