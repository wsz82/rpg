package io.wsz.model.plugin;

import io.wsz.model.item.CreatureControl;
import io.wsz.model.item.CreatureSize;
import io.wsz.model.item.ItemType;
import io.wsz.model.stage.Coords;

import java.util.List;

public class CreatureSerializable extends PosItemSerializable {
    protected Coords dest;
    protected CreatureSize size;
    protected CreatureControl control;
    protected Integer speed;

    public CreatureSerializable(String prototype, String name, ItemType type, String path, Coords pos, Integer level,
                                List<Coords> coverLine, List<List<Coords>> collisionPolygons,
                                Coords dest, CreatureSize size, CreatureControl control, Integer speed) {
        super(prototype, name, type, path, pos, level, coverLine, collisionPolygons);
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

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(Integer speed) {
        this.speed = speed;
    }
}
