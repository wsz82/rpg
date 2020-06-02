package io.wsz.model.plugin;

import io.wsz.model.item.CreatureControl;
import io.wsz.model.item.CreatureSize;
import io.wsz.model.item.ItemType;

public class CreatureSerializable extends PosItemSerializable {
    protected CoordsSerializable dest;
    protected CreatureSize size = CreatureSize.M;
    protected CreatureControl control = CreatureControl.CONTROLABLE;
    protected int speed = 30;

    public CreatureSerializable(String name, ItemType type, String path, CoordsSerializable pos, int level,
                                CoordsSerializable dest, CreatureSize size, CreatureControl control, int speed) {
        super(name, type, path, pos, level);
        this.dest = dest;
        this.size = size;
        this.control = control;
        this.speed = speed;
    }

    public CreatureSerializable(String name, ItemType type, String path, CoordsSerializable pos, int level) {
        super(name, type, path, pos, level);
    }

    public CoordsSerializable getDest() {
        return dest;
    }

    public void setDest(CoordsSerializable dest) {
        this.dest = dest;
    }

    public CreatureSize getSize() {
        return size;
    }

    public void setSize(CreatureSize size) {
        this.size = size;
    }

    public CreatureControl getControl() {
        return control;
    }

    public void setControl(CreatureControl control) {
        this.control = control;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }
}
