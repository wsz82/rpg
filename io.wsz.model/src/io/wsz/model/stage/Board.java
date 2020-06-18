package io.wsz.model.stage;

import io.wsz.model.Controller;
import io.wsz.model.item.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static io.wsz.model.Constants.METER;
import static io.wsz.model.stage.Comparator.Comparison;
import static io.wsz.model.stage.Comparator.Comparison.*;
import static io.wsz.model.stage.Comparator.compare;
import static java.lang.Math.pow;

public class Board {
    private static Board singleton;
    private final Coords boardPos = new Coords(0, 0);

    public static Board get() {
        if (singleton == null) {
            singleton = new Board();
        }
        return singleton;
    }

    private Board(){}

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
        final double i1_imgWidth = i1_img.getWidth() / METER;
        final double i1_imgHeight = i1_img.getHeight() / METER;
        final LinkedList<Coords> i1_list = new LinkedList<>();
        if (!i1_cl.isEmpty()) {
            looseCoordsReference(i1_cl, i1_list);
            translateCoords(i1_list, i1_posX, i1_posY);
        } else {
            Coords SW = new Coords(i1_posX, i1_posY+i1_imgHeight);
            Coords SE = new Coords(i1_posX+i1_imgWidth, i1_posY+i1_imgHeight);
            i1_list.add(SW);
            i1_list.add(SE);
        }

        final List<Coords> i2_cl = i2.getCoverLine();
        final Coords i2_pos = i2.getPos();
        final Image i2_img = i2.getImage();
        final double i2_posX = i2_pos.x;
        final double i2_posY = i2_pos.y;
        final double i2_imgWidth = i2_img.getWidth() / METER;
        final double i2_imgHeight = i2_img.getHeight() / METER;
        final LinkedList<Coords> i2_list = new LinkedList<>();
        if (!i2_cl.isEmpty()) {
            looseCoordsReference(i2_cl, i2_list);
            translateCoords(i2_list, i2_posX, i2_posY);
            addLeftAndRightPoints(i2_list, i2_posX, i2_imgWidth);
        } else {
            Coords SW = new Coords(i2_posX, i2_posY+i2_imgHeight);
            Coords SE = new Coords(i2_posX+i2_imgWidth, i2_posY+i2_imgHeight);
            i2_list.add(SW);
            i2_list.add(SE);
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
        List<ItemType> typesList = new ArrayList<>(1);
        Collections.addAll(typesList, types);
        List<PosItem> items = new ArrayList<>(Controller.get().getCurrentLocation().getItems());
        items = items.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> typesList.contains(pi.getType()))
                .filter(pi -> {
                    int level = pi.getLevel();
                    int actualLevel = Controller.get().getCurrentLayer().getLevel();
                    if (includeLevelsBelow) {
                        return level <= actualLevel;
                    } else {
                        return level == actualLevel;
                    }
                })
                .collect(Collectors.toList());
        if (items.isEmpty()) {
            return null;
        }
        sortItems(items);
        Collections.reverse(items);

        for (PosItem pi : items) {
            for (Coords pos : poss) {
                int x = (int) (pos.x * METER);
                int y = (int) (pos.y * METER);

                int cX = (int) (pi.getPos().x * METER);
                int cWidth = (int) pi.getImage().getWidth();
                boolean fitX = x >= cX && x <= cX + cWidth;
                if (!fitX) {
                    continue;
                }

                int cY = (int) (pi.getPos().y * METER);
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
                    color = img.getPixelReader().getColor(imgX, imgY);    //TODO fix index ot of bounds exception
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
            Coords left = new Coords(i2_posX, first.y);
            linkedCoords.addFirst(left);
        }

        Coords last = linkedCoords.getLast();
        double rightX = i2_posX + i2_imgWidth;
        if (last.x != rightX) {
            Coords right = new Coords(rightX, last.y);
            linkedCoords.addLast(right);
        }
    }

    private void translateCoords(List<Coords> list, double i2_posX, double i2_posY) {
        list.forEach(c -> {
                    c.x = i2_posX + c.x;
                    c.y = i2_posY + c.y;
                });
    }

