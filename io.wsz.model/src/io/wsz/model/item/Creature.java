package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.animation.creature.CreatureAnimation;
import io.wsz.model.animation.creature.CreatureAnimationPos;
import io.wsz.model.animation.creature.CreatureBaseAnimationPos;
import io.wsz.model.animation.creature.PortraitAnimation;
import io.wsz.model.location.FogStatusWithImage;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Paths;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import io.wsz.model.stage.ResolutionImage;
import io.wsz.model.textures.CreatureBase;
import io.wsz.model.textures.Fog;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static io.wsz.model.location.FogStatus.CLEAR;
import static io.wsz.model.sizes.Paths.IDLE;
import static io.wsz.model.sizes.Paths.PORTRAIT;

public class Creature extends PosItem<Creature, CreatureAnimationPos> implements Containable {
    private static final long serialVersionUID = 1L;

    private static final Coords TEMP_NEXT_FOG_PIECE_CENTER_POS = new Coords();

    private final Coords tempCenterBottom = new Coords();
    private final Coords tempReversedCenterBottom = new Coords();

    private CreatureAnimation animation;
    private PortraitAnimation portraitAnimation;

    private final Task task = new Task();
    private CreatureAnimationPos animationPos;
    private AnimationPos portraitAnimationPos;
    private CreatureBaseAnimationPos baseAnimationPos;
    private Coords middlePoint;
    private Inventory inventory;
    private CreatureSize size;
    private CreatureControl control;
    private Double speed;
    private Double visionRange;
    private Double range;
    private Integer strength;
    private ResolutionImage portrait;
    private ResolutionImage base;
    private OnTeleportAction onChangeLocationAction;

    public Creature() {}

    public Creature(Controller controller) {
        super(ItemType.CREATURE, controller);
        this.animationPos = new CreatureAnimationPos();
        this.portraitAnimationPos = new AnimationPos();
        this.baseAnimationPos = new CreatureBaseAnimationPos();
        this.inventory = new Inventory(this);
    }

    public Creature(Creature prototype, Boolean visible) {
        super(prototype, visible);
        this.animationPos = new CreatureAnimationPos();
        this.portraitAnimationPos = new AnimationPos();
        this.baseAnimationPos = new CreatureBaseAnimationPos();
    }

    @Override
    protected String getAssetDirName() {
        return Paths.CREATURES;
    }

    @Override
    public double getCollisionLeft(List<List<Coords>> cp) {
        return getCollisionLeft(cp, getCenter());
    }

    @Override
    public double getCollisionLeft(List<List<Coords>> cp, Coords nextPos) {
        double halfWidth = getSize().getWidth() / 2;
        return nextPos.x - halfWidth;
    }

    @Override
    public double getCollisionRight(List<List<Coords>> cp) {
        return getCollisionRight(cp, getCenter());
    }

    @Override
    public double getCollisionRight(List<List<Coords>> cp, Coords nextPos) {
        double halfWidth = getSize().getWidth() / 2;
        return nextPos.x + halfWidth;
    }

    @Override
    public double getCollisionTop(List<List<Coords>> cp) {
        return getCollisionTop(cp, getCenter());
    }

    @Override
    public double getCollisionTop(List<List<Coords>> cp, Coords nextPos) {
        double halfHeight = getSize().getHeight() / 2;
        return nextPos.y - halfHeight;
    }

    @Override
    public double getCollisionBottom(List<List<Coords>> cp) {
        return getCollisionBottom(cp, getCenter());
    }

    @Override
    public double getCollisionBottom(List<List<Coords>> cp, Coords nextPos) {
        double halfHeight = getSize().getHeight() / 2;
        return nextPos.y + halfHeight;
    }

    private void checkSurrounding() {
        Teleport t = getController().getBoard().getTeleport(getCenter(), this, this.pos.getLocation());
        if (t != null) t.enter(this);
    }

