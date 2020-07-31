package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class Container extends Equipment<Container> implements Containable, Openable {
    private static final long serialVersionUID = 1L;

    private final List<Equipment> items = new ArrayList<>(0);
    private Double nettoWeight;
    private Integer nettoSize;
    private boolean open;
    private Image openImage;
    private String openImagePath;
    private List<Coords> openContainerCoverLine;
    private List<List<Coords>> openContainerCollisionPolygons;

    public Container() {}

    public Container(ItemType type) {
        super(type);
        this.openContainerCoverLine = new ArrayList<>(0);
        this.openContainerCollisionPolygons = new ArrayList<>(0);
    }

    public Container(Container prototype, String name, ItemType type, String path, Boolean visible) {
        super(prototype, name, type, path, visible);
    }

    @Override
    public Container cloneEquipment() {
        Container clone = new Container(prototype, name.get(), type.get(), relativePath.get(), visible.get());
        clone.setCoverLine(Coords.cloneCoordsList(coverLine));
        clone.setCollisionPolygons(Coords.cloneCoordsPolygons(collisionPolygons));
        clone.getPos().x = this.pos.x;
        clone.getPos().y = this.pos.y;
        clone.setWeight(weight);
        clone.setSize(size);
        clone.getItems().addAll(Equipment.cloneEquipmentList(getItems()));
        clone.setNettoWeight(nettoWeight);
        clone.setNettoSize(nettoSize);
        clone.setOpen(isOpen());
        clone.setOpenImagePath(getOpenImagePath());
        clone.setOpenContainerCoverLine(Coords.cloneCoordsList(prototype.getOpenContainerCoverLine()));
        clone.setOpenContainerCollisionPolygons(Coords.cloneCoordsPolygons(prototype.getOpenContainerCollisionPolygons()));
        return clone;
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
        if (open) {
            searchOpenContainer(cr);
        } else {
            open();
            if (open) {
                searchOpenContainer(cr);
            }
        }
    }

    private void searchOpenContainer(Creature cr) {
        if (cr.getControl() == CreatureControl.CONTROL) {
            Controller.get().setCreatureToOpenInventory(cr);
            Controller.get().setContainerToOpen(this);
            Controller.get().setInventory(true);
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
            if (prototype == null) {
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
            if (prototype == null) {
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

    public List<Coords> getOpenContainerCoverLine() {
        if (openContainerCoverLine == null) {
            if (prototype == null) {
                return new ArrayList<>(0);
            }
            return prototype.openContainerCoverLine;
        } else {
            return openContainerCoverLine;
        }
    }

    public void setOpenContainerCoverLine(List<Coords> openContainerCoverLine) {
        this.openContainerCoverLine = openContainerCoverLine;
    }

    public List<List<Coords>> getOpenContainerCollisionPolygons() {
        if (openContainerCollisionPolygons == null) {
            if (prototype == null) {
                return new ArrayList<>(0);
            }
            return prototype.openContainerCollisionPolygons;
        } else {
            return openContainerCollisionPolygons;
        }
    }

    public void setOpenContainerCollisionPolygons(List<List<Coords>> openContainerCollisionPolygons) {
        this.openContainerCollisionPolygons = openContainerCollisionPolygons;
    }

    @Override
    public List<List<Coords>> getActualCollisionPolygons() {
        if (open) {
            return getOpenContainerCollisionPolygons();
        } else {
            return super.getActualCollisionPolygons();
        }
    }

    public String getOpenImagePath() {
        if (openImagePath == null || openImagePath.isEmpty()) {
            if (prototype == null) {
                return "";
            }
            return prototype.openImagePath;
        } else {
            return openImagePath;
        }
    }

    public void setOpenImagePath(String openImagePath) {
        this.openImagePath = openImagePath;
    }

    @Override
    public Image getOpenImage() {
        if (prototype == null) {
            if (openImage == null) {
                setOpenImage(loadImageFromPath(getOpenImagePath()));
            }
            return openImage;
        } else {
            return prototype.getOpenImage();
        }
    }

    @Override
    public void setOpenImage(Image openImage) {
        this.openImage = openImage;
    }

    @Override
    public Image getImage() {
        if (open) {
            if (prototype == null) {
                return getOpenImage();
            } else {
                return prototype.getOpenImage();
            }
        } else {
            if (prototype == null) {
                return getInitialImage();
            } else {
                return prototype.getInitialImage();
            }
        }
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
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
    public Double getWeight() {
        return getNettoWeight() + getItems().stream()
                .mapToDouble(Equipment::getWeight)
                .sum();
    }

    @Override
    public List<Equipment> getItems() {
        return items;
    }

    @Override
    public void open() {
        open = true;
        PosItem collision = getCollision();
        if (collision != null) {
            open = false;
            System.out.println(getName() + " cannot be open: collides with " + collision.getName());
        } else {
            System.out.println(getName() + " open");
        }
    }

    @Override
    public void close() {
        open = false;
        PosItem collision = getCollision();
        if (collision != null) {
            open = true;
            System.out.println(getName() + " cannot be closed: collides with " + collision.getName());
        } else {
            System.out.println(getName() + " closed");
        }
    }

    @Override
    public boolean creatureSecondaryInteract(Creature cr) {
        CreatureSize size = cr.getSize();
        if (withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight())) {
            if (getObstacleOnWay(cr) != null) return false;
            if (open) {
                close();
            } else {
                open();
            }
            return true;
        }
        return false;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(items);

        out.writeObject(nettoWeight);

        out.writeObject(nettoSize);

        out.writeObject(openImagePath);
        
        out.writeBoolean(open);

        out.writeObject(openContainerCoverLine);

        out.writeObject(openContainerCollisionPolygons);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        List<Equipment> serItems = (List<Equipment>) in.readObject();
        items.addAll(serItems);

        nettoWeight = (Double) in.readObject();

        nettoSize = (Integer) in.readObject();

        openImagePath = (String) in.readObject();

        open = in.readBoolean();

        openContainerCoverLine = (List<Coords>) in.readObject();

        openContainerCollisionPolygons = (List<List<Coords>>) in.readObject();

    }
}
