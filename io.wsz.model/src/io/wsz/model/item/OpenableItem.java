package io.wsz.model.item;

import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

public class OpenableItem implements Externalizable {
    private static final long serialVersionUID = 1L;

    public static OpenableItem clone(OpenableItem openableItem) {
        if (openableItem == null) return null;
        OpenableItem clone = new OpenableItem();
        clone.setOpenCoverLine(Coords.cloneCoordsList(openableItem.getOpenCoverLine()));
        clone.setOpenCollisionPolygons(Coords.cloneCoordsPolygons(openableItem.getOpenCollisionPolygons()));
        return clone;
    }

    private List<Coords> openCoverLine;
    private List<List<Coords>> openCollisionPolygons;

    public OpenableItem() {
        this.openCoverLine = new ArrayList<>(0);
        this.openCollisionPolygons = new ArrayList<>(0);
    }

    public List<Coords> getOpenCoverLine() {
        return openCoverLine;
    }

    public void setOpenCoverLine(List<Coords> openCoverLine) {
        this.openCoverLine = openCoverLine;
    }

    public List<List<Coords>> getOpenCollisionPolygons() {
        return openCollisionPolygons;
    }

    public void setOpenCollisionPolygons(List<List<Coords>> openCollisionPolygons) {
        this.openCollisionPolygons = openCollisionPolygons;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(Sizes.VERSION);

        out.writeObject(openCoverLine);

        out.writeObject(openCollisionPolygons);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        openCoverLine = (List<Coords>) in.readObject();

        openCollisionPolygons = (List<List<Coords>>) in.readObject();
    }
}
