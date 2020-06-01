package editor.model;

import editor.model.settings.SettingsCaretaker;
import editor.model.settings.SettingsMemento;
import editor.view.plugin.PluginSettingsStage;
import io.wsz.model.Controller;
import io.wsz.model.item.AssetsList;
import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
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

    public void initNewPlugin() {
        Controller.get().getAssetsList().clear();
        Controller.get().getLocationsList().clear();

        Location location = new Location("new", 800, 600);
        Layer layer = new Layer("new");
        location.getLayers().get().add(layer);
        LocationsList.get().add(location);
        Controller.get().getCurrentLocation().setLocation(location);
        Controller.get().getCurrentLayer().setLayer(layer);
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
        ActivePlugin.get().setPlugin(p);
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
        loadEditorActivePluginToLists();
        PluginSettingsStage.setStartingLocation(p.isStartingLocation());
        PluginSettingsStage.setStartLocationName(p.getStartLocation());
        PluginSettingsStage.setStartX(p.getStartX());
        PluginSettingsStage.setStartY(p.getStartY());
        PluginSettingsStage.setStartLayer(p.getStartLayer());
    }

    public void loadEditorActivePluginToLists() {
        if (ActivePlugin.get().getPlugin() == null) {
            return;
        }
        LocationsList.get().clear();
        AssetsList.get().clear();

        Plugin p = ActivePlugin.get().getPlugin();
        Location first = p.getLocations().get(0);
        CurrentLocation.get().setLocation(first);
        CurrentLayer.get().setLayer(first.getLayers().get().get(0));

        AssetsList.get().setAll(p.getAssets());
        LocationsList.get().setAll(p.getLocations());
    }
}
