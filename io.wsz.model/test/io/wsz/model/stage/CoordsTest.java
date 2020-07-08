package io.wsz.model.stage;

import io.wsz.model.item.CreatureSize;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CoordsTest {

    @Test
    public void testEquals() {
        double x = 1.678220111;
        double y = 6.335873621;
        Coords a = new Coords(x, y);
        Coords b = new Coords(x, y);

        assertEquals(a, b);
    }

    @Test
    public void testNotEquals() {
        double x1 = 1.678220111;
        double y1 = 6.335873621;
        Coords a = new Coords(x1, y1);
        double x2 = 1.678229111;
        double y2 = 6.335873681;
        Coords b = new Coords(x2, y2);

        assertNotEquals(a, b);
    }

    @Test
    public void ovalsIntersect_equalY() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getWidth()/2 + medium.getWidth()/2 - 1;
        Coords a = new Coords(0, 0);
        Coords b = new Coords(dist, 0);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertTrue(ovalsIntersect);
    }

    @Test
    public void ovalsIntersectOnEdge_equalY() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getWidth()/2 + medium.getWidth()/2;
        Coords a = new Coords(0, 0);
        Coords b = new Coords(dist, 0);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertTrue(ovalsIntersect);
    }

    @Test
    public void ovalsNotIntersect_equalY() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getWidth()/2 + medium.getWidth()/2 + 1;
        Coords a = new Coords(0, 0);
        Coords b = new Coords(dist, 0);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertFalse(ovalsIntersect);
    }

    @Test
    public void ovalsIntersect_equalX() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getHeight()/2 + medium.getHeight()/2 - 1;
        Coords a = new Coords(0, 0);
        Coords b = new Coords(0, dist);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertTrue(ovalsIntersect);
    }

    @Test
    public void ovalsIntersectOnEdge_equalX() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getHeight()/2 + medium.getHeight()/2;
        Coords a = new Coords(0, 0);
        Coords b = new Coords(0, dist);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertTrue(ovalsIntersect);
    }

    @Test
    public void ovalsNotIntersect_equalX() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getHeight()/2 + medium.getHeight()/2 + 1;
        Coords a = new Coords(0, 0);
        Coords b = new Coords(0, dist);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertFalse(ovalsIntersect);
    }

    @Test
    public void ovalsIntersect_oneInsideAnother() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        Coords a = new Coords(0, 0);
        Coords b = new Coords(0, 0);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertTrue(ovalsIntersect);
    }

    @Test
    public void ovalIntersectsPolygon() {
        CreatureSize small = CreatureSize.S;
        List<Coords> polygon = List.of(new Coords(0, 0), new Coords(1, 0), new Coords(1, 1), new Coords(0, 1));
        Coords a = new Coords(-0.2, -0.1);

        boolean ovalsIntersectsPolygon = Coords.ovalIntersectsPolygon(a, small, polygon);
        assertTrue(ovalsIntersectsPolygon);
    }

    @Test
    public void ovalIntersectsPolygon_equalY() {
        CreatureSize small = CreatureSize.S;
        List<Coords> polygon = List.of(new Coords(-0.71, 0), new Coords(1, -1), new Coords(1, 1));
        Coords a = new Coords(-1, 0);

        boolean ovalsIntersectsPolygon = Coords.ovalIntersectsPolygon(a, small, polygon);
        assertTrue(ovalsIntersectsPolygon);
    }

    @Test
    public void ovalIntersectsPolygon_fromTop() {
        CreatureSize small = CreatureSize.S;
        List<Coords> polygon = List.of(new Coords(-0.71, 0), new Coords(1, -1), new Coords(1, 1));
        Coords a = new Coords(1, 1.1);

        boolean ovalsIntersectsPolygon = Coords.ovalIntersectsPolygon(a, small, polygon);
        assertTrue(ovalsIntersectsPolygon);
    }

    @Test
    public void ovalNotIntersectsPolygon() {
        CreatureSize small = CreatureSize.S;
        List<Coords> polygon = List.of(new Coords(0, 0), new Coords(1, 0), new Coords(1, 1), new Coords(0, 1));
        Coords a = new Coords(-1, -1);

        boolean ovalsIntersectsPolygon = Coords.ovalIntersectsPolygon(a, small, polygon);
        assertFalse(ovalsIntersectsPolygon);
    }

    @Test
    public void lineIntersectsCircle() {
        double radius = 0.3;
        Coords a = new Coords(1, -0.1);
        Coords b = new Coords(-1, 0.1);

        double delta = Coords.calcOvalLineIntersection(radius, a.x, b.x, a.y, b.y);
        assertTrue(delta > 0);
    }

    @Test
    public void lineIntersectsCircle_onEdge() {
        double radius = 0.3;
        Coords a = new Coords(-1, 0.3);
        Coords b = new Coords(1, 0.3);

        double delta = Coords.calcOvalLineIntersection(radius, a.x, b.x, a.y, b.y);
        assertTrue(delta == 0);
    }

    @Test
    public void lineAboveCircle() {
        double radius = 0.3;
        Coords a = new Coords(-1, 0.4);
        Coords b = new Coords(1, 0.4);

        double delta = Coords.calcOvalLineIntersection(radius, a.x, b.x, a.y, b.y);
        assertTrue(delta < 0);
    }

    @Test
    public void circleSWfromLine() {
        double radius = 0.3;
        Coords a = new Coords(0, 2);
        Coords b = new Coords(2, 0);

        double delta = Coords.calcOvalLineIntersection(radius, a.x, b.x, a.y, b.y);
        assertTrue(delta < 0);
    }

    @Test
    public void segmentsIntersect() {
        Coords p1 = new Coords(-1, 0);
        Coords q1 = new Coords(1, 0);
        Coords p2 = new Coords(0, -1);
        Coords q2 = new Coords(0, 1);

        boolean intersect = Coords.doIntersect(p1, q1, p2, q2);
        assertTrue(intersect);
    }

    @Test
    public void segmentsIntersect_edge() {
        Coords p1 = new Coords(-1, 0);
        Coords q1 = new Coords(1.1, 0);
        Coords p2 = new Coords(1.1, -1);
        Coords q2 = new Coords(1.1, 1);

        boolean intersect = Coords.doIntersect(p1, q1, p2, q2);
        assertTrue(intersect);
    }

    @Test
    public void segmentsNotIntersect() {
        Coords p1 = new Coords(-1, 0);
        Coords q1 = new Coords(1, 0);
        Coords p2 = new Coords(2, -1);
        Coords q2 = new Coords(2, 1);

        boolean intersect = Coords.doIntersect(p1, q1, p2, q2);
        assertFalse(intersect);
    }
}