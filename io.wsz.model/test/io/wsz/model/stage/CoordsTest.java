package io.wsz.model.stage;

import io.wsz.model.item.CreatureSize;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CoordsTest {

    @Test
    public void testEquals() {
        double x = 1.678220111;
        double y = 6.335873621;
        Coords a = new Coords(x, y, null);
        Coords b = new Coords(x, y, null);

        assertEquals(a, b);
    }

    @Test
    public void testNotEquals() {
        double x1 = 1.678220111;
        double y1 = 6.335873621;
        Coords a = new Coords(x1, y1, null);
        double x2 = 1.678229111;
        double y2 = 6.335873681;
        Coords b = new Coords(x2, y2, null);

        assertNotEquals(a, b);
    }

    @Test
    public void ovalsIntersect_equalY() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getWidth()/2 + medium.getWidth()/2 - 1;
        Coords a = new Coords(0, 0, null);
        Coords b = new Coords(dist, 0, null);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertTrue(ovalsIntersect);
    }

    @Test
    public void ovalsIntersectOnEdge_equalY() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getWidth()/2 + medium.getWidth()/2;
        Coords a = new Coords(0, 0, null);
        Coords b = new Coords(dist, 0, null);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertTrue(ovalsIntersect);
    }

    @Test
    public void ovalsNotIntersect_equalY() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getWidth()/2 + medium.getWidth()/2 + 1;
        Coords a = new Coords(0, 0, null);
        Coords b = new Coords(dist, 0, null);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertFalse(ovalsIntersect);
    }

    @Test
    public void ovalsIntersect_equalX() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getHeight()/2 + medium.getHeight()/2 - 1;
        Coords a = new Coords(0, 0, null);
        Coords b = new Coords(0, dist, null);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertTrue(ovalsIntersect);
    }

    @Test
    public void ovalsIntersectOnEdge_equalX() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getHeight()/2 + medium.getHeight()/2;
        Coords a = new Coords(0, 0, null);
        Coords b = new Coords(0, dist, null);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertTrue(ovalsIntersect);
    }

    @Test
    public void ovalsNotIntersect_equalX() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        double dist = small.getHeight()/2 + medium.getHeight()/2 + 1;
        Coords a = new Coords(0, 0, null);
        Coords b = new Coords(0, dist, null);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertFalse(ovalsIntersect);
    }

    @Test
    public void ovalsIntersect_oneInsideAnother() {
        CreatureSize small = CreatureSize.S;
        CreatureSize medium = CreatureSize.M;
        Coords a = new Coords(0, 0, null);
        Coords b = new Coords(0, 0, null);

        boolean ovalsIntersect = Coords.ovalsIntersect(a, small, b, medium);
        assertTrue(ovalsIntersect);
    }
}