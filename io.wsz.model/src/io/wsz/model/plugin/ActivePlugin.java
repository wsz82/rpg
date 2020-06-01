package io.wsz.model.plugin;

public class ActivePlugin {
    private Plugin plugin;
    private static ActivePlugin singleton;

    public static ActivePlugin get() {
        if (singleton == null) {
            singleton = new ActivePlugin();
        }
        return singleton;
    }

    private ActivePlugin() {
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void setPlugin(Plugin plugin) {
        this.plugin = plugin;
    }
}
