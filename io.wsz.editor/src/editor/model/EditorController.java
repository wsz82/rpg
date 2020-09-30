package editor.model;

import editor.model.settings.SettingsCaretaker;
import editor.model.settings.SettingsMemento;
import editor.view.asset.ObservableAssets;
import editor.view.asset.items.ItemsStage;
import editor.view.plugin.PluginSettingsStage;
import editor.view.script.variable.ObservableVariables;
import io.wsz.model.Controller;
import io.wsz.model.Model;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.item.Creature;
import io.wsz.model.item.EquipmentType;
import io.wsz.model.item.InventoryPlaceType;
import io.wsz.model.item.PosItem;
import io.wsz.model.layer.CurrentObservableLayer;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.CurrentObservableLocation;
import io.wsz.model.location.Location;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginCaretaker;
import io.wsz.model.plugin.PluginFileCaretaker;
import io.wsz.model.plugin.PluginMetadata;
import io.wsz.model.script.Script;
import io.wsz.model.script.variable.*;
import io.wsz.model.stage.Coords;
import io.wsz.model.world.World;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EditorController extends Controller {
    private final ObservableAssets observableAssets = new ObservableAssets();
    private final ObservableList<Location> observableLocations = FXCollections.observableArrayList();
    private final ObservableList<EquipmentType> observableEquipmentTypes = FXCollections.observableArrayList();
    private final ObservableList<InventoryPlaceType> observableInventoryPlaceTypes = FXCollections.observableArrayList();
    private final ObservableList<Dialog> observableDialogs = FXCollections.observableArrayList();
    private final ObservableVariables observableGlobals = new ObservableVariables();
    private final ObservableList<Script> observableScripts = FXCollections.observableArrayList();
    private final Coords dragPos = new Coords(-1, -1);

    private PosItem activeItem;
    private ItemsStage activeItemsStage;

    public EditorController(){}

    public SettingsMemento restoreSettings(File programDir) {
        return new SettingsCaretaker(programDir).loadMemento();
    }

    public void storeSettings(File programDir, SettingsMemento memento) {
        new SettingsCaretaker(programDir).saveMemento(memento);
    }

    public void initNewPlugin() {
        clearObservableLists();

        CurrentObservableLocation currentObservableLocation = model.getCurrentLocation();
        CurrentObservableLayer currentObservableLayer = model.getCurrentLayer();

        World newWorld = new World();

        initWorldsList(currentObservableLocation, currentObservableLayer, newWorld);

        Plugin newPlugin = new Plugin();

        model.setActivePlugin(newPlugin);
        PluginMetadata newPluginMetadata = new PluginMetadata();

        model.setActivePluginMetadata(newPluginMetadata);
        newPlugin.setWorld(newWorld);
    }

    private void initWorldsList(CurrentObservableLocation currentObservableLocation, CurrentObservableLayer currentObservableLayer, World newWorld) {
        initNewLocation(currentObservableLocation, currentObservableLayer);
        initLocations(newWorld);
        initAssets(newWorld);
        initEquipmentTypes(newWorld);
        initInventoryPlaceTypes(newWorld);
        initDialogs(newWorld);
        initGlobalVariables(newWorld);
        initScripts(newWorld);
    }

    private void initScripts(World newWorld) {
        List<Script> scripts = new ArrayList<>(0);
        newWorld.setScripts(scripts);
    }

    private void initGlobalVariables(World newWorld) {
        Variables variables = new Variables();

        List<VariableString> strings = new ArrayList<>(0);
        variables.setStrings(strings);
        List<VariableBoolean> booleans = new ArrayList<>(0);
        variables.setBooleans(booleans);
        List<VariableInteger> integers = new ArrayList<>(0);
        variables.setIntegers(integers);
        List<VariableDecimal> decimals = new ArrayList<>(0);
        variables.setDecimals(decimals);

        newWorld.setVariables(variables);
    }

    private void initLocations(World newWorld) {
        List<Location> locations = new ArrayList<>(0);
        newWorld.setLocations(locations);
    }

    private void initDialogs(World newWorld) {
        List<Dialog> dialogs = new ArrayList<>(0);
        newWorld.setDialogs(dialogs);
    }

    private void initInventoryPlaceTypes(World newWorld) {
        List<InventoryPlaceType> inventoryPlaceTypes = new ArrayList<>(0);
        newWorld.setInventoryPlaces(inventoryPlaceTypes);
    }

    private void clearObservableLists() {
        observableAssets.clearLists();
        observableLocations.clear();
        observableEquipmentTypes.clear();
        observableLocations.clear();
        observableInventoryPlaceTypes.clear();
        observableDialogs.clear();
        observableGlobals.clear();
        observableScripts.clear();
    }

    private void initEquipmentTypes(World newWorld) {
        List<EquipmentType> equipmentTypes = new ArrayList<>(0);
        newWorld.setEquipmentTypes(equipmentTypes);
    }

    private void initAssets(World newWorld) {
        List<Asset> assets = new ArrayList<>(0);
        newWorld.setAssets(assets);
    }

    private void initNewLocation(CurrentObservableLocation currentObservableLocation, CurrentObservableLayer currentObservableLayer) {
        Location location = new Location("new", 20, 20);
        Layer layer = new Layer("new");
        location.getLayers().add(layer);

        currentObservableLocation.setLocation(location);
        currentObservableLayer.setLayer(layer);
        observableLocations.add(location);
    }

    public void savePluginAs(String pluginName, Coords startPos) {
        savePlugin(pluginName, startPos, model);
    }

    private void savePlugin(String pluginName, Coords startPos, Model model) {
        Plugin activePlugin = model.getActivePlugin();

        model.getCurrentLocation().saveCurrent();
        model.getCurrentLayer().saveCurrent();

        activePlugin.setStartPos(startPos);
        World world = activePlugin.getWorld();
        loadObservableListsToPlugin(world);
        PluginFileCaretaker fileCaretaker = new PluginFileCaretaker(programDir);
        PluginMetadata metadata = model.getActivePluginMetadata();

        fileCaretaker.save(activePlugin, metadata, pluginName);
    }

    public void saveActivePlugin(String pluginName, Coords startPos) {
        savePlugin(pluginName, startPos, model);
    }

    private void loadObservableListsToPlugin(World world) {
        loadObservableAssetsToPlugin(world);
        loadObservableLocationsToPlugin(world);
        loadObservableEquipmentTypesToPlugin(world);
        loadObservableInventoryPlaceTypesToPlugin(world);
        loadObservableDialogsToPlugin(world);
        loadObservableGlobalVariablesToPlugin(world);
        loadObservableScriptsToPlugin(world);
    }

    private void loadObservableScriptsToPlugin(World world) {
        List<Script> variables = new ArrayList<>(observableScripts);
        world.setScripts(variables);
    }

    private void loadObservableGlobalVariablesToPlugin(World world) {
        Variables variables = new Variables();

        List<VariableString> strings = new ArrayList<>(observableGlobals.getStrings());
        variables.setStrings(strings);
        List<VariableBoolean> booleans = new ArrayList<>(observableGlobals.getBooleans());
        variables.setBooleans(booleans);
        List<VariableInteger> integers = new ArrayList<>(observableGlobals.getIntegers());
        variables.setIntegers(integers);
        List<VariableDecimal> decimals = new ArrayList<>(observableGlobals.getDecimals());
        variables.setDecimals(decimals);

        world.setVariables(variables);
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
        List<Asset> mergedAssets = new ArrayList<>(observableAssets.getMergedAssets());
        world.setAssets(mergedAssets);
    }

    public void loadAndRestorePlugin(PluginMetadata metadata, PluginSettingsStage pss) {
        if (metadata == null) return;

        model.setActivePluginMetadata(metadata);
        String pluginName = metadata.getPluginName();
        PluginCaretaker pluginCaretaker = new PluginCaretaker(programDir);
        Plugin loadedPlugin = pluginCaretaker.deserialize(pluginName);
        if (loadedPlugin == null) return;

        model.setActivePlugin(loadedPlugin);
        World world = loadedPlugin.getWorld();
        loadPluginToObservableLists(world);

        List<Location> locations = world.getLocations();
        List<Asset> assets = world.getAssets();
        restoreItemsReferences(assets, locations, world);

        restoreFirstLocationAndLayer(model, locations);
        restorePluginSettingsStage(pss, metadata, loadedPlugin);
    }

    void loadPluginToObservableLists(World world) {
        restoreObservableAssets(world);
        restoreObservableLocations(world);
        restoreObservableEquipmentTypes(world);
        restoreObservableInventoryPlaces(world);
        restoreObservableDialogs(world);
        restoreObservableGlobalVariables(world);
        restoreObservableScripts(world);
    }

    private void restoreObservableScripts(World world) {
        observableScripts.clear();
        List<Script> scripts = world.getScripts();
        observableScripts.addAll(scripts);
    }

    private void restoreObservableGlobalVariables(World world) {
        observableGlobals.fillLists(world.getVariables());
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

    private void restorePluginSettingsStage(PluginSettingsStage pss, PluginMetadata metadata, Plugin activePlugin) {
        boolean isStartingLocation = metadata.isStartingLocation();
        pss.setStartingLocation(isStartingLocation);
        Coords startPos = activePlugin.getStartPos();
        restoreLocationOfCoords(startPos);
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
        if (locations.isEmpty()) return;
        Location firstLocation = locations.get(0);
        CurrentObservableLocation currentObservableLocation = model.getCurrentLocation();
        currentObservableLocation.setLocation(null);
        currentObservableLocation.setLocation(firstLocation);
        Layer firstLayer = firstLocation.getLayers().get(0);
        model.getCurrentLayer().setLayer(firstLayer);
    }

    public void updateCreaturesInventoryPlacesNames(String oldName, String newName) {
        List<Creature> creatures = getObservableAssets().getCreatures();
        for (Creature cr : creatures) {
            Map<InventoryPlaceType, List<Coords>> inventoryPlaces = cr.getInventory().getInventoryPlaces();
            if (inventoryPlaces.isEmpty()) continue;
            Optional<InventoryPlaceType> optType = inventoryPlaces.keySet().stream()
                    .filter(t -> t.getId().equals(oldName))
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

    public ObservableVariables getObservableGlobals() {
        return observableGlobals;
    }

    public ObservableList<VariableString> getObservableGlobalStrings() {
        return observableGlobals.getStrings();
    }

    public ObservableList<VariableBoolean> getObservableGlobalBooleans() {
        return observableGlobals.getBooleans();
    }

    public ObservableList<VariableInteger> getObservableGlobalIntegers() {
        return observableGlobals.getIntegers();
    }

    public ObservableList<VariableDecimal> getObservableGlobalDecimals() {
        return observableGlobals.getDecimals();
    }

    public ObservableList<Script> getObservableScripts() {
        return observableScripts;
    }
}
