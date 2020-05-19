package game.view.launcher;

import model.plugin.Plugin;

import java.io.*;

class PluginMemento implements Serializable {
    private Plugin plugin;

    PluginMemento(){}

    PluginMemento(Plugin plugin){
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}
