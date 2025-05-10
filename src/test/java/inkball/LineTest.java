package inkball;

import org.junit.jupiter.api.Test;
import processing.core.PVector;
import static org.junit.jupiter.api.Assertions.*;


public class LineTest {
    @Test
    public void testNormalForStandardLine() {
        PVector start = new PVector(0, 0);
        PVector end = new PVector(0, 10);
        Line line = new Line(start, end);

        // For a vertical line, the normal should be horizontal (-1, 0)
        PVector expectedNormal = new PVector(-1, 0);
        PVector actualNormal = line.getNormal(null);

        assertEquals(expectedNormal.x, actualNormal.x, 0.001);
        assertEquals(expectedNormal.y, actualNormal.y, 0.001);
    }

    @Test
    public void testNormalForDot() {
        PVector start = new PVector(5, 5);
        Line line = new Line(start, start);  // Dot, zero-length line

        // With ballPosition null, normal should default to (0, 1)
        PVector expectedNormal = new PVector(0, 1);
        PVector actualNormal = line.getNormal(null);

        assertEquals(expectedNormal.x, actualNormal.x, 0.001);
        assertEquals(expectedNormal.y, actualNormal.y, 0.001);
    }

    @Test
    public void testDistanceToPoint() {
        PVector start = new PVector(0, 0);
        PVector end = new PVector(10, 0);
        Line line = new Line(start, end);

        // Point directly above the line
        PVector pointAbove = new PVector(5, 5);
        float distance = line.distanceToPoint(pointAbove);

        // Expected distance is 5 units
        assertEquals(5, distance, 0.001);
    }

    @Test
    public void testDistanceToDot() {
        PVector start = new PVector(5, 5);
        Line dot = new Line(start, start);  // Zero-length line (dot)

        // Point near the dot
        PVector point = new PVector(6, 6);
        float distance = dot.distanceToPoint(point);

        // Expected distance is the Euclidean distance between the point and the dot
        assertEquals(Math.sqrt(2), distance, 0.001);
    }

    @Test
    public void testLength() {
        PVector start = new PVector(0, 0);
        PVector end = new PVector(3, 4);
        Line line = new Line(start, end);

        // The length should be 5 units (3-4-5 triangle)
        assertEquals(5, line.length(), 0.001);
    }

    @Test
    public void testMidpoint() {
        PVector start = new PVector(0, 0);
        PVector end = new PVector(10, 0);
        Line line = new Line(start, end);

        // Midpoint should be (5, 0)
        PVector expectedMidpoint = new PVector(5, 0);
        PVector actualMidpoint = line.getMidpoint();

        assertEquals(expectedMidpoint.x, actualMidpoint.x, 0.001);
        assertEquals(expectedMidpoint.y, actualMidpoint.y, 0.001);
    }
}
