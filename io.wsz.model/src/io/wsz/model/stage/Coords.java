package io.wsz.model.stage;

import java.io.Serializable;
import java.util.List;

public class Coords implements Serializable {
    public volatile int x;
    public volatile int y;

    public Coords(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean is0() {
        return x == 0 && y == 0;
    }

    public Coords add(Coords pos1, Coords pos2) {
           int x = pos1.x + pos2.x;
           int y = pos1.y + pos2.y;
           return new Coords(x, y);
    }

    public Coords subtract(Coords pos1, Coords pos2) {
        int x = pos1.x - pos2.x;
        int y = pos1.y - pos2.y;
        return new Coords(x, y);
    }

    public boolean isInsidePolygon(List<Coords> polygon, int maxX) {
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
        int val = (q.y - p.y) * (r.x - q.x)
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
}
