package io.wsz.model.stage;

import io.wsz.model.Controller;
import io.wsz.model.item.*;
import io.wsz.model.location.CurrentLocation;
import io.wsz.model.sizes.Sizes;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static io.wsz.model.stage.Comparator.Comparison;
import static io.wsz.model.stage.Comparator.Comparison.*;
import static io.wsz.model.stage.Comparator.compare;
import static java.lang.Math.*;

public class Board {
    private static Board singleton;
    private final Coords boardPos = new Coords(0, 0);
    private final List<PosItem> allItems = new ArrayList<>(0);
    private final List<PosItem> items = new ArrayList<>(0);
    private final List<Equipment> equipment = new ArrayList<>(0);
    private final List<Creature> creatures = new ArrayList<>(0);
    private final List<Coords> itemCoords = new ArrayList<>(0);
    private final LinkedList<Coords> i1_list = new LinkedList<>();
    private final LinkedList<Coords> i2_list = new LinkedList<>();
    private final Coords i1_left = new Coords();
    private final Coords i1_right = new Coords();
    private final Coords i2_left = new Coords();
    private final Coords i2_right = new Coords();
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

        Graph graph = new Graph(new ArrayList<>(0));
        List<Node> nodes = graph.getNodes();

        for (PosItem pi : items) {
            Node newNode = new Node(pi, new ArrayList<>(0), new ArrayList<>(0));

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

        List<PosItem> sortedItems = new ArrayList<>(1);

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

    public Comparison isCovered(PosItem i1, PosItem i2) {
        final List<Coords> i1_cl = i1.getCoverLine();
        final Coords i1_pos = i1.getPos();
        final Image i1_img = i1.getImage();
        final double i1_posX = i1_pos.x;
        final double i1_posY = i1_pos.y;
        final double i1_imgWidth = i1_img.getWidth() / Sizes.getMeter();
        final double i1_imgHeight = i1_img.getHeight() / Sizes.getMeter();
        i1_list.clear();
        if (!i1_cl.isEmpty()) {
            looseCoordsReference(i1_cl, i1_list);
            translateCoords(i1_list, i1_posX, i1_posY);
        } else {
            i1_left.x = i1_posX;
            i1_left.y = i1_posY + i1_imgHeight;
            i1_right.x = i1_posX + i1_imgWidth;
            i1_right.y = i1_posY+i1_imgHeight;

            i1_list.add(i1_left);
            i1_list.add(i1_right);
        }

        final List<Coords> i2_cl = i2.getCoverLine();
        final Coords i2_pos = i2.getPos();
        final Image i2_img = i2.getImage();
        final double i2_posX = i2_pos.x;
        final double i2_posY = i2_pos.y;
        final double i2_imgWidth = i2_img.getWidth() / Sizes.getMeter();
        final double i2_imgHeight = i2_img.getHeight() / Sizes.getMeter();
        i2_list.clear();
        if (!i2_cl.isEmpty()) {
            looseCoordsReference(i2_cl, i2_list);
            translateCoords(i2_list, i2_posX, i2_posY);
            addLeftAndRightPoints(i2_list, i2_posX, i2_imgWidth);
        } else {
            i2_left.x = i2_posX;
            i2_left.y = i2_posY + i2_imgHeight;
            i2_right.x = i2_posX + i2_imgWidth;
            i2_right.y = i2_posY + i2_imgHeight;

            i2_list.add(i2_left);
            i2_list.add(i2_right);
        }

        return isCoverLineAbove(i1_list, i2_list);
    }

    private Comparison isCoverLineAbove(LinkedList<Coords> i1_list, LinkedList<Coords> i2_list) {
        for (int i = 0; i < i2_list.size() - 1; i++) {
            Coords first = i2_list.get(i);
            double x1 = first.x;
            double y1 = first.y;
            Coords second = i2_list.get(i+1);
            double x2 = second.x;
            double y2 = second.y;

            if (x1 == x2) {
                continue;
            }

            for (Coords compared : i1_list) {
                double x = compared.x;
                if (x == x1) {
                    continue;
                }
                boolean xIsBetweenLine = x >= x1 && x <= x2;
                if (!xIsBetweenLine) {
                    continue;
                }
                double y = compared.y;
                double func = (x * y1 - x * y2 + x1 * y2 - x2 * y1) / (x1 - x2);
                if (y > func) {
                    return GREAT;
                } else {
                    return LESS;
                }
            }
        }
        return INCOMPARABLE;
    }

    public PosItem lookForContent(Coords[] poss, ItemType[] types, boolean includeLevelsBelow) {
        allItems.clear();
        allItems.addAll(Controller.get().getCurrentLocation().getItems());
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
                    int actualLevel = Controller.get().getCurrentLayer().getLevel();
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

    private void looseCoordsReference(List<Coords> from, List<Coords> to) {
        for (Coords pos : from) {
            Coords newPos = pos.clone();
            to.add(newPos);
        }
    }

    private void addLeftAndRightPoints(LinkedList<Coords> linkedCoords, double i2_posX, double i2_imgWidth) {
        Coords first = linkedCoords.getFirst();
        if (first.x != i2_posX) {
            i2_left.x = i2_posX;
            i2_left.y = first.y;
            linkedCoords.addFirst(i2_left);
        }

        Coords last = linkedCoords.getLast();
        double rightX = i2_posX + i2_imgWidth;
        if (last.x != rightX) {
            i2_right.x = rightX;
            i2_right.y = last.y;
            linkedCoords.addLast(i2_right);
        }
    }

    private void translateCoords(List<Coords> list, double i2_posX, double i2_posY) {
        list.forEach(c -> {
                    c.x = i2_posX + c.x;
                    c.y = i2_posY + c.y;
                });
    }

    public List<Creature> getCreatures() {
        creatures.clear();
        Controller.get().getCurrentLocation().getItems().stream()
                .filter(pi -> {
                    int level = Controller.get().getCurrentLayer().getLevel();
                    return pi.getLevel().equals(level);
                })
                .filter(pi -> pi.getType().equals(ItemType.CREATURE))
                .map(pi -> (Creature) pi)
                .collect(Collectors.toCollection(() -> creatures));
        return creatures;
    }

    public List<Creature> getControlledCreatures() {
        creatures.clear();
        Controller.get().getCurrentLocation().getItems().stream()
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
        List<Creature> creatures = getCreatures();
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

    public PosItem lookForObstacle(Coords[] poss) {
        items.clear();
        Controller.get().getCurrentLocation().getItems().stream()
                .filter(PosItem::getVisible)
                .filter(pi -> pi.getCollisionPolygons() != null)
                .filter(pi -> {
                    int level = Controller.get().getCurrentLayer().getLevel();
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
                    looseCoordsReference(polygon, itemCoords);
                    translateCoords(itemCoords, cX, cY);

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
        items.clear();
        items.addAll(Controller.get().getCurrentLocation().getItems());
        equipment.clear();
        items.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> pi.getLevel().equals(cr.getLevel()))
                .filter(pi -> pi instanceof Equipment)
                .map(pi -> (Equipment) pi)
                .collect(Collectors.toCollection(() -> equipment));
        if (equipment.isEmpty()) {
            return null;
        }
        sortItems(items);
        Collections.reverse(items);

        List<Equipment> output = new ArrayList<>(0);
        Double range = cr.getRange();
        for (Equipment e : equipment) {
            Coords eCenter = e.getCenter();
            for (Coords pos : poss) {
                if (pointWithinRange(pos, range, eCenter)) {
                    if (!output.contains(e)) {
                        output.add(e);
                    }
                }
            }
        }
        return output;
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

                CurrentLocation cl = Controller.get().getCurrentLocation();
                if (x < 0 || x > cl.getWidth()) {
                    continue;
                }
                if (y < 0 || y > cl.getHeight()) {
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

    public List<Creature> getControllableCreatures() {
        return Controller.get().getCurrentLocation().getItems().stream()
                .filter(pi -> pi.getType().equals(ItemType.CREATURE))
                .filter(pi -> {
                    Creature cr = (Creature) pi;
                    return cr.getControl().equals(CreatureControl.CONTROLLABLE);
                })
                .map(pi -> (Creature) pi)
                .collect(Collectors.toList());
    }

    public void looseCreaturesControl() {
        List<Creature> creatures = getControlledCreatures();
        Controller.get().getCreaturesToLooseControl().addAll(creatures);
    }
}
