package io.wsz.model.item.list;

import io.wsz.model.item.*;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static io.wsz.model.item.ItemType.*;

public class ItemsList extends AssetList<PosItem<?,?>> {
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
        if (!initLists) return;
        this.landscapes = new ArrayList<>(0);
        this.covers = new ArrayList<>(0);
        this.creatures = new ArrayList<>(0);
        this.teleports = new ArrayList<>(0);
        this.inDoors = new ArrayList<>(0);
        this.outDoors = new ArrayList<>(0);
        this.equipment = new EquipmentList(initLists);
    }

    @Override
    public void add(PosItem<?, ?> item) {
        item.addItemToList(this);
    }

    @Override
    public void remove(PosItem<?,?> item) {
        item.removeItemFromList(this);
    }

    @Override
    public boolean contains(PosItem<?, ?> equipment) {
        boolean contains = landscapes.contains(equipment);
        if (!contains) contains = covers.contains(equipment);
        if (!contains) contains = creatures.contains(equipment);
        if (!contains) contains = teleports.contains(equipment);
        if (!contains) contains = inDoors.contains(equipment);
        if (!contains) contains = outDoors.contains(equipment);
        if (contains) return true;
        return this.equipment.contains(equipment);
    }

    @Override
    public PosItem<?,?> getItemByItemOrAssetId(String itemOrAssetId) {
        PosItem<?, ?> item = getItemFromList(landscapes, itemOrAssetId);
        if (item == null) item = getItemFromList(covers, itemOrAssetId);
        if (item == null) item = getItemFromList(creatures, itemOrAssetId);
        if (item == null) item = getItemFromList(teleports, itemOrAssetId);
        if (item == null) item = getItemFromList(inDoors, itemOrAssetId);
        if (item == null) item = getItemFromList(outDoors, itemOrAssetId);
        if (item == null) return equipment.getItemByItemOrAssetId(itemOrAssetId);
        return item;
    }

    @Override
    public void forEach(Consumer<? super PosItem<?,?>> action) {
        landscapes.forEach(action);
        covers.forEach(action);
        creatures.forEach(action);
        teleports.forEach(action);
        inDoors.forEach(action);
        outDoors.forEach(action);
        equipment.forEach(action);
    }

    @Override
    public void clear() {
        landscapes.clear();
        covers.clear();
        creatures.clear();
        teleports.clear();
        inDoors.clear();
        outDoors.clear();
        equipment.clear();
    }

    public void fillLists(List<PosItem<?,?>> items) {
        List<Creature> tempCr = items.stream()
                .filter(a -> a.getType().equals(CREATURE))
                .map(a -> (Creature) a)
                .collect(Collectors.toList());
        creatures.addAll(tempCr);
        List<Landscape> tempL = items.stream()
                .filter(a -> a.getType().equals(LANDSCAPE))
                .map(a -> (Landscape) a)
                .collect(Collectors.toList());
        landscapes.addAll(tempL);
        List<Cover> tempC = items.stream()
                .filter(a -> a.getType().equals(COVER))
                .map(a -> (Cover) a)
                .collect(Collectors.toList());
        covers.addAll(tempC);
        List<Teleport> tempT = items.stream()
                .filter(a -> a.getType().equals(TELEPORT))
                .map(a -> (Teleport) a)
                .collect(Collectors.toList());
        teleports.addAll(tempT);
        List<InDoor> tempInDoor = items.stream()
                .filter(a -> a.getType().equals(INDOOR))
                .map(a -> (InDoor) a)
                .collect(Collectors.toList());
        inDoors.addAll(tempInDoor);
        List<OutDoor> tempOutDoor = items.stream()
                .filter(a -> a.getType().equals(OUTDOOR))
                .map(a -> (OutDoor) a)
                .collect(Collectors.toList());
        outDoors.addAll(tempOutDoor);

        List<Equipment<?,?>> tempE = items.stream()
                .filter(a -> a instanceof Equipment)
                .map(a -> (Equipment<?,?>) a)
                .collect(Collectors.toList());
        equipment.fillLists(tempE);
    }

    @Override
    public List<PosItem<?,?>> getMergedList() {
        merged.clear();
        merged.addAll(landscapes);
        merged.addAll(covers);
        merged.addAll(creatures);
        merged.addAll(teleports);
        merged.addAll(inDoors);
        merged.addAll(outDoors);
        merged.addAll(equipment.getMergedList());
        return merged;
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

    public List<Equipment<?,?>> getMergedEquipment() {
        return equipment.getMergedList();
    }

    public List<EquipmentMayCountable<?,?>> getMergedEquipmentMayCountable() {
        return equipment.getEquipmentMayCountableList().getMergedList();
    }

    public List<Landscape> getLandscapes() {
        return landscapes;
    }

    public List<Cover> getCovers() {
        return covers;
    }

    public List<Creature> getCreatures() {
        return creatures;
    }

    public List<Teleport> getTeleports() {
        return teleports;
    }

    public List<InDoor> getInDoors() {
        return inDoors;
    }

    public List<OutDoor> getOutDoors() {
        return outDoors;
    }

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
