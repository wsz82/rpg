package editor.model;

import editor.model.settings.SettingsCaretaker;
import editor.model.settings.SettingsMemento;
import editor.view.asset.ItemsStage;
import editor.view.asset.ObservableAssets;
import editor.view.plugin.PluginSettingsStage;
import io.wsz.model.Controller;
import io.wsz.model.Model;
import io.wsz.model.asset.Asset;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.item.PosItem;
import io.wsz.model.item.WeaponType;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginCaretaker;
import io.wsz.model.stage.Coords;
import io.wsz.model.world.World;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EditorController {
    private final Controller controller;
    private final ObservableAssets observableAssets = new ObservableAssets();
    private final ObservableList<Location> observableLocations = FXCollections.observableArrayList();
    private final ObservableList<WeaponType> observableWeaponTypes = FXCollections.observableArrayList();
    private final ObservableList<InventoryPlaceType> observableInventoryPlaceTypes = FXCollections.observableArrayList();
    private final Coords dragPos = new Coords();

    private PosItem activeItem;
    private ItemsStage activeItemsStage;

    public EditorController(Controller controller){
        this.controller = controller;
    }

    public SettingsMemento restoreSettings(File programDir) {
        return new SettingsCaretaker(programDir).loadMemento();
    }

    public void storeSettings(File programDir, SettingsMemento memento) {
        new SettingsCaretaker(programDir).saveMemento(memento);
    }

    public void initNewPlugin() {
        observableAssets.clearLists();
        World newWorld = new World();

        initLocations(newWorld);
        initAssets(newWorld);
        initWeaponTypes(newWorld);

        Plugin newPlugin = new Plugin();
        newPlugin.setWorld(newWorld);
        controller.getModel().setActivePlugin(newPlugin);
    }

    private void initWeaponTypes(World newWorld) {
        List<WeaponType> weaponTypes = new ArrayList<>(0);
        newWorld.setWeaponTypes(weaponTypes);
    }

    private void initAssets(World newWorld) {
        List<Asset> assets = new ArrayList<>(0);
        newWorld.setAssets(assets);
    }

    private void initLocations(World newWorld) {
        List<Location> locations = new ArrayList<>(0);
        Location location = new Location("new", 20, 20);
        Layer layer = new Layer("new");
        location.getLayers().add(layer);
        locations.add(location);
        controller.getModel().getCurrentLocation().setLocation(location);
        controller.getModel().getCurrentLayer().setLayer(layer);
        newWorld.setLocations(locations);
    }

    public void saveActivePlugin(PluginSettingsStage pss) {
        Model model = controller.getModel();
        Plugin activePlugin = model.getActivePlugin();
        loadObservableAssetsoPlugin(activePlugin);
        loadObservableLocationsToPlugin(activePlugin);
        loadObservableWeaponTypesToPlugin(activePlugin);
        loadObservableInventoryPlaceTypesToPlugin(activePlugin);
        File programDir = controller.getProgramDir();
        PluginCaretaker pc = new PluginCaretaker(programDir);
        setPluginParams(activePlugin, pss);
        pc.save(activePlugin);
    }

    private void loadObservableInventoryPlaceTypesToPlugin(Plugin activePlugin) {
        List<InventoryPlaceType> inventoryPlaceTypes = new ArrayList<>(observableInventoryPlaceTypes);
        activePlugin.getWorld().setInventoryPlaces(inventoryPlaceTypes);
    }

    private void loadObservableWeaponTypesToPlugin(Plugin activePlugin) {
        List<WeaponType> weaponTypes = new ArrayList<>(observableWeaponTypes);
        activePlugin.getWorld().setWeaponTypes(weaponTypes);
    }

    private void loadObservableLocationsToPlugin(Plugin activePlugin) {
        List<Location> locations = new ArrayList<>(observableLocations);
        activePlugin.getWorld().setLocations(locations);
    }

    private void loadObservableAssetsoPlugin(Plugin activePlugin) {
        List<Asset> mergedAssets = observableAssets.getMergedAssets();
        activePlugin.getWorld().setAssets(mergedAssets);
    }

    public void savePluginAs(String pluginName, PluginSettingsStage pss) {
        Plugin activePlugin = controller.getModel().getActivePlugin();
        loadObservableAssetsoPlugin(activePlugin);
        loadObservableLocationsToPlugin(activePlugin);
        File programDir = controller.getProgramDir();
        PluginCaretaker pc = new PluginCaretaker(programDir);
        activePlugin.setName(pluginName);
        setPluginParams(activePlugin, pss);
        pc.saveAs(activePlugin);
    }

    private void setPluginParams(Plugin activePlugin, PluginSettingsStage pss) {
        boolean isStartingLocation = pss.isStartingLocation();
        activePlugin.setStartingLocation(isStartingLocation);
        Coords startPos = new Coords();
        startPos.setLocation(pss.getStartLocation());
        startPos.x = pss.getStartX();
        startPos.y = pss.getStartY();
        startPos.level = pss.getStartLevel();
        activePlugin.setStartPos(startPos);
    }

    public void loadAndRestorePlugin(String pluginName, PluginSettingsStage pss) {
        File programDir = controller.getProgramDir();
        PluginCaretaker pc = new PluginCaretaker(programDir);
        Plugin loadedPlugin = pc.load(pluginName, controller);
        if (loadedPlugin == null) return;
        Model model = controller.getModel();
        model.setActivePlugin(loadedPlugin);
        Plugin activePlugin = model.getActivePlugin();
        World world = activePlugin.getWorld();
        List<Location> locations = world.getLocations();
        controller.restoreItemsCoords(locations);
        restoreObservableAssets(activePlugin);
        restoreObservableLocations(activePlugin);
        restoreObservableWeaponTypes(activePlugin);
        restoreObservableInventoryPlaces(activePlugin);
        restoreFirstLocationAndLayer(model, locations);
        restorePluginSettingsStage(pss, loadedPlugin);
    }

    private void restoreObservableInventoryPlaces(Plugin activePlugin) {
        observableInventoryPlaceTypes.clear();
        List<InventoryPlaceType> inventoryPlaceTypes = activePlugin.getWorld().getInventoryPlaces();
        observableInventoryPlaceTypes.addAll(inventoryPlaceTypes);
    }

    private void restoreObservableWeaponTypes(Plugin activePlugin) {
        observableWeaponTypes.clear();
        List<WeaponType> weaponTypes = activePlugin.getWorld().getWeaponTypes();
        observableWeaponTypes.addAll(weaponTypes);
    }

    private void restoreObservableLocations(Plugin activePlugin) {
        observableLocations.clear();
        List<Location> locations = activePlugin.getWorld().getLocations();
        observableLocations.addAll(locations);
    }

    private void restorePluginSettingsStage(PluginSettingsStage pss, Plugin activePlugin) {
        boolean isStartingLocation = activePlugin.isStartingLocation();
        pss.setStartingLocation(isStartingLocation);
        Coords startPos = activePlugin.getStartPos();
        controller.restoreCoordsOfLocation(startPos);
        pss.setStartLocation(startPos.getLocation());
        pss.setStartX(startPos.x);
        pss.setStartY(startPos.y);
        pss.setStartLevel(startPos.level);
    }

    private void restoreObservableAssets(Plugin activePlugin) {
        List<Asset> assets = activePlugin.getWorld().getAssets();
        observableAssets.fillLists(assets);
    }

    private void restoreFirstLocationAndLayer(Model model, List<Location> locations) {
        Location firstLocation = locations.get(0);
        model.getCurrentLocation().setLocation(firstLocation);
        Layer firstLayer = firstLocation.getLayers().get(0);
        model.getCurrentLayer().setLayer(firstLayer);
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
        return activeItemsStage;
    }

    public void setItemsStageToAddItems(ItemsStage itemsStage) {
        this.activeItemsStage = itemsStage;
    }

    public Controller getController() {
        return controller;
    }

    public PosItem getActiveItem() {
        return activeItem;
    }

    public void setActiveItem(PosItem activeItem) {
        this.activeItem = activeItem;
    }

    public ObservableAssets getObservableAssets() {
        return observableAssets;
    }

    public ObservableList<Location> getObservableLocations() {
        return observableLocations;
    }

    public ObservableList<WeaponType> getObservableWeaponTypes() {
        return observableWeaponTypes;
    }

    public ObservableList<InventoryPlaceType> getObservableInventoryPlacesTypes() {
        return observableInventoryPlaceTypes;
    }
}
