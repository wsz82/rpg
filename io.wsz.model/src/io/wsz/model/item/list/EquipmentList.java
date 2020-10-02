package io.wsz.model.item.list;

import io.wsz.model.item.*;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.wsz.model.item.ItemType.CONTAINER;

public class EquipmentList extends AssetList<Equipment<?,?>> {
    private static final long serialVersionUID = 1L;

    private final List<Equipment<?,?>> mergedEquipment = new ArrayList<>(0);

    private List<Container> containers;
    private EquipmentMayCountableList equipmentMayCountableList;

    public EquipmentList() {
    }

    public EquipmentList(boolean initLists) {
        if (!initLists) return;
        this.containers = new ArrayList<>(0);
        this.equipmentMayCountableList = new EquipmentMayCountableList(initLists);
    }

    public EquipmentList(EquipmentList other, boolean keepId) {
        this.containers = Equipment.cloneEquipmentList(other.getContainers(), keepId);
        this.equipmentMayCountableList = new EquipmentMayCountableList(other.getEquipmentMayCountableList());
    }

    @Override
    public void clear() {
        containers.clear();
        equipmentMayCountableList.clear();
    }

    @Override
    public void add(Equipment<?,?> equipment) {
        equipment.addItemToEquipmentList(this);
    }

    @Override
    public void remove(Equipment<?,?> equipment) {
        equipment.removeItemFromEquipmentList(this);
    }

    @Override
    public boolean contains(PosItem<?, ?> equipment) {
        boolean contains;
        contains = containers.contains(equipment);
        if (contains) return true;
        return equipmentMayCountableList.contains(equipment);
    }

    public void fillLists(List<Equipment<?,?>> items) {
        List<Container> tempCon = items.stream()
                .filter(a -> a.getType().equals(CONTAINER))
                .map(a -> (Container) a)
                .collect(Collectors.toList());
        containers.addAll(tempCon);
        List<EquipmentMayCountable<?,?>> tempCountable = items.stream()
                .filter(a -> a instanceof EquipmentMayCountable<?,?>)
                .map(a -> (EquipmentMayCountable<?,?>) a)
                .collect(Collectors.toList());
        equipmentMayCountableList.fillLists(tempCountable);
    }

    @Override
    public List<Equipment<?,?>> getMergedList() {
        mergedEquipment.clear();
        mergedEquipment.addAll(containers);
        mergedEquipment.addAll(equipmentMayCountableList.getMergedList());
        return mergedEquipment;
    }

    @Override
    public void forEach(Consumer<? super Equipment<?,?>> action) {
        containers.forEach(action);
        equipmentMayCountableList.forEach(action);
    }

    @Override
    public Equipment<?,?> getItemByItemOrAssetId(String itemOrAssetId) {
        Equipment<?, ?> equipment = getItemFromList(containers, itemOrAssetId);
        if (equipment == null) {
            return equipmentMayCountableList.getItemByItemOrAssetId(itemOrAssetId);
        }
        return equipment;
    }

    public List<Container> getContainers() {
        return containers;
    }

    public EquipmentMayCountableList getEquipmentMayCountableList() {
        return equipmentMayCountableList;
    }

    public List<Misc> getMiscs() {
        return equipmentMayCountableList.getMiscs();
    }

    public List<Weapon> getWeapons() {
        return equipmentMayCountableList.getWeapons();
    }

    public void setContainers(List<Container> containers) {
        this.containers = containers;
    }

    public void setEquipmentMayCountableList(EquipmentMayCountableList equipmentMayCountableList) {
        this.equipmentMayCountableList = equipmentMayCountableList;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(containers);
        out.writeObject(equipmentMayCountableList);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        containers = (List<Container>) in.readObject();
        equipmentMayCountableList = (EquipmentMayCountableList) in.readObject();
    }
}
