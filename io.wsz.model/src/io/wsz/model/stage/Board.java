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

import static io.wsz.model.stage.Comparator.Comparison;
import static io.wsz.model.stage.Comparator.Comparison.GREAT;
import static io.wsz.model.stage.Comparator.Comparison.LESS;
import static io.wsz.model.stage.Comparator.compare;
import static java.lang.Math.*;

public class Board {
    private static Board singleton;
    private final Coords boardPos = new Coords(0, 0, null);
    private final Graph graph = new Graph(new ArrayList<>(0));
    private final List<PosItem> allItems = new ArrayList<>(0);
    private final List<PosItem> sortedItems = new ArrayList<>(0);
    private final List<PosItem> items = new ArrayList<>(0);
    private final List<Equipment> equipment = new ArrayList<>(0);
    private final List<Equipment> equipmentResult = new ArrayList<>(0);
    private final List<Creature> creatures = new ArrayList<>(0);
    private final List<Coords> itemCoords = new ArrayList<>(0);
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

    public void sortItems(List<PosItem> items) {

        if (items == null || items.isEmpty()) {
            return;
        }

        List<Node> nodes = graph.getNodes();
        nodes.clear();

        for (PosItem pi : items) {
            Node newNode = new Node(pi);

            for (Node n : nodes) {
                Comparison result = compare(pi, n);

                if (result.equals(GREAT)) {
                    n.getGreater().add(newNode);
                    newNode.getLesser().add(n);
                } else if (result.equals(LESS)) {
                    n.getLesser().add(newNode);
                    newNode.getGreater().add(n);
                }
            }
            nodes.add(newNode);
        }

        sortedItems.clear();
        if (!nodes.isEmpty()) {
            Node n = nodes.get(0);
            int size  = nodes.size();
            while (size > 0) {
                if (n == null) {
                    n = nodes.get(0);
                }
                Node min = findMin(n);

                sortedItems.add(min.getItem());
                nodes.remove(min);

                size = size - 1;

                n = findFirstNotEmptyGreater(min);
            }

            items.clear();
            items.addAll(sortedItems);
        }
    }

    private Node findFirstNotEmptyGreater(Node last) {
        List<Node> greater = last.getGreater();
        return greater.isEmpty() ? null : greater.get(0);
    }

    private Node findMin(Node n) {
        List<Node> lesser = n.getLesser();
        while (!lesser.isEmpty()) {
            n = lesser.get(0);
            lesser = n.getLesser();
        }
        for (Node greater : n.getGreater()) {
            greater.getLesser().remove(n);
        }
        return n;
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
        sortItems(items);
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

    public Creature getCornersCreature(Coords[] corners, Creature cr) {
        Location location = cr.getPos().getLocation();
        List<Creature> creatures = getCreatures(location);
        if (creatures.isEmpty()) {
            return null;
        }

        for (Creature c : creatures) {
            if (cr == c) {
                continue;
            }
            for (Coords pos : corners) {
                double x = pos.x;
                double y = pos.y;

                Coords cPos = c.getCenterBottomPos();
                double h = cPos.x;
                double k = cPos.y;
                double rx = c.getSize().getWidth()/2;
                double ry = c.getSize().getHeight()/2;

                double eq = pow(x - h, 2)/pow(rx, 2) + pow(y - k, 2)/pow(ry, 2);
                if (eq <= 1) {
                    return c;
                }
            }
        }
        return null;
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
                    itemCoords.clear();
                    Coords.looseCoordsReference(polygon, itemCoords);
                    Coords.translateCoords(itemCoords, cX, cY);

                    double maxObstacleX = itemCoords.stream()
                            .mapToDouble(p -> p.x)
                            .max()
                            .getAsDouble();
                    double minObstacleX = itemCoords.stream()
                            .mapToDouble(p -> p.x)
                            .min()
                            .getAsDouble();
                    boolean fitObstacleX = x >= minObstacleX && x <= maxObstacleX;
                    if (!fitObstacleX) {
                        continue;
                    }

                    double maxObstacleY = itemCoords.stream()
                            .mapToDouble(p -> p.y)
                            .max()
                            .getAsDouble();
                    double minObstacleY = itemCoords.stream()
                            .mapToDouble(p -> p.y)
                            .min()
                            .getAsDouble();
                    boolean fitObstacleY = y >= minObstacleY && y <= maxObstacleY;
                    if (!fitObstacleY) {
                        continue;
                    }

                    boolean isInsidePolygon = pos.isInsidePolygon(itemCoords, maxObstacleX);
                    if (isInsidePolygon) {
                        return pi;
                    }
                }
            }
        }
        return null;
    }

    public List<Equipment> getEquipmentWithinRange(Coords[] poss, Creature cr) {
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
        sortItems(items);
        Collections.reverse(items);

        Double range = cr.getRange();
        for (Equipment e : equipment) {
            Coords eCenter = e.getCenter();
            for (Coords pos : poss) {
                if (pointWithinRange(pos, range, eCenter)) {
                    if (!equipmentResult.contains(e)) {
                        equipmentResult.add(e);
                    }
                }
            }
        }
        return equipmentResult;
    }

    private boolean pointWithinRange(Coords pos, Double range, Coords eCenter) {
        double eX = eCenter.x;
        double eY = eCenter.y;
        double x = pos.x;
        double y = pos.y;

        return pow(eX - x, 2) + pow(eY - y, 2) < pow(range, 2);
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
}
