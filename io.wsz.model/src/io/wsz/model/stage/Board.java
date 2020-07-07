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

import static java.lang.Math.*;

public class Board {
    private static Board singleton;
    private final GraphSorter<PosItem> posItemGraphSorter = new GraphSorter<>();
    private final GraphSorter<Equipment> equipmentGraphSorter = new GraphSorter<>();
    private final Coords boardPos = new Coords(0, 0, null);
    private final List<PosItem> allItems = new ArrayList<>(0);
    private final List<PosItem> items = new ArrayList<>(0);
    private final List<Equipment> equipment = new ArrayList<>(0);
    private final List<Equipment> equipmentResult = new ArrayList<>(0);
    private final List<Creature> creatures = new ArrayList<>(0);
    private final Coords resultCoords = new Coords();

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

    public Creature getCornersCreature(Creature cr, Coords nextPos) {
        Location location = cr.getPos().getLocation();
        List<Creature> creatures = getCreatures(location);
        if (creatures.isEmpty()) return null;

        for (Creature c : creatures) {
            if (cr == c) continue;
            Coords cPos = c.getCenterBottomPos();
            if (Coords.ovalsIntersect(nextPos, cr.getSize(), cPos, c.getSize())) return c;
        }
        return null;
    }

    public boolean pointWithinEllipse(Coords point, Coords center, double width, double height) {
        double x = point.x;
        double y = point.y;
        double h = center.x;
        double k = center.y;
        double rx = width /2;
        double ry = height /2;

        double eq = pow(x - h, 2)/pow(rx, 2) + pow(y - k, 2)/pow(ry, 2);
        return eq <= 1;
    }

    public PosItem lookForObstacle(Coords[] poss, Location location) {
        if (location == null) {
            return null;
        }
        items.clear();
        location.getItems().get().stream()
                .filter(PosItem::getVisible)
                .filter(pi -> pi.getCollisionPolygons() != null)
                .filter(pi -> {
                    int level = Controller.get().getCurrentLayer().getLevel(); // TODO
                    return pi.getLevel().equals(level);
                })
                .collect(Collectors.toCollection(() -> items));
        if (items.isEmpty()) {
            return null;
        }

        for (PosItem pi : items) {
            final Image img = pi.getImage();
            final Coords cPos = pi.getPos();

            for (Coords pos : poss) {
                double x = pos.x;
                double y = pos.y;

                double cX = cPos.x;
                double cWidth = img.getWidth() / Sizes.getMeter();
                boolean fitX = x >= cX && x <= cX + cWidth;
                if (!fitX) {
                    continue;
                }

                double cY = cPos.y;
                double cHeight = img.getHeight() / Sizes.getMeter();
                boolean fitY = y >= cY && y <= cY + cHeight;
                if (!fitY) {
                    continue;
                }

                final List<List<Coords>> cp = pi.getCollisionPolygons();
                for (List<Coords> polygon : cp) {
                    List<Coords> lostRef = Coords.looseCoordsReferences(polygon);
                    Coords.translateCoords(lostRef, cX, cY);

                    double maxObstacleX = lostRef.stream()
                            .mapToDouble(p -> p.x)
                            .max()
                            .getAsDouble();
                    double minObstacleX = lostRef.stream()
                            .mapToDouble(p -> p.x)
                            .min()
                            .getAsDouble();
                    boolean fitObstacleX = x >= minObstacleX && x <= maxObstacleX;
                    if (!fitObstacleX) {
                        continue;
                    }

                    double maxObstacleY = lostRef.stream()
                            .mapToDouble(p -> p.y)
                            .max()
                            .getAsDouble();
                    double minObstacleY = lostRef.stream()
                            .mapToDouble(p -> p.y)
                            .min()
                            .getAsDouble();
                    boolean fitObstacleY = y >= minObstacleY && y <= maxObstacleY;
                    if (!fitObstacleY) {
                        continue;
                    }

                    boolean isInsidePolygon = pos.isInsidePolygon(lostRef, maxObstacleX);
                    if (isInsidePolygon) {
                        return pi;
                    }
                }
            }
        }
        return null;
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
        if (equipment.isEmpty()) {
            return equipmentResult;
        }

        double range = cr.getRange();
        double width = cr.getSize().getWidth() + 2*range;
        double height = cr.getSize().getHeight() + 2*range;
        Coords point = cr.getCenterBottomPos();
        for (Equipment e : equipment) {
            Coords center = e.getCenter();
            if (pointWithinEllipse(point, center, width, height)) {
                equipmentResult.add(e);
            }
        }
        sortEquipment(equipmentResult);
        return equipmentResult;
    }

    public Coords getFreePosAround(Creature cr) {
        Coords pos = cr.getCenterBottomPos();

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
                if (x < 0 || x > location.getWidth()) {
                    continue;
                }
                if (y < 0 || y > location.getHeight()) {
                    continue;
                }

                resultCoords.x = x;
                resultCoords.y = y;
                PosItem pi = cr.getCollision(resultCoords);

                if (pi == null) {
                    return resultCoords;
                }
                angle = (int) (angleScope * j);
            }
        }
        return pos;
    }

    public List<Creature> getControllableCreatures(Location location) {
        creatures.clear();
        location.getItems().get().stream()
                .filter(pi -> pi.getType().equals(ItemType.CREATURE))
                .filter(pi -> {
                    Creature cr = (Creature) pi;
                    return cr.getControl().equals(CreatureControl.CONTROLLABLE);
                })
                .map(pi -> (Creature) pi)
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
                .filter(pi -> pi.getType().equals(ItemType.CREATURE))
                .map(pi -> (Creature) pi)
                .filter(c -> c.getControl().equals(CreatureControl.CONTROLLABLE)
                        || c.getControl().equals(CreatureControl.CONTROL))
                .filter(c -> {
                    Coords centerBottom = c.getCenterBottomPos();
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
