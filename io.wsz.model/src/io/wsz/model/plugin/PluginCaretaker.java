package io.wsz.model.plugin;

import io.wsz.model.sizes.Paths;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class PluginCaretaker {
    private static final String TEMP = "temp_plugin";
    private static final String TEMP_DIR = File.separator + TEMP;

    private final File programDir;

    public PluginCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void serialize(Plugin plugin, String pluginName) {
        String pluginDir = File.separator + pluginName;
        String pluginProgramDir = programDir + Paths.PLUGINS_DIR + pluginDir;
        String tempPluginProgramDir = pluginProgramDir + TEMP_DIR;
        File tempFile = new File(tempPluginProgramDir);
        try (
                ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(tempFile))
        ) {
            os.writeObject(plugin);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        String path = pluginProgramDir + Paths.PLUGIN_DIR;
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
