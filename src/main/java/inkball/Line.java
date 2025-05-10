package inkball;

import processing.core.PVector;
import processing.core.PApplet;

public class Line {
    private PVector start;
    private PVector end;
    private PVector normal;

    public Line(PVector start, PVector end) {
        this.start = start;
        this.end = end;
        calculateNormal(null);
    }

    private void calculateNormal(PVector ballPosition) {
        PVector direction = PVector.sub(end, start);
        if (direction.mag() == 0) {
            // For zero-length lines (dots), set an arbitrary normal
//            normal = PVector.sub(ballPosition, start).normalize();
            if (ballPosition != null) {
                normal = PVector.sub(ballPosition, start).normalize();
            } else {
                // If no ball position is provided, set a default normal
                normal = new PVector(0, 1);
            }
        } else {
            normal = new PVector(-direction.y, direction.x).normalize();
        }
    }

    public float distanceToPoint(PVector point) {
        if (start.equals(end)) {
            return PVector.dist(start, point);
        }
        PVector AP = PVector.sub(point, start);
        PVector AB = PVector.sub(end, start);
        float projection = AP.dot(AB) / AB.magSq();
        projection = PApplet.constrain(projection, 0, 1);
        PVector closest = PVector.add(start, PVector.mult(AB, projection));
        return PVector.dist(point, closest);
    }

    public void draw(PApplet app) {
        if (start.equals(end)) {
            // Draw a point for zero-length lines (dots)
            app.point(start.x, start.y);
        } else {
            app.line(start.x, start.y, end.x, end.y);
        }
    }

    public PVector getStart() {
        return start.copy();
    }
    public PVector getEnd() {
        return end.copy();
    }
    public PVector getNormal(PVector ballPosition) {
        if (start.equals(end)) {
            // Recalculate normal for dots based on current ball position
            calculateNormal(ballPosition);
        }
        return normal.copy();
    }
    public PVector getMidpoint() {
        return PVector.add(start, end).div(2);
    }
    public float length() {
        return PVector.dist(start, end);
    }
}
