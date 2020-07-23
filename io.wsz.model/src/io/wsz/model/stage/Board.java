package io.wsz.model.stage;

import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.location.Location;
import io.wsz.model.sizes.Sizes;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Math.tan;
import static java.lang.Math.toRadians;

public class Board {
    private static Board singleton;

    private final GraphSorter<PosItem> posItemGraphSorter = new GraphSorter<>();
    private final GraphSorter<Equipment> equipmentGraphSorter = new GraphSorter<>();
    private final Coords boardPos = new Coords(0, 0);

    private final List<PosItem> allItems = new ArrayList<>(0);
    private final List<PosItem> items = new ArrayList<>(0);
    private final List<Equipment> equipment = new ArrayList<>(0);
    private final List<Equipment> equipmentResult = new ArrayList<>(0);
    private final List<Teleport> teleports = new ArrayList<>(0);
    private final List<Creature> creatures = new ArrayList<>(0);
    private final Coords resultCoords = new Coords();
    private final List<Coords> way = Collections.unmodifiableList(List.of(new Coords(), new Coords()));
    private final List<List<Coords>> listOfWay = List.of(way);
    private final ItemType[] allTypes = ItemType.values();

    public static Board get() {
        if (singleton == null) {
            singleton = new Board();
        }
        return singleton;
    }

    private Board() {}

    public Coords getBoardPos() {
        return boardPos;
    }

    public PosItem lookForItem(Location location, double x, double y, ItemType[] types, boolean includeLevelsBelow) {
        allItems.clear();
        if (location == null) return null;
        allItems.addAll(location.getItems().get());
        items.clear();
        allItems.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> {
                    for (ItemType type : types) {
                        if (type == pi.getType()) return true;
                    }
                    return false;
                })
                .filter(pi -> {
                    int level = pi.getPos().level;
                    int actualLevel = Controller.get().getCurrentLayer().getLevel(); //TODO checked location
                    if (includeLevelsBelow) {
                        return level <= actualLevel;
                    } else {
                        return level == actualLevel;
                    }
                })
                .collect(Collectors.toCollection(() -> items));
        if (items.isEmpty()) {
            return null;
        }
        this.sortPosItems(items);
        Collections.reverse(items);

        int pixelX = (int) (x * Sizes.getMeter());
        int pixelY = (int) (y * Sizes.getMeter());