    @Override
    public Coords getCenter() {
        Coords middlePoint = getTranslatedMiddlePos(pos);
        if (middlePoint != null) {
            return middlePoint;
        } else {
            return getCenterFrom(pos);
        }
    }

    private Coords getTranslatedMiddlePos(Coords pos) {
        Coords middlePoint = getMiddlePoint();
        if (middlePoint == null) return null;
        tempCenterBottom.level = pos.level;
        tempCenterBottom.setLocation(pos.getLocation());
        tempCenterBottom.x = 0;
        tempCenterBottom.y = 0;
        tempCenterBottom.add(pos);
        tempCenterBottom.add(middlePoint);
        return tempCenterBottom;
    }

    @Override
    protected Coords getCoordsForCollisionCheck() {
        return getCenter();
    }

    public Coords getCenterFrom(Coords pos) {
        Coords middlePoint = getMiddlePoint();
        if (middlePoint != null) {
             return getTranslatedMiddlePos(pos);
        } else {
            return getImageBasedCenter(pos);
        }
    }

    private Coords getImageBasedCenter(Coords pos) {
        ResolutionImage image = getImage();
        if (image == null) return this.pos;
        double width = image.getWidth() / Sizes.getMeter();
        double height = image.getHeight() / Sizes.getMeter();
        tempCenterBottom.x = pos.x + width/2;
        tempCenterBottom.y = pos.y + height;
        tempCenterBottom.level = pos.level;
        return tempCenterBottom;
    }

    public Coords getReversedCenter(double x, double y, int level, Location location) {
        Coords middlePointBasedReversedCenterPos = getMiddlePointBasedReversedCenterPos(x, y, level, location);
        if (middlePointBasedReversedCenterPos != null) {
            return middlePointBasedReversedCenterPos;
        } else {
            return getImageBasedReversedCenterPos(x, y, level, location);
        }

    }

    private Coords getMiddlePointBasedReversedCenterPos(double x, double y, int level, Location location) {
        Coords middlePoint = getMiddlePoint();
        if (middlePoint == null) return null;
        tempReversedCenterBottom.x = x;
        tempReversedCenterBottom.y = y;
        tempReversedCenterBottom.subtract(middlePoint);
        tempReversedCenterBottom.level = level;
        tempReversedCenterBottom.setLocation(location);
        return tempReversedCenterBottom;
    }

    private Coords getImageBasedReversedCenterPos(double x, double y, int level, Location location) {
        double width = getImage().getWidth() / Sizes.getMeter();
        double height = getImage().getHeight() / Sizes.getMeter();
        tempReversedCenterBottom.x = x - width/2;
        tempReversedCenterBottom.y = y - height;
        tempReversedCenterBottom.level = level;
        tempReversedCenterBottom.setLocation(location);
        return tempReversedCenterBottom;
    }

    public double reverseCenterBottomPosX(double x) {
        double width = getImage().getWidth() / Sizes.getMeter();
        x -= width/2;
        return x;
    }

    public double reverseCenterBottomPosY(double y) {
        double height = getImage().getHeight() / Sizes.getMeter();
        y -= height;
        return y;
    }

    public Coords getReversedCenter(Coords difPos) {
        return getReversedCenter(difPos.x, difPos.y, difPos.level, difPos.getLocation());
    }

    public void onFirstAction(PosItem pi) {
        if (pi == null) {
            return;
        }
        setItemTask(pi);
    }

    public void goTo(double x, double y) {
        this.task.setFinished(false);
        this.task.setItem(this, null);
        this.task.setDestX(reverseCenterBottomPosX(x));
        this.task.setDestY(reverseCenterBottomPosY(y));
    }

    private void setItemTask(PosItem e) {
        this.task.setFinished(false);
        this.task.setItem(this, e);
    }

