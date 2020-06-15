package editor.model;

import editor.model.settings.SettingsCaretaker;
import editor.model.settings.SettingsMemento;
import editor.view.asset.ObservableAssets;
import editor.view.plugin.PluginSettingsStage;
import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.asset.AssetsList;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import io.wsz.model.plugin.ActivePlugin;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginCaretaker;
import io.wsz.model.stage.Coords;

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

    public ActiveItem getActiveContent() {
        return ActiveItem.get();
    }

    public SettingsMemento restoreSettings(File programDir) {
        return new SettingsCaretaker(programDir).loadMemento();
    }

    public void storeSettings(File programDir, SettingsMemento memento) {
        new SettingsCaretaker(programDir).saveMemento(memento);
    }

    public void initNewPlugin() {
        Controller.get().getAssetsList().clear();
        ObservableAssets.get().clearLists();
        Controller.get().getLocationsList().clear();

        Location location = new Location("new", 8, 6);
        Layer layer = new Layer("new");
        location.getLayers().get().add(layer);
        Controller.get().getLocationsList().add(location);
        Controller.get().getCurrentLocation().setLocation(location);
        Controller.get().getCurrentLayer().setLayer(layer);
    }

    public void saveActivePlugin(PluginSettingsStage pss) {
        mergeAssetsLists();
        PluginCaretaker pc = new PluginCaretaker();
        Plugin p = ActivePlugin.get().getPlugin();
        setPluginsParams(p, pss);
        pc.save(p);
    }

    private void mergeAssetsLists() {
        Controller.get().getAssetsList().clear();
        Controller.get().getAssetsList().addAll(ObservableAssets.get().merge());
    }

    public void savePluginAs(String pluginName, PluginSettingsStage pss) {
        mergeAssetsLists();
        PluginCaretaker pc = new PluginCaretaker();
        Plugin p = new Plugin();
        p.setName(pluginName);
        setPluginsParams(p, pss);
        pc.saveAs(p);
        ActivePlugin.get().setPlugin(p);
    }

    private void setPluginsParams(Plugin p, PluginSettingsStage pss) {
        p.setAssets(AssetsList.get());
        p.setLocations(LocationsList.get());
        boolean isStartingLocation = pss.isStartingLocation();
        p.setStartingLocation(isStartingLocation);
        String startLocation = pss.getStartLocationName();
        p.setStartLocation(startLocation);
        double startX = pss.getStartX();
        double startY = pss.getStartY();
        p.setStartPos(new Coords(startX, startY));
        int startLayer = pss.getStartLayer();
        p.setStartLayer(startLayer);
    }

    public void loadAndRestorePlugin(String pluginName, PluginSettingsStage pss) {
        PluginCaretaker pc = new PluginCaretaker();
        Plugin p = pc.load(pluginName);
        Controller.get().setActivePlugin(p);
        loadEditorActivePluginToLists();
        pss.setStartingLocation(p.isStartingLocation());
        pss.setStartLocationName(p.getStartLocation());
        pss.setStartX(p.getStartPos().x);
        pss.setStartY(p.getStartPos().y);
        pss.setStartLayer(p.getStartLayer());
    }

    public void loadEditorActivePluginToLists() {
        if (ActivePlugin.get().getPlugin() == null) {
            return;
        }
        Controller.get().getAssetsList().clear();
        ObservableAssets.get().clearLists();
        Controller.get().getLocationsList().clear();

        Plugin p = ActivePlugin.get().getPlugin();

        boolean listAreLoaded = loadLists(p);
        if (listAreLoaded) {
            Location first = p.getLocations().get(0);
            Controller.get().getCurrentLocation().setLocation(first);
            Controller.get().getCurrentLayer().setLayer(first.getLayers().get().get(0));
        }
    }

    private boolean loadLists(Plugin p) {
        List<Asset> assets = Controller.get().getAssetsList();
        assets.addAll(p.getAssets());
        ObservableAssets.get().fillLists(assets);
        Controller.get().getLocationsList().setAll(p.getLocations());
        return true;
    }
}
