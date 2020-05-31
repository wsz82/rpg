package editor.model;

import editor.model.settings.SettingsCaretaker;
import editor.model.settings.SettingsMemento;
import editor.view.plugin.PluginSettingsStage;
import io.wsz.model.Controller;
import io.wsz.model.asset.AssetsList;
import io.wsz.model.location.LocationsList;
import io.wsz.model.plugin.ActivePlugin;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginCaretaker;

import java.io.File;

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
        Plugin p = ActivePlugin.get().getPlugin();
        setPluginsParams(p);
        pc.save(p);
    }

    public void savePluginAs(File pluginDir) {
        PluginCaretaker pc = new PluginCaretaker();
        Plugin p = new Plugin();
        p.setFile(pluginDir);
        setPluginsParams(p);
        pc.saveAs(p);
        ActivePlugin.get().setActivePlugin(p);
    }

    private void setPluginsParams(Plugin p) {
        p.setAssets(AssetsList.get());
        p.setLocations(LocationsList.get());
        boolean isStartingLocation = PluginSettingsStage.isStartingLocation();
        p.setStartingLocation(isStartingLocation);
        String startLocation = PluginSettingsStage.getStartLocationName();
        p.setStartLocation(startLocation);
        int startX = PluginSettingsStage.getStartX();
        p.setStartX(startX);
        int startY = PluginSettingsStage.getStartY();
        p.setStartY(startY);
        int startLayer = PluginSettingsStage.getStartLayer();
        p.setStartLayer(startLayer);
    }

    public void loadAndRestorePlugin(File pluginDir) {
        PluginCaretaker pc = new PluginCaretaker();
        Plugin p = pc.load(pluginDir);
        Controller.get().setActivePlugin(p);
        Controller.get().loadActivePluginToLists();
        PluginSettingsStage.setStartingLocation(p.isStartingLocation());
        PluginSettingsStage.setStartLocationName(p.getStartLocation());
        PluginSettingsStage.setStartX(p.getStartX());
        PluginSettingsStage.setStartY(p.getStartY());
        PluginSettingsStage.setStartLayer(p.getStartLayer());
    }
}
