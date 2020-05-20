package game.model.plugin;

import model.plugin.Plugin;

import java.io.Serializable;

public class PluginMemento implements Serializable {
    private Plugin plugin;

    public PluginMemento(){}

    public PluginMemento(Plugin plugin){
        this.plugin = plugin;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}
