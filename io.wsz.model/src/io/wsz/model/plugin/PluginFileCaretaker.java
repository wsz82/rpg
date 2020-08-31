package io.wsz.model.plugin;

import java.io.File;

public class PluginFileCaretaker {
    private final File programDir;

    public PluginFileCaretaker(File programDir) {
        this.programDir = programDir;
    }

    public void save(Plugin plugin, PluginMetadata metadata, String pluginName) {
        PluginCaretaker pluginCaretaker = new PluginCaretaker(programDir);
        pluginCaretaker.serialize(plugin, pluginName);

        PluginMetadataCaretaker pluginMetadataCaretaker = new PluginMetadataCaretaker(programDir);
        pluginMetadataCaretaker.serialize(metadata, pluginName);
    }
}
