package inkball;

import org.junit.jupiter.api.Test;
import processing.core.PVector;

import static org.junit.jupiter.api.Assertions.*;

public class HitboxTest {

    @Test
    void testCheckCollision() {
        Line segment = new Line(new PVector(0, 0), new PVector(10, 10));
        PVector ballPosition = new PVector(5, 5);
        PVector ballVelocity = new PVector(1, 1);
        assertTrue(Hitbox.checkCollision(ballPosition, ballVelocity, segment));
    }

    @Test
    void testCalculateNewTrajectory() {
        Line segment = new Line(new PVector(0, 0), new PVector(10, 10));
        PVector ballPosition = new PVector(5, 5);
        PVector ballVelocity = new PVector(1, 1);
        PVector newTrajectory = Hitbox.calculateNewTrajectory(ballPosition, ballVelocity, segment);
        assertNotNull(newTrajectory);
    }
}