        for (PosItem pi : items) {
            int cX = (int) (pi.getPos().x * Sizes.getMeter());
            int cWidth = (int) pi.getImage().getWidth();
            boolean fitX = pixelX >= cX && pixelX <= cX + cWidth;
            if (!fitX) {
                continue;
            }

            int cY = (int) (pi.getPos().y * Sizes.getMeter());
            int cHeight = (int) pi.getImage().getHeight();
            boolean fitY = pixelY >= cY && pixelY <= cY + cHeight;
            if (!fitY) {
                continue;
            }

            Image img = pi.getImage();
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
            return pi;
        }
        return null;
    }

    public List<Creature> getControlledCreatures(Location location) {
        creatures.clear();
        location.getItems().get().stream()
                .filter(pi -> pi.getType().equals(ItemType.CREATURE))
                .filter(pi -> {
                    Creature cr = (Creature) pi;
                    return cr.getControl().equals(CreatureControl.CONTROL);
                })
                .map(pi -> (Creature) pi)
                .collect(Collectors.toCollection(() -> creatures));
        return creatures;
    }

    public PosItem getObstacle(Coords nextPos, PosItem item, Location location) {
        return getObstacle(nextPos, item, location, allTypes);
    }

    public PosItem getObstacle(Coords nextPos, PosItem i, Location location, ItemType[] types) {
        items.clear();
        location.getItems().get().stream()
                .filter(PosItem::getVisible)
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
                    int level = i.getPos().level;
                    return pi.getPos().level == level;
                })
                .collect(Collectors.toCollection(() -> items));
        if (items.isEmpty()) return null;
        List<List<Coords>> iPolygons = i.getActualCollisionPolygons();
        if (iPolygons.isEmpty() && !(i instanceof Creature)) return null;

        double left = i.getCollisionLeft(iPolygons, nextPos);
        double right = i.getCollisionRight(iPolygons, nextPos);
        double top = i.getCollisionTop(iPolygons, nextPos);
        double bottom = i.getCollisionBottom(iPolygons, nextPos);

        for (PosItem o : items) {
            if (o == i) continue;
            List<List<Coords>> oPolygons = o.getActualCollisionPolygons();
            PosItem collision = getCollision(left, right, top, bottom, nextPos, i, iPolygons, o, oPolygons);
            if (collision != null) return collision;
        }
        return null;
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
        location.getItems().get().stream()
                .filter(PosItem::getVisible)
                .filter(pi -> {
                    double piLeft = pi.getLeft();
                    double piRight = pi.getRight();
                    double piTop = pi.getTop();
                    double piBottom = pi.getBottom();
                    return Coords.doOverlap(
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
        l.getItems().get().stream()
                .filter(PosItem::getVisible)
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
        boolean collides = Coords.polygonsIntersect(nextPos.x, nextPos.y, iPolygons, oPos, oPolygons);
        if (collides) {
            System.out.println(i.getName() + " collides " + o.getName());
        }
        return collides;
    }

    private boolean getWayCollision(Coords oPos, List oPolygons) {
        boolean collides = Coords.polygonsIntersect(0, 0, listOfWay, oPos, oPolygons);
        if (collides) {
            System.out.println("Way collision");
        }
        return collides;
    }

    private boolean getCreatureCreatureCollision(Coords nextPos, Creature cr, Creature crO) {
        boolean collides = Coords.ovalsIntersect(nextPos, cr.getSize(), crO.getCenter(), crO.getSize());
        if (collides) {
            System.out.println(cr.getName() + " collides " + crO.getName());
        }
        return collides;
    }

    public boolean getObstacleCreatureCollision(Coords nextPos, Creature cr, List<List<Coords>> iPolygons, PosItem i) {
        for (List<Coords> polygon : iPolygons) {
            List<Coords> lostRef = Coords.looseCoordsReferences1(polygon);
            Coords.translateCoords(lostRef, nextPos.x, nextPos.y);

            boolean ovalIntersectsPolygon = Coords.ovalIntersectsPolygon(cr.getCenter(), cr.getSize(), lostRef);
            if (ovalIntersectsPolygon) {
                System.out.println(i.getName() + " collides " + cr.getName());
                return true;
            }
        }
        return false;
    }

    public boolean getCreatureObstacleCollision(Coords nextPos, Creature cr, List<List<Coords>> oPolygons, PosItem o) {
        for (List<Coords> polygon : oPolygons) {
            List<Coords> lostRef = Coords.looseCoordsReferences1(polygon);
            Coords oPos = o.getPos();
            Coords.translateCoords(lostRef, oPos.x, oPos.y);

            boolean ovalIntersectsPolygon = Coords.ovalIntersectsPolygon(nextPos, cr.getSize(), lostRef);
            if (ovalIntersectsPolygon) {
                System.out.println(cr.getName() + " collides " + o.getName());
                return true;
            }
        }
        return false;
    }

    public List<Equipment> getEquipmentWithinRange(Creature cr) {
        equipmentResult.clear();
        items.clear();
        Location location = cr.getPos().getLocation();
        items.addAll(location.getItems().get());
        equipment.clear();
        items.stream()
                .filter(PosItem::getVisible)
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
            if (Coords.pointWithinOval(point, center, width, height)) {
                equipmentResult.add(e);
            }
        }
        sortEquipment(equipmentResult);
        return equipmentResult;
    }

    public Coords getFreePosAround(Creature cr) {
        Coords pos = cr.getCenter();

        CreatureSize size = cr.getSize();
        double height = size.getHeight();
        double width = size.getWidth();
        double offset = 0.1;

        for (int i = 1; i < 10; i++) {
            double angleScope = 45.0 / i;
            int iterations = i * 8;
            int angle = 0;
            for (int j = 1; j < iterations; j++) {
                double dx = i*width + offset;
                double dyAngle = dx * tan(toRadians(angle));
                double dyStraight = i*height + offset;
                double x = 0;
                double y = 0;
                if (angle >= 360) {
                    break;
                } else if (angle == 0) {
                    x = pos.x + dx;
                    y = pos.y;
                } else if (angle == 90) {
                    x = pos.x;
                    y = pos.y - dyStraight;
                } else if (angle == 180) {
                    x = pos.x - dx;
                    y = pos.y;
                } else if (angle == 270) {
                    x = pos.x;
                    y = pos.y + dyStraight;
                } else if (angle > 270) {
                    x = pos.x + dx;
                    y = pos.y + dyAngle;
                } else if (angle > 180) {
                    x = pos.x - dx;
                    y = pos.y + dyAngle;
                } else if (angle > 90) {
                    x = pos.x - dx;
                    y = pos.y - dyAngle;
                } else if (angle > 0) {
                    x = pos.x + dx;
                    y = pos.y - dyAngle;
                }

                Location location = cr.getPos().getLocation();
                if (x < 0 || x > location.getWidth()) continue;
                if (y < 0 || y > location.getHeight()) continue;

                resultCoords.x = x;
                resultCoords.y = y;
                PosItem pi = cr.getCollision(resultCoords);

                if (pi == null) return resultCoords;
                angle = (int) (angleScope * j);
            }
        }
        return pos;
    }

    public List<Creature> getControllableCreatures(Location location) {
        creatures.clear();
        location.getItems().get().stream()
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
        location.getItems().get().stream()
                .filter(pi -> pi instanceof Creature)
                .map(pi -> (Creature) pi)
                .filter(c -> c.getControl().equals(CreatureControl.CONTROLLABLE)
                            || c.getControl().equals(CreatureControl.CONTROL))
                .collect(Collectors.toCollection(() -> creatures));
        return creatures;
    }

    public void looseCreaturesControl(Location location) {
        List<Creature> creatures = getControlledCreatures(location);
        Controller.get().getCreaturesToLooseControl().addAll(creatures);
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
        location.getItems().get().stream()
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
