package io.wsz.model.item.list;

import io.wsz.model.item.Container;
import io.wsz.model.item.Equipment;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class EquipmentList extends AbstractEquipmentList implements Externalizable {
    private static final long serialVersionUID = 1L;

    private List<Container> containers;
    private EquipmentMayCountableList equipmentMayCountableList;

    public EquipmentList() {
    }

    public EquipmentList(boolean initLists) {
        super(initLists);
    }

    public EquipmentList(AbstractEquipmentList other, boolean keepId) {
        super(other, keepId);
    }

    @Override
    protected void cloneLists(AbstractEquipmentList other, boolean keepId) {
        this.containers = Equipment.cloneEquipmentList(other.getContainers(), keepId);
        this.equipmentMayCountableList = new EquipmentMayCountableList(other.getEquipmentMayCountableList(), keepId);
    }

    @Override
    protected void initLists(boolean initLists) {
        this.containers = new ArrayList<>(0);
        this.equipmentMayCountableList = new EquipmentMayCountableList(initLists);
    }

    @Override
    public List<Container> getContainers() {
        return containers;
    }

    @Override
    public EquipmentMayCountableList getEquipmentMayCountableList() {
        return equipmentMayCountableList;
    }

    @Override
    public void fillWith(AbstractEquipmentList equipmentList) {
        containers = new ArrayList<>(equipmentList.getContainers());
        equipmentMayCountableList = new EquipmentMayCountableList();
        equipmentMayCountableList.fillWith(equipmentList.getEquipmentMayCountableList());
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
