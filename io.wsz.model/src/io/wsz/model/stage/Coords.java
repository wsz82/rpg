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
    private static final List<Coords> lostReferences = new ArrayList<>(0);
    private static final List<Coords> resultCoords = new ArrayList<>(0);

    public static List<Coords> cloneCoordsList(List<Coords> from) {
        List<Coords> poss = new ArrayList<>(from.size());
        for (Coords pos : from) {
            poss.add(pos.clonePos());
        }
        return poss;
    }

    public static List<List<Coords>> cloneCoordsPolygons(List<List<Coords>> from) {
        List<List<Coords>> polygons = new ArrayList<>(from.size());
        for (List<Coords> poss : from) {
            polygons.add(cloneCoordsList(poss));
        }
        return polygons;
    }

    public static List<Coords> looseCoordsReferences(List<Coords> from) {
        return looseCoordsReferences(from, lostReferences, resultCoords);
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

        for (int i = 0; i < polygon.size(); i++) {
            Coords first = polygon.get(i);
            if (polygon.size() == 2 && i == 1) continue;
            Coords second;
            if (i+1 == polygon.size()) {
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
                double dist = sqrt(powDist);
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
        double x = point.x;
        double y = point.y;
        double h = ovalCenter.x;
        double k = ovalCenter.y;
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

    public double x;

    public double y;

    private Location location;

    public Coords() {}

    public Coords(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Coords(double x, double y, Location location) {
        this.x = x;
        this.y = y;
        this.location = location;
    }

    public boolean is0() {
        return x == 0 && y == 0;
    }

    public Coords clonePos() {
        return new Coords(this.x, this.y, this.location);
    }

    public void add(Coords pos2) {
        this.x += pos2.x;
        this.y += pos2.y;
    }

    public void subtract(Coords pos2) {
        this.x -= pos2.x;
        this.y -= pos2.y;
    }

    public boolean isInsidePolygon(List<Coords> polygon, double maxX) {
        int n = polygon.size();
        if (n < 3) {
            return false;
        }

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
        } else {
            locationName = null;
        }
        return "Location:" + locationName + ": " + "X: " + x + ", Y: " + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coords coords = (Coords) o;
        return Double.compare(coords.x, x) == 0 &&
                Double.compare(coords.y, y) == 0 &&
                Objects.equals(coords.location, location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y, location);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeDouble(x);

        out.writeDouble(y);

        if (location != null) {
            out.writeUTF(location.getName());
        } else {
            out.writeUTF("");
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        x = in.readDouble();

        y = in.readDouble();

        String locationName = in.readUTF();
        if (!locationName.isEmpty()) {
            location = new Location(locationName);
        }
    }
}
