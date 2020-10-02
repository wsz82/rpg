package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.item.list.ItemsList;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.world.World;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import static io.wsz.model.sizes.Paths.IDLE;

public class Teleport extends PosItem<Teleport, AnimationPos> {
    private static final long serialVersionUID = 1L;

    private Animation<AnimationPos, Teleport> animation;

    private TeleportableDelagate teleportableDelagate;
    private AnimationPos animationPos;
    private Coords exit;
    private List<List<Coords>> teleportCollisionPolygons;

    public Teleport() {}

    public Teleport(Controller controller) {
        super(ItemType.TELEPORT, controller);
        this.animationPos = new AnimationPos();
        this.exit = new Coords();
        this.teleportCollisionPolygons = new ArrayList<>(0);
    }

    public Teleport(Teleport prototype) {
        super(prototype);
        this.animationPos = new AnimationPos();
        this.teleportableDelagate = new TeleportableDelagate();
    }

    @Override
    public void addItemToList(ItemsList list) {
        list.getTeleports().add(this);
    }

    @Override
    public void removeItemFromList(ItemsList list) {
        list.getTeleports().remove(this);
    }

    @Override
    protected String getAssetDirName() {
        return Paths.TELEPORTS;
    }

    public void enter(Creature cr) {
        teleportableDelagate.teleport(cr, getExit(), getController());
    }

    @Override
    public void restoreReferences(Controller controller, ItemsList assets, World world) {
        super.restoreReferences(controller, assets, world);
        Coords exit = getExit();
        controller.restoreLocationOfCoords(exit);
    }

    @Override
    protected List<Teleport> getSpecificItemsList(ItemsList itemsList) {
        return itemsList.getTeleports();
    }

    @Override
    protected Teleport getNewItemFromPrototype() {
        return new Teleport(this);
    }

    public Coords getIndividualExit() {
        return exit;
    }

    public Coords getExit() {
        if (exit == null) {
            if (isThisPrototype()) {
                return new Coords(0, 0, 0, null);
            }
            return prototype.exit;
        } else {
            return exit;
        }
    }

    public void setExit(Coords exit) {
        this.exit = exit;
    }

    public List<List<Coords>> getIndividualTeleportCollisionPolygons() {
        return teleportCollisionPolygons;
    }

    public List<List<Coords>> getTeleportCollisionPolygons() {
        if (teleportCollisionPolygons == null) {
            if (isThisPrototype()) {
                return new ArrayList<>(0);
            }
            return prototype.teleportCollisionPolygons;
        } else {
            return teleportCollisionPolygons;
        }
    }

    public void setTeleportCollisionPolygons(List<List<Coords>> teleportCollisionPolygons) {
        this.teleportCollisionPolygons = teleportCollisionPolygons;
    }

    @Override
    protected Animation<AnimationPos, Teleport> getConcreteAnimation() {
        if (animation == null) {
            return new Animation<>(getDir(), IDLE);
        } else {
            return animation;
        }
    }

    @Override
    public AnimationPos getAnimationPos() {
        return animationPos;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(teleportableDelagate);

        out.writeObject(animationPos);

        out.writeObject(exit);

        out.writeObject(teleportCollisionPolygons);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        teleportableDelagate = (TeleportableDelagate) in.readObject();

        animationPos = (AnimationPos) in.readObject();

        if (isThisPrototype()) {
            animation = new Animation<>(getDir(), IDLE);
        }

        exit = (Coords) in.readObject();

        teleportCollisionPolygons = (List<List<Coords>>) in.readObject();
    }
}
