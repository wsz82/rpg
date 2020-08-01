package editor.model;

import editor.model.settings.SettingsCaretaker;
import editor.model.settings.SettingsMemento;
import editor.view.asset.ItemsStage;
import editor.view.asset.ObservableAssets;
import editor.view.plugin.PluginSettingsStage;
import io.wsz.model.Controller;
import io.wsz.model.asset.Asset;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import io.wsz.model.plugin.ActivePlugin;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginCaretaker;
import io.wsz.model.stage.Coords;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditorController {
    private static EditorController singleton;
    private final Controller controller = Controller.get();
    private final Coords dragPos = new Coords();
    private ItemsStage itemsStage;

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
        controller.getAssetsList().clear();
        ObservableAssets.get().clearLists();
        controller.getLocationsList().clear();

        Location location = new Location("new", 20, 20);
        Layer layer = new Layer("new");
        location.getLayers().get().add(layer);
        controller.getLocationsList().add(location);
        controller.getCurrentLocation().setLocation(location);
        controller.getCurrentLayer().setLayer(layer);
        controller.setActivePlugin(null);
    }

    public void saveActivePlugin(PluginSettingsStage pss) {
        mergeAssetsLists();
        PluginCaretaker pc = new PluginCaretaker();
        Plugin p = ActivePlugin.get().getPlugin();
        setPluginsParams(p, pss);
        pc.save(p);
    }

    private void mergeAssetsLists() {
        controller.getAssetsList().clear();
        controller.getAssetsList().addAll(ObservableAssets.get().merge());
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
        p.setAssets(controller.getAssetsList());
        p.setLocations(new ArrayList<>(LocationsList.get()));
        boolean isStartingLocation = pss.isStartingLocation();
        p.setStartingLocation(isStartingLocation);
        Coords startPos = new Coords();
        startPos.setLocation(pss.getStartLocation());
        startPos.x = pss.getStartX();
        startPos.y = pss.getStartY();
        startPos.level = pss.getStartLevel();
        p.setStartPos(startPos);
    }

    public void loadAndRestorePlugin(String pluginName, PluginSettingsStage pss) {
        PluginCaretaker pc = new PluginCaretaker();
        Plugin p = pc.load(pluginName);
        if (p == null) return;
        controller.setActivePlugin(p);
        loadEditorActivePluginToLists();
        restorePluginSettingsStage(pss, p);
    }

    private void restorePluginSettingsStage(PluginSettingsStage pss, Plugin p) {
        pss.setStartingLocation(p.isStartingLocation());
        Coords startPos = p.getStartPos();
        controller.restoreCoordsLocation(startPos);
        pss.setStartLocation(startPos.getLocation());
        pss.setStartX(startPos.x);
        pss.setStartY(startPos.y);
        pss.setStartLevel(startPos.level);
    }

    public void loadEditorActivePluginToLists() {
        if (ActivePlugin.get().getPlugin() == null) {
            return;
        }
        controller.getAssetsList().clear();
        ObservableAssets.get().clearLists();
        controller.getLocationsList().clear();

        Plugin p = ActivePlugin.get().getPlugin();

        boolean listAreLoaded = loadLists(p);
        if (listAreLoaded) {
            Location first = p.getLocations().get(0);
            controller.getCurrentLocation().setLocation(first);
            controller.getCurrentLayer().setLayer(first.getLayers().get().get(0));
        }
    }

    private boolean loadLists(Plugin p) {
        List<Asset> assets = controller.getAssetsList();
        assets.addAll(p.getAssets());
        ObservableAssets.get().fillLists(assets);
        controller.fillLocationsList(p.getLocations());
        return true;
    }

    public void setDragPos(Coords pos) {
        this.dragPos.x = pos.x;
        this.dragPos.y = pos.y;
        this.dragPos.level = pos.level;
        this.dragPos.setLocation(pos.getLocation());
    }

    public Coords getDragPos() {
        return dragPos;
    }

    public ItemsStage getItemsStageToAddItems() {
        return itemsStage;
    }

    public void setItemsStageToAddItems(ItemsStage itemsStage) {
        this.itemsStage = itemsStage;
    }
}