    public Coords getFreePosCreature(Coords[] corners, Creature cr) {
        List<Coords> coordsList = new ArrayList<>();
        Collections.addAll(coordsList, corners);
        Collections.shuffle(coordsList);
        for (Coords pos : coordsList) {
            int x = (int) (pos.x * METER);
            int y = (int) (pos.y * METER);
            Coords cPos = cr.getPos();
            Image img = cr.getImage();

            int cX = (int) (cPos.x * METER);
            int cWidth = (int) img.getWidth();
            boolean fitX = x >= cX && x <= cX + cWidth;
            if (!fitX) {
                return pos;
            }

            int cY = (int) (cPos.y * METER);
            int cYbottom = cY + (int) img.getHeight();
            int cSizeHeight = (int) (cr.getSize().getHeight()/2 * METER);
            boolean fitY = y <= cYbottom && y >= cYbottom + cSizeHeight;
            if (!fitY) {
                return pos;
            }

            int imgX = x - cX;
            int imgY = y - cY;
            Color color;
            try {
                color = img.getPixelReader().getColor(imgX, imgY);
            } catch (IndexOutOfBoundsException e) {
                return pos;
            }
            boolean isPixelTransparent = color.equals(Color.TRANSPARENT);
            if (isPixelTransparent) {
                return pos;
            }
        }
        return null;
    }

    public List<Creature> getCreatures() {
        return Controller.get().getCurrentLocation().getItems().stream()
                .filter(pi -> pi.getLevel() == Controller.get().getCurrentLayer().getLevel())
                .filter(pi -> pi.getType().equals(ItemType.CREATURE))
                .map(pi -> (Creature) pi)
                .collect(Collectors.toList());
    }

    public List<Creature> getControlledCreatures() {
        return Controller.get().getCurrentLocation().getItems().stream()
                .filter(pi -> pi.getType().equals(ItemType.CREATURE))
                .filter(pi -> {
                    Creature cr = (Creature) pi;
                    return cr.getControl().equals(CreatureControl.CONTROL);
                })
                .map(pi -> (Creature) pi)
                .collect(Collectors.toList());
    }

    public Creature getCornersCreature(Coords[] corners, Creature cr) {
        List<Creature> creatures = getCreatures();
        if (creatures.isEmpty()) {
            return null;
        }

        for (Creature c : creatures) {
            if (cr.equals(c)) {
                continue;
            }
            for (Coords pos : corners) {
                double x = pos.x;
                double y = pos.y;

                Coords cPos = c.posToCenter();
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
        List<PosItem> items = new ArrayList<>(Controller.get().getCurrentLocation().getItems());
        items = items.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> pi.getCollisionPolygons() != null)
                .filter(pi -> pi.getLevel() == Controller.get().getCurrentLayer().getLevel())
                .collect(Collectors.toList());
        if (items.isEmpty()) {
            return null;
        }

        for (PosItem pi : items) {
            for (Coords pos : poss) {
                double x = pos.x;
                double y = pos.y;

                final Image img = pi.getImage();
                final Coords cPos = pi.getPos();
                double cX = cPos.x;
                double cWidth = img.getWidth() / METER;
                boolean fitX = x >= cX && x <= cX + cWidth;
                if (!fitX) {
                    continue;
                }

                double cY = cPos.y;
                double cHeight = img.getHeight() / METER;
                boolean fitY = y >= cY && y <= cY + cHeight;
                if (!fitY) {
                    continue;
                }

                final List<List<Coords>> cp = pi.getCollisionPolygons();
                for (final List<Coords> polygon : cp) {
                    List<Coords> tc = new ArrayList<>();
                    looseCoordsReference(polygon, tc);
                    translateCoords(tc, cX, cY);


                    double maxObstacleX = tc.stream()
                            .mapToDouble(p -> p.x)
                            .max()
                            .getAsDouble();
                    double minObstacleX = tc.stream()
                            .mapToDouble(p -> p.x)
                            .min()
                            .getAsDouble();
                    boolean fitObstacleX = x >= minObstacleX && x <= maxObstacleX;
                    if (!fitObstacleX) {
                        continue;
                    }

                    double maxObstacleY = tc.stream()
                            .mapToDouble(p -> p.y)
                            .max()
                            .getAsDouble();
                    double minObstacleY = tc.stream()
                            .mapToDouble(p -> p.y)
                            .min()
                            .getAsDouble();
                    boolean fitObstacleY = y >= minObstacleY && y <= maxObstacleY;
                    if (!fitObstacleY) {
                        continue;
                    }

                    boolean isInsidePolygon = pos.isInsidePolygon(tc, maxObstacleX);
                    if (isInsidePolygon) {
                        return pi;
                    }
                }
            }
        }
        return null;
    }

    public List<Equipment> getEquipmentWithinRange(Coords[] poss, Creature cr) {
        List<PosItem> items = new ArrayList<>(Controller.get().getCurrentLocation().getItems());
        List<Equipment> equipment = items.stream()
                .filter(PosItem::getVisible)
                .filter(pi -> pi.getLevel() == cr.getLevel())
                .filter(pi -> pi instanceof Equipment)
                .map(pi -> (Equipment) pi)
                .collect(Collectors.toList());
        if (items.isEmpty()) {
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
}
