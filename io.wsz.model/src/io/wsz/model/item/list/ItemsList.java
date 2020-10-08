package io.wsz.model.item.list;

import io.wsz.model.item.*;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class ItemsList extends AbstractItemsList implements Externalizable {
    private static final long serialVersionUID = 1L;

    private final List<PosItem<?,?>> merged = new ArrayList<>(0);

    private List<Landscape> landscapes;
    private List<Cover> covers;
    private List<Creature> creatures;
    private List<Teleport> teleports;
    private List<InDoor> inDoors;
    private List<OutDoor> outDoors;
    private EquipmentList equipment;

    public ItemsList() {
    }

    public ItemsList(boolean initLists) {
        super(initLists);
    }

    @Override
    protected void initLists(boolean initLists) {
        this.landscapes = new ArrayList<>(0);
        this.covers = new ArrayList<>(0);
        this.creatures = new ArrayList<>(0);
        this.teleports = new ArrayList<>(0);
        this.inDoors = new ArrayList<>(0);
        this.outDoors = new ArrayList<>(0);
        this.equipment = new EquipmentList(initLists);
    }

    @Override
    public void fillWith(AbstractItemsList itemsList) {
        landscapes = new ArrayList<>(itemsList.getLandscapes());
        covers = new ArrayList<>(itemsList.getCovers());
        creatures = new ArrayList<>(itemsList.getCreatures());
        teleports = new ArrayList<>(itemsList.getTeleports());
        inDoors = new ArrayList<>(itemsList.getInDoors());
        outDoors = new ArrayList<>(itemsList.getOutDoors());
        equipment = new EquipmentList();
        equipment.fillWith(itemsList.getEquipment());
    }

    public List<PosItem<?,?>> getMergedListWithoutCreatures() {
        merged.clear();
        merged.addAll(landscapes);
        merged.addAll(covers);
        merged.addAll(teleports);
        merged.addAll(inDoors);
        merged.addAll(outDoors);
        merged.addAll(equipment.getMergedList());
        return merged;
    }

    @Override
    public List<Landscape> getLandscapes() {
        return landscapes;
    }

    @Override
    public List<Cover> getCovers() {
        return covers;
    }

    @Override
    public List<Creature> getCreatures() {
        return creatures;
    }

    @Override
    public List<Teleport> getTeleports() {
        return teleports;
    }

    @Override
    public List<InDoor> getInDoors() {
        return inDoors;
    }

    @Override
    public List<OutDoor> getOutDoors() {
        return outDoors;
    }

    @Override
    public EquipmentList getEquipment() {
        return equipment;
    }

    public void setLandscapes(List<Landscape> landscapes) {
        this.landscapes = landscapes;
    }

    public void setCovers(List<Cover> covers) {
        this.covers = covers;
    }

    public void setCreatures(List<Creature> creatures) {
        this.creatures = creatures;
    }

    public void setTeleports(List<Teleport> teleports) {
        this.teleports = teleports;
    }

    public void setInDoors(List<InDoor> inDoors) {
        this.inDoors = inDoors;
    }

    public void setOutDoors(List<OutDoor> outDoors) {
        this.outDoors = outDoors;
    }

    public void setEquipment(EquipmentList equipment) {
        this.equipment = equipment;
    }

    public EquipmentMayCountableList getEquipmentMayCountableList() {
        return equipment.getEquipmentMayCountableList();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(landscapes);
        out.writeObject(covers);
        out.writeObject(creatures);
        out.writeObject(teleports);
        out.writeObject(inDoors);
        out.writeObject(outDoors);
        out.writeObject(equipment);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        landscapes = (List<Landscape>) in.readObject();
        covers = (List<Cover>) in.readObject();
        creatures = (List<Creature>) in.readObject();
        teleports = (List<Teleport>) in.readObject();
        inDoors = (List<InDoor>) in.readObject();
        outDoors = (List<OutDoor>) in.readObject();
        equipment = (EquipmentList) in.readObject();
    }
}
