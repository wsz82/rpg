package editor.view.asset;

import io.wsz.model.item.*;
import io.wsz.model.item.list.EquipmentList;
import io.wsz.model.item.list.EquipmentMayCountableList;
import io.wsz.model.item.list.ItemsList;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ObservableItemsList {
    private final ObservableList<Creature> creatures = FXCollections.observableArrayList();
    private final ObservableList<Landscape> landscapes = FXCollections.observableArrayList();
    private final ObservableList<Cover> covers = FXCollections.observableArrayList();
    private final ObservableList<Teleport> teleports = FXCollections.observableArrayList();
    private final ObservableList<Weapon> weapons = FXCollections.observableArrayList();
    private final ObservableList<Container> containers = FXCollections.observableArrayList();
    private final ObservableList<InDoor> inDoors = FXCollections.observableArrayList();
    private final ObservableList<OutDoor> outDoors = FXCollections.observableArrayList();
    private final ObservableList<Misc> miscs = FXCollections.observableArrayList();

    public ObservableItemsList() {}

    public ObservableList<PosItem<?,?>> getMergedAssets() {
        ObservableList<PosItem<?,?>> assets = FXCollections.observableArrayList();
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

    public ItemsList createItemsList() {
        ItemsList itemsList = new ItemsList();
        itemsList.setLandscapes(new ArrayList<>(landscapes));
        itemsList.setCovers(new ArrayList<>(covers));
        itemsList.setCreatures(new ArrayList<>(creatures));
        itemsList.setTeleports(new ArrayList<>(teleports));
        itemsList.setOutDoors(new ArrayList<>(outDoors));
        itemsList.setInDoors(new ArrayList<>(inDoors));
        EquipmentList equipmentList = new EquipmentList();
        equipmentList.setContainers(new ArrayList<>(containers));
        EquipmentMayCountableList equipmentMayCountableList = new EquipmentMayCountableList();
        equipmentMayCountableList.setMiscs(new ArrayList<>(miscs));
        equipmentMayCountableList.setWeapons(new ArrayList<>(weapons));
        equipmentList.setEquipmentMayCountableList(equipmentMayCountableList);
        itemsList.setEquipment(equipmentList);
        return itemsList;
    }

    public ObservableList<PosItem<?,?>> getEquipmentAssets() {
        ObservableList<PosItem<?,?>> equipments = FXCollections.observableArrayList();
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

    public void fillLists(ItemsList assetsList) {
        clearLists();
        creatures.addAll(assetsList.getCreatures());
        landscapes.addAll(assetsList.getLandscapes());
        covers.addAll(assetsList.getCovers());
        teleports.addAll(assetsList.getTeleports());
        inDoors.addAll(assetsList.getInDoors());
        outDoors.addAll(assetsList.getOutDoors());
        EquipmentList equipment = assetsList.getEquipment();
        weapons.addAll(equipment.getWeapons());
        containers.addAll(equipment.getContainers());
        miscs.addAll(equipment.getMiscs());
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
