package editor.model;

import editor.model.settings.SettingsCaretaker;
import editor.model.settings.SettingsMemento;
import editor.view.asset.ItemsStage;
import editor.view.asset.ObservableAssets;
import editor.view.plugin.PluginSettingsStage;
import io.wsz.model.Controller;
import io.wsz.model.Model;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.item.Creature;
import io.wsz.model.item.EquipmentType;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.item.PosItem;
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
import java.util.Map;
import java.util.Optional;

public class EditorController {
    private final Controller controller;
    private final ObservableAssets observableAssets = new ObservableAssets();
    private final ObservableList<Location> observableLocations = FXCollections.observableArrayList();
    private final ObservableList<EquipmentType> observableEquipmentTypes = FXCollections.observableArrayList();
    private final ObservableList<InventoryPlaceType> observableInventoryPlaceTypes = FXCollections.observableArrayList();
    private final ObservableList<Dialog> observableDialogs = FXCollections.observableArrayList();
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
        initEquipmentTypes(newWorld);

        Plugin newPlugin = new Plugin();
        newPlugin.setWorld(newWorld);
        controller.getModel().setActivePlugin(newPlugin);
    }

    private void initEquipmentTypes(World newWorld) {
        List<EquipmentType> equipmentTypes = new ArrayList<>(0);
        newWorld.setEquipmentTypes(equipmentTypes);
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

    public void savePluginAs(String pluginName, PluginSettingsStage pss) {
        Plugin activePlugin = controller.getModel().getActivePlugin();
        World world = activePlugin.getWorld();
        loadObservableListToPlugin(world);
        File programDir = controller.getProgramDir();
        PluginCaretaker pc = new PluginCaretaker(programDir);
        activePlugin.setName(pluginName);
        setPluginParams(activePlugin, pss);
        pc.saveAs(activePlugin);
    }

    public void saveActivePlugin(PluginSettingsStage pss) {
        Model model = controller.getModel();
        Plugin activePlugin = model.getActivePlugin();
        World world = activePlugin.getWorld();
        loadObservableListToPlugin(world);
        File programDir = controller.getProgramDir();
        PluginCaretaker pc = new PluginCaretaker(programDir);
        setPluginParams(activePlugin, pss);
        pc.save(activePlugin);
    }

    private void loadObservableListToPlugin(World world) {
        loadObservableAssetsToPlugin(world);
        loadObservableLocationsToPlugin(world);
        loadObservableEquipmentTypesToPlugin(world);
        loadObservableInventoryPlaceTypesToPlugin(world);
        loadObservableDialogsToPlugin(world);
    }

    private void loadObservableDialogsToPlugin(World world) {
        List<Dialog> dialogs = new ArrayList<>(observableDialogs);
        world.setDialogs(dialogs);
    }

    private void loadObservableInventoryPlaceTypesToPlugin(World world) {
        List<InventoryPlaceType> inventoryPlaceTypes = new ArrayList<>(observableInventoryPlaceTypes);
        world.setInventoryPlaces(inventoryPlaceTypes);
    }

    private void loadObservableEquipmentTypesToPlugin(World world) {
        List<EquipmentType> equipmentTypes = new ArrayList<>(observableEquipmentTypes);
        world.setEquipmentTypes(equipmentTypes);
    }

    private void loadObservableLocationsToPlugin(World world) {
        List<Location> locations = new ArrayList<>(observableLocations);
        world.setLocations(locations);
    }

    private void loadObservableAssetsToPlugin(World world) {
        List<Asset> mergedAssets = observableAssets.getMergedAssets();
        world.setAssets(mergedAssets);
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
        loadPluginToObservableLists(world);
        restoreFirstLocationAndLayer(model, locations);
        restorePluginSettingsStage(pss, loadedPlugin);
        controller.restoreItemsReferences(locations);
    }

    void loadPluginToObservableLists(World world) {
        restoreObservableAssets(world);
        restoreObservableLocations(world);
        restoreObservableEquipmentTypes(world);
        restoreObservableInventoryPlaces(world);
        restoreObservableDialogs(world);
    }

    private void restoreObservableDialogs(World world) {
        observableDialogs.clear();
        List<Dialog> dialogs = world.getDialogs();
        observableDialogs.addAll(dialogs);
    }

    private void restoreObservableInventoryPlaces(World world) {
        observableInventoryPlaceTypes.clear();
        List<InventoryPlaceType> inventoryPlaceTypes = world.getInventoryPlaces();
        observableInventoryPlaceTypes.addAll(inventoryPlaceTypes);
    }

    private void restoreObservableEquipmentTypes(World world) {
        observableEquipmentTypes.clear();
        List<EquipmentType> equipmentTypes = world.getEquipmentTypes();
        observableEquipmentTypes.addAll(equipmentTypes);
    }

    private void restoreObservableLocations(World world) {
        observableLocations.clear();
        List<Location> locations = world.getLocations();
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

    private void restoreObservableAssets(World world) {
        List<Asset> assets = world.getAssets();
        observableAssets.fillLists(assets);
    }

    private void restoreFirstLocationAndLayer(Model model, List<Location> locations) {
        Location firstLocation = locations.get(0);
        model.getCurrentLocation().setLocation(firstLocation);
        Layer firstLayer = firstLocation.getLayers().get(0);
        model.getCurrentLayer().setLayer(firstLayer);
    }

    public void updateCreaturesInventoryPlacesNames(String oldName, String newName) {
        List<Creature> creatures = getObservableAssets().getCreatures();
        for (Creature cr : creatures) {
            Map<InventoryPlaceType, List<Coords>> inventoryPlaces = cr.getInventory().getInventoryPlaces();
            if (inventoryPlaces.isEmpty()) continue;
            Optional<InventoryPlaceType> optType = inventoryPlaces.keySet().stream()
                    .filter(t -> t.getName().equals(oldName))
                    .findFirst();
            InventoryPlaceType type = optType.orElse(null);
            if (type == null) continue;
            InventoryPlaceType newType = new InventoryPlaceType(newName);
            List<Coords> polygon = inventoryPlaces.get(type);
            inventoryPlaces.remove(type);
            inventoryPlaces.put(newType, polygon);
        }
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

    public ObservableList<EquipmentType> getObservableEquipmentTypes() {
        return observableEquipmentTypes;
    }

    public ObservableList<InventoryPlaceType> getObservableInventoryPlacesTypes() {
        return observableInventoryPlaceTypes;
    }

    public ObservableList<Dialog> getObservableDialogs() {
        return observableDialogs;
    }
}
