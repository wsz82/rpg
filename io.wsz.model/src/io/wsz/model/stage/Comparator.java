package io.wsz.model.stage;

import io.wsz.model.item.Creature;
import io.wsz.model.item.Equipment;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import io.wsz.model.sizes.Sizes;

import static io.wsz.model.item.ItemType.*;
import static io.wsz.model.stage.Comparator.Comparison.*;

public class Comparator {

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

                    return Board.get().isCovered(i1, i2);

                }

                else if (t1.equals(COVER)) {

                    return Board.get().isCovered(i1, i2);

                }

            }

            else if ((t1.equals(CREATURE)) && !t2.equals(LANDSCAPE)) {

                if (i2 instanceof Equipment) {

                    if (i2.getCoverLine().isEmpty()) {
                        return GREAT;
                    }

                }

                return Board.get().isCovered(i1, i2);

            }

            else if ((!t1.equals(LANDSCAPE) && t2.equals(CREATURE))) {

                if (i1 instanceof Equipment) {

                    if (i1.getCoverLine().isEmpty()) {
                        return LESS;
                    }

                }

                Comparison isCovered = Board.get().isCovered(i2, i1);

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

    public enum Comparison {
        GREAT,
        LESS,
        INCOMPARABLE
    }
}
