package io.wsz.model.stage;

import io.wsz.model.location.Location;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Coords implements Externalizable {
    private static final long serialVersionUID = 1L;

    private static final Coords EXTREME = new Coords();
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

    public static boolean doOverlap(double o1x_left, double o1y_top, double o1x_right, double o1y_bottom,
                                    double o2x_left, double o2y_top, double o2x_right, double o2y_bottom) {
        if (o1x_left > o2x_right || o2x_left > o1x_right) {
            return false;
        }
        return o1y_top <= o2y_bottom && o2y_top <= o1y_bottom;
    }

    public double x;
    public double y;

    private Location location;

    public Coords() {}

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

    private boolean onSegment(Coords p, Coords q, Coords r) {
        return q.x <= Math.max(p.x, r.x)
                && q.x >= Math.min(p.x, r.x)
                && q.y <= Math.max(p.y, r.y)
                && q.y >= Math.min(p.y, r.y);
    }

    private int orientation(Coords p, Coords q, Coords r) {
        double val = (q.y - p.y) * (r.x - q.x)
                - (q.x - p.x) * (r.y - q.y);

        if (val == 0) {
            return 0;
        }
        return (val > 0) ? 1 : 2;
    }

    private boolean doIntersect(Coords p1, Coords q1,
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
