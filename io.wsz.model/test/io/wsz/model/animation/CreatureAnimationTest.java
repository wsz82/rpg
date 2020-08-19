package io.wsz.model.animation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CreatureAnimationTest {

    @Test
    void getMoveSide() {
        CreatureAnimation creatureAnimation = new CreatureAnimation("path");
        MoveDirection moveDirection;

        double xFrom = 5;
        double yFrom = 5;
        double xTo = 5;
        double yTo = 1;
        moveDirection = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveDirection.UP, moveDirection);

        xTo = 9;
        yTo = 1;
        moveDirection = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveDirection.UP_RIGHT, moveDirection);

        xTo = 9;
        yTo = 5;
        moveDirection = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveDirection.RIGHT, moveDirection);

        xTo = 9;
        yTo = 9;
        moveDirection = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveDirection.DOWN_RIGHT, moveDirection);

        xTo = 5;
        yTo = 9;
        moveDirection = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveDirection.DOWN, moveDirection);

        xTo = 1;
        yTo = 9;
        moveDirection = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveDirection.DOWN_LEFT, moveDirection);

        xTo = 1;
        yTo = 5;
        moveDirection = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveDirection.LEFT, moveDirection);

        xTo = 1;
        yTo = 1;
        moveDirection = creatureAnimation.getMoveSide(xFrom, yFrom, xTo, yTo);
        assertEquals(MoveDirection.UP_LEFT, moveDirection);
    }
}