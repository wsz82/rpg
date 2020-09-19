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
    private final Controller controller;
    private final GraphSorter<PosItem> posItemGraphSorter = new GraphSorter<>();
    private final GraphSorter<Equipment> equipmentGraphSorter = new GraphSorter<>();
    private final Coords curPos = new Coords(0, 0);

    private final List<PosItem> allItems = new ArrayList<>(0);
    private final List<PosItem> items = new ArrayList<>(0);
    private final List<Equipment> equipment = new ArrayList<>(0);
    private final List<Equipment> equipmentResult = new ArrayList<>(0);
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
            Location actual = controller.getCurrentLocation().getLocation();
            if (actual != locationToGo) {
                controller.getCurrentLocation().setLocation(locationToGo);
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
        controller.getCurrentLayer().setLayer(layer);

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

    public <A extends PosItem> EquipmentMayCountable lookForMayCountableEquipment(List<A> items, double x, double y,
                                                                                  int lookedLevel,
                                                                                  double horTolerance, double verTolerance) {
        return (EquipmentMayCountable) lookForItem(items, x, y, lookedLevel, horTolerance, verTolerance,
                ItemType.EQUIPMENT_MAY_COUNTABLE_TYPES, false);
    }

    public <A extends PosItem> PosItem lookForItem(List<A> items, double x, double y, int lookedLevel, ItemType[] types,
                                                   boolean includeLevelsBelow) {
        return lookForItem(items, x, y, lookedLevel, 0, 0, types, includeLevelsBelow);
    }

    public <A extends PosItem> PosItem lookForItem(List<A> items, double x, double y, int lookedLevel,
                                                   double horTolerance,
                                                   double verTolerance,
                                                   ItemType[] types, boolean includeLevelsBelow) {
        allItems.clear();
        if (items == null || items.isEmpty()) return null;
        allItems.addAll(items);
        this.items.clear();
        allItems.stream()
                .filter(PosItem::getIsVisible)
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

        for (PosItem pi : this.items) {
            Coords pos = pi.getPos();
            int cX = (int) (pos.x * meter);
            ResolutionImage image = pi.getImage();
            int cWidth = (int) image.getWidth();
            int pixelHorTolerance = (int) (horTolerance * meter);
            boolean fitX = pixelX >= cX - pixelHorTolerance && pixelX <= cX + cWidth + pixelHorTolerance;
            if (!fitX) {
                continue;
            }

            int cY = (int) (pos.y * meter);
            int cHeight = (int) image.getHeight();
            int pixelVerTolerance = (int) (verTolerance * meter);
            boolean fitY = pixelY >= cY - pixelVerTolerance && pixelY <= cY + cHeight + pixelVerTolerance;
            if (!fitY) {
                continue;
            }

            if (horTolerance == 0 && verTolerance == 0) {
                Image img = image.getFxImage();
                int imgX = pixelX - cX;
                int imgY = pixelY - cY;
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
            return pi;
        }
        return null;
    }

    public List<Creature> getControlledCreatures(Location location) {
        creatures.clear();
        location.getItems().stream()
                .filter(pi -> pi instanceof Creature)
                .filter(pi -> {
                    Creature cr = (Creature) pi;
                    return cr.getControl().equals(CreatureControl.CONTROL);
                })
                .map(pi -> (Creature) pi)
                .collect(Collectors.toCollection(() -> creatures));
        return creatures;
    }

    public PosItem getObstacle(Coords nextPos, PosItem item, List<PosItem> items) {
        return getObstacle(nextPos, item, allTypes, items);
    }

    public <A extends PosItem> PosItem getObstacle(Coords nextPos, PosItem colliding, ItemType[] types, List<A> items) {
        if (items == null || items.isEmpty()) return null;
        this.items.clear();
        items.stream()
                .filter(PosItem::getIsVisible)
                .filter(pi -> {
                    if (types == this.allTypes) return true;
                    ItemType piType = pi.getType();
                    for (ItemType type : types) {
                        if (type == piType) return true;
                    }
                    return false;
                })
                .filter(pi -> pi.getActualCollisionPolygons() != null || pi instanceof Creature)
                .filter(pi -> {
                    int level = nextPos.level;
                    return pi.getPos().level == level;
                })
                .collect(Collectors.toCollection(() -> this.items));
        if (this.items.isEmpty()) return null;
        List<List<Coords>> iPolygons = colliding.getActualCollisionPolygons();
        if (iPolygons.isEmpty() && !(colliding instanceof Creature)) return null;

        double left = colliding.getCollisionLeft(iPolygons, nextPos);
        double right = colliding.getCollisionRight(iPolygons, nextPos);
        double top = colliding.getCollisionTop(iPolygons, nextPos);
        double bottom = colliding.getCollisionBottom(iPolygons, nextPos);

        for (PosItem obstacle : this.items) {
            PosItem collision = getObstacle(nextPos, colliding, iPolygons, left, right, top, bottom, obstacle);
            if (collision != null) return collision;
        }
        return null;
    }

    private PosItem getObstacle(Coords nextPos, PosItem colliding, List<List<Coords>> iPolygons,
                                double left, double right, double top, double bottom, PosItem obstacle) {
        if (obstacle == colliding) return null;
        boolean isDoorAgainstLandscapeChecked = colliding instanceof Door && obstacle instanceof Landscape;
        boolean isLandscapeAgainstDoorChecked = colliding instanceof Landscape && obstacle instanceof Door;
        boolean isDoorAgainstCoverChecked = colliding instanceof Door && obstacle instanceof Cover;
        boolean isCoverAgainstDoorChecked = colliding instanceof Cover && obstacle instanceof Door;
        boolean isLandscapeAgainstLandscapeChecked = colliding instanceof Landscape && obstacle instanceof Landscape;
        if (isDoorAgainstLandscapeChecked || isLandscapeAgainstDoorChecked
                || isDoorAgainstCoverChecked || isCoverAgainstDoorChecked
                || isLandscapeAgainstLandscapeChecked) return null;
        List<List<Coords>> oPolygons = obstacle.getActualCollisionPolygons();
        PosItem collision = getCollision(left, right, top, bottom, nextPos, colliding, iPolygons, obstacle, oPolygons);
        return collision;
    }

    public PosItem getObstacle(Coords nextPos, Creature colliding, PosItem obstacle) {
        if (obstacle == colliding) return null;
        List<List<Coords>> oPolygons = obstacle.getActualCollisionPolygons();
        if (getCreatureObstacleCollision(nextPos, colliding, oPolygons, obstacle)) {
            return obstacle;
        } else {
            return null;
        }
    }

    public PosItem getObstacleOnWay(Location location, int level, double fromX, double fromY, PosItem i, double toX, double toY) {
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
        location.getItems().stream()
                .filter(PosItem::getIsVisible)
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

        for (PosItem o : items) {
            if (o == i) continue;
            List<List<Coords>> oPolygons = o.getActualCollisionPolygons();
            if (oPolygons.isEmpty()) continue;
            boolean obstacle = getWayCollision(o.getPos(), oPolygons);
            if (obstacle) {
                return o;
            }
        }
        return null;
    }

    public Teleport getTeleport(Coords nextPos, PosItem i, Location l) {
        teleports.clear();
        l.getItems().stream()
                .filter(PosItem::getIsVisible)
                .filter(pi -> pi instanceof Teleport)
                .map(pi -> (Teleport) pi)
                .filter(pi -> pi.getActualCollisionPolygons() != null)
                .filter(pi -> {
                    int level = i.getPos().level;
                    return pi.getPos().level == level;
                })
                .collect(Collectors.toCollection(() -> teleports));
        if (teleports.isEmpty()) return null;
        List<List<Coords>> iPolygons = i.getActualCollisionPolygons();
        if (iPolygons.isEmpty() && !(i instanceof Creature)) return null;

        double left = i.getCollisionLeft(iPolygons, nextPos);
        double right = i.getCollisionRight(iPolygons, nextPos);
        double top = i.getCollisionTop(iPolygons, nextPos);
        double bottom = i.getCollisionBottom(iPolygons, nextPos);

        for (Teleport t : teleports) {
            if (t == i) continue;
            List<List<Coords>> oPolygons = t.getTeleportCollisionPolygons();
            Teleport collision = getCollision(left, right, top, bottom, nextPos, i, iPolygons, t, oPolygons);
            if (collision != null) return collision;
        }
        return null;
    }

    private <A extends PosItem> A getCollision(double left, double right, double top, double bottom, Coords nextPos, PosItem i,
                                 List<List<Coords>> iPolygons, A o, List<List<Coords>> oPolygons) {
        if (oPolygons.isEmpty() && !(o instanceof Creature)) return null;
        double oLeft = o.getCollisionLeft(oPolygons);
        double oRight = o.getCollisionRight(oPolygons);
        if (right < oLeft || left > oRight) return null;

        double oTop = o.getCollisionTop(oPolygons);
        double oBottom = o.getCollisionBottom(oPolygons);
        if (bottom < oTop || top > oBottom) return null;

        if (i instanceof Creature && !(o instanceof Creature)) {

            Creature cr = (Creature) i;
            if (getCreatureObstacleCollision(nextPos, cr, oPolygons, o)) return o;

        } else if (o instanceof Creature && !(i instanceof Creature)) {

            Creature crO = (Creature) o;
            if (getObstacleCreatureCollision(nextPos, crO, iPolygons, i)) return o;

        } else if (o instanceof Creature) {

            Creature cr = (Creature) i;
            Creature crO = (Creature) o;
            if (getCreatureCreatureCollision(nextPos, cr, crO)) return o;

        } else {

            if (getObstacleObstacleCollision(i, nextPos, iPolygons, o, o.getPos(), oPolygons)) return o;

        }
        return null;
    }

    private boolean getObstacleObstacleCollision(PosItem i, Coords nextPos, List iPolygons, PosItem o, Coords oPos, List oPolygons) {
        boolean collides = Geometry.polygonsIntersect(nextPos.x, nextPos.y, iPolygons, oPos, oPolygons);
        if (collides) {
            controller.getLogger().logItemCollides(i.getName(), o.getName());
        }
        return collides;
    }

    private boolean getWayCollision(Coords oPos, List oPolygons) {
        boolean collides = Geometry.polygonsIntersect(0, 0, listOfWay, oPos, oPolygons);
        if (collides) {
            controller.getLogger().logWayCollision();
        }
        return collides;
    }

    private boolean getCreatureCreatureCollision(Coords nextPos, Creature cr, Creature crO) {
        boolean collides = Geometry.ovalsIntersect(nextPos, cr.getSize(), crO.getCenter(), crO.getSize());
        if (collides) {
            controller.getLogger().logItemCollides(cr.getName(), crO.getName());
        }
        return collides;
    }

    public boolean getObstacleCreatureCollision(Coords nextPos, Creature cr, List<List<Coords>> iPolygons, PosItem i) {
        for (List<Coords> polygon : iPolygons) {
            List<Coords> lostRef = Geometry.looseCoordsReferences1(polygon);
            Geometry.translateCoords(lostRef, nextPos.x, nextPos.y);

            boolean ovalIntersectsPolygon = Geometry.ovalIntersectsPolygon(cr.getCenter(), cr.getSize(), lostRef);
            if (ovalIntersectsPolygon) {
                controller.getLogger().logItemCollides(i.getName(), cr.getName());
                return true;
            }
        }
        return false;
    }

    public boolean getCreatureObstacleCollision(Coords nextPos, Creature cr, List<List<Coords>> oPolygons, PosItem o) {
        for (List<Coords> polygon : oPolygons) {
            List<Coords> lostRef = Geometry.looseCoordsReferences1(polygon);
            Coords oPos = o.getPos();
            Geometry.translateCoords(lostRef, oPos.x, oPos.y);

            boolean ovalIntersectsPolygon = Geometry.ovalIntersectsPolygon(nextPos, cr.getSize(), lostRef);
            if (ovalIntersectsPolygon) {
                controller.getLogger().logItemCollides(cr.getName(), o.getName());
                return true;
            }
        }
        return false;
    }

    public List<Equipment> getEquipmentWithinRange(Creature cr) {
        equipmentResult.clear();
        items.clear();
        Location location = cr.getPos().getLocation();
        items.addAll(location.getItems());
        equipment.clear();
        items.stream()
                .filter(PosItem::getIsVisible)
                .filter(pi -> pi.getPos().level == cr.getPos().level)
                .filter(pi -> pi instanceof Equipment)
                .map(pi -> (Equipment) pi)
                .collect(Collectors.toCollection(() -> equipment));
        if (equipment.isEmpty()) return equipmentResult;

        double range = cr.getRange();
        double width = cr.getSize().getWidth() + 2*range;
        double height = cr.getSize().getHeight() + 2*range;
        Coords point = cr.getCenter();
        for (Equipment e : equipment) {
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
        location.getItems().stream()
                .filter(pi -> pi instanceof Creature)
                .map(pi -> (Creature) pi)
                .filter(pi -> {
                    Creature cr = pi;
                    return cr.getControl().equals(CreatureControl.CONTROLLABLE);
                })
                .collect(Collectors.toCollection(() -> creatures));
        return creatures;
    }

    public List<Creature> getControlledAndControllableCreatures(Location location) {
        creatures.clear();
        location.getItems().stream()
                .filter(pi -> pi instanceof Creature)
                .map(pi -> (Creature) pi)
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
        location.getItems().stream()
                .filter(pi -> pi instanceof Creature)
                .map(pi -> (Creature) pi)
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

    public void sortPosItems(List<PosItem> items) {
        posItemGraphSorter.sortItems(items);
    }

    public void sortEquipment(List<Equipment> items) {
        equipmentGraphSorter.sortItems(items);
    }
}
