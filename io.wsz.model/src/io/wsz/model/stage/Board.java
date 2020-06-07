package io.wsz.model.stage;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.item.*;
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
        if (lesser.isEmpty()) {
            for (Node greater : n.getGreater()) {
                greater.getLesser().remove(n);
            }
            return n;
        } else {
            Node next = lesser.get(0);
            return findMin(next);
        }
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
                int x = pos.getX();
                int y = pos.getY();

                int cX = c.getItem().getPos().getX();
                int cWidth = (int) c.getItem().getImage().getWidth();
                boolean fitX = x >= cX && x <= cX + cWidth;
                if (!fitX) {
                    continue;
                }

                int cY = c.getItem().getPos().getY();
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

    public boolean isCovered(PosItem i, Cover cover) {
        Coords[] poss;
        if (i instanceof Creature) {
            poss = ((Creature) i).getCorners();
        } else {
            poss = new Coords[] {i.getPos()};
        }
        for (Coords pos : poss) {
            int x = pos.getX();
            int y = pos.getY();

            int cX = cover.getPos().getX();
            int cWidth = (int) cover.getImage().getWidth();
            boolean fitX = x >= cX && x <= cX + cWidth;
            if (!fitX) {
                continue;
            }

            int cY = cover.getPos().getY();

            Image img = cover.getImage();
            int imgX = x - cX;
            int imgY = y - cY;
            Color color;
            try {
                color = img.getPixelReader().getColor(imgX, imgY);
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
            boolean isPixelTransparent = color.equals(Color.TRANSPARENT);
            if (!isPixelTransparent) {
                return  true;
            }
        }
        return false;
    }



    public Coords getFreePos(Coords[] poss, Content obstacle) {
        List<Coords> coordsList = new ArrayList<>();
        Collections.addAll(coordsList, poss);
        Collections.shuffle(coordsList);
        for (Coords pos : coordsList) {
            int x = pos.getX();
            int y = pos.getY();

            int cX = obstacle.getItem().getPos().getX();
            int cWidth = (int) obstacle.getItem().getImage().getWidth();
            boolean fitX = x >= cX && x <= cX + cWidth;
            if (!fitX) {
                return pos;
            }

            int cY = obstacle.getItem().getPos().getY();
            int cHeight = (int) obstacle.getItem().getImage().getHeight();
            boolean fitY = y >= cY && y <= cY + cHeight;
            if (!fitY) {
                return pos;
            }

            Image img = obstacle.getItem().getImage();
            int imgX = x - cX;
            int imgY = y - cY;
            Color color;
            try {
                color = img.getPixelReader().getColor(imgX, imgY);
            } catch (IndexOutOfBoundsException e) {
                continue; //TODO return pos?
            }
            boolean isPixelTransparent = color.equals(Color.TRANSPARENT);
            if (isPixelTransparent) {
                return pos;
            }
        }
        return null;
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
}
