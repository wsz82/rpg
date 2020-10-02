package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.cursor.CursorType;
import io.wsz.model.animation.equipment.container.ContainerAnimation;
import io.wsz.model.animation.equipment.container.ContainerAnimationPos;
import io.wsz.model.animation.openable.OpenableAnimationType;
import io.wsz.model.item.list.EquipmentList;
import io.wsz.model.item.list.ItemsList;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
import io.wsz.model.world.World;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Set;

import static io.wsz.model.sizes.Paths.IDLE;

public class Container extends Equipment<Container, ContainerAnimationPos> implements Containable, Openable {
    private static final long serialVersionUID = 1L;

    private ContainerAnimation animation;

    private ContainerAnimationPos animationPos;
    private OpenableItem openableItem;
    private EquipmentList equipmentList;
    private Double nettoWeight;
    private Integer nettoSize;
    private boolean isOpen;

    public Container() {
    }

    @Override
    public void removeItemFromList(ItemsList list) {
        removeItemFromEquipmentList(list.getEquipment());
    }

    public Container(Controller controller) {
        super(ItemType.CONTAINER, controller);
        this.animationPos = new ContainerAnimationPos();
        this.openableItem = new OpenableItem();
        this.equipmentList = new EquipmentList(true);
    }

    public Container(Container prototype) {
        super(prototype);
        this.animationPos = new ContainerAnimationPos();
        this.equipmentList = new EquipmentList(prototype.getEquipmentList(), false);
        setOpen(prototype.isOpen());
    }

    public Container(Container other, boolean keepId) {
        super(other, keepId);
        this.animationPos = new ContainerAnimationPos(other.animationPos);
        this.equipmentList = new EquipmentList(other.equipmentList, keepId);
        this.nettoWeight = other.weight;
        this.nettoSize = other.nettoSize;
        this.isOpen = other.isOpen;
    }

    @Override
    public void addItemToEquipmentList(EquipmentList list) {
        list.getContainers().add(this);
    }

    @Override
    public void removeItemFromEquipmentList(EquipmentList equipment) {
        equipment.getContainers().remove(this);
    }

    @Override
    protected String getAssetDirName() {
        return Paths.CONTAINERS;
    }

    public boolean tryAdd(Equipment<?,?> equipment, boolean doMergeCountable) { //TODO addable delegate for this and inventory
        if (!fitsContainer(equipment)) {
            return false;
        }
        if (equipment.isCountable() && doMergeCountable) {
            EquipmentMayCountable<?,?> countable = getEquipmentList().getEquipmentMayCountableList().getMergedList().stream()
                    .filter(e -> e.getAssetId().equals(equipment.getAssetId()))
                    .filter(e -> e.isUnitIdentical(equipment))
                    .findFirst()
                    .orElse(null);
            if (countable == null) {
                equipment.addItemToEquipmentList(this.equipmentList);
            } else {
                Integer addedAmount = equipment.getAmount();
                Integer alreadyInAmount = countable.getAmount();
                int sum = alreadyInAmount + addedAmount;
                countable.setAmount(sum);
            }
        } else {
            equipment.addItemToEquipmentList(this.equipmentList);
        }
        return true;
    }

    private boolean fitsContainer(Equipment<?,?> equipment) {
        double size = equipment.getSize();
        return getFilledSpace() + size < getMaxSize();
    }

    private double getMaxSize() {
        return getSize() - getNettoSize();
    }

