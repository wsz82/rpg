package io.wsz.model.stage;

import io.wsz.model.item.CreatureSize;
import io.wsz.model.location.Location;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.lang.Math.*;

public class Coords implements Externalizable {
    private static final long serialVersionUID = 1L;

    private static final Coords EXTREME = new Coords();
    private static final Coords temp1 = new Coords();
    private static final Coords temp2 = new Coords();
    private static final Coords temp3 = new Coords();
    private static final Coords temp4 = new Coords();
    private static final List<Coords> lostReferences1 = new ArrayList<>(0);
    private static final List<Coords> lostReferences2 = new ArrayList<>(0);
    private static final List<Coords> resultCoords1 = new ArrayList<>(0);
    private static final List<Coords> resultCoords2 = new ArrayList<>(0);

    public static List<Coords> cloneCoordsList(List<Coords> from) {
        if (from == null) return null;
        List<Coords> poss = new ArrayList<>(from.size());
        for (Coords pos : from) {
            poss.add(pos.clonePos());
        }
        return poss;
    }

    public static List<List<Coords>> cloneCoordsPolygons(List<List<Coords>> from) {
        if (from == null) return null;
        List<List<Coords>> polygons = new ArrayList<>(from.size());
        for (List<Coords> poss : from) {
            polygons.add(cloneCoordsList(poss));
        }
        return polygons;
    }

    public static List<Coords> looseCoordsReferences1(List<Coords> from) {
        return looseCoordsReferences(from, lostReferences1, resultCoords1);
    }

    public static List<Coords> looseCoordsReferences2(List<Coords> from) {
        return looseCoordsReferences(from, lostReferences2, resultCoords2);
    }

    public static List<Coords> looseCoordsReferences(List<Coords> from, List<Coords> to, List<Coords> result) {
        int dif = from.size() - to.size();
        if (dif > 0) {
            for (int i = 0; i < dif; i++) {
                to.add(new Coords());
            }
        }
        for (int i = 0; i < from.size(); i++) {
            Coords fromCoords = from.get(i);
            Coords actual = to.get(i);
            actual.x = fromCoords.x;
            actual.y = fromCoords.y;
            actual.setLocation(fromCoords.getLocation());
        }
        result.clear();
        for (int i = 0; i < from.size(); i++) {
            result.add(to.get(i));
        }
        return result;
    }

    public static void translateCoords(List<Coords> list, double x, double y) {
        if (x == 0 && y == 0) return;
        list.forEach(c -> {
            c.x += x;
            c.y += y;
        });
    }

    private static void extendCoords(List<Coords> list, int xRatio, double yRatio) {
        list.forEach(c -> {
            c.x *= xRatio;
            c.y *= yRatio;
        });
    }

    public static boolean doOverlap(double o1x_left, double o1y_top, double o1x_right, double o1y_bottom,
                                    double o2x_left, double o2y_top, double o2x_right, double o2y_bottom) {
        if (o1x_left > o2x_right || o2x_left > o1x_right) {
            return false;
        }
        return o1y_top <= o2y_bottom && o2y_top <= o1y_bottom;
    }

    public static boolean ovalsIntersect(Coords c1, CreatureSize s1, Coords c2, CreatureSize s2) {
        return ovalsIntersect(c1, s1, c2, s2.getWidth());
    }

    public static boolean ovalsIntersect(Coords c1, CreatureSize s1, Coords c2, double w2) {
        return ovalsIntersect(c1, s1.getWidth(), s1.getHeight(), c2, w2);
    }

    public static boolean ovalsIntersect(Coords c1, double w1, double h1, Coords c2, double w2) {
        double xDif = Math.abs(c1.x - c2.x);
        double yDif = Math.abs(c1.y - c2.y);
        double ratio = w1 / h1;
        yDif *= ratio;
        double r1 = w1 / 2;
        double r2 = w2 / 2;
        double dist = sqrt(pow(xDif, 2) + pow(yDif, 2));
        double radiusSum = r1 + r2;
        return dist <= radiusSum;
    }

