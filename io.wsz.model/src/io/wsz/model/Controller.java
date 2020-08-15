package io.wsz.model;

import io.wsz.model.asset.Asset;
import io.wsz.model.asset.AssetsList;
import io.wsz.model.dialog.DialogMemento;
import io.wsz.model.item.*;
import io.wsz.model.layer.CurrentLayer;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.location.Location;
import io.wsz.model.location.LocationsList;
import io.wsz.model.plugin.ActivePlugin;
import io.wsz.model.plugin.Plugin;
import io.wsz.model.plugin.PluginCaretaker;
import io.wsz.model.stage.Board;
import io.wsz.model.stage.Coords;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class Controller {
    private static Controller singleton;
    private static File programDir;

    private final AtomicBoolean isInventory = new AtomicBoolean(false);
    private final LinkedList<Creature> heroes = new LinkedList<>();
    private final List<Creature> creaturesToControl = new ArrayList<>(0);
    private final List<Creature> creaturesToLooseControl = new ArrayList<>(0);
    private final Coords posToCenter = new Coords();

    private Location locationToUpdate;
    private Creature creatureToOpenInventory;
    private Container containerToOpen;
    private DialogMemento dialogMemento;

    public static Controller get() {
        if (singleton == null) {
            singleton = new Controller();
        }
        return singleton;
    }

    public static File getProgramDir() {
        return programDir;
    }

    public static void setProgramDir(File programDir) {
        Controller.programDir = programDir;
    }

    private Controller(){}

    public void setActivePlugin(Plugin plugin) {
        ActivePlugin.get().setPlugin(plugin);
    }

    public Plugin getActivePlugin() {
        return ActivePlugin.get().getPlugin();
    }

    public void loadAssetsToList() {
        Plugin plugin = ActivePlugin.get().getPlugin();

        AssetsList.get().clear();
        AssetsList.get().addAll(plugin.getAssets());
    }

    public Plugin loadPlugin(String pluginName) {
        if (pluginName == null) {
            return new Plugin();
        }
        PluginCaretaker pc = new PluginCaretaker();
        return pc.load(pluginName);
    }

    public void removeItem(PosItem pi) {
        CurrentLocation.get().getItems().remove(pi);
    }

    public CurrentLayer getCurrentLayer() {
        return CurrentLayer.get();
    }

    public CurrentLocation getCurrentLocation() {
        return CurrentLocation.get();
    }

    public void fillLocationsList(List<Location> locations) {
        getLocationsList().addAll(locations);
        restoreCoordsLocations();
    }

    private void restoreCoordsLocations() {
        List<Location> locations = getLocationsList();
        for (Location l : locations) {
            for (PosItem pi : l.getItems().get()) {
                Coords pos = pi.getPos();
                restoreCoordsLocation(pos);

                if (pi instanceof Teleport) {
                    Teleport t = (Teleport) pi;
                    Coords exit = t.getExit();
                    restoreCoordsLocation(exit);
                }

                if (pi instanceof OutDoor) {
                    OutDoor od = (OutDoor) pi;
                    Coords exit = od.getExit();
                    restoreCoordsLocation(exit);
                    restoreOutDoorConnection(od);
                }
            }
        }
    }

    private void restoreOutDoorConnection(OutDoor od) {
        OutDoor serConnection = od.getIndividualConnection();
        if (serConnection == null) return;
        String name = serConnection.getName();
        Coords pos = serConnection.getPos();
        restoreCoordsLocation(pos);
        Location loc = pos.getLocation();
        Optional<OutDoor> optConnection = loc.getItems().get().stream()
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

    public void restoreCoordsLocation(Coords pos) {
        Location serLoc = pos.getLocation();
        if (serLoc != null) {
            Optional<Location> optionalLocation = getLocationsList().stream()
                    .filter(refLoc -> refLoc.getName().equals(serLoc.getName()))
                    .findFirst();
            Location foundLoc = optionalLocation.orElse(null);
            if (foundLoc == null) {
                throw new NullPointerException("Location \"" + serLoc.getName() + "\" should be in locations list");
            }
            pos.setLocation(foundLoc);
        }
    }

    public ObservableList<Location> getLocationsList() {
        return LocationsList.get();
    }

    public List<Asset> getAssetsList() {
        return AssetsList.get();
    }

    public Board getBoard() {
        return Board.get();
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
        PluginCaretaker pc = new PluginCaretaker();
        return pc.getPluginMetadata(name);
    }


    public Location getLocationToUpdate() {
        return locationToUpdate;
    }

    public void setLocationToUpdate(Location locationToUpdate) {
        this.locationToUpdate = locationToUpdate;
    }

    public Coords getCurPos() {
        return Board.get().getCurPos();
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
        Location start = getCurrentLocation().getLocation();
        List<Creature> controllables = Controller.get().getBoard().getControllableCreatures(start);
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

    public void clearHeroesPortraits() {
        for (Creature hero : Controller.get().getHeroes()) {
            hero.getAnimation().reloadPortraits();
        }
    }

    public boolean isInventory() {
        return isInventory.get();
    }

    public void setInventory(boolean inventory) {
        isInventory.set(inventory);
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
}