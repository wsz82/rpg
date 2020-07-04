package io.wsz.model.stage;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;
import javafx.scene.image.Image;

import java.util.LinkedList;
import java.util.List;

import static io.wsz.model.item.ItemType.*;
import static io.wsz.model.stage.Comparator.Comparison.*;

public class Comparator {
    private static final LinkedList<Coords> i1_list = new LinkedList<>();
    private static final LinkedList<Coords> i2_list = new LinkedList<>();
    private static final Coords i1_left = new Coords();
    private static final Coords i1_right = new Coords();
    private static final Coords i2_left = new Coords();
    private static final Coords i2_right = new Coords();

    public static Comparison compare(PosItem i1, Node n) {
        PosItem i2 = n.getItem();
        int o1level = i1.getLevel();
        int o2level = i2.getLevel();
        ItemType t1 = i1.getType();
        ItemType t2 = i2.getType();
        Coords pos1 = i1.getPos();
        Coords pos2 = i2.getPos();
        double o1y_top = pos1.y;
        double o2y_top = pos2.y;
        double o1x_left = pos1.x;
        double o2x_left = pos2.x;
        double i1_height = i1.getImage().getHeight() / Sizes.getMeter();
        double o1y_bottom = o1y_top + i1_height;
        double i2_height = i2.getImage().getHeight() / Sizes.getMeter();
        double o2y_bottom = o2y_top + i2_height;
        double i1_width = i1.getImage().getWidth() / Sizes.getMeter();
        double o1x_right = o1x_left + i1_width;
        double i2_width = i2.getImage().getWidth() / Sizes.getMeter();
        double o2x_right = o2x_left + i2_width;
        if (i1 instanceof Creature) {
            Creature c1 = (Creature) i1;
            o1y_bottom = c1.getCreatureBottom(o1y_bottom);
            o1y_top = c1.getCreatureTop(o1y_top, i1_height);
            o1x_right = c1.getCreatureRight(i1_width, o1x_right);
            o1x_left = c1.getCreatureLeft(o1x_left, i1_width);
        }
        if (i2 instanceof Creature) {
            Creature c2 = (Creature) i2;
            o2y_bottom = c2.getCreatureBottom(o2y_bottom);
            o2y_top = c2.getCreatureTop(o2y_top, i2_height);
            o2x_right = c2.getCreatureRight(i2_width, o2x_right);
            o2x_left = c2.getCreatureLeft(o2x_left, i2_width);
        }

        boolean doOverlap = doOverlap(o1x_left, o1y_top, o1x_right, o1y_bottom,
                o2x_left, o2y_top, o2x_right, o2y_bottom);

        if (!doOverlap) {
            return INCOMPARABLE;
        }

        if (o1level > o2level) {
            return GREAT;
        } else if (o1level < o2level) {
            return LESS;
        }

        else {

            if (t1.equals(t2)) {

                if (t1.equals(LANDSCAPE)) {

                    if (o1y_bottom > o2y_bottom) {
                        return LESS;
                    } else if (o1y_bottom < o2y_bottom) {
                        return GREAT;
                    } else {
                        return INCOMPARABLE;
                    }

                }

                else if ((t1.equals(CREATURE))) {

                    return isCovered(i1, i2);

                }

                else if (t1.equals(COVER)) {

                    return isCovered(i1, i2);

                }

            }

            else if ((t1.equals(CREATURE)) && !t2.equals(LANDSCAPE)) {

                if (i2 instanceof Equipment) {

                    if (i2.getCoverLine().isEmpty()) {
                        return GREAT;
                    }

                }

                return isCovered(i1, i2);

            }

            else if ((!t1.equals(LANDSCAPE) && t2.equals(CREATURE))) {

                if (i1 instanceof Equipment) {

                    if (i1.getCoverLine().isEmpty()) {
                        return LESS;
                    }

                }

                Comparison isCovered = isCovered(i2, i1);

                if (isCovered.equals(LESS)) {
                    return GREAT;
                } else if (isCovered.equals(GREAT)) {
                    return LESS;
                } else {
                    return INCOMPARABLE;
                }

            }

            else {
                int or1 = t1.ordinal();
                int or2 = t2.ordinal();

                if (or1 < or2) {
                    return GREAT;
                } else if (or1 > or2) {
                    return LESS;
                }
            }
        }
        return INCOMPARABLE;
    }

