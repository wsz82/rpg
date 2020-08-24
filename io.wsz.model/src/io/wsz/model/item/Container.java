package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.equipment.EquipmentAnimationPos;
import io.wsz.model.animation.equipment.container.ContainerAnimation;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class Container extends Equipment<Container, EquipmentAnimationPos> implements Containable, Openable {
    private static final long serialVersionUID = 1L;

    private ContainerAnimation animation;

    private final EquipmentAnimationPos animationPos;
    private OpenableItem openableItem;
    private final List<Equipment> items;
    private Double nettoWeight;
    private Integer nettoSize;
    private boolean isOpen;

    public Container() {
        this.animationPos = new EquipmentAnimationPos();
        this.items = new ArrayList<>(0);
    }

    public Container(ItemType type) {
        super(type);
        this.animationPos = new EquipmentAnimationPos();
        this.openableItem = new OpenableItem();
        this.items = new ArrayList<>(0);
    }

    public Container(Container prototype, Boolean visible) {
        super(prototype, visible);
        this.animationPos = new EquipmentAnimationPos();
        this.items = new ArrayList<>(0);
    }

    public Container(Container other) {
        super(other);
        this.animationPos = new EquipmentAnimationPos(other.animationPos);
        this.items = Equipment.cloneEquipmentList(other.items);
        this.nettoWeight = other.weight;
        this.nettoSize = other.nettoSize;
        this.isOpen = other.isOpen;
    }

    public boolean add(Equipment e) {
        double size = e.getSize();
        if (getFilledSpace() + size > getSize() - getNettoSize()) {
            return false;
        }
        items.add(e);
        return true;
    }

    public void remove(Equipment e) {
        items.remove(e);
        setWeight(getWeight() - e.getWeight());
    }

    public void searchContainer(Creature cr) {
        if (isOpen() || isNotOpenable()) {
            searchOpenContainer(cr);
        } else {
            open();
            if (isOpen()) {
                searchOpenContainer(cr);
            }
        }
    }

    private void searchOpenContainer(Creature cr) {
        if (cr.getControl() == CreatureControl.CONTROL) {
            Controller controller = getController();
            controller.setCreatureToOpenInventory(cr);
            controller.setContainerToOpen(this);
            controller.setInventory(true);
            System.out.println(getName() + " searched by " + cr.getName());
        }
    }

    public int getFilledSpace() {
        return getItems().stream()
                .mapToInt(Equipment::getSize)
                .sum();
    }

    public Double getIndividualNettoWeight() {
        return nettoWeight;
    }

    public Double getNettoWeight() {
        if (nettoWeight == null) {
            if (isThisPrototype()) {
                return 0.0;
            }
            return prototype.nettoWeight;
        } else {
            return nettoWeight;
        }
    }

    public void setNettoWeight(Double nettoWeight) {
        this.nettoWeight = nettoWeight;
    }

    public Integer getIndividualNettoSize() {
        return nettoSize;
    }

    public Integer getNettoSize() {
        if (nettoSize == null) {
            if (isThisPrototype()) {
                return 0;
            }
            return prototype.nettoSize;
        } else {
            return nettoSize;
        }
    }

    public void setNettoSize(Integer nettoSize) {
        this.nettoSize = nettoSize;
    }

    @Override
    public Double getWeight() {
        return getNettoWeight() + getItemsWeight();
    }

    private double getItemsWeight() {
        if (getItems() == null) return 0;
        return getItems().stream()
                .mapToDouble(Equipment::getWeight)
                .sum();
    }

    @Override
    public List<List<Coords>> getActualCollisionPolygons() {
        if (isOpen) {
            return getOpenableItem().getOpenCollisionPolygons();
        } else {
            return super.getActualCollisionPolygons();
        }
    }

    @Override
    public Image getImage() {
        if (image == null) {
            File programDir = getController().getProgramDir();
            if (isOpen()) {
                return getAnimation().getOpenableAnimation().getBasicMainOpen(programDir);
            } else {
                return getAnimation().getBasicMain(programDir);
            }
        } else {
            return image;
        }
    }

    @Override
    public Image getOpenImage() {
        File programDir = getController().getProgramDir();
        return getAnimation().getOpenableAnimation().getBasicMainOpen(programDir);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public OpenableItem getIndividualOpenableItem() {
        return openableItem;
    }

    public OpenableItem getOpenableItem() {
        if (isThisPrototype()) {
            return openableItem;
        } else {
            return prototype.getOpenableItem();
        }
    }

    public void setOpenableItem(OpenableItem openableItem) {
        this.openableItem = openableItem;
    }

    @Override
    public Container cloneEquipment() {
        return new Container(this);
    }

    @Override
    public List<Equipment> getItems() {
        return items;
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        CreatureSize size = cr.getSize();
        if (withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (getObstacleOnWay(cr) != null) return false;
            searchContainer(cr);
            return true;
        }
        return false;
    }

    @Override
    public boolean creatureSecondaryInteract(Creature cr) {
        CreatureSize size = cr.getSize();
        if (withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (getObstacleOnWay(cr) != null) return false;
            if (isOpen() || isNotOpenable()) {
                close();
            } else {
                open();
            }
            return true;
        }
        return false;
    }

    private boolean isNotOpenable() {
        File programDir = getController().getProgramDir();
        return getAnimation().getOpenableAnimation().isNotOpenable(programDir);
    }

    @Override
    public void open() {
        isOpen = true;
        PosItem collision = getCollision();
        if (collision != null) {
            isOpen = false;
            System.out.println(getName() + " cannot be open: collides with " + collision.getName());
        } else {
            System.out.println(getName() + " open");
        }
    }

    @Override
    public void close() {
        isOpen = false;
        PosItem collision = getCollision();
        if (collision != null) {
            isOpen = true;
            System.out.println(getName() + " cannot be closed: collides with " + collision.getName());
        } else {
            System.out.println(getName() + " closed");
        }
    }

    @Override
    public ContainerAnimation getAnimation() {
        if (isThisPrototype()) {
            return animation;
        } else {
            return prototype.getAnimation();
        }
    }

    @Override
    public EquipmentAnimationPos getAnimationPos() {
        return animationPos;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(items);

        out.writeObject(nettoWeight);

        out.writeObject(nettoSize);

        out.writeBoolean(isOpen);

        out.writeObject(openableItem);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        if (isThisPrototype()) {
            animation = new ContainerAnimation(getDir());
        }

        List<Equipment> serItems = (List<Equipment>) in.readObject();
        items.addAll(serItems);

        nettoWeight = (Double) in.readObject();

        nettoSize = (Integer) in.readObject();

        isOpen = in.readBoolean();

        openableItem = (OpenableItem) in.readObject();
    }
}
