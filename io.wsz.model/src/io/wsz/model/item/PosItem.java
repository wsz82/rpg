package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.animation.cursor.CursorType;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.item.draw.ItemsDrawer;
import io.wsz.model.item.list.AbstractItemsList;
import io.wsz.model.item.list.ItemsList;
import io.wsz.model.location.Location;
import io.wsz.model.script.Script;
import io.wsz.model.script.command.ItemMover;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import io.wsz.model.stage.PolygonsGetter;
import io.wsz.model.stage.ResolutionImage;
import io.wsz.model.world.World;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.*;

public abstract class PosItem<I extends PosItem<I, A>, A extends AnimationPos> extends Asset<I>
        implements Updatable, Interactable, Animable<A, I> {
    private static final long serialVersionUID = 1L;

    protected final Coords center = new Coords();

    protected Controller controller;

    protected String itemId;
    protected boolean isVisible;
    protected boolean isBlocked;
    protected Coords pos;
    protected I prototype;
    protected List<Coords> coverLine;
    protected List<List<Coords>> collisionPolygons;
    protected Dialog dialog;
    protected Coords interactionPoint;
    protected Double animationSpeed;
    protected ResolutionImage image;
    protected Script script;

    public PosItem() {
        this.pos = new Coords();
    }

    public PosItem(ItemType type, Controller controller) {
        super(type);
        this.isVisible = true;
        this.isBlocked = false;
        this.pos = new Coords();
        this.controller = controller;
        this.coverLine = new ArrayList<>(0);
        this.collisionPolygons = new ArrayList<>(0);
    }

    public PosItem(I prototype) {
        this.prototype = prototype;
        this.isVisible = true;
        this.isBlocked = false;
        this.pos = new Coords();
    }

    public PosItem(I other, boolean keepId) {
        super(other);
        this.prototype = other.prototype;
        if (keepId) {
            this.itemId = other.itemId;
        } else {
            this.itemId = getUniqueId(other.itemId);
        }
        this.isVisible = other.isVisible;
        this.isBlocked = other.isBlocked;
        this.pos = other.pos.clonePos();
        this.coverLine = Geometry.cloneCoordsList(other.coverLine);
        this.collisionPolygons = Geometry.cloneCoordsPolygons(other.collisionPolygons);
        this.dialog = other.dialog;
        Coords interactionCoords = other.interactionPoint;
        if (interactionCoords == null) {
            this.interactionPoint = null;
        } else {
            this.interactionPoint = interactionCoords.clonePos();
        }
        this.image = other.image;
    }

    private String getUniqueId(String itemId) {
        if (doesItemIdAlreadyExist(itemId)) {
            itemId += 1;
            return getUniqueId(itemId);
        } else {
            return itemId;
        }
    }

    private boolean doesItemIdAlreadyExist(String itemId) {
        return getController().getLocations().stream()
                .anyMatch(l -> l.getItemsList().getMergedList().stream().anyMatch(i -> {
                    String id = i.getItemId();
                    if (id == null) {
                        return false;
                    } else {
                        return id.equals(itemId);
                    }
                }));
    }

    public Coords getImageCenter() {
        center.x = pos.x + getImageWidth()/2;
        center.y = pos.y + getImageHeight()/2;
        center.level = pos.level;
        return center;
    }

    public Coords getCenter() {
        center.x = pos.x + getImageWidth()/2;
        center.y = pos.y + getImageHeight()/2;
        center.level = pos.level;
        return center;
    }

    public double getLeft() {
        return pos.x;
    }

    public double getTop() {
        return pos.y;
    }

    public double getRight() {
        return pos.x + getImageWidth();
    }

    public double getBottom() {
        return pos.y + getImageHeight();
    }

    public void changePosition(Location from, Coords exit) {
        changeLocation(from, exit);
        pos.x = exit.x;
        pos.y = exit.y;
        pos.level = exit.level;
    }

    protected void changeLocation(Location from, Coords exit) {
        Location target = exit.getLocation();
        if (!from.equals(target)) {
            AbstractItemsList itemsToRemove = from.getItemsToRemove();
            addItemToList(itemsToRemove);
            AbstractItemsList itemsToAdd = target.getItemsToAdd();
            addItemToList(itemsToAdd);
        }
    }

    public abstract void addItemToList(AbstractItemsList list);

    public abstract void removeItemFromList(AbstractItemsList list);

    public boolean withinRange(Coords pos, double range, double sizeWidth, double sizeHeight) {
        return Geometry.isPointWithinOval(getInteractionPoint(), pos, sizeWidth + 2*range, sizeHeight + 2*range);
    }

    public PosItem<?,?> getCollision() {
        return getCollision(getCoordsForCollisionCheck());
    }

    protected Coords getCoordsForCollisionCheck() {
        return pos;
    }

    public PosItem<?,?> getCollision(Coords nextPos) {
        return getCollision(nextPos, pos.getLocation());
    }

    public PosItem<?,?> getCollision(Location location) {
        return getCollision(getCoordsForCollisionCheck(), location);
    }

    public PosItem<?,?> getCollision(Coords nextPos, Location nextLocation) {
        if (nextLocation == null) return null;
        List<PosItem<?,?>> items = nextLocation.getItemsList().getMergedList();
        Controller controller = getController();
        return controller.getBoard().getObstacle(nextPos, this, items);
    }

    public double getCollisionLeft(List<List<Coords>> cp) {
        return getCollisionLeft(cp, pos);
    }

    public double getCollisionLeft(List<List<Coords>> cp, Coords nextPos) {
        if (cp.isEmpty()) return nextPos.x;
        double left =  cp.stream()
                .mapToDouble(l -> l.stream()
                        .mapToDouble(p -> p.x)
                        .min()
                        .getAsDouble())
                .min()
                .getAsDouble();
        return nextPos.x + left;
    }

    public double getCollisionRight(List<List<Coords>> cp) {
        return getCollisionRight(cp, pos);
    }

    public double getCollisionRight(List<List<Coords>> cp, Coords nextPos) {
        if (cp.isEmpty()) return nextPos.x;
        double right =  cp.stream()
                .mapToDouble(l -> l.stream()
                        .mapToDouble(p -> p.x)
                        .max()
                        .getAsDouble())
                .max()
                .getAsDouble();
        return nextPos.x + right;
    }

    public double getCollisionTop(List<List<Coords>> cp) {
        return getCollisionTop(cp, pos);
    }

    public double getCollisionTop(List<List<Coords>> cp, Coords nextPos) {
        if (cp.isEmpty()) return nextPos.y;
        double top =  cp.stream()
                .mapToDouble(l -> l.stream()
                        .mapToDouble(p -> p.y)
                        .min()
                        .getAsDouble())
                .min()
                .getAsDouble();
        return nextPos.y + top;
    }

    public double getCollisionBottom(List<List<Coords>> cp) {
        return getCollisionBottom(cp, pos);
    }

    public double getCollisionBottom(List<List<Coords>> cp, Coords nextPos) {
        if (cp.isEmpty()) return nextPos.y;
        double bottom = cp.stream()
                .mapToDouble(l -> l.stream()
                        .mapToDouble(p -> p.y)
                        .max()
                        .getAsDouble())
                .max()
                .getAsDouble();
        return nextPos.y + bottom;
    }

    public void onChangeLocationAction(Location location) {
        pos.setLocation(location);
    }

    public void setCursor(CursorSetter cursorSetter) {
        cursorSetter.set(CursorType.MAIN);
    }

    protected boolean isThisPrototype() {
        return prototype == null;
    }

    @Override
    public boolean creaturePrimaryInteract(Creature cr) {
        return getObstacleOnWay(cr) == null;
    }

    @Override
    public boolean creatureSecondaryInteract(Creature cr) {
        return getObstacleOnWay(cr) == null;
    }

    protected PosItem getObstacleOnWay(Creature cr) {
        Coords crCenter = cr.getCenter();
        double xFrom = crCenter.x;
        double yFrom = crCenter.y;
        Coords toCoords = getInteractionPoint();
        double xTo = toCoords.x;
        double yTo = toCoords.y;
        return getController().getBoard().getObstacleOnWay(
                pos.getLocation(), pos.level, xFrom, yFrom, this, xTo, yTo);
    }

    @Override
    public void update() {
        playAnimation();
        runScript();
    }

    private void runScript() {
        if (script != null) {
            script.execute(getController(), null, null);
        }
    }

    private void playAnimation() {
        Animation animation = getAnimation();
        if (animation == null) return;
        animation.play(this);
    }

    @Override
    public <M extends Animation<A, I>> M getAnimation() {
        M animation;
        if (isThisPrototype()) {
            animation = getConcreteAnimation();
        } else {
            animation = prototype.getAnimation();
        }
        if (animation == null) {
            if (path == null) return null;
            animation = getConcreteAnimation();
        }
        return animation;
    }

    protected abstract <M extends Animation<A, I>> M getConcreteAnimation();

    public abstract A getAnimationPos();

    @Override
    public void restoreReferences(Controller controller, ItemsList assets, World world) {
        restorePrototype(assets);
        controller.restoreLocationOfCoords(pos);
        restoreDialog(world.getDialogs());
    }

    private void restorePrototype(ItemsList assets) {
        I serPrototype = getPrototype();
        if (serPrototype == null) return;
        String prototypeName = serPrototype.getAssetId();
        getSpecificItemsList(assets).stream()
                .filter(i -> i.getAssetId().equals(prototypeName))
                .findFirst().ifPresentOrElse(this::setPrototype, () -> throwNoPrototypeFoundException(prototypeName));
    }

    private void throwNoPrototypeFoundException(String prototypeName) {
        throw new NullPointerException(prototypeName + " reference is not found");
    }

    protected abstract List<I> getSpecificItemsList(ItemsList itemsList);

    private void restoreDialog(List<Dialog> dialogs) {
        Dialog serDialog = dialog;
        if (serDialog == null) return;
        String serID = serDialog.getID();
        Optional<Dialog> optDialog = dialogs.stream()
                .filter(d -> d.getID().equals(serID))
                .findFirst();
        Dialog dialog = optDialog.orElse(null);
        if (dialog == null) {
            throw new NullPointerException(getAssetId() + " dialog \"" + serDialog.getID() + "\" should be in list of dialogs");
        }
        setDialog(dialog);
    }

    public boolean tryAddToLocation(Location location, ItemsList locationItems) {
        boolean itemWillCollide = getCollision(location) != null;
        if (itemWillCollide) return false;
        addToLocation(location, locationItems);
        return true;
    }

    @Override
    public final void addNewItemToLocation(Location toLocation, int toLevel, double toX, double toY, String newItemId) {
        I item = getNewItemFromPrototype();
        item.setItemId(newItemId);
        item.addToLocation(toLocation, toLevel, toX, toY);
    }

    @Override
    public final int getAmountById(String checkedId) {
        if (itemId != null && itemId.equals(checkedId) || getAssetId().equals(checkedId)) {
            return getAmount();
        } else {
            return getInnerAmountById(checkedId);
        }
    }

    public int getAmount() {
        return 1;
    }

    protected int getInnerAmountById(String checkedId) {
        return 0;
    }

    protected abstract I getNewItemFromPrototype();

    protected void addToLocation(Location toLocation, int toLevel, double toX, double toY) {
        setPos(toX, toY, toLevel, toLocation);
        addItemToList(toLocation.getItemsToAdd());
    }

    public void addToLocation(Location location, ItemsList locationItems) {
        addItemToList(locationItems);
        onChangeLocationAction(location);
    }

    public PosItem<?,?> getItemByAssetId(String lookedId) {
        String assetId = this.getAssetId();
        if (assetId != null && assetId.equals(lookedId)) {
            return this;
        } else {
            return null;
        }
    }

    public PosItem<?,?> getItemByItemId(String lookedId) {
        if (this.itemId != null && this.itemId.equals(lookedId)) {
            return this;
        } else {
            return null;
        }
    }

    public void addItemToListByAssetId(List<PosItem<?,?>> items, String assetId) {
        PosItem<?, ?> item = getItemByAssetId(assetId);
        if (item == null) return;
        items.add(item);
    }

    public void initAnimations(File programDir) {
        getAnimation().initAllAnimations(programDir);
    }

    public <P extends PosItem<?, ?>> void moveItemTo(P receiving, ItemMover itemMover) {
        getController().getLogger().logCannotGive(getName());
    }

    protected void receiveItemFrom(ItemMover itemMover, Containable giving) {
        getController().getLogger().logCannotReceive(getName());
    }

    public Equipment<?,?> pickEquipment() {
        return null;
    }

    @Override
    public String getAssetId() {
        if (prototype != null) {
            return prototype.getAssetId();
        } else {
            return super.getAssetId();
        }
    }

    @Override
    public String getName() {
        if (name == null) {
            if (prototype == null) {
                return super.getName();
            } else {
                return prototype.getName();
            }
        } else {
            return name;
        }
    }

    @Override
    public ItemType getType() {
        if (prototype != null) {
            return prototype.getType();
        } else {
            return super.getType();
        }
    }

    public void addPrototypeToSet(Set<PosItem<?,?>> prototypes) {
        prototypes.add(getPrototype());
    }

    @Override
    public String getPath() {
        if (prototype != null) {
            return prototype.getPath();
        }
        return super.getPath();
    }

    public boolean checkIfCanCollide() {
        List<List<Coords>> actualCollisionPolygons = getActualCollisionPolygons();
        return actualCollisionPolygons != null && !actualCollisionPolygons.isEmpty();
    }

    public <P extends PosItem<?, ?>> P findCollision(Coords nextPos, List<P> items, PolygonsGetter<P> obstaclePolygonsGetter) {
        List<List<Coords>> collisionPolygons = getActualCollisionPolygons();
        if (!collisionPolygons.isEmpty()) {
            double left = getCollisionLeft(collisionPolygons);
            double right = getCollisionRight(collisionPolygons);
            double top = getCollisionTop(collisionPolygons);
            double bottom = getCollisionBottom(collisionPolygons);

            return findCollisionWithItems(collisionPolygons, nextPos, items, left, right, top, bottom, obstaclePolygonsGetter);
        }
        return null;
    }

    protected final <P extends PosItem<?, ?>> P findCollisionWithItems(List<List<Coords>> collisionPolygons, Coords nextPos,
                                                                       List<P> items,
                                                                       double left, double right, double top, double bottom,
                                                                       PolygonsGetter<P> obstaclePolygonsGetter) {
        for (P obstacle : items) {
            if (obstacle == this) continue;
            P collision = null;
            boolean itemsTypesCanCollide = !ifItemTypeCannotCollide(obstacle);
            if (itemsTypesCanCollide) {
                collision = findCollisionWith(nextPos, collisionPolygons, left, right, top, bottom,
                        obstacle, obstaclePolygonsGetter);
            }

            if (collision != null) return collision;
        }
        return null;
    }

    protected  <P extends PosItem<?, ?>> boolean ifItemTypeCannotCollide(P obstacle) {
        return false;
    }

    protected boolean ifItemTypeCannotCollideWithDoor() {
        return false;
    }

    protected boolean ifItemTypeCannotCollideWithLandscape() {
        return false;
    }

    protected boolean ifItemTypeCannotCollideWithCover() {
        return false;
    }

    private <P extends PosItem<?, ?>> P findCollisionWith(Coords nextPos, List<List<Coords>> collisionPolygons,
                                                          double left, double right, double top, double bottom,
                                                          P obstacle, PolygonsGetter<P> obstaclePolygonsGetter) {
        List<List<Coords>> obstaclePolygons = obstaclePolygonsGetter.get(obstacle);
        if (obstacle.ifCollisionsDoNotOverlap(left, right, top, bottom, obstaclePolygons)) return null;

        if (calculateIfCollides(nextPos, collisionPolygons, obstacle, obstaclePolygons)) {
            getController().getLogger().logItemCollides(getName(), obstacle.getName());
            return obstacle;
        } else {
            return null;
        }
    }

    protected boolean ifCollisionsDoNotOverlap(double left, double right, double top, double bottom,
                                               List<List<Coords>> obstaclePolygons) {
        if (obstaclePolygons.isEmpty()) return true;
        double oLeft = getCollisionLeft(obstaclePolygons);
        double oRight = getCollisionRight(obstaclePolygons);
        if (right < oLeft || left > oRight) return true;

        double oTop = getCollisionTop(obstaclePolygons);
        double oBottom = getCollisionBottom(obstaclePolygons);
        return bottom < oTop || top > oBottom;
    }

    protected boolean calculateIfCollides(Coords nextPos, List<List<Coords>> collisionPolygons,
                                          PosItem<?,?> obstacle, List<List<Coords>> obstaclePolygons) {
        return obstacle.calculateIfCollidesWithPosItem(nextPos, collisionPolygons, obstaclePolygons);
    }

    protected boolean calculateIfCollidesWithPosItem(Coords nextPos, List<List<Coords>> collisionPolygons,
                                                     List<List<Coords>> obstaclePolygons) {
        return Geometry.polygonsIntersect(nextPos.x, nextPos.y, collisionPolygons, getPos(), obstaclePolygons);
    }

    protected boolean calculateIfCollidesWithCreature(Coords nextPos, List<List<Coords>> collisionPolygons,
                                                      Creature creature, List<List<Coords>> obstaclePolygons) {
        for (List<Coords> polygon : obstaclePolygons) {
            List<Coords> lostRef = Geometry.looseCoordsReferences1(polygon);
            Coords oPos = getPos();
            Geometry.translateCoords(lostRef, oPos.x, oPos.y);

            boolean ovalIntersectsPolygon = Geometry.ovalIntersectsPolygon(nextPos, creature.getSize(), lostRef);
            if (ovalIntersectsPolygon) {
                return true;
            }
        }
        return false;
    }

    public void draw(ItemsDrawer drawer) {}

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean isBlocked) {
        this.isBlocked = isBlocked;
    }

    public Coords getPos() {
        return pos;
    }

    public void setPos(Coords pos) {
        this.pos.x = pos.x;
        this.pos.y = pos.y;
        this.pos.level = pos.level;
        this.pos.setLocation(pos.getLocation());
    }

    public void setPos(double x, double y, int level, Location location) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.level = level;
        this.pos.setLocation(location);
    }

    public Coords getIndividualInteractionPoint() {
        return interactionPoint;
    }

    public Coords getInteractionPoint() {
        if (interactionPoint == null) {
            if (prototype != null) {
                Coords prototypePoint = prototype.interactionPoint;
                if (prototypePoint != null) {
                    center.x = prototypePoint.x;
                    center.y = prototypePoint.y;
                } else {
                    return getCenter();
                }
            } else {
                return getCenter();
            }
        } else {
            center.x = interactionPoint.x;
            center.y = interactionPoint.y;
        }
        center.add(pos);
        return center;
    }

    public void setInteractionPoint(Coords interactionPoint) {
        this.interactionPoint = interactionPoint;
    }

    public List<Coords> getCoverLine() {
        if (coverLine == null) {
            if (prototype == null) {
                return new ArrayList<>(0);
            }
            return prototype.coverLine;
        } else {
            return coverLine;
        }
    }

    public List<Coords> getActualCoverLine() {
        return getCoverLine();
    }

    public void setCoverLine(List<Coords> coverLine) {
        this.coverLine = coverLine;
    }

    public List<List<Coords>> getCollisionPolygons() {
        if (collisionPolygons == null) {
            if (prototype == null) {
                return new ArrayList<>(0);
            }
            return prototype.collisionPolygons;
        } else {
            return collisionPolygons;
        }
    }

    public List<List<Coords>> getActualCollisionPolygons() {
        return getCollisionPolygons();
    }

    public void setCollisionPolygons(List<List<Coords>> collisionPolygons) {
        this.collisionPolygons = collisionPolygons;
    }

    public Dialog getIndividualDialog() {
        return dialog;
    }

    public Dialog getDialog() {
        if (dialog == null) {
            if (prototype == null) {
                return null;
            } else {
                return prototype.dialog;
            }
        } else {
            return dialog;
        }
    }

    public void setDialog(Dialog dialog) {
        this.dialog = dialog;
    }

    public I getPrototype() {
        return prototype;
    }

    public void setPrototype(I prototype) {
        this.prototype = prototype;
    }

    protected Controller getController() {
        if (prototype == null) {
            return controller;
        } else {
            return prototype.getController();
        }
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public Double getIndividualAnimationSpeed() {
        return animationSpeed;
    }

    public Double getAnimationSpeed() {
        if (animationSpeed == null) {
            if (prototype == null) {
                return 1.0;
            } else {
                return prototype.getAnimationSpeed();
            }
        } else {
            return animationSpeed;
        }
    }

    public void setAnimationSpeed(Double animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    public final ResolutionImage getInitialImage() {
        if (image == null) {
            File programDir = getController().getProgramDir();
            Animation<A, I> animation = getAnimation();
            if (animation == null) return null;
            image = animation.getBasicMain(programDir);
        }
        return image;
    }

    public ResolutionImage getImage() {
        if (image == null) {
            if (prototype == null) {
                Controller controller = getController();
                if (controller == null) return null;
                File programDir = controller.getProgramDir();
                Animation<A, I> animation = getAnimation();
                if (animation == null) return null;
                image = animation.getBasicMain(programDir);
                return image;
            } else {
                return prototype.getImage();
            }
        } else {
            return image;
        }
    }

    public double getImageHeight() {
        ResolutionImage img = getImage();
        if (img == null) return 0;
        return img.getHeight() / Sizes.getMeter();
    }

    public double getImageWidth() {
        ResolutionImage img = getImage();
        if (img == null) return 0;
        return img.getWidth() / Sizes.getMeter();
    }

    public void setImage(ResolutionImage image) {
        this.image = image;
    }

    public Script getIndividualScript() {
        return script;
    }

    public Script getScript() {
        if (script == null) {
            if (prototype == null) {
                return null;
            } else {
                return prototype.getScript();
            }
        } else {
            return script;
        }
    }

    public void setScript(Script script) {
        this.script = script;
    }

    public boolean isUnitIdentical(Object o) {
        if (this == o) return true;
        if (!(o instanceof PosItem)) return false;
        if (!super.equals(o)) return false;
        PosItem<?, ?> posItem = (PosItem<?, ?>) o;
        return Objects.equals(isVisible(), posItem.isVisible()) &&
                Objects.equals(getPrototype(), posItem.getPrototype()) &&
                Objects.equals(getCoverLine(), posItem.getCoverLine()) &&
                Objects.equals(getCollisionPolygons(), posItem.getCollisionPolygons()) &&
                Objects.equals(getDialog(), posItem.getDialog()) &&
                Objects.equals(getIndividualInteractionPoint(), posItem.getIndividualInteractionPoint()) &&
                Objects.equals(getAnimationSpeed(), posItem.getAnimationSpeed()) &&
                Objects.equals(getScript(), posItem.getScript());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PosItem)) return false;
        if (!super.equals(o)) return false;
        PosItem<?, ?> posItem = (PosItem<?, ?>) o;
        return Objects.equals(isVisible(), posItem.isVisible()) &&
                Objects.equals(getPos(), posItem.getPos()) &&
                Objects.equals(getPrototype(), posItem.getPrototype()) &&
                Objects.equals(getCoverLine(), posItem.getCoverLine()) &&
                Objects.equals(getCollisionPolygons(), posItem.getCollisionPolygons()) &&
                Objects.equals(getDialog(), posItem.getDialog()) &&
                Objects.equals(getIndividualInteractionPoint(), posItem.getIndividualInteractionPoint()) &&
                Objects.equals(getAnimationSpeed(), posItem.getAnimationSpeed()) &&
                Objects.equals(getScript(), posItem.getScript());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), isVisible(), getPos(), getPrototype(), getCoverLine(),
                getCollisionPolygons(), getDialog(), getInteractionPoint(), getAnimationSpeed(), getScript());
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(Sizes.VERSION);

        out.writeObject(prototype);

        out.writeObject(itemId);

        out.writeObject(animationSpeed);

        out.writeBoolean(isVisible);

        out.writeBoolean(isBlocked);

        out.writeObject(pos);

        out.writeObject(coverLine);

        out.writeObject(collisionPolygons);

        String id = "";
        if (dialog != null) {
            id = dialog.getID();
        }
        out.writeUTF(id);

        out.writeObject(interactionPoint);

        out.writeObject(script);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        long ver = in.readLong();

        prototype = (I) in.readObject();

        itemId = (String) in.readObject();

        animationSpeed = (Double) in.readObject();

        isVisible = in.readBoolean();

        isBlocked = in.readBoolean();

        Coords pos = (Coords) in.readObject();
        this.setPos(pos);

        coverLine = (List<Coords>) in.readObject();

        collisionPolygons = (List<List<Coords>>) in.readObject();

        String dialogId = in.readUTF();
        if (!dialogId.isEmpty()) {
            dialog = new Dialog(dialogId);
        }

        interactionPoint = (Coords) in.readObject();

        script = (Script) in.readObject();
    }
}
