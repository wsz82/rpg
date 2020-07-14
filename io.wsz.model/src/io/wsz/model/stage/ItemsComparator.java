package io.wsz.model.stage;

import io.wsz.model.item.Equipment;
import io.wsz.model.item.ItemType;
import io.wsz.model.item.PosItem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static io.wsz.model.item.ItemType.CREATURE;
import static io.wsz.model.item.ItemType.LANDSCAPE;
import static io.wsz.model.stage.ItemsComparator.Comparison.*;

public class ItemsComparator {
    private static final LinkedList<Coords> oneList = new LinkedList<>();
    private static final LinkedList<Coords> twoList = new LinkedList<>();
    private static final List<Coords> oneLostReferences = new ArrayList<>(0);
    private static final List<Coords> oneResultCoords = new ArrayList<>(0);
    private static final List<Coords> twoLostReferences = new ArrayList<>(0);
    private static final List<Coords> twoResultCoords = new ArrayList<>(0);
    private static final Coords oneLeft = new Coords();
    private static final Coords oneRight = new Coords();
    private static final Coords twoLeft = new Coords();
    private static final Coords twoRight = new Coords();

    public static Comparison compare(PosItem i1, Node n) {
        PosItem i2 = n.getItem();
        int o1level = i1.getPos().level;
        int o2level = i2.getPos().level;
        ItemType t1 = i1.getType();
        ItemType t2 = i2.getType();
        double i1_top = i1.getTop();
        double i2_top = i2.getTop();
        double i1_left = i1.getLeft();
        double i2_left = i2.getLeft();
        double i1_bottom = i1.getBottom();
        double i2_bottom = i2.getBottom();
        double i1_right = i1.getRight();
        double i2_right = i2.getRight();

        boolean doOverlap = Coords.doOverlap(
                i1_left, i1_top, i1_right, i1_bottom,
                i2_left, i2_top, i2_right, i2_bottom);

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

                    if (i1_bottom > i2_bottom) {
                        return LESS;
                    } else if (i1_bottom < i2_bottom) {
                        return GREAT;
                    } else {
                        return INCOMPARABLE;
                    }
                }

                else {
                    return isCovered(i1, i2);
                }

            }

            else if (!t1.equals(LANDSCAPE) && !t2.equals(LANDSCAPE)) {

                if ((t1.equals(CREATURE))) {

                    if (i2 instanceof Equipment) {

                        if (i2.getActualCoverLine().isEmpty()) {
                            return GREAT;
                        }
                    }

                    return isCovered(i1, i2);

                }

                else if (t2.equals(CREATURE)) {

                    if (i1 instanceof Equipment) {

                        if (i1.getActualCoverLine().isEmpty()) {
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
                    return isCovered(i1, i2);
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

    public static Comparison isCovered(PosItem i1, PosItem i2) {
        addCoverCoordsToList(i1, oneList, oneLeft, oneRight, oneLostReferences, oneResultCoords);
        addCoverCoordsToList(i2, twoList, twoLeft, twoRight, twoLostReferences, twoResultCoords);
        return isCoverLineAbove(oneList, twoList);
    }

    private static void addCoverCoordsToList(PosItem pi, LinkedList<Coords> poss, Coords leftPos, Coords rightPos,
                                             List<Coords> lostRef, List<Coords> resultCoords) {
        final List<Coords> cl = pi.getActualCoverLine();
        final double left = pi.getLeft();
        final double top = pi.getTop();
        final double right = pi.getRight();
        final double bottom = pi.getBottom();
        leftPos.x = left;
        rightPos.x = right;
        leftPos.y = rightPos.y = bottom;
        poss.clear();
        if (!cl.isEmpty()) {
            Coords.looseCoordsReferences(cl, lostRef, resultCoords);
            poss.addAll(lostRef);
            Coords.translateCoords(poss, left, top);

            addLeftAndRightCoords(poss, leftPos, rightPos);
        } else {
            poss.add(leftPos);
            poss.add(rightPos);
        }
    }

    private static Comparison isCoverLineAbove(LinkedList<Coords> oneList, LinkedList<Coords> twoList) {
        for (int i = 0; i < twoList.size() - 1; i++) {
            Coords first = twoList.get(i);
            double x1 = first.x;
            Coords second = twoList.get(i+1);
            double x2 = second.x;

            if (x1 == x2) {
                continue;
            }

            for (Coords compared : oneList) {
                double x = compared.x;
                if (x == x1) {
                    continue;
                }
                boolean xIsBetweenLine = x >= x1 && x <= x2;
                if (!xIsBetweenLine) {
                    continue;
                }
                double y = compared.y;
                double y1 = first.y;
                double y2 = second.y;

                double func = (x*y1 - x*y2 + x1*y2 - x2*y1) / (x1 - x2);
                if (y > func) {
                    return GREAT;
                } else {
                    return LESS;
                }
            }
        }
        for (int i = 0; i < oneList.size() - 1; i++) {
            Coords first = oneList.get(i);
            double x1 = first.x;
            Coords second = oneList.get(i+1);
            double x2 = second.x;

            if (x1 == x2) {
                continue;
            }

            for (Coords compared : twoList) {
                double x = compared.x;
                if (x == x1) {
                    continue;
                }
                boolean xIsBetweenLine = x >= x1 && x <= x2;
                if (!xIsBetweenLine) {
                    continue;
                }
                double y = compared.y;
                double y1 = first.y;
                double y2 = second.y;

                double func = (x*y1 - x*y2 + x1*y2 - x2*y1) / (x1 - x2);
                if (y > func) {
                    return LESS;
                } else {
                    return GREAT;
                }
            }
        }
        return INCOMPARABLE;
    }

    private static void addLeftAndRightCoords(LinkedList<Coords> poss, Coords leftPos, Coords rightPos) {
        Coords first = poss.getFirst();
        if (first.x != leftPos.x) {
            leftPos.y = first.y;
            poss.addFirst(leftPos);
        }

        Coords last = poss.getLast();
        if (last.x != rightPos.x) {
            rightPos.y = last.y;
            poss.addLast(rightPos);
        }
    }

    public enum Comparison {
        GREAT,
        LESS,
        INCOMPARABLE
    }
}
