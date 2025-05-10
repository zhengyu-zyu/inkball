package inkball;

import processing.core.PVector;
import java.util.List;

public class Hitbox {
    private static float BALL_RADIUS = App.CELLSIZE / 2.0f;

    public static boolean checkCollision(PVector ballPosition, PVector ballVelocity, Line segment) {
        PVector p1 = segment.getStart();
        PVector p2 = segment.getEnd();

        float distanceP1 = PVector.dist(p1, PVector.add(ballPosition, ballVelocity));
        float distanceP2 = PVector.dist(p2, PVector.add(ballPosition, ballVelocity));
        float segmentLength = segment.length();

        return (distanceP1 + distanceP2) < (segmentLength + BALL_RADIUS);
    }

    public static PVector calculateNewTrajectory(PVector ballPosition,PVector ballVelocity, Line segment) {
        PVector normal = segment.getNormal(ballPosition);
        float dotProduct = ballVelocity.dot(normal);
        return PVector.sub(ballVelocity, PVector.mult(normal, 2 * dotProduct));
    }

    public static Line findCollidingSegment(PVector ballPosition, PVector ballVelocity, List<Line> segments) {
        for (Line segment : segments) {
            if (checkCollision(ballPosition, ballVelocity, segment)) {
                return segment;
            }
        }
        return null;
    }
}