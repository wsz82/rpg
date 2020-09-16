package io.wsz.model.animation;

import io.wsz.model.animation.creature.CreatureAnimation;
import org.junit.jupiter.api.Test;

import static io.wsz.model.sizes.Paths.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreatureAnimationTest {

    @Test
    void getMoveSide() {
        CreatureAnimation creatureAnimation = new CreatureAnimation("path", IDLE);
        String moveDirection;

        double xFrom = 5;
        double yFrom = 5;
        double xTo = 5;
        double yTo = 1;
        moveDirection = creatureAnimation.getMoveDirection(xFrom, yFrom, xTo, yTo);
        assertEquals(N, moveDirection);

        xTo = 9;
        yTo = 1;
        moveDirection = creatureAnimation.getMoveDirection(xFrom, yFrom, xTo, yTo);
        assertEquals(NE, moveDirection);

        xTo = 9;
        yTo = 5;
        moveDirection = creatureAnimation.getMoveDirection(xFrom, yFrom, xTo, yTo);
        assertEquals(E, moveDirection);

        xTo = 9;
        yTo = 9;
        moveDirection = creatureAnimation.getMoveDirection(xFrom, yFrom, xTo, yTo);
        assertEquals(SE, moveDirection);

        xTo = 5;
        yTo = 9;
        moveDirection = creatureAnimation.getMoveDirection(xFrom, yFrom, xTo, yTo);
        assertEquals(S, moveDirection);

        xTo = 1;
        yTo = 9;
        moveDirection = creatureAnimation.getMoveDirection(xFrom, yFrom, xTo, yTo);
        assertEquals(SW, moveDirection);

        xTo = 1;
        yTo = 5;
        moveDirection = creatureAnimation.getMoveDirection(xFrom, yFrom, xTo, yTo);
        assertEquals(W, moveDirection);

        xTo = 1;
        yTo = 1;
        moveDirection = creatureAnimation.getMoveDirection(xFrom, yFrom, xTo, yTo);
        assertEquals(NW, moveDirection);
    }
}