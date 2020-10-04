package io.wsz.model.stage;

import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.layer.Layer;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Board {
    private static final PolygonsGetter<PosItem<?, ?>> actualCollisionPolygonsGetter = PosItem::getActualCollisionPolygons;
    private static final PolygonsGetter<Teleport> teleportPolygonsGetter = Teleport::getTeleportCollisionPolygons;

    private final Controller controller;
    private final GraphSorter<PosItem<?,?>> posItemGraphSorter = new GraphSorter<>();
    private final GraphSorter<Equipment<?,?>> equipmentGraphSorter = new GraphSorter<>();
    private final Coords curPos = new Coords(0, 0);

    private final List<PosItem<?,?>> allItems = new ArrayList<>(0);
    private final List<PosItem<?,?>> items = new ArrayList<>(0);
    private final List<Equipment<?,?>> equipment = new ArrayList<>(0);
    private final List<Equipment<?,?>> equipmentResult = new ArrayList<>(0);
    private final List<Teleport> teleports = new ArrayList<>(0);
    private final List<Creature> creatures = new ArrayList<>(0);
    private final List<Coords> way = Collections.unmodifiableList(List.of(new Coords(), new Coords()));
    private final List<List<Coords>> listOfWay = List.of(way);
    private final ItemType[] allTypes = ItemType.values();

    public Board(Controller controller) {
        this.controller = controller;
    }

    public Coords getCurPos() {
        return curPos;
    }

    public void centerScreenOn(Coords posToCenter, double canvasMeterWidth, double canvasMeterHeight, double widthCorrection) {
        Location locationToGo = posToCenter.getLocation();
        if (locationToGo != null) {
            Location actual = controller.getCurrentLocation();
            if (actual != locationToGo) {
                controller.setCurrentLocation(locationToGo);
            }
        }
        curPos.level = posToCenter.level;
        List<Layer> layers = controller.getCurrentLocation().getLayers();
        Layer layer;
        try {
            layer = layers.get(curPos.level);
        } catch (IndexOutOfBoundsException e) {
            layer = layers.get(0);
        }
        controller.setCurrentLayer(layer);

        double x = posToCenter.x - canvasMeterWidth/2;
        double y = posToCenter.y - canvasMeterHeight/2;
        double locWidth = controller.getCurrentLocation().getWidth();
        double locHeight = controller.getCurrentLocation().getHeight();
        if (x > locWidth - canvasMeterWidth - widthCorrection) {
            curPos.x = locWidth - canvasMeterWidth - widthCorrection;
        } else {
            curPos.x = Math.max(x, 0);
        }
        if (y > locHeight - canvasMeterHeight) {
            curPos.y = locHeight - canvasMeterHeight;
        } else {
            curPos.y = Math.max(y, 0);
        }
    }

    public <A extends PosItem<?,?>> EquipmentMayCountable<?,?> lookForMayCountableEquipment(
            List<A> items, double x, double y, int lookedLevel, double horTolerance, double verTolerance) {
        return (EquipmentMayCountable<?,?>) lookForItem(items, x, y, lookedLevel, horTolerance, verTolerance,
                ItemType.EQUIPMENT_MAY_COUNTABLE_TYPES, false);
    }

    public <A extends PosItem<?,?>> PosItem<?,?> lookForItem(List<A> items, double x, double y, int lookedLevel, ItemType[] types,
                                                   boolean includeLevelsBelow) {
        return lookForItem(items, x, y, lookedLevel, 0, 0, types, includeLevelsBelow);
    }

    public <A extends PosItem<?,?>> PosItem<?,?> lookForItem(List<A> items, double x, double y, int lookedLevel,
                                                   double horTolerance,
                                                   double verTolerance,
                                                   ItemType[] types, boolean includeLevelsBelow) {
        allItems.clear();
        if (items == null || items.isEmpty()) return null;
        allItems.addAll(items);
        this.items.clear();
        allItems.stream()
                .filter(PosItem::isVisible)
                .filter(pi -> {
                    for (ItemType type : types) {
                        if (type == pi.getType()) return true;
                    }
                    return false;
                })
                .filter(pi -> {
                    int level = pi.getPos().level;
                    if (includeLevelsBelow) {
                        return level <= lookedLevel;
                    } else {
                        return level == lookedLevel;
                    }
                })
                .collect(Collectors.toCollection(() -> this.items));
        if (this.items.isEmpty()) {
            return null;
        }
        this.sortPosItems(this.items);
        Collections.reverse(this.items);

        int meter = Sizes.getMeter();
        int pixelX = (int) (x * meter);
        int pixelY = (int) (y * meter);

        for (PosItem<?,?> item : this.items) {
            Coords pos = item.getPos();
            int iX = (int) (pos.x * meter);
            ResolutionImage image = item.getImage();
            int iWidth = (int) image.getWidth();
            int pixelHorTolerance = (int) (horTolerance * meter);
            boolean fitX = pixelX >= iX - pixelHorTolerance && pixelX <= iX + iWidth + pixelHorTolerance;
            if (!fitX) {
                continue;
            }

            int iY = (int) (pos.y * meter);
            int iHeight = (int) image.getHeight();
            int pixelVerTolerance = (int) (verTolerance * meter);
            boolean fitY = pixelY >= iY - pixelVerTolerance && pixelY <= iY + iHeight + pixelVerTolerance;
            if (!fitY) {
                continue;
            }

            if (horTolerance == 0 && verTolerance == 0) {
                Image img = image.getFxImage();
                int imgX = pixelX - iX;
                int imgY = pixelY - iY;
                Color color;
                try {
                    color = img.getPixelReader().getColor(imgX, imgY);
                } catch (IndexOutOfBoundsException e) {
                    continue;
                }
                boolean isPixelTransparent = color.equals(Color.TRANSPARENT);
                if (isPixelTransparent) {
                    continue;
                }
            }
            return item;
        }
        return null;
    }

    public List<Creature> getControlledCreatures(Location location) {
        creatures.clear();
        location.getItemsList().getCreatures().stream()
                .filter(c -> c.getControl().equals(CreatureControl.CONTROL))
                .collect(Collectors.toCollection(() -> creatures));
        return creatures;
    }

    public PosItem<?,?> getObstacle(Coords nextPos, PosItem<?,?> item, List<PosItem<?,?>> items) {
        return getObstacle(nextPos, item, allTypes, items);
    }

    public <A extends PosItem<?,?>> PosItem<?,?> getObstacle(Coords nextPos, PosItem<?,?> colliding, ItemType[] types, List<A> items) {
        if (items == null || items.isEmpty()) return null;
        this.items.clear();
        items.stream()
                .filter(PosItem::isVisible)
                .filter(pi -> checkIfSuitTypes(types, pi))
                .filter(pi -> pi.checkIfCanCollide())
                .filter(pi -> pi.getPos().level == nextPos.level)
                .collect(Collectors.toCollection(() -> this.items));
        if (this.items.isEmpty()) return null;
        return colliding.findCollision(nextPos, this.items, actualCollisionPolygonsGetter);
    }

    protected <A extends PosItem<?,?>> boolean checkIfSuitTypes(ItemType[] types, A pi) {
        if (types == this.allTypes) return true;
        ItemType piType = pi.getType();
        for (ItemType type : types) {
            if (type == piType) return true;
        }
        return false;
    }

    public PosItem<?,?> getObstacleOnWay(Location location, int level, double fromX, double fromY, PosItem<?,?> i, double toX, double toY) {
        Coords from = way.get(0);
        from.x = fromX;
        from.y = fromY;
        Coords to = way.get(1);
        to.x = toX;
        to.y = toY;

        double left = Math.min(fromX, toX);
        double right = Math.max(fromX, toX);
        double top = Math.min(fromY, toY);
        double bottom = Math.max(fromY, toY);

        items.clear();
        location.getItemsList().getMergedList().stream()
                .filter(PosItem::isVisible)
                .filter(o -> {
                    List<List<Coords>> actualCollisionPolygons = o.getActualCollisionPolygons();
                    double piLeft = o.getCollisionLeft(actualCollisionPolygons);
                    double piRight = o.getCollisionRight(actualCollisionPolygons);
                    double piTop = o.getCollisionTop(actualCollisionPolygons);
                    double piBottom = o.getCollisionBottom(actualCollisionPolygons);
                    return Geometry.doOverlap(
                            left, top, right, bottom,
                            piLeft, piTop, piRight, piBottom);
                })
                .filter(pi -> pi.getPos().level == level)
                .collect(Collectors.toCollection(() -> items));

        for (PosItem<?,?> o : items) {
            if (o == i) continue;
            List<List<Coords>> oPolygons = o.getActualCollisionPolygons();
            if (oPolygons.isEmpty()) continue;
            boolean obstacle = isWayCollision(o.getPos(), oPolygons);
            if (obstacle) {
                return o;
            }
        }
        return null;
    }

    public Teleport getTeleport(Coords nextPos, PosItem<?,?> colliding, Location location) {
        teleports.clear();
        location.getItemsList().getTeleports().stream()
                .filter(PosItem::isVisible)
                .filter(pi -> pi.getTeleportCollisionPolygons() != null)
                .filter(pi -> {
                    int level = colliding.getPos().level;
                    return pi.getPos().level == level;
                })
                .collect(Collectors.toCollection(() -> teleports));
        if (teleports.isEmpty()) return null;
        return colliding.findCollision(nextPos, teleports, teleportPolygonsGetter);
    }

    private boolean isWayCollision(Coords oPos, List<List<Coords>> oPolygons) {
        boolean collides = Geometry.polygonsIntersect(0, 0, listOfWay, oPos, oPolygons);
        if (collides) {
            controller.getLogger().logWayCollision();
        }
        return collides;
    }

    public List<Equipment<?,?>> getEquipmentWithinRange(Creature cr) {
        equipmentResult.clear();
        equipment.clear();
        Location location = cr.getPos().getLocation();
        equipment.addAll(location.getItemsList().getMergedEquipment());
        equipment.stream()
                .filter(PosItem::isVisible)
                .filter(pi -> pi.getPos().level == cr.getPos().level)
                .collect(Collectors.toCollection(() -> equipmentResult));
        if (equipmentResult.isEmpty()) return equipmentResult;

        double range = cr.getRange();
        double width = cr.getSize().getWidth() + 2*range;
        double height = cr.getSize().getHeight() + 2*range;
        Coords point = cr.getCenter();
        for (Equipment<?,?> e : equipment) {
            Coords center = e.getImageCenter();
            if (Geometry.isPointWithinOval(point, center, width, height)) {
                equipmentResult.add(e);
            }
        }
        sortEquipment(equipmentResult);
        return equipmentResult;
    }

    public List<Creature> getControllableCreatures(Location location) {
        creatures.clear();
        location.getItemsList().getCreatures().stream()
                .filter(c -> c.getControl().equals(CreatureControl.CONTROLLABLE))
                .collect(Collectors.toCollection(() -> creatures));
        return creatures;
    }

    public List<Creature> getControlledAndControllableCreatures(Location location) {
        creatures.clear();
        location.getItemsList().getCreatures().stream()
                .filter(c -> c.getControl().equals(CreatureControl.CONTROLLABLE)
                            || c.getControl().equals(CreatureControl.CONTROL))
                .collect(Collectors.toCollection(() -> creatures));
        return creatures;
    }

    public void looseCreaturesControl(Location location) {
        List<Creature> creatures = getControlledCreatures(location);
        controller.getCreaturesToLooseControl().addAll(creatures);
    }

    public List<Creature> getControllablesWithinRectangle(double left, double top, double right, double bottom, Location location) {
        if (left > right) {
            double tempLeft = left;
            left = right;
            right = tempLeft;
        }
        if (top > bottom) {
            double tempTop = top;
            top = bottom;
            bottom = tempTop;
        }
        creatures.clear();
        double finalLeft = left;
        double finalRight = right;
        double finalTop = top;
        double finalBottom = bottom;
        location.getItemsList().getCreatures().stream()
                .filter(c -> c.getControl().equals(CreatureControl.CONTROLLABLE)
                        || c.getControl().equals(CreatureControl.CONTROL))
                .filter(c -> {
                    Coords centerBottom = c.getCenter();
                    double x = centerBottom.x;
                    double y = centerBottom.y;
                    return x > finalLeft && x < finalRight
                            && y > finalTop && y < finalBottom;
                })
                .collect(Collectors.toCollection(() -> creatures));
        return creatures;
    }

    public void sortPosItems(List<PosItem<?,?>> items) {
        posItemGraphSorter.sortItems(items);
    }

    public void sortEquipment(List<Equipment<?,?>> items) {
        equipmentGraphSorter.sortItems(items);
    }
}
