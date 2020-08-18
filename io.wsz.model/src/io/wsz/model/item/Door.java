package io.wsz.model.item;

import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.ResolutionImage;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public abstract class Door<I extends Door> extends PosItem<I> implements Openable {
    private static final long serialVersionUID = 1L;

    protected String openImagePath;
    protected Image openImage;
    protected boolean open;
    protected List<Coords> openDoorCoverLine;
    protected List<List<Coords>> openDoorCollisionPolygons;

    public Door() {}

    public Door(ItemType type) {
        super(type);
        this.openDoorCoverLine = new ArrayList<>(0);
        this.openDoorCollisionPolygons = new ArrayList<>(0);
    }

    public Door(I prototype, Boolean visible) {
            super(prototype, visible);
    }

    public String getIndividualOpenImagePath() {
        return openImagePath;
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
            if (openImagePath.isEmpty()) {
                return getInitialImage();
            }
            if (openImage == null) {
                String type = getType().toString().toLowerCase();
                String openImagePath = getOpenImagePath();
                File programDir = getController().getProgramDir();
                setOpenImage(ResolutionImage.loadImage(programDir, type, openImagePath));
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

    public List<Coords> getOpenDoorCoverLine() {
        if (prototype != null) {
            return prototype.openDoorCoverLine;
        } else {
            return openDoorCoverLine;
        }
    }

    public void setOpenDoorCoverLine(List<Coords> openDoorCoverLine) {
        this.openDoorCoverLine = openDoorCoverLine;
    }

    public List<List<Coords>> getOpenDoorCollisionPolygons() {
        if (prototype != null) {
            return prototype.openDoorCollisionPolygons;
        } else {
            return openDoorCollisionPolygons;
        }
    }

    public void setOpenDoorCollisionPolygons(List<List<Coords>> openDoorCollisionPolygons) {
        this.openDoorCollisionPolygons = openDoorCollisionPolygons;
    }

    @Override
    public List<Coords> getActualCoverLine() {
        if (open) {
            return getOpenDoorCoverLine();
        } else {
            return super.getActualCoverLine();
        }
    }

    @Override
    public List<List<Coords>> getActualCollisionPolygons() {
        if (open) {
            return getOpenDoorCollisionPolygons();
        } else {
            return super.getActualCollisionPolygons();
        }
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

        out.writeObject(openImagePath);

        out.writeBoolean(open);

        out.writeObject(openDoorCoverLine);

        out.writeObject(openDoorCollisionPolygons);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        openImagePath = (String) in.readObject();

        open = in.readBoolean();

        openDoorCoverLine = (List<Coords>) in.readObject();

        openDoorCollisionPolygons = (List<List<Coords>>) in.readObject();
    }
}
