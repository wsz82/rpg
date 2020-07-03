package io.wsz.model.stage;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
}