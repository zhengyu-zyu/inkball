package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerLineTest {
    private Level level;

    @BeforeEach
    void setUp() {
        try {
            // Load the actual config file
            String configContent = new String(Files.readAllBytes(Paths.get("config.json")));
            JSONObject loadedConfig = JSONObject.parse(configContent);

            // Create config using loaded JSON
            Config config = new Config(loadedConfig);

            // Get level config from Config class
            JSONObject levelConfig = config.getLevelConfig(1); // Using level index 1

            // Initialize level
            level = new Level(levelConfig, config, 1);
            level.loadFile();

        } catch (Exception e) {
            fail("Failed to load configuration: " + e.getMessage());
        }
    }


    @Test
    public void testAddPointWithinBounds() {
        PlayerLine playerLine = new PlayerLine();

        // Add a point within the bounds
        float x = 100;
        float y = App.TOPBAR + 50;
        playerLine.addPoint(x, y);

        // Verify that a segment has been created
        List<Line> segments = playerLine.getSegments();
        assertEquals(1, segments.size());

        Line firstSegment = segments.get(0);
        PVector expectedPoint = new PVector(x, y);

        // For the first point, the start and end of the line segment should be the same
        assertEquals(expectedPoint, firstSegment.getStart());
        assertEquals(expectedPoint, firstSegment.getEnd());
    }

    @Test
    public void testAddPointOutOfBounds() {
        PlayerLine playerLine = new PlayerLine();

        // Add a point outside the bounds
        float x = -10; // Out of bounds
        float y = App.TOPBAR + 50;
        playerLine.addPoint(x, y);

        // Verify that no segments are created
        List<Line> segments = playerLine.getSegments();
        assertEquals(0, segments.size());
    }

    @Test
    public void testAddMultiplePoints() {
        PlayerLine playerLine = new PlayerLine();

        // Add two points
        playerLine.addPoint(50, App.TOPBAR + 50);
        playerLine.addPoint(100, App.TOPBAR + 100);

        // Verify that two segments are created (even though one segment is a dot)
        List<Line> segments = playerLine.getSegments();
        assertEquals(2, segments.size());

        Line firstSegment = segments.get(0);
        Line secondSegment = segments.get(1);

        // Verify first point is a dot (start and end points are the same)
        assertEquals(new PVector(50, App.TOPBAR + 50), firstSegment.getStart());
        assertEquals(new PVector(50, App.TOPBAR + 50), firstSegment.getEnd());

        // Verify the second line segment is created between the two points
        assertEquals(new PVector(50, App.TOPBAR + 50), secondSegment.getStart());
        assertEquals(new PVector(100, App.TOPBAR + 100), secondSegment.getEnd());
    }

    @Test
    public void testIntersects() {
        PlayerLine playerLine = new PlayerLine();

        // Add some points to create line segments
        playerLine.addPoint(50, App.TOPBAR + 50);
        playerLine.addPoint(100, App.TOPBAR + 100);
        playerLine.addPoint(150, App.TOPBAR + 150);

        // Check intersection with a point near the second line segment
        assertTrue(playerLine.intersects(105, App.TOPBAR + 105));

        // Check no intersection far from the line
        assertFalse(playerLine.intersects(200, App.TOPBAR + 200));
    }

    @Test
    public void testEmptyPlayerLine() {
        PlayerLine playerLine = new PlayerLine();

        // Test that the segments list is initially empty
        assertEquals(0, playerLine.getSegments().size());

        // Test intersection with an empty line (should return false)
        assertFalse(playerLine.intersects(50, App.TOPBAR + 50));
    }
}
