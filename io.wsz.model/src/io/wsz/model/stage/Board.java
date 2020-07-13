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
    private final Coords boardPos = new Coords(0, 0, null);

    private final List<PosItem> allItems = new ArrayList<>(0);
    private final List<PosItem> items = new ArrayList<>(0);
    private final List<Equipment> equipment = new ArrayList<>(0);
    private final List<Equipment> equipmentResult = new ArrayList<>(0);
    private final List<Teleport> teleports = new ArrayList<>(0);
    private final List<Creature> creatures = new ArrayList<>(0);
    private final Coords resultCoords = new Coords();
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

    public PosItem lookForContent(Location location, Coords[] poss, ItemType[] types, boolean includeLevelsBelow) {
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
                    int level = pi.getLevel();
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

        for (PosItem pi : items) {
            for (Coords pos : poss) {
                int x = (int) (pos.x * Sizes.getMeter());
                int y = (int) (pos.y * Sizes.getMeter());

                int cX = (int) (pi.getPos().x * Sizes.getMeter());
                int cWidth = (int) pi.getImage().getWidth();
                boolean fitX = x >= cX && x <= cX + cWidth;
                if (!fitX) {
                    continue;
                }

                int cY = (int) (pi.getPos().y * Sizes.getMeter());
                int cHeight = (int) pi.getImage().getHeight();
                boolean fitY = y >= cY && y <= cY + cHeight;
                if (!fitY) {
                    continue;
                }

                Image img = pi.getImage();
                int imgX = x - cX;
                int imgY = y - cY;
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
        }
        return null;
    }

    public List<Creature> getCreatures(Location location) {
        creatures.clear();
        location.getItems().get().stream()
                .filter(pi -> {
                    int level = Controller.get().getCurrentLayer().getLevel(); // TODO
                    return pi.getLevel().equals(level);
                })
                .filter(pi -> pi.getType().equals(ItemType.CREATURE))
                .map(pi -> (Creature) pi)
                .collect(Collectors.toCollection(() -> creatures));
        return creatures;
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

    public PosItem getObstacle(Coords nextPos, PosItem item, Location location, ItemType[] types) {
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
                    int level = item.getLevel();
                    return pi.getLevel().equals(level);
                })
                .collect(Collectors.toCollection(() -> items));
        if (items.isEmpty()) return null;
        List<List<Coords>> iPolygons = item.getActualCollisionPolygons();
        if (iPolygons.isEmpty() && !(item instanceof Creature)) return null;

        double left = item.getCollisionLeft(iPolygons, nextPos);
        double right = item.getCollisionRight(iPolygons, nextPos);
        double top = item.getCollisionTop(iPolygons, nextPos);
        double bottom = item.getCollisionBottom(iPolygons, nextPos);

        for (PosItem obstacle : items) {
            if (obstacle == item) continue;
            List<List<Coords>> oPolygons = obstacle.getActualCollisionPolygons();
            PosItem collision = getCollision(left, right, top, bottom, nextPos, item, iPolygons, obstacle, oPolygons);
            if (collision != null) return collision;
        }
        return null;
    }

    public Teleport getTeleport(Coords nextPos, PosItem item, Location location) {
        teleports.clear();
        location.getItems().get().stream()
                .filter(PosItem::getVisible)
                .filter(pi -> pi instanceof Teleport)
                .map(pi -> (Teleport) pi)
                .filter(pi -> pi.getActualCollisionPolygons() != null)
                .filter(pi -> {
                    int level = item.getLevel();
                    return pi.getLevel().equals(level);
                })
                .collect(Collectors.toCollection(() -> teleports));
        if (teleports.isEmpty()) return null;
        List<List<Coords>> iPolygons = item.getActualCollisionPolygons();
        if (iPolygons.isEmpty() && !(item instanceof Creature)) return null;

        double left = item.getCollisionLeft(iPolygons, nextPos);
        double right = item.getCollisionRight(iPolygons, nextPos);
        double top = item.getCollisionTop(iPolygons, nextPos);
        double bottom = item.getCollisionBottom(iPolygons, nextPos);

        for (Teleport t : teleports) {
            if (t == item) continue;
            List<List<Coords>> oPolygons = t.getTeleportCollisionPolygons();
            Teleport collision = getCollision(left, right, top, bottom, nextPos, item, iPolygons, t, oPolygons);
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
            if (getCreatureObstacleCollision(nextPos, cr.getSize(), oPolygons, o.getPos())) return o;

        } else if (o instanceof Creature && !(i instanceof Creature)) {

            Creature crO = (Creature) o;
            if (getObstacleCreatureCollision(nextPos, crO, iPolygons)) return o;

        } else if (o instanceof Creature) {

            Creature cr = (Creature) i;
            Creature crO = (Creature) o;
            if (getCreatureCreatureCollision(nextPos, cr, crO)) return o;

        } else {

            if (getObstacleObstacleCollision(nextPos, iPolygons, i.getPos(), oPolygons)) return o;

        }
        return null;
    }

    private boolean getObstacleObstacleCollision(Coords nextPos, List iPolygons, Coords oPos, List oPolygons) {
        boolean collides = Coords.polygonsIntersect(nextPos, iPolygons, oPos, oPolygons);
        if (collides) {
            System.out.println("Obstacle collides obstacle");
        }
        return collides;
    }

    private boolean getCreatureCreatureCollision(Coords nextPos, Creature cr, Creature crO) {
        boolean collides = Coords.ovalsIntersect(nextPos, cr.getSize(), crO.getCenter(), crO.getSize());
        if (collides) {
            System.out.println("Creature collides creature");
        }
        return collides;
    }

    public boolean getObstacleCreatureCollision(Coords nextPos, Creature cr, List<List<Coords>> iPolygons) {
        for (List<Coords> polygon : iPolygons) {
            List<Coords> lostRef = Coords.looseCoordsReferences1(polygon);
            Coords.translateCoords(lostRef, nextPos.x, nextPos.y);

            boolean ovalIntersectsPolygon = Coords.ovalIntersectsPolygon(cr.getCenter(), cr.getSize(), lostRef);
            if (ovalIntersectsPolygon) {
                System.out.println("Obstacle collides creature");
                return true;
            }
        }
        return false;
    }

    public boolean getCreatureObstacleCollision(Coords nextPos, CreatureSize s, List<List<Coords>> oPolygons, Coords oPos) {
        for (List<Coords> polygon : oPolygons) {
            List<Coords> lostRef = Coords.looseCoordsReferences1(polygon);
            Coords.translateCoords(lostRef, oPos.x, oPos.y);

            boolean ovalIntersectsPolygon = Coords.ovalIntersectsPolygon(nextPos, s, lostRef);
            if (ovalIntersectsPolygon) {
                System.out.println("Creature collides obstacle");
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
                .filter(pi -> pi.getLevel().equals(cr.getLevel()))
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

    public List<Creature> getControllablesWithinRectangle(Coords first, Coords second, Location location) {
        double left;
        double right;
        if (first.x <= second.x) {
            left = first.x;
            right = second.x;
        } else {
            left = second.x;
            right = first.x;
        }
        double top;
        double bottom;
        if (first.y <= second.y) {
            top = first.y;
            bottom = second.y;
        } else {
            top = second.y;
            bottom = first.y;
        }
        creatures.clear();
        location.getItems().get().stream()
                .filter(pi -> pi instanceof Creature)
                .map(pi -> (Creature) pi)
                .filter(c -> c.getControl().equals(CreatureControl.CONTROLLABLE)
                        || c.getControl().equals(CreatureControl.CONTROL))
                .filter(c -> {
                    Coords centerBottom = c.getCenter();
                    double x = centerBottom.x;
                    double y = centerBottom.y;
                    return x > left && x < right
                            && y > top && y < bottom;
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
