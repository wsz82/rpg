package model.plugin;

public class ActivePlugin {
    private Plugin activePlugin;
    private static ActivePlugin singleton;

    public static ActivePlugin get() {
        if (singleton == null) {
            singleton = new ActivePlugin();
        }
        return singleton;
    }

    private ActivePlugin() {
    }

    public Plugin getActivePlugin() {
        return activePlugin;
    }

    public void setActivePlugin(Plugin activePlugin) {
        this.activePlugin = activePlugin;
    }
}
