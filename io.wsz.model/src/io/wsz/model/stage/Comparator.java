package io.wsz.model.stage;

import io.wsz.model.content.Content;
import io.wsz.model.item.Cover;
import io.wsz.model.item.Creature;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;

import static io.wsz.model.item.ItemType.*;
import static io.wsz.model.stage.Comparator.Comparison.*;

public class Comparator {

    public static Comparison compare(Content c1, Node n) {
        Content c2 = n.getContent();
        PosItem i1 = c1.getItem();
        PosItem i2 = c2.getItem();
        int o1level = i1.getLevel();
        int o2level = i2.getLevel();
        ItemType t1 = i1.getType();
        ItemType t2 = i2.getType();
        Coords pos1 = i1.getPos();
        Coords pos2 = i2.getPos();
        int o1y_bottom = pos1.getY() + (int) i1.getImage().getHeight();
        int o2y_bottom = pos2.getY() + (int) i2.getImage().getHeight();
        int o1x_right = pos1.getX() + (int) i1.getImage().getWidth();
        int o2x_right = pos2.getX() + (int) i2.getImage().getWidth();
        int o1y_top = pos1.getY();
        int o2y_top = pos2.getY();
        int o1x_left = pos1.getX();
        int o2x_left = pos2.getX();

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

                    boolean isCovered = Board.get().isCovered(i1, i2);

                    if (isCovered) {
                        return LESS;
                    } else {
                        return GREAT;
                    }

                }

                else if (t1.equals(COVER)) {

                    boolean isCovered = Board.get().isCovered(i1, i2);

                    if (isCovered) {
                        return LESS;
                    } else {
                        return GREAT;
                    }

                }

            }

            else if ((t1.equals(CREATURE) && t2.equals(COVER))) {

                boolean isCovered = Board.get().isCovered(i1, i2);

                if (isCovered) {
                    return LESS;
                } else {
                    return GREAT;
                }

            }

            else if ((t1.equals(COVER) && t2.equals(CREATURE))) {

                boolean isCovered = Board.get().isCovered(i2, i1);

                if (isCovered) {
                    return GREAT;
                } else {
                    return LESS;
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

    private static boolean doOverlap(int o1x_left, int o1y_top, int o1x_right, int o1y_bottom,
                                     int o2x_left, int o2y_top, int o2x_right, int o2y_bottom) {
        if (o1x_left > o2x_right || o2x_left > o1x_right) {
            return false;
        }
        return o1y_top <= o2y_bottom && o2y_top <= o1y_bottom;
    }

    private static Comparison isCreatureAbove(Creature cr, Cover c) {
        Coords pos = cr.getCenterBottomPos();
        int crX = pos.getX();
        int crTopSizeY = pos.getY() - cr.getSize().getHeight()/2;
        int crBottomSizeY = pos.getY() + cr.getSize().getHeight()/2;
        Coords cPos = c.getPos();
        int cRightX = cPos.getX();
        Image img = c.getImage();
        PixelReader pr = img.getPixelReader();

        int imgX = crX - cRightX;
        int cTopY = cPos.getY();
        int imgY = crTopSizeY - cTopY;
        boolean reachesTop = false;
        for (int i = imgY; i >= cTopY; i--) {
            try {
                Color color = pr.getColor(imgX, i);
                if (!color.equals(Color.TRANSPARENT)) {
                    reachesTop = true;
                }
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
        }

        int cBottomY = cTopY + (int) c.getImage().getHeight();
        imgY = crBottomSizeY - cTopY;
        boolean reachesBottom = false;
        for (int i = imgY; i <= cBottomY; i++) {
            try {
                Color color = pr.getColor(imgX, i);
                if (!color.equals(Color.TRANSPARENT)) {
                    reachesBottom = true;
                }
            } catch (IndexOutOfBoundsException e) {
                continue;
            }
        }
        if (!reachesBottom && !reachesTop) {
            return INCOMPARABLE;
        } else if (reachesBottom) {
            return LESS;
        } else {
            return GREAT;
        }
    }

    public enum Comparison {
        GREAT,
        LESS,
        INCOMPARABLE
    }
}