    @Override
    public boolean withinRange(Coords pos, double range, double sizeWidth, double sizeHeight) {
        Coords thisCenter = getCenter();

        CreatureSize thisSize = getSize();
        double width = sizeWidth + 2*range;
        return Geometry.ovalsIntersect(thisCenter, thisSize, pos, width);
    }

    public List<Equipment> getEquipmentWithinRange(Controller controller) {
        return getController().getBoard().getEquipmentWithinRange(this);
    }

    private void checkTask() {
        task.doTask(this);
    }

    @Override
    public double getLeft() {
        double width = getSize().getWidth();
        double imgWidth = getImageWidth();
        double imgLeft = pos.x;
        if (imgWidth < width) {
            double halfDif = (width - imgWidth) / 2;
            imgLeft -= halfDif;
        }
        return imgLeft;
    }

    @Override
    public double getRight() {
        double width = getSize().getWidth();
        double imgWidth = getImageWidth();
        double imgRight = pos.x + imgWidth;
        if (imgWidth < width) {
            double halfDif = (width - imgWidth) / 2;
            imgRight += halfDif;
        }
        return imgRight;
    }

    @Override
    public double getTop() {
        double halfHeight = getCreatureHalfHeight();
        double imgHeight = getImageHeight();
        double imgBottom = pos.y + imgHeight;
        double imgTop = pos.y;
        if (imgHeight < halfHeight) {
            imgTop = imgBottom - halfHeight;
        }
        return imgTop;
    }

    @Override
    public double getBottom() {
        double imgHeight = getImageHeight();
        double imgBottom = pos.y + imgHeight;
        return imgBottom;
    }

    private double getCreatureHalfHeight() {
        double height = getSize().getHeight();
        return height / 2;
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        CreatureSize size = cr.getSize();
        boolean outOfRange = !withinRange(cr.getCenter(), cr.getRange(), size.getWidth(), size.getHeight());
        if (outOfRange) return false;
        if (getControl() == CreatureControl.ENEMY) return false;
        if (getObstacleOnWay(cr) != null) return false;
        if (cr == this) return false;
        Controller controller = getController();
        controller.setDialogNpc(cr);
        controller.setAnswering(this);
        controller.setPosToCenter(getCenter());
        return true;
    }

    @Override
    public void changePosition(Location from, Coords exit) {
        changeLocation(from, exit);
        Coords reversed = getReversedCenter(exit);
        this.pos.x = reversed.x;
        this.pos.y = reversed.y;
        this.pos.level = reversed.level;
        task.clear();
    }

    @Override
    public void onChangeLocationAction(Location location) {
        super.onChangeLocationAction(location);
        if (onChangeLocationAction != null) {
            onChangeLocationAction.act();
        }
    }

    @Override
    public void update() {
        super.update();
        playBaseAnimation();
        updateFogVisibility();
        checkSurrounding();
        checkTask();
    }

    private void playBaseAnimation() {
        CreatureBase base = CreatureBase.getCreatureBase(getSize(), getControl());
        if (base == null) return;
        base.getAnimation().play(this);
    }

    private void updateFogVisibility() {
        CreatureControl control = getControl();
        if (control != CreatureControl.CONTROL && control != CreatureControl.CONTROLLABLE) {
            return;
        }
        Location creatureLocation = this.pos.getLocation();
        List<List<FogStatusWithImage>> discoveredFog = creatureLocation.getDiscoveredFog();
        if (discoveredFog == null) return;
        double heightPieces = discoveredFog.size();
        double widthPieces = discoveredFog.get(0).size();
        double horizontalVisionRangeFactor = Sizes.HORIZONTAL_VISION_RANGE_FACTOR;
        double verticalVisionRangeFactor = Sizes.VERTICAL_VISION_RANGE_FACTOR;
        Coords nextPieceCenterPos = TEMP_NEXT_FOG_PIECE_CENTER_POS;
        Controller controller = getController();
        if (controller == null) return;
        Fog fog = controller.getFog();
        double fogSize = fog.getFogSize();
        double half = fog.getHalfFogSize();
        double y = -fogSize;
        for (int i = 0; i < heightPieces; i++) {
            if (i != 0) {
                y += half;
            }
            double x = -fogSize;
            List<FogStatusWithImage> horStatuses = discoveredFog.get(i);
            for (int j = 0; j < widthPieces; j++) {
                if (j != 0) {
                    x += half;
                }
                nextPieceCenterPos.x = x + half;
                nextPieceCenterPos.y = y + half;

                double visionRange = getVisionRange();
                double visWidth = visionRange * horizontalVisionRangeFactor;
                double visHeight = visionRange * verticalVisionRangeFactor;
                boolean isPieceWithinHeroView = Geometry.isPointWithinOval(nextPieceCenterPos, getCenter(), visWidth, visHeight);

                FogStatusWithImage statusWithImage = horStatuses.get(j);
                if (isPieceWithinHeroView) {
                    statusWithImage.setStatus(CLEAR);
                }
            }
        }
    }

