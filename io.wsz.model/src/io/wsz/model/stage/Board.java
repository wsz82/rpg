package io.wsz.model.stage;

import io.wsz.model.Controller;
import io.wsz.model.content.Content;
import io.wsz.model.content.ContentComparator;
import io.wsz.model.item.Creature;
import io.wsz.model.item.CreatureControl;
import io.wsz.model.item.ItemType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Board {
    private static Board singleton;

    public static Board get() {
        if (singleton == null) {
            singleton = new Board();
        }
        return singleton;
    }

    private Board(){}

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
        contents.sort(new ContentComparator() {
            @Override
            public int compare(Content c1, Content c2) {
                return super.compare(c2, c1);
            }
        });
        for (Content c : contents) {
            for (Coords pos : poss) {
                double x = pos.getX();
                double y = pos.getY();

                double cX = c.getItem().getPos().getX();
                double cWidth = c.getItem().getImage().getWidth();
                boolean fitX = x >= cX && x <= cX + cWidth;
                if (!fitX) {
                    continue;
                }

                double cY = c.getItem().getPos().getY();
                double cHeight = c.getItem().getImage().getHeight();
                boolean fitY = y >= cY && y <= cY + cHeight;
                if (!fitY) {
                    continue;
                }

                Image img = c.getItem().getImage();
                int imgX = (int) (x - cX);
                int imgY = (int) (y - cY);
                Color color;
                try {
                    color = img.getPixelReader().getColor(imgX, imgY);    //TODO fix index ot of bounds exception
                } catch (IndexOutOfBoundsException e) {
                    return null;
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

    public Coords getFreePos(Coords[] poss, Content obstacle) {
        List<Content> contents = new ArrayList<>(Controller.get().getCurrentLocation().getContent());
        contents = contents.stream()
                .filter(c -> c.getItem().getType().equals(ItemType.OBSTACLE))
                .filter(c -> c.isVisible())
                .filter(c -> c.getItem().getLevel() <= Controller.get().getCurrentLayer().getLevel())
                .collect(Collectors.toList());
        if (contents.isEmpty()) {
            return null;
        }
        contents.sort(new ContentComparator() {
            @Override
            public int compare(Content c1, Content c2) {
                return super.compare(c2, c1);
            }
        });
        List<Coords> coordsList = new ArrayList<>();
        Collections.addAll(coordsList, poss);
        Collections.shuffle(coordsList);
        for (Coords pos : coordsList) {
            double x = pos.getX();
            double y = pos.getY();

            double cX = obstacle.getItem().getPos().getX();
            double cWidth = obstacle.getItem().getImage().getWidth();
            boolean fitX = x >= cX && x <= cX + cWidth;
            if (!fitX) {
                return pos;
            }

            double cY = obstacle.getItem().getPos().getY();
            double cHeight = obstacle.getItem().getImage().getHeight();
            boolean fitY = y >= cY && y <= cY + cHeight;
            if (!fitY) {
                return pos;
            }

            Image img = obstacle.getItem().getImage();
            int imgX = (int) (x - cX);
            int imgY = (int) (y - cY);
            Color color;
            try {
                color = img.getPixelReader().getColor(imgX, imgY);    //TODO fix index ot of bounds exception
            } catch (IndexOutOfBoundsException e) {
                return null;
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
