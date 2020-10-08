package editor.view.asset.lists;

import io.wsz.model.item.*;
import io.wsz.model.item.list.AbstractItemsList;
import io.wsz.model.item.list.ItemsList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ObservableItemsList extends AbstractItemsList {
    private ObservableList<Creature> creatures;
    private ObservableList<Landscape> landscapes;
    private ObservableList<Cover> covers;
    private ObservableList<Teleport> teleports;
    private ObservableList<InDoor> inDoors;
    private ObservableList<OutDoor> outDoors;
    private ObservableEquipmentList equipmentList;

    public ObservableItemsList() {}

    public ObservableItemsList(boolean initLists) {
        super(initLists);
    }

    @Override
    protected void initLists(boolean initLists) {
        creatures = FXCollections.observableArrayList();
        landscapes = FXCollections.observableArrayList();
        covers = FXCollections.observableArrayList();
        teleports = FXCollections.observableArrayList();
        inDoors = FXCollections.observableArrayList();
        outDoors = FXCollections.observableArrayList();
        equipmentList = new ObservableEquipmentList(true);
    }

    public ItemsList createItemsList() {
        ItemsList itemsList = new ItemsList();
        itemsList.setLandscapes(new ArrayList<>(landscapes));
        itemsList.setCovers(new ArrayList<>(covers));
        itemsList.setCreatures(new ArrayList<>(creatures));
        itemsList.setTeleports(new ArrayList<>(teleports));
        itemsList.setOutDoors(new ArrayList<>(outDoors));
        itemsList.setInDoors(new ArrayList<>(inDoors));
        itemsList.setEquipment(equipmentList.createEquipmentList());
        return itemsList;
    }

    public ObservableList<PosItem<?,?>> getEquipmentAssets() {
        ObservableList<PosItem<?,?>> equipments = FXCollections.observableArrayList();
        equipments.addAll(equipmentList.getMergedList());
        return equipments;
    }

    @Override
    public void fillWith(AbstractItemsList itemsList) {
        clear();
        creatures.addAll(itemsList.getCreatures());
        landscapes.addAll(itemsList.getLandscapes());
        covers.addAll(itemsList.getCovers());
        teleports.addAll(itemsList.getTeleports());
        inDoors.addAll(itemsList.getInDoors());
        outDoors.addAll(itemsList.getOutDoors());
        this.equipmentList.fillWith(itemsList.getEquipment());
    }

    public void addListener(ListChangeListener<? super PosItem<?,?>> locationListener) {
        creatures.addListener(locationListener);
        landscapes.addListener(locationListener);
        covers.addListener(locationListener);
        teleports.addListener(locationListener);
        inDoors.addListener(locationListener);
        outDoors.addListener(locationListener);
        equipmentList.addListener(locationListener);
    }

    @Override
    public ObservableList<Creature> getCreatures() {
        return creatures;
    }

    @Override
    public ObservableList<Landscape> getLandscapes() {
        return landscapes;
    }

    @Override
    public ObservableList<Cover> getCovers() {
        return covers;
    }

    @Override
    public ObservableList<Teleport> getTeleports() {
        return teleports;
    }

    @Override
    public ObservableList<InDoor> getInDoors() {
        return inDoors;
    }

    @Override
    public ObservableList<OutDoor> getOutDoors() {
        return outDoors;
    }

    @Override
    public ObservableEquipmentList getEquipment() {
        return equipmentList;
    }

    public ObservableList<Container> getContainers() {
        return equipmentList.getContainers();
    }

    public ObservableList<Weapon> getWeapons() {
        return equipmentList.getEquipmentMayCountableList().getWeapons();
    }

    public ObservableList<Misc> getMiscs() {
        return equipmentList.getEquipmentMayCountableList().getMiscs();
    }
}
