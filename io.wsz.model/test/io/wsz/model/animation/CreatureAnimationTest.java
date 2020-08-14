package io.wsz.model.animation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreatureAnimationTest {

    @Test
    void getMoveSide() {
        CreatureAnimation creatureAnimation = new CreatureAnimation("path");
        MoveSide moveSide;

        double xFrom = 5;
        double yFrom = 5;
        double xTo = 5;
        double yTo = 1;
        moveSide = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveSide.UP, moveSide);

        xTo = 9;
        yTo = 1;
        moveSide = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveSide.UP_RIGHT, moveSide);

        xTo = 9;
        yTo = 5;
        moveSide = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveSide.RIGHT, moveSide);

        xTo = 9;
        yTo = 9;
        moveSide = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveSide.DOWN_RIGHT, moveSide);

        xTo = 5;
        yTo = 9;
        moveSide = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveSide.DOWN, moveSide);

        xTo = 1;
        yTo = 9;
        moveSide = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveSide.DOWN_LEFT, moveSide);

        xTo = 1;
        yTo = 5;
        moveSide = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveSide.LEFT, moveSide);

        xTo = 1;
        yTo = 1;
        moveSide = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveSide.UP_LEFT, moveSide);
    }
}