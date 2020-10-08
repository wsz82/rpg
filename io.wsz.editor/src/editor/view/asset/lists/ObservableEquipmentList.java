package editor.view.asset.lists;

import io.wsz.model.item.Container;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.PosItem;
import io.wsz.model.item.list.AbstractEquipmentList;
import io.wsz.model.item.list.EquipmentList;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import java.util.ArrayList;

public class ObservableEquipmentList extends AbstractEquipmentList {
    private ObservableList<Container> containers;
    private ObservableEquipmentMayCountableList equipmentMayCountableList;

    public ObservableEquipmentList() {
    }

    public ObservableEquipmentList(boolean initLists) {
        super(initLists);
    }

    public ObservableEquipmentList(AbstractEquipmentList other, boolean keepId) {
        super(other, keepId);
    }

    @Override
    protected void cloneLists(AbstractEquipmentList other, boolean keepId) {
        this.containers = FXCollections.observableArrayList(Equipment.cloneEquipmentList(other.getContainers(), keepId));
        this.equipmentMayCountableList = new ObservableEquipmentMayCountableList(other.getEquipmentMayCountableList(), keepId);
    }

    @Override
    protected void initLists(boolean initLists) {
        this.containers = FXCollections.observableArrayList();
        this.equipmentMayCountableList = new ObservableEquipmentMayCountableList(true);
    }

    @Override
    public ObservableList<Container> getContainers() {
        return containers;
    }

    @Override
    public ObservableEquipmentMayCountableList getEquipmentMayCountableList() {
        return equipmentMayCountableList;
    }

    public EquipmentList createEquipmentList() {
        EquipmentList equipmentList = new EquipmentList();
        equipmentList.setContainers(new ArrayList<>(containers));
        equipmentList.setEquipmentMayCountableList(equipmentMayCountableList.createEquipmentMayCountableList());
        return equipmentList;
    }

    public void fillWith(AbstractEquipmentList equipmentList) {
        clear();
        containers.addAll(equipmentList.getContainers());
        equipmentMayCountableList.fillWith(equipmentList.getEquipmentMayCountableList());
    }

    public void addListener(ListChangeListener<? super PosItem<?,?>> locationListener) {
        containers.addListener(locationListener);
        equipmentMayCountableList.addListener(locationListener);
    }
}