    public static boolean doOverlap(double o1x_left, double o1y_top, double o1x_right, double o1y_bottom,
                                    double o2x_left, double o2y_top, double o2x_right, double o2y_bottom) {
        if (o1x_left > o2x_right || o2x_left > o1x_right) {
            return false;
        }
        return o1y_top <= o2y_bottom && o2y_top <= o1y_bottom;
    }

    public static Comparison isCovered(PosItem i1, PosItem i2) {
        final List<Coords> i1_cl = i1.getCoverLine();
        final Coords i1_pos = i1.getPos();
        final Image i1_img = i1.getImage();
        final double i1_posX = i1_pos.x;
        final double i1_posY = i1_pos.y;
        final double i1_imgWidth = i1_img.getWidth() / Sizes.getMeter();
        final double i1_imgHeight = i1_img.getHeight() / Sizes.getMeter();
        i1_list.clear();
        if (!i1_cl.isEmpty()) {
            Coords.looseCoordsReference(i1_cl, i1_list);
            Coords.translateCoords(i1_list, i1_posX, i1_posY);
        } else {
            i1_left.x = i1_posX;
            double bottom = i1_posY + i1_imgHeight;
            i1_right.x = i1_posX + i1_imgWidth;

            if (i1 instanceof Creature) {
                Creature c1 = (Creature) i1;
                bottom = c1.getCreatureBottom(bottom);
                i1_right.x = c1.getCreatureRight(i1_imgWidth, i1_right.x);
                i1_left.x = c1.getCreatureLeft(i1_left.x, i1_imgWidth);
            }

            i1_left.y = i1_right.y = bottom;

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
            Coords.looseCoordsReference(i2_cl, i2_list);
            Coords.translateCoords(i2_list, i2_posX, i2_posY);
            addLeftAndRightPoints(i2_list, i2_posX, i2_imgWidth);
        } else {
            i2_left.x = i2_posX;
            double bottom = i2_posY + i2_imgHeight;
            i2_right.x = i2_posX + i2_imgWidth;

            if (i2 instanceof Creature) {
                Creature c2 = (Creature) i2;
                bottom = c2.getCreatureBottom(bottom);
                i2_right.x = c2.getCreatureRight(i2_imgWidth, i2_right.x);
                i2_left.x = c2.getCreatureLeft(i2_left.x, i2_imgWidth);
            }

            i2_left.y = i2_right.y = bottom;

            i2_list.add(i2_left);
            i2_list.add(i2_right);
        }

        return isCoverLineAbove(i1_list, i2_list);
    }

    private static Comparison isCoverLineAbove(LinkedList<Coords> i1_list, LinkedList<Coords> i2_list) {
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
        for (int i = 0; i < i1_list.size() - 1; i++) {
            Coords first = i1_list.get(i);
            double x1 = first.x;
            double y1 = first.y;
            Coords second = i1_list.get(i+1);
            double x2 = second.x;
            double y2 = second.y;

            if (x1 == x2) {
                continue;
            }

            for (Coords compared : i2_list) {
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
                    return LESS;
                } else {
                    return GREAT;
                }
            }
        }
        return INCOMPARABLE;
    }

    private static void addLeftAndRightPoints(LinkedList<Coords> linkedCoords, double i2_posX, double i2_imgWidth) {
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

    public enum Comparison {
        GREAT,
        LESS,
        INCOMPARABLE
    }
}
