package io.wsz.model.stage;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.item.Creature;
import io.wsz.model.item.CreatureControl;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
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
import static java.lang.Math.pow;

public class Board {
    private static Board singleton;

    public static Board get() {
        if (singleton == null) {
            singleton = new Board();
        }
        return singleton;
    }

    private Board(){}

    public void sortContents(List<Content> contents) {

        if (contents == null || contents.isEmpty()) {
            return;
        }

        Graph graph = new Graph(new ArrayList<>(0));
        List<Node> nodes = graph.getNodes();

        for (Content c : contents) {
            Node newNode = new Node(c, new ArrayList<>(0), new ArrayList<>(0));

            for (Node n : nodes) {
                Comparison result = compare(c, n);

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

        List<Content> sortedContent = new ArrayList<>(1);

        if (!nodes.isEmpty()) {
            Node n = nodes.get(0);
            int size  = nodes.size();
            while (size > 0) {
                if (n == null) {
                    n = nodes.get(0);
                }
                Node min = findMin(n);

                sortedContent.add(min.getContent());
                nodes.remove(min);

                size = size - 1;

                n = findFirstNotEmptyGreater(min);
            }

            contents.clear();
            contents.addAll(sortedContent);
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
        final int i1_posX = i1_pos.x;
        final int i1_posY = i1_pos.y;
        final int i1_imgWidth = (int) i1_img.getWidth();
        final int i1_imgHeight = (int) i1_img.getHeight();
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
        final int i2_posX = i2_pos.x;
        final int i2_posY = i2_pos.y;
        final int i2_imgWidth = (int) i2_img.getWidth();
        final int i2_imgHeight = (int) i2_img.getHeight();
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
            int x1 = first.x;
            int y1 = first.y;
            Coords second = i2_list.get(i+1);
            int x2 = second.x;
            int y2 = second.y;

            if (x1 == x2) {
                continue;
            }

            for (Coords compared : i1_list) {
                int x = compared.x;
                if (x == x1) {
                    continue;
                }
                boolean xIsBetweenLine = x >= x1 && x <= x2;
                if (!xIsBetweenLine) {
                    continue;
                }
                int y = compared.y;
                double func = (x * y1 - x * y2 + x1 * y2 - x2 * y1) / (double) (x1 - x2);
                if (y > func) {
                    return GREAT;
                } else {
                    return LESS;
                }
            }
        }
        return INCOMPARABLE;
    }

    public Content lookForContent(Coords[] poss, ItemType[] types, boolean includeLevelsBelow) {
        List<ItemType> typesList = new ArrayList<>(1);
        Collections.addAll(typesList, types);
        List<Content> contents = new ArrayList<>(Controller.get().getCurrentLocation().getContent());
        contents = contents.stream()
                .filter(c -> typesList.contains(c.getItem().getType()))
                .filter(c -> c.isVisible())
                .filter(c -> {
                    int level = c.getItem().getLevel();
                    int actualLevel = Controller.get().getCurrentLayer().getLevel();
                    if (includeLevelsBelow) {
                        return level <= actualLevel;
                    } else {
                        return level == actualLevel;
                    }
                })
                .collect(Collectors.toList());
        if (contents.isEmpty()) {
            return null;
        }
        sortContents(contents);
        Collections.reverse(contents);

        for (Content c : contents) {
            for (Coords pos : poss) {
                int x = pos.x;
                int y = pos.y;

                int cX = c.getItem().getPos().x;
                int cWidth = (int) c.getItem().getImage().getWidth();
                boolean fitX = x >= cX && x <= cX + cWidth;
                if (!fitX) {
                    continue;
                }

                int cY = c.getItem().getPos().y;
                int cHeight = (int) c.getItem().getImage().getHeight();
                boolean fitY = y >= cY && y <= cY + cHeight;
                if (!fitY) {
                    continue;
                }

                Image img = c.getItem().getImage();
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
                return c;
            }
        }
        return null;
    }

    private void looseCoordsReference(List<Coords> from, List<Coords> to) {
        for (Coords pos : from) {
            Coords newPos = new Coords(pos.x, pos.y);
            to.add(newPos);
        }
    }

    private void addLeftAndRightPoints(LinkedList<Coords> linkedCoords, int i2_posX, int i2_imgWidth) {
        Coords first = linkedCoords.getFirst();
        if (first.x != i2_posX) {
            Coords left = new Coords(i2_posX, first.y);
            linkedCoords.addFirst(left);
        }

        Coords last = linkedCoords.getLast();
        int rightX = i2_posX + i2_imgWidth;
        if (last.x != rightX) {
            Coords right = new Coords(rightX, last.y);
            linkedCoords.addLast(right);
        }
    }

    private void translateCoords(List<Coords> list, int i2_posX, int i2_posY) {
        list.forEach(c -> {
                    c.x = i2_posX + c.x;
                    c.y = i2_posY + c.y;
                });
    }

    public Coords getFreePos(Coords[] corners, Content c) {
        List<Coords> coordsList = new ArrayList<>();
        Collections.addAll(coordsList, corners);
        Collections.shuffle(coordsList);
        for (Coords pos : coordsList) {
            int x = pos.x;
            int y = pos.y;
            PosItem i = c.getItem();
            Coords oPos = i.getPos();
            Image img = i.getImage();

            int cX = oPos.x;
            int cWidth = (int) img.getWidth();
            boolean fitX = x >= cX && x <= cX + cWidth;
            if (!fitX) {
                return pos;
            }

            int cY = oPos.y;
            int cHeight = (int) img.getHeight();
            boolean fitY = y >= cY && y <= cY + cHeight;
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

    public Coords getFreePosCreature(Coords[] corners, Creature cr) {
        List<Coords> coordsList = new ArrayList<>();
        Collections.addAll(coordsList, corners);
        Collections.shuffle(coordsList);
        for (Coords pos : coordsList) {
            int x = pos.x;
            int y = pos.y;
            Coords cPos = cr.getPos();
            Image img = cr.getImage();

            int cX = cPos.x;
            int cWidth = (int) img.getWidth();
            boolean fitX = x >= cX && x <= cX + cWidth;
            if (!fitX) {
                return pos;
            }

            int cY = cPos.y;
            int cYbottom = cY + (int) img.getHeight();
            int cSizeHeight = cr.getSize().getHeight()/2;
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
        return Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> c.getItem().getLevel() == Controller.get().getCurrentLayer().getLevel())
                .filter(c -> c.getItem().getType().equals(ItemType.CREATURE))
                .map(c -> (Creature) c.getItem())
                .collect(Collectors.toList());
    }

    public List<Creature> getControlledCreatures() {
        return Controller.get().getCurrentLocation().getContent().stream()
                .filter(c -> c.getItem().getType().equals(ItemType.CREATURE))
                .filter(c -> {
                    Creature cr = (Creature) c.getItem();
                    return cr.getControl().equals(CreatureControl.CONTROL);
                })
                .map(c -> (Creature) c.getItem())
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
                int x = pos.x;
                int y = pos.y;

                Coords cPos = c.posToCenter();
                int h = cPos.x;
                int k = cPos.y;
                int rx = c.getSize().getWidth()/2;
                int ry = c.getSize().getHeight()/2;

                double eq = pow(x - h, 2)/pow(rx, 2) + pow(y - k, 2)/pow(ry, 2);
                if (eq <= 1) {
                    return c;
                }
            }
        }
        return null;
    }

    public Content lookForObstacle(Coords[] poss) {
        List<Content> contents = new ArrayList<>(Controller.get().getCurrentLocation().getContent());
        contents = contents.stream()
                .filter(c -> c.getItem().getCollisionPolygons() != null)
                .filter(Content::isVisible)
                .filter(c -> c.getItem().getLevel() == Controller.get().getCurrentLayer().getLevel())
                .collect(Collectors.toList());
        if (contents.isEmpty()) {
            return null;
        }

        for (Content c : contents) {
            for (Coords pos : poss) {
                int x = pos.x;
                int y = pos.y;

                final PosItem it = c.getItem();
                final Image img = it.getImage();
                final Coords cPos = it.getPos();
                int cX = cPos.x;
                int cWidth = (int) img.getWidth();
                boolean fitX = x >= cX && x <= cX + cWidth;
                if (!fitX) {
                    continue;
                }

                int cY = cPos.y;
                int cHeight = (int) img.getHeight();
                boolean fitY = y >= cY && y <= cY + cHeight;
                if (!fitY) {
                    continue;
                }

                final List<List<Coords>> cp = it.getCollisionPolygons();
                for (final List<Coords> polygon : cp) {
                    List<Coords> tc = new ArrayList<>();
                    looseCoordsReference(polygon, tc);
                    translateCoords(tc, cX, cY);


                    int maxObstacleX = tc.stream()
                            .mapToInt(p -> p.x)
                            .max()
                            .getAsInt();
                    int minObstacleX = tc.stream()
                            .mapToInt(p -> p.x)
                            .min()
                            .getAsInt();
                    boolean fitObstacleX = x >= minObstacleX && x <= maxObstacleX;
                    if (!fitObstacleX) {
                        continue;
                    }

                    int maxObstacleY = tc.stream()
                            .mapToInt(p -> p.y)
                            .max()
                            .getAsInt();
                    int minObstacleY = tc.stream()
                            .mapToInt(p -> p.y)
                            .min()
                            .getAsInt();
                    boolean fitObstacleY = y >= minObstacleY && y <= maxObstacleY;
                    if (!fitObstacleY) {
                        continue;
                    }

                    boolean isInsidePolygon = pos.isInsidePolygon(tc, maxObstacleX);
                    if (isInsidePolygon) {
                        return c;
                    }
                }
            }
        }
        return null;
    }
}
