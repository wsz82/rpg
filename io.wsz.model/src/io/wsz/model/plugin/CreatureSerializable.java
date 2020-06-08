package io.wsz.model.plugin;

import io.wsz.model.item.CreatureControl;
import io.wsz.model.item.CreatureSize;
import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

public class CreatureSerializable extends PosItemSerializable {
    protected Coords dest;
    protected CreatureSize size;
    protected CreatureControl control;
    protected int speed;

    public CreatureSerializable(String name, ItemType type, String path, Coords pos, int level, Coords[] coverLine,
                                Coords dest, CreatureSize size, CreatureControl control, int speed) {
        super(name, type, path, pos, level, coverLine);
        this.dest = dest;
        this.size = size;
        this.control = control;
        this.speed = speed;
    }

    public Coords getDest() {
        return dest;
    }

    public void setDest(Coords dest) {
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