    public void remove(Equipment<?,?> e) {
        removeItemFromEquipmentList(equipmentList);
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
            controller.getLogger().logContainerSearchedBy(getName(), cr.getName());
        }
    }

    public double getFilledSpace() {
        return getEquipmentList().getMergedList().stream()
                .mapToDouble(Equipment::getSize)
                .sum();
    }

    @Override
    public void restoreReferences(Controller controller, ItemsList assets, World world) {
        super.restoreReferences(controller, assets, world);
        getEquipmentList().forEach(e -> e.restoreReferences(controller, assets, world));
    }

    @Override
    protected List<Container> getSpecificItemsList(ItemsList itemsList) {
        return itemsList.getEquipment().getContainers();
    }

    @Override
    protected Container getNewItemFromPrototype() {
        return new Container(this);
    }

    @Override
    public void setCursor(CursorSetter cursorSetter) {
        CursorType type;
        if (isOpen()) {
            type = CursorType.CONTAINER_OPEN;
        } else {
            type = CursorType.CONTAINER_CLOSED;
        }
        cursorSetter.set(type);
    }

    @Override
    public PosItem<?,?> getItemByAssetId(String lookedId) {
        return getItemByAssetId(this, lookedId);
    }

    @Override
    public PosItem<?,?> getItemByItemId(String lookedId) {
        return getItemByItemId(this, lookedId);
    }

    protected int getInnerAmountById(String checkedId) {
        return getContainableAmountById(checkedId);
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
        if (getEquipmentList() == null) return 0;
        return getEquipmentList().getMergedList().stream()
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
    public ResolutionImage getImage() {
        if (image == null) {
            File programDir = getController().getProgramDir();
            ContainerAnimation animation = getAnimation();
            if (isOpen()) {
                return animation.getOpenableAnimation().getBasicMainOpen(programDir);
            } else {
                return animation.getBasicMain(programDir);
            }
        } else {
            return image;
        }
    }

    @Override
    public ResolutionImage getOpenImage() {
        File programDir = getController().getProgramDir();
        ContainerAnimation animation = getAnimation();
        if (animation == null) return null;
        return animation.getOpenableAnimation().getBasicMainOpen(programDir);
    }

    @Override
    public void addPrototypeToSet(Set<PosItem<?,?>> prototypes) {
        addPrototypesToSet(this, prototypes);
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
    public Container cloneEquipment(boolean keepId) {
        return new Container(this, keepId);
    }

    @Override
    public EquipmentList getEquipmentList() {
        return equipmentList;
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
        ContainerAnimation animation = getAnimation();
        return animation.getOpenableAnimation().isNotOpenable(programDir);
    }

    @Override
    public void open() {
        isOpen = true;
        PosItem<?,?> collision = getCollision();
        String message = "open";
        if (collision != null) {
            isOpen = false;
            onOperateActionFailure(collision, message);
        } else {
            onOperateActionSuccess(message);
        }
    }

    @Override
    public void close() {
        isOpen = false;
        PosItem<?,?> collision = getCollision();
        String message = "closed";
        if (collision != null) {
            isOpen = true;
            onOperateActionFailure(collision, message);
        } else {
            onOperateActionSuccess(message);
        }
    }

    private void onOperateActionFailure(PosItem<?,?> collision, String message) {
        animationPos.getOpenableAnimationPos().setOpenableAnimationType(OpenableAnimationType.IDLE);
        getController().getLogger().logItemCannotBeActionBecauseCollides(getName(), message, collision.getName());
    }

    private void onOperateActionSuccess(String message) {
        animationPos.getOpenableAnimationPos().setOpenableAnimationType(OpenableAnimationType.OPERATING);
        getController().getLogger().logItemAction(getName(), message);
    }

    @Override
    protected ContainerAnimation getConcreteAnimation() {
        if (animation == null) {
            return new ContainerAnimation(getDir(), IDLE);
        } else {
            return animation;
        }
    }

    @Override
    public ContainerAnimationPos getAnimationPos() {
        return animationPos;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(animationPos);

        out.writeObject(equipmentList);

        out.writeObject(nettoWeight);

        out.writeObject(nettoSize);

        out.writeBoolean(isOpen);

        out.writeObject(openableItem);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        animationPos = (ContainerAnimationPos) in.readObject();

        if (isThisPrototype()) {
            animation = new ContainerAnimation(getDir(), IDLE);
        }

        equipmentList = (EquipmentList) in.readObject();

        nettoWeight = (Double) in.readObject();

        nettoSize = (Integer) in.readObject();

        isOpen = in.readBoolean();

        openableItem = (OpenableItem) in.readObject();
    }
}
