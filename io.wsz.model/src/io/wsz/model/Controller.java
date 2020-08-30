package io.wsz.model;

import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.dialog.DialogMemento;
import io.wsz.model.item.*;
import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginCaretaker;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import io.wsz.model.textures.Fog;
import io.wsz.model.world.World;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller {
    private final Board board = new Board(this);
    private final Fog fog = new Fog();
    private final LinkedList<Creature> heroes = new LinkedList<>();
    private final List<Creature> creaturesToControl = new ArrayList<>(0);
    private final List<Creature> creaturesToLooseControl = new ArrayList<>(0);
    private final Coords posToCenter = new Coords();
    private final AtomicBoolean isInventory = new AtomicBoolean();

    private Model model;
    private File programDir;
    private Location locationToUpdate;
    private Creature creatureToOpenInventory;
    private Container containerToOpen;
    private DialogMemento dialogMemento;

    public Controller(){}

    public File getProgramDir() {
        return programDir;
    }

    public void setProgramDir(File programDir) {
        this.programDir = programDir;
    }

    public Plugin loadPlugin(String pluginName) {
        if (pluginName == null) {
            return new Plugin();
        }
        PluginCaretaker pc = new PluginCaretaker(programDir);
        return pc.load(pluginName, this);
    }

    public void restoreItemsReferences(List<Location> locations) {
        World world = getModel().getActivePlugin().getWorld();
        List<Dialog> dialogs = world.getDialogs();
        List<InventoryPlaceType> places = world.getInventoryPlaces();
        for (Asset a : getAssets()) {
            restoreItemReferences(world, dialogs, places, a);
        }
        for (Location l : locations) {
            for (PosItem pi : l.getItems()) {
                restoreItemReferences(world, dialogs, places, pi);
            }
        }
    }

    void restoreItemReferences(World world, List<Dialog> dialogs, List<InventoryPlaceType> places, Asset a) {
        if (a instanceof PosItem) {
            PosItem pi = (PosItem) a;
            Coords pos = pi.getPos();
            restoreCoordsOfLocation(pos);
            restoreDialog(pi, dialogs);
        }
        if (a instanceof Creature) {
            Creature cr = (Creature) a;
            restoreCreatureEquippedItemsPlaces(cr, places);
        }
        if (a instanceof Teleport) {
            Teleport t = (Teleport) a;
            Coords exit = t.getExit();
            restoreCoordsOfLocation(exit);
        }
        if (a instanceof OutDoor) {
            OutDoor od = (OutDoor) a;
            Coords exit = od.getExit();
            restoreCoordsOfLocation(exit);
            restoreOutDoorConnection(od);
        }
        if (a instanceof Equipment) {
            Equipment e = (Equipment) a;
            restoreEquipmentType(e, world);
            restoreOccupiedPlace(e, world);
        }
    }

    private void restoreDialog(PosItem pi, List<Dialog> dialogs) {
        Dialog serDialog = pi.getDialog();
        if (serDialog == null) return;
        String serID = serDialog.getID();
        Optional<Dialog> optDialog = dialogs.stream()
                .filter(d -> d.getID().equals(serID))
                .findFirst();
        Dialog dialog = optDialog.orElse(null);
        if (dialog == null) {
            throw new NullPointerException(pi.getName() + " dialog \"" + serDialog.getID() + "\" should be in list of dialogs");
        }
        pi.setDialog(dialog);
    }

    private void restoreCreatureEquippedItemsPlaces(Creature cr, List<InventoryPlaceType> places) {
        Inventory inventory = cr.getInventory();
        Map<InventoryPlaceType, Equipment> equippedItems = inventory.getEquippedItems();
        Map<InventoryPlaceType,Equipment> restored = new HashMap<>(equippedItems.size());
        for (InventoryPlaceType serType : equippedItems.keySet()) {
            InventoryPlaceType typeWithRef = getReferencedPlaceType(places, serType);
            restored.put(typeWithRef, equippedItems.get(serType));
        }
        inventory.setEquippedItems(restored);
    }

    private void restoreOccupiedPlace(Equipment e, World world) {
        List<InventoryPlaceType> places = world.getInventoryPlaces();
        InventoryPlaceType serOccupiedPlace = e.getIndividualOccupiedPlace();
        InventoryPlaceType place = getReferencedPlaceType(places, serOccupiedPlace);
        if (place == null) return;
        e.setOccupiedPlace(place);
    }

    private InventoryPlaceType getReferencedPlaceType(List<InventoryPlaceType> places, InventoryPlaceType serType) {
        if (serType == null) {
            return null;
        }
        Optional<InventoryPlaceType> optType = places.stream()
                .filter(t -> t.getName().equals(serType.getName()))
                .findFirst();
        InventoryPlaceType place = optType.orElse(null);
        if (place == null) {
            throw new NullPointerException("Inventory place \"" + serType.getName() + "\" should be in list of inventory places");
        }
        return place;
    }

    private void restoreEquipmentType(Equipment e, World world) {
        List<EquipmentType> types = world.getEquipmentTypes();
        EquipmentType serEquipmentType = e.getIndividualEquipmentType();
        if (serEquipmentType == null) {
            return;
        }
        Optional<EquipmentType> optType = types.stream()
                .filter(t -> t.getName().equals(serEquipmentType.getName()))
                .findFirst();
        EquipmentType equipmentType = optType.orElse(null);
        if (equipmentType == null) {
            throw new NullPointerException("Equipment type \"" + serEquipmentType.getName() + "\" should be in list of equipment types");
        }
        e.setEquipmentType(equipmentType);
    }

    private void restoreOutDoorConnection(OutDoor od) {
        OutDoor serConnection = od.getIndividualConnection();
        if (serConnection == null) return;
        String name = serConnection.getName();
        Coords pos = serConnection.getPos();
        restoreCoordsOfLocation(pos);
        Location location = pos.getLocation();
        Optional<OutDoor> optConnection = location.getItems().stream()
                .filter(o -> o instanceof OutDoor)
                .map(o -> (OutDoor) o)
                .filter(o -> o.getName().equals(name))
                .filter(o -> o.getPos().equals(pos))
                .findFirst();
        OutDoor connection = optConnection.orElse(null);
        if (connection == null) {
            throw new NullPointerException("OutDoor connection \"" + serConnection.getName() + "\" should be in location outDoors list");
        }
        od.setConnection(connection);
    }

    public void restoreCoordsOfLocation(Coords pos) {
        Location serLoc = pos.getLocation();
        if (serLoc != null) {
            Optional<Location> optionalLocation = getLocations().stream()
                    .filter(refLoc -> refLoc.getName().equals(serLoc.getName()))
                    .findFirst();
            Location foundLoc = optionalLocation.orElse(null);
            if (foundLoc == null) {
                throw new NullPointerException("Location \"" + serLoc.getName() + "\" should be in locations list");
            }
            pos.setLocation(foundLoc);
        }
    }

    public Board getBoard() {
        return board;
    }

    public Coords getPosToCenter() {
        return posToCenter;
    }

    public void setPosToCenter(Coords posToCenter) {
        this.posToCenter.x = posToCenter.x;
        this.posToCenter.y = posToCenter.y;
        this.posToCenter.level = posToCenter.level;
        this.posToCenter.setLocation(posToCenter.getLocation());
    }

    public Plugin loadPluginMetadata(String name) {
        PluginCaretaker pc = new PluginCaretaker(programDir);
        return pc.getPluginMetadata(name);
    }


    public Location getLocationToUpdate() {
        return locationToUpdate;
    }

    public void setLocationToUpdate(Location locationToUpdate) {
        this.locationToUpdate = locationToUpdate;
    }

    public Coords getCurPos() {
        return board.getCurPos();
    }

    public Creature getCreatureToOpenInventory() {
        return creatureToOpenInventory;
    }

    public void setCreatureToOpenInventory(Creature creatureToOpenContainer) {
        this.creatureToOpenInventory = creatureToOpenContainer;
    }

    public void setContainerToOpen(Container container) {
        this.containerToOpen = container;
    }

    public Container getContainerToOpen() {
        return containerToOpen;
    }

    public PosItem getAsking() {
        if (dialogMemento == null) {
            return null;
        } else {
            return dialogMemento.getAsking();
        }
    }

    public void setAsking(PosItem asking) {
        initDialogMementoIfNull();
        this.dialogMemento.setAsking(asking);
    }

    public PosItem getAnswering() {
        if (dialogMemento == null) {
            return null;
        } else {
            return dialogMemento.getAnswering();
        }
    }

    public void setAnswering(PosItem answering) {
        initDialogMementoIfNull();
        this.dialogMemento.setAnswering(answering);
    }

    private void initDialogMementoIfNull() {
        if (dialogMemento == null) {
            dialogMemento = new DialogMemento();
        }
    }

    public LinkedList<Creature> getHeroes() {
        return heroes;
    }

    public void initNewGameHeroes() {
        this.heroes.clear();
        Location start = model.getCurrentLocation().getLocation();
        List<Creature> controllables = board.getControllableCreatures(start);
        for (Creature cr : controllables) {
            this.heroes.addLast(cr);
        }
    }

    public List<Creature> getCreaturesToControl() {
        return creaturesToControl;
    }

    public List<Creature> getCreaturesToLooseControl() {
        return creaturesToLooseControl;
    }

    public void initLoadGameHeroes(LinkedList<Creature> heroes) {
        this.heroes.clear();
        this.heroes.addAll(heroes);
    }

    public void clearResizablePictures() {
        getAssets().stream()
                .filter(a -> a instanceof Creature)
                .forEach(a -> ((Creature) a).getAnimation().clearResizablePictures());
    }

    public boolean isInventory() {
        return isInventory.get();
    }

    public void setInventory(boolean isInventory) {
        this.isInventory.set(isInventory);
    }

    public void closeInventory() {
        setInventory(false);
        setContainerToOpen(null);
        setCreatureToOpenInventory(null);
    }

    public DialogMemento getDialogMemento() {
        return dialogMemento;
    }

    public void setDialogMemento(DialogMemento dialogMemento) {
        this.dialogMemento = dialogMemento;
    }

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public List<Location> getLocations() {
        return model.getActivePlugin().getWorld().getLocations();
    }

    public List<Asset> getAssets() {
        return model.getActivePlugin().getWorld().getAssets();
    }

    public CurrentLocation getCurrentLocation() {
        return model.getCurrentLocation();
    }

    public CurrentLayer getCurrentLayer() {
        return model.getCurrentLayer();
    }

    public Plugin getActivePlugin() {
        return model.getActivePlugin();
    }

    public void initNewModel() {
        model = new Model();
        model.setCurrentLocation(new CurrentLocation());
        model.setCurrentLayer(new CurrentLayer());
    }

    public Fog getFog() {
        return fog;
    }
}