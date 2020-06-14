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
    protected Double speed;

    public CreatureSerializable(String prototype, String name, ItemType type, String path,
                                Boolean visible, Coords pos, Integer level,
                                List<Coords> coverLine, List<List<Coords>> collisionPolygons,
                                Coords dest, CreatureSize size, CreatureControl control, Double speed) {
        super(prototype, name, type, path, visible, pos, level, coverLine, collisionPolygons);
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

    public Double getSpeed() {
        return speed;
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }
}
