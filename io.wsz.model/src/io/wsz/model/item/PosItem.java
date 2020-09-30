package io.wsz.model.item;

import io.wsz.model.Controller;
import io.wsz.model.animation.Animation;
import io.wsz.model.animation.AnimationPos;
import io.wsz.model.animation.cursor.CursorType;
import io.wsz.model.asset.Asset;
import io.wsz.model.dialog.Dialog;
import io.wsz.model.location.Location;
import io.wsz.model.script.Script;
import io.wsz.model.sizes.Sizes;
import io.wsz.model.stage.Coords;
import io.wsz.model.stage.Geometry;
import io.wsz.model.stage.ResolutionImage;
import io.wsz.model.world.World;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class PosItem<A extends PosItem<?,?>, B extends AnimationPos> extends Asset<A> implements Updatable, Interactable, Animable {
    private static final long serialVersionUID = 1L;

    protected final Coords center = new Coords();

    protected Controller controller;

    protected String itemId;
    protected boolean isVisible;
    protected BooleanProperty isBlocked;
    protected Coords pos;
    protected A prototype;
    protected List<Coords> coverLine;
    protected List<List<Coords>> collisionPolygons;
    protected Dialog dialog;
    protected Coords interactionPoint;
    protected Double animationSpeed;
    protected ResolutionImage image;
    protected Script script;

    public PosItem() {
        this.isBlocked = new SimpleBooleanProperty(this, "isBlocked");
        this.pos = new Coords();
    }

    public PosItem(ItemType type, Controller controller) {
        super(type);
        this.isVisible = true;
        this.isBlocked = new SimpleBooleanProperty(this, "isBlocked", false);
        this.pos = new Coords();
        this.controller = controller;
        this.coverLine = new ArrayList<>(0);
        this.collisionPolygons = new ArrayList<>(0);
    }

    public PosItem(A prototype) {
        this.prototype = prototype;
        this.isVisible = true;
        this.isBlocked = new SimpleBooleanProperty(this, "isBlocked", false);
        this.pos = new Coords();
    }

    public PosItem(PosItem<A, B> other, boolean keepId) {
        super(other);
        this.prototype = other.prototype;
        if (keepId) {
            this.itemId = other.itemId;
        } else {
            this.itemId = getUniqueId(other.itemId);
        }
        this.isVisible = other.isVisible;
        this.isBlocked = new SimpleBooleanProperty(this, "isBlocked", other.isBlocked.get());
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
                .anyMatch(l -> l.getItems().stream().anyMatch(i -> {
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
            from.getItemsToRemove().add(this);
            target.getItemsToAdd().add(this);
        }
    }

    public boolean withinRange(Coords pos, double range, double sizeWidth, double sizeHeight) {
        return Geometry.isPointWithinOval(getInteractionPoint(), pos, sizeWidth + 2*range, sizeHeight + 2*range);
    }

    public PosItem getCollision() {
        return getCollision(getCoordsForCollisionCheck());
    }

    protected Coords getCoordsForCollisionCheck() {
        return pos;
    }

    public PosItem getCollision(Coords nextPos) {
        return getCollision(nextPos, pos.getLocation());
    }

    public PosItem getCollision(Location location) {
        return getCollision(getCoordsForCollisionCheck(), location);
    }

    public PosItem getCollision(Coords nextPos, Location nextLocation) {
        if (nextLocation == null) return null;
        List<PosItem> items = nextLocation.getItems();
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
    public <M extends Animation<?>> M getAnimation() {
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

    protected abstract <M extends Animation<?>> M getConcreteAnimation();

    public abstract B getAnimationPos();

    @Override
    public void restoreReferences(Controller controller, List<Asset> assets, World world) {
        restorePrototype(assets);
        controller.restoreLocationOfCoords(pos);
        restoreDialog(world.getDialogs());
    }

    private void restorePrototype(List<Asset> assets) {
        A serPrototype = getPrototype();
        if (serPrototype == null) return;
        String prototypeName = serPrototype.getAssetId();
        Asset asset = assets.stream()
                .filter(a -> a.getAssetId().equals(prototypeName))
                .findFirst().orElse(null);
        A p = (A) asset;
        if (p == null) {
            throw new NullPointerException(prototypeName + " reference is not found");
        }
        setPrototype(p);
    }

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

    public boolean tryAddToLocation(Location location, List<PosItem> locationItems) {
        boolean itemWillCollide = getCollision(location) != null;
        if (itemWillCollide) return false;
        addToLocation(location, locationItems);
        return true;
    }

    @Override
    public final void addNewItemToLocation(Location toLocation, int toLevel, double toX, double toY, String newItemId) {
        A item = getNewItemFromPrototype();
        item.setItemId(newItemId);
        addNewItemToLocation(toLocation, toLevel, toX, toY, item);
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

    protected abstract A getNewItemFromPrototype();

    protected void addNewItemToLocation(Location toLocation, int toLevel, double toX, double toY, A item) {
        item.setPos(toX, toY, toLevel, toLocation);
        toLocation.getItemsToAdd().add(item);
    }

    public void addToLocation(Location location, List<PosItem> locationItems) {
        locationItems.add(this);
        onChangeLocationAction(location);
    }

    public PosItem getItemByAssetId(String lookedId) {
        String assetId = this.getAssetId();
        if (assetId != null && assetId.equals(lookedId)) {
            return this;
        } else {
            return null;
        }
    }

    public PosItem getItemByItemId(String lookedId) {
        if (this.itemId != null && this.itemId.equals(lookedId)) {
            return this;
        } else {
            return null;
        }
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

    @Override
    public String getPath() {
        if (prototype != null) {
            return prototype.getPath();
        }
        return super.getPath();
    }

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
        return isBlocked.get();
    }

    public BooleanProperty isBlockedProperty() {
        return isBlocked;
    }

    public void setIsBlocked(boolean isBlocked) {
        this.isBlocked.set(isBlocked);
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

    public A getPrototype() {
        return prototype;
    }

    public void setPrototype(A prototype) {
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
            Animation animation = getAnimation();
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
                Animation animation = getAnimation();
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

        out.writeBoolean(isBlocked.get());

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

        prototype = (A) in.readObject();

        itemId = (String) in.readObject();

        animationSpeed = (Double) in.readObject();

        isVisible = in.readBoolean();

        isBlocked.set(in.readBoolean());

        Coords pos = (Coords) in.readObject();
        this.pos.x = pos.x;
        this.pos.y = pos.y;
        this.pos.level = pos.level;
        this.pos.setLocation(pos.getLocation());

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