    @Override
    public List<Equipment> getItems() {
        return inventory.getItems();
    }

    public Task getTask() {
        return task;
    }

    public Inventory getIndividualInventory() {
        return inventory;
    }

    public Inventory getInventory() {
        if (inventory == null) {
            if (isThisPrototype()) {
                return null;
            } else {
                return prototype.getInventory();
            }
        } else {
            return inventory;
        }
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Coords getIndividualMiddlePoint() {
        return middlePoint;
    }

    public Coords getMiddlePoint() {
        if (middlePoint == null) {
            if (isThisPrototype()) {
                return middlePoint;
            } else if (prototype.getMiddlePoint() == null) {
                return getCenter();
            } else {
                return prototype.getMiddlePoint();
            }
        } else {
            return middlePoint;
        }
    }

    public void setMiddlePoint(Coords middlePoint) {
        this.middlePoint = middlePoint;
    }

    public Map<InventoryPlaceType, List<Coords>> getInventoryPlaces() {
        if (inventory == null) {
            if (isThisPrototype()) {
                return null;
            } else {
                return prototype.getInventoryPlaces();
            }
        } else {
            Map<InventoryPlaceType, List<Coords>> inventoryPlaces = inventory.getInventoryPlaces();
            if (inventoryPlaces == null) {
                return prototype.getInventoryPlaces();
            } else {
                return inventoryPlaces;
            }
        }
    }

    public Double getIndividualSpeed() {
        return speed;
    }

    public Double getSpeed() {
        if (speed == null) {
            if (isThisPrototype()) {
                return 0.0;
            }
            return prototype.speed;
        } else {
            return speed;
        }
    }

    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    public Double getIndividualRange() {
        return range;
    }

    public Double getRange() {
        if (range == null) {
            if (isThisPrototype()) {
                return 0.0;
            }
            return prototype.range;
        } else {
            return range;
        }
    }

    public void setRange(Double range) {
        this.range = range;
    }

    public Double getIndividualVisionRange() {
        return visionRange;
    }

    public Double getVisionRange() {
        if (visionRange == null) {
            if (isThisPrototype()) {
                return 1.0;
            }
            return prototype.visionRange;
        } else {
            return visionRange;
        }
    }

    public void setVisionRange(Double visionRange) {
        this.visionRange = visionRange;
    }

    public CreatureSize getIndividualSize() {
        return size;
    }

    public CreatureSize getSize() {
        if (size == null) {
            if (isThisPrototype()) {
                return CreatureSize.getDefault();
            }
            return prototype.size;
        } else {
            return size;
        }
    }

    public void setSize(CreatureSize size) {
        this.size = size;
    }

    public CreatureControl getIndividualControl() {
        return control;
    }

    public CreatureControl getControl() {
        if (control == null) {
            if (isThisPrototype()) {
                return CreatureControl.getDefault();
            }
            return prototype.getControl();
        } else {
            return control;
        }
    }

    public void setControl(CreatureControl control) {
        this.control = control;
    }

    public Integer getIndividualStrength() {
        return strength;
    }

    public Integer getStrength() {
        if (strength == null) {
            if (isThisPrototype()) {
                return 0;
            }
            return prototype.strength;
        } else {
            return strength;
        }
    }

    public void setStrength(Integer strength) {
        this.strength = strength;
    }

    public void onSecondAction(PosItem pi) {
        pi.creatureSecondaryInteract(this);
    }

    public OnTeleportAction getOnChangeLocationAction() {
        return onChangeLocationAction;
    }

    public void setOnChangeLocationAction(OnTeleportAction onChangeLocationAction) {
        this.onChangeLocationAction = onChangeLocationAction;
    }

    @Override
    protected CreatureAnimation getConcreteAnimation() {
        if (animation == null) {
            return new CreatureAnimation(getDir(), IDLE);
        } else {
            return animation;
        }
    }

    @Override
    public CreatureAnimationPos getAnimationPos() {
        return animationPos;
    }

    public PortraitAnimation getPortraitAnimation() {
        if (isThisPrototype()) {
            return portraitAnimation;
        } else {
            return prototype.getPortraitAnimation();
        }
    }

    public AnimationPos getPortraitAnimationPos() {
        return portraitAnimationPos;
    }

    public ResolutionImage getPortrait() {
        return portrait;
    }

    public void setPortrait(ResolutionImage portrait) {
        this.portrait = portrait;
    }

    public CreatureBaseAnimationPos getBaseAnimationPos() {
        return baseAnimationPos;
    }

    public ResolutionImage getBase() {
        return base;
    }

    public void setBase(ResolutionImage base) {
        this.base = base;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Creature)) return false;
        if (!super.equals(o)) return false;
        Creature creature = (Creature) o;
        return Objects.equals(getAnimation(), creature.getAnimation()) &&
                Objects.equals(getTask(), creature.getTask()) &&
                Objects.equals(getAnimationPos(), creature.getAnimationPos()) &&
                Objects.equals(getInventory(), creature.getInventory()) &&
                getSize() == creature.getSize() &&
                getControl() == creature.getControl() &&
                Objects.equals(getSpeed(), creature.getSpeed()) &&
                Objects.equals(getVisionRange(), creature.getVisionRange()) &&
                Objects.equals(getRange(), creature.getRange()) &&
                Objects.equals(getStrength(), creature.getStrength());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getAnimation(), getTask(), getAnimationPos(), getInventory(), getSize(), getControl(), getSpeed(), getVisionRange(), getRange(), getStrength());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(animationPos);

        out.writeObject(portraitAnimationPos);

        out.writeObject(baseAnimationPos);

        out.writeObject(task);

        out.writeObject(middlePoint);

        out.writeObject(inventory);

        out.writeObject(size);

        out.writeObject(control);

        out.writeObject(speed);

        out.writeObject(visionRange);

        out.writeObject(range);

        out.writeObject(strength);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        animationPos = (CreatureAnimationPos) in.readObject();

        portraitAnimationPos = (AnimationPos) in.readObject();

        baseAnimationPos = (CreatureBaseAnimationPos) in.readObject();

        if (isThisPrototype()) {
            animation = new CreatureAnimation(getDir(), IDLE);
            portraitAnimation = new PortraitAnimation(getDir(), PORTRAIT);
        }

        Task serTask = (Task) in.readObject();
        task.setDest(serTask.getDest());
        task.setItem(this, serTask.getItem());
        task.setFinished(serTask.isFinished());

        middlePoint = (Coords) in.readObject();

        inventory = (Inventory) in.readObject();

        size = (CreatureSize) in.readObject();

        control = (CreatureControl) in.readObject();

        speed = (Double) in.readObject();

        visionRange = (Double) in.readObject();

        range = (Double) in.readObject();

        strength = (Integer) in.readObject();
    }
}
