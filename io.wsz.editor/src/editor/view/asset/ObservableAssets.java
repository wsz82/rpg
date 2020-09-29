package editor.view.asset;

import io.wsz.model.asset.Asset;
import io.wsz.model.item.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.stream.Collectors;

import static io.wsz.model.item.ItemType.*;

public class ObservableAssets {
    private final ObservableList<Creature> creatures = FXCollections.observableArrayList();
    private final ObservableList<Landscape> landscapes = FXCollections.observableArrayList();
    private final ObservableList<Cover> covers = FXCollections.observableArrayList();
    private final ObservableList<Teleport> teleports = FXCollections.observableArrayList();
    private final ObservableList<Weapon> weapons = FXCollections.observableArrayList();
    private final ObservableList<Container> containers = FXCollections.observableArrayList();
    private final ObservableList<InDoor> inDoors = FXCollections.observableArrayList();
    private final ObservableList<OutDoor> outDoors = FXCollections.observableArrayList();
    private final ObservableList<Misc> miscs = FXCollections.observableArrayList();

    public ObservableAssets() {}

    public ObservableList<Asset> getMergedAssets() {
        ObservableList<Asset> assets = FXCollections.observableArrayList();
        assets.addAll(creatures);
        assets.addAll(landscapes);
        assets.addAll(covers);
        assets.addAll(teleports);
        assets.addAll(weapons);
        assets.addAll(containers);
        assets.addAll(inDoors);
        assets.addAll(outDoors);
        assets.addAll(miscs);
        return assets;
    }

    public ObservableList<Asset<?>> getEquipmentAssets() {
        ObservableList<Asset<?>> equipments = FXCollections.observableArrayList();
        equipments.addAll(weapons);
        equipments.addAll(containers);
        equipments.addAll(miscs);
        return equipments;
    }

    public void clearLists() {
        creatures.clear();
        landscapes.clear();
        covers.clear();
        teleports.clear();
        weapons.clear();
        containers.clear();
        inDoors.clear();
        outDoors.clear();
        miscs.clear();
    }

    public void fillLists(List<Asset> assets) {
        clearLists();
        List<Creature> tempCr = assets.stream()
                .filter(a -> a.getType().equals(CREATURE))
                .map(a -> (Creature) a)
                .collect(Collectors.toList());
        creatures.addAll(tempCr);
        List<Landscape> tempL = assets.stream()
                .filter(a -> a.getType().equals(LANDSCAPE))
                .map(a -> (Landscape) a)
                .collect(Collectors.toList());
        landscapes.addAll(tempL);
        List<Cover> tempC = assets.stream()
                .filter(a -> a.getType().equals(COVER))
                .map(a -> (Cover) a)
                .collect(Collectors.toList());
        covers.addAll(tempC);
        List<Teleport> tempT = assets.stream()
                .filter(a -> a.getType().equals(TELEPORT))
                .map(a -> (Teleport) a)
                .collect(Collectors.toList());
        teleports.addAll(tempT);
        List<Weapon> tempW = assets.stream()
                .filter(a -> a.getType().equals(WEAPON))
                .map(a -> (Weapon) a)
                .collect(Collectors.toList());
        weapons.addAll(tempW);
        List<Container> tempCon = assets.stream()
                .filter(a -> a.getType().equals(CONTAINER))
                .map(a -> (Container) a)
                .collect(Collectors.toList());
        containers.addAll(tempCon);
        List<InDoor> tempInDoor = assets.stream()
                .filter(a -> a.getType().equals(INDOOR))
                .map(a -> (InDoor) a)
                .collect(Collectors.toList());
        inDoors.addAll(tempInDoor);
        List<OutDoor> tempOutDoor = assets.stream()
                .filter(a -> a.getType().equals(OUTDOOR))
                .map(a -> (OutDoor) a)
                .collect(Collectors.toList());
        outDoors.addAll(tempOutDoor);
        List<Misc> tempMiscs = assets.stream()
                .filter(a -> a.getType().equals(MISC))
                .map(a -> (Misc) a)
                .collect(Collectors.toList());
        miscs.addAll(tempMiscs);
    }

    public ObservableList<Creature> getCreatures() {
        return creatures;
    }

    public ObservableList<Landscape> getLandscapes() {
        return landscapes;
    }

    public ObservableList<Cover> getCovers() {
        return covers;
    }

    public ObservableList<Teleport> getTeleports() {
        return teleports;
    }

    public ObservableList<Weapon> getWeapons() {
        return weapons;
    }

    public ObservableList<Container> getContainers() {
        return containers;
    }

    public ObservableList<InDoor> getInDoors() {
        return inDoors;
    }

    public ObservableList<OutDoor> getOutDoors() {
        return outDoors;
    }

    public ObservableList<Misc> getMiscs() {
        return miscs;
    }
}
