package io.wsz.model.stage;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class Coords implements Serializable {
    private static final long serialVersionUID = 1L;

    public double x;
    public double y;

    public Coords() {}

    public Coords(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean is0() {
        return x == 0 && y == 0;
    }

    public Coords clone() {
        return new Coords(this.x, this.y);
    }

    public Coords add(Coords pos2) {
        double x = this.x + pos2.x;
        double y = this.y + pos2.y;
        return new Coords(x, y);
    }

    public Coords subtract(Coords pos2) {
        double x = this.x - pos2.x;
        double y = this.y - pos2.y;
        return new Coords(x, y);
    }

    public boolean isInsidePolygon(List<Coords> polygon, double maxX) {
        int n = polygon.size();
        if (n < 3) {
            return false;
        }

        Coords extreme = new Coords(maxX, this.y);

        int count = 0, i = 0;
        boolean intersectedOnVertex = false;
        do {
            int next = (i + 1) % n;

            Coords pi = polygon.get(i);
            Coords pNext = polygon.get(next);
            inner: if (doIntersect(pi, pNext, this, extreme)) {
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

    @Override
    public String toString() {
        return "X: " + x + ", Y: " + y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coords coords = (Coords) o;
        return Double.compare(coords.x, x) == 0 &&
                Double.compare(coords.y, y) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