    public static boolean ovalIntersectsPolygon(Coords c, CreatureSize s, List<Coords> polygon) {
        double ratio = s.getWidth() / s.getHeight();
        double radius = s.getWidth() / 2;

        double x = c.x;
        double y = c.y * ratio;

        double halfWidth = s.getWidth() / 2;
        double halfHeight = s.getHeight() / 2;
        double left = c.x - halfWidth;
        double right = c.x + halfWidth;
        double top = c.y - halfHeight;
        double bottom = c.y + halfHeight;

        int pSize = polygon.size();
        for (int i = 0; i < pSize; i++) {
            Coords first = polygon.get(i);
            if (pSize == 1) return first.isInsidePolygon(polygon);
            if (pSize == 2 && i == 1) continue;
            Coords second;
            if (i+1 == pSize) {
                second = polygon.get(0);
            } else {
                second = polygon.get(i+1);
            }

            double x1 = first.x;
            double x2 = second.x;
            double y1 = first.y;
            double y2 = second.y;

            double leftPointX = min(x1, x2);
            double rightPointX = max(x1, x2);
            double topPointY = min(y1, y2);
            double bottomPointY = max(y1, y2);

            if (right < leftPointX || left > rightPointX) continue;

            if (bottom < topPointY || top > bottomPointY) continue;

            y1 *= ratio;
            y2 *= ratio;

            double powDist = pow((y2 - y1)*x - (x2 - x1)*y + x2*y1 - y2*x1, 2) / (pow(y2 - y1, 2) + pow(x2 - x1, 2));
            double powRadius = pow(radius, 2);

            double p1dist = getSquareDistance(x1, x, y1, y);
            if (p1dist < powRadius) return true;
            double p2dist = getSquareDistance(x2, x, y2, y);
            if (p2dist < powRadius) return true;

            if (powDist < powRadius) {
                double dist = sqrt(powDist) + 0.01; //TODO collision with horizontal line
                temp1.x = x1;
                temp1.y = y1;
                temp2.x = x2;
                temp2.y = y2;

                temp3.x = x - dist;
                temp3.y = y - dist;

                temp4.x = x + dist;
                temp4.y = y + dist;

                boolean intersectNW_SE = doIntersect(temp1, temp2, temp3, temp4);

                temp3.x = x + dist;
                temp3.y = y - dist;

                temp4.x = x - dist;
                temp4.y = y + dist;

                boolean intersectNE_SW = doIntersect(temp1, temp2, temp3, temp4);

                if (intersectNW_SE || intersectNE_SW) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean pointWithinOval(Coords point, Coords ovalCenter, double width, double height) {
        return pointWithinOval(point.x, point.y, ovalCenter, width, height);
    }

    public static boolean pointWithinOval(double x, double y, Coords ovalCenter, double width, double height) {
        return pointWithinOval(x, y, ovalCenter.x, ovalCenter.y, width, height);
    }

    public static boolean pointWithinOval(double x, double y, double h, double k, double width, double height) {
        double rx = width /2;
        double ry = height /2;

        double eq = pow(x - h, 2)/pow(rx, 2) + pow(y - k, 2)/pow(ry, 2);
        return eq <= 1;
    }

    public static double calcOvalLineIntersection(double radius, double x1, double x2, double y1, double y2) {
        return pow(radius, 2) * (pow(x2 - x1, 2) + pow(y2 - y1, 2)) - pow(x1*y2 - x2*y1, 2);
    }

    public static double getDistance(Coords c1, Coords c2) {
        return sqrt(pow(c2.x - c1.x, 2) + pow(c2.y - c1.y, 2));
    }

    public static double getSquareDistance(Coords c1, Coords c2) {
        return pow(c2.x - c1.x, 2) + pow(c2.y - c1.y, 2);
    }
    public static double getSquareDistance(double x1, double x2, double y1, double y2) {
        return pow(x2 - x1, 2) + pow(y2 - y1, 2);
    }

    public static boolean polygonsIntersect(double nextPosX, double nextPosY,
                                            List<List<Coords>> iPolygons, Coords oPos, List<List<Coords>> oPolygons) {
        if (iPolygons.isEmpty()) return false;
        if (oPolygons.isEmpty()) return false;

        for (List<Coords> rawIPolygon : iPolygons) {

            List<Coords> iPolygon = looseCoordsReferences1(rawIPolygon);
            translateCoords(iPolygon, nextPosX, nextPosY);

            for (List<Coords> rawOPolygon : oPolygons) {

                List<Coords> oPolygon = looseCoordsReferences2(rawOPolygon);
                translateCoords(oPolygon, oPos.x, oPos.y);

                if (!polygonsRectanglesOverlap(iPolygon, oPolygon)) continue;

                int iSize = iPolygon.size();
                for (int ic = 0; ic < iSize; ic++) {
                    Coords i1 = iPolygon.get(ic);
                    if (iSize == 1) return i1.isInsidePolygon(oPolygon);
                    if (iSize == 2 && ic == 1) continue;
                    Coords i2;
                    if (ic + 1 == iSize) {
                        i2 = iPolygon.get(0);
                    } else {
                        i2 = iPolygon.get(ic + 1);
                    }

                    int oSize = oPolygon.size();
                    for (int oc = 0; oc < oSize; oc++) {
                        Coords o1 = oPolygon.get(oc);
                        if (oSize == 1) return o1.isInsidePolygon(iPolygon);
                        if (oSize == 2 && oc == 1) continue;
                        Coords o2;
                        if (oc + 1 == oSize) {
                            o2 = oPolygon.get(0);
                        } else {
                            o2 = oPolygon.get(oc + 1);
                        }

                        if (doIntersect(i1, i2, o1, o2)) return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean polygonsRectanglesOverlap(List<Coords> polygon1, List<Coords> polygon2) {
        double p1Left = polygon1.stream()
                .mapToDouble(c -> c.x)
                .min()
                .getAsDouble();
        double p1Top = polygon1.stream()
                .mapToDouble(c -> c.y)
                .min()
                .getAsDouble();
        double p1Right = polygon1.stream()
                .mapToDouble(c -> c.x)
                .max()
                .getAsDouble();
        double p1Bottom = polygon1.stream()
                .mapToDouble(c -> c.y)
                .max()
                .getAsDouble();

        double p2Left = polygon2.stream()
                .mapToDouble(c -> c.x)
                .min()
                .getAsDouble();
        double p2Top = polygon2.stream()
                .mapToDouble(c -> c.y)
                .min()
                .getAsDouble();
        double p2Right = polygon2.stream()
                .mapToDouble(c -> c.x)
                .max()
                .getAsDouble();
        double p2Bottom = polygon2.stream()
                .mapToDouble(c -> c.y)
                .max()
                .getAsDouble();

        return doOverlap(p1Left, p1Top, p1Right, p1Bottom, p2Left, p2Top, p2Right, p2Bottom);
    }

    public double x;
    public double y;
    public int level;

    private Location location;

    public Coords() {}

    public Coords(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coords(double x, double y, int level) {
        this.x = x;
        this.y = y;
        this.level = level;
    }

    public Coords(double x, double y, int level, Location location) {
        this.x = x;
        this.y = y;
        this.level = level;
        this.location = location;
    }

    public boolean is0() {
        return x == 0 && y == 0;
    }

    public boolean isEmpty() {
        return x == 0 && y == 0 && level == 0 && location == null;
    }

    public Coords clonePos() {
        return new Coords(this.x, this.y, this.level, this.location);
    }

    public void add(Coords pos2) {
        this.x += pos2.x;
        this.y += pos2.y;
    }

    public void subtract(Coords pos2) {
        this.x -= pos2.x;
        this.y -= pos2.y;
    }

    public boolean isInsidePolygon(List<Coords> polygon) {
        int n = polygon.size();
        if (n < 3) {
            return false;
        }

        double maxX = polygon.stream()
                .mapToDouble(c -> c.x)
                .max()
                .getAsDouble();

        EXTREME.x = maxX;
        EXTREME.y = this.y;

        int count = 0, i = 0;
        boolean intersectedOnVertex = false;
        do {
            int next = (i + 1) % n;

            Coords pi = polygon.get(i);
            Coords pNext = polygon.get(next);
            inner: if (doIntersect(pi, pNext, this, EXTREME)) {
                if (orientation(pi, this, pNext) == 0) {
                    return onSegment(pi, this,
                            pNext);
                }
                if (intersectedOnVertex && count > 1) {
                    intersectedOnVertex = false;
                    break inner;
                }
                if (this.y == pNext.y) {
                    intersectedOnVertex = true;
                }
                count++;
            }
            i = next;
        } while (i != 0);

        return (count % 2 == 1);
    }

    private static boolean onSegment(Coords p, Coords q, Coords r) {
        return q.x <= Math.max(p.x, r.x)
                && q.x >= Math.min(p.x, r.x)
                && q.y <= Math.max(p.y, r.y)
                && q.y >= Math.min(p.y, r.y);
    }

    private static int orientation(Coords p, Coords q, Coords r) {
        double val = (q.y - p.y) * (r.x - q.x)
                - (q.x - p.x) * (r.y - q.y);

        if (val == 0) {
            return 0;
        }
        return (val > 0) ? 1 : 2;
    }

    public static boolean doIntersect(Coords p1, Coords q1,
                                       Coords p2, Coords q2) {
        int o1 = orientation(p1, q1, p2);
        int o2 = orientation(p1, q1, q2);
        int o3 = orientation(p2, q2, p1);
        int o4 = orientation(p2, q2, q1);

        if (o1 != o2 && o3 != o4) {
            return true;
        }
        if (o1 == 0 && onSegment(p1, p2, q1)) {
            return true;
        }
        if (o2 == 0 && onSegment(p1, q2, q1)) {
            return true;
        }
        if (o3 == 0 && onSegment(p2, p1, q2)) {
            return true;
        }
        return o4 == 0 && onSegment(p2, q1, q2);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        String locationName;
        if (location != null) {
            locationName = location.getName();
            return "Location: " + locationName + ": " + "X: " + x + ", Y: " + y + ", level: " + level;
        } else {
            return "X: " + x + ", Y: " + y + ", level: " + level;
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coords)) return false;
        Coords coords = (Coords) o;
        return Double.compare(coords.x, x) == 0 &&
                Double.compare(coords.y, y) == 0 &&
                level == coords.level &&
                Objects.equals(getLocation(), coords.getLocation());
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, level, location);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(serialVersionUID);

        out.writeDouble(x);

        out.writeDouble(y);

        out.writeInt(level);

        if (location != null) {
            out.writeUTF(location.getName());
        } else {
            out.writeUTF("");
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        long ver = in.readLong();

        x = in.readDouble();

        y = in.readDouble();

        level = in.readInt();

        String locationName = in.readUTF();
        if (!locationName.isEmpty()) {
            location = new Location(locationName);
        }
    }
}
