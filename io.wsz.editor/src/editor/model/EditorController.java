package editor.model;

import editor.model.settings.SettingsCaretaker;
import editor.model.settings.SettingsMemento;
import io.wsz.model.asset.Asset;
import io.wsz.model.asset.AssetsList;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import io.wsz.model.plugin.ActivePlugin;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginCaretaker;

import java.io.File;
import java.util.List;

public class EditorController {
    private static EditorController singleton;

    public static EditorController get() {
        if (singleton == null) {
            singleton = new EditorController();
        }
        return singleton;
    }

    private EditorController(){}

    public ActiveContent getActiveContent() {
        return ActiveContent.get();
    }

    public SettingsMemento restoreSettings(File programDir) {
        return new SettingsCaretaker(programDir).loadMemento();
    }

    public void storeSettings(File programDir, SettingsMemento memento) {
        new SettingsCaretaker(programDir).saveMemento(memento);
    }

    public void saveActivePlugin() {
        PluginCaretaker pc = new PluginCaretaker();
        ActivePlugin.get().getPlugin().setAssets(AssetsList.get());
        ActivePlugin.get().getPlugin().setLocations(LocationsList.get());
        Plugin plugin = ActivePlugin.get().getPlugin();
        pc.save(plugin);
    }

    public void savePluginAs(File file) {
        PluginCaretaker pc = new PluginCaretaker();
        List<Location> locations = LocationsList.get();
        List<Asset> assets = AssetsList.get();
        Plugin plugin = new Plugin(file, locations, assets);
        pc.saveAs(plugin);
        ActivePlugin.get().setActivePlugin(plugin);
    }
}
