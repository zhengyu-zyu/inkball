package inkball;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;
import java.util.List;

public class PlayerLine {
    private List<Line> segments;
    private static final float LINE_THICKNESS = 10;
    private static final int LINE_COLOR = 0xFF000000;

    private static final float MIN_X = 0;
    private static final float MIN_Y = App.TOPBAR;
    private static final float MAX_X = App.WIDTH;
    private static final float MAX_Y = App.HEIGHT;

    public PlayerLine() {
        segments = new ArrayList<>();
    }

    public void addPoint(float x, float y) {
        if (x < MIN_X || x > MAX_X || y < MIN_Y || y > MAX_Y) {
            return;
        }

        PVector newPoint = new PVector(x, y);

        if (!segments.isEmpty()) {
            Line lastSegment = segments.get(segments.size() - 1);
            PVector lastPoint = lastSegment.getEnd();

            // Check if the new point is different from the last point
            if (!newPoint.equals(lastPoint)) {
                segments.add(new Line(lastPoint, newPoint));
            }
        } else {
            // For the first point, create a segment with the same start and end point
            segments.add(new Line(newPoint, newPoint));
        }
    }

    public void draw(PApplet app) {
        app.stroke(LINE_COLOR);
        app.strokeWeight(LINE_THICKNESS);
        for (Line segment : segments) {
            segment.draw(app);
        }
    }

    public boolean intersects(float x, float y) {
        PVector point = new PVector(x, y);
        for (Line segment : segments) {
            if (segment.distanceToPoint(point) < LINE_THICKNESS) {
                return true;
            }
        }
        return false;
    }

    public List<Line> getSegments() {
        return new ArrayList<>(segments);
    }

}
