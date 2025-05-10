package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PImage;
import processing.data.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class HoleTest {
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
    void testHoleColor() {
        Hole hole = new Hole(0, 0, '1', null);
        assertEquals('1', hole.getColor());
    }

    @Test
    void testContainsPoint() {
        Hole hole = new Hole(0, 0, '1', null);
        assertTrue(hole.containsPoint(16, 64));
        assertFalse(hole.containsPoint(15, 63));
    }

    class TestApp extends App {
        boolean spriteCalled = false;
        boolean imageCalled = false;
        String spriteName;
        PImage image;
        float imageX, imageY;

        @Override
        public PImage getSprite(String s) {
            spriteCalled = true;
            spriteName = s;
            return image; // Returning a mock PImage directly
        }

        @Override
        public void image(PImage img, float x, float y) {
            imageCalled = true;
            image = img;
            imageX = x;
            imageY = y;
        }
    }

    @Test
    void testDrawWithHoleColor() {
        // Arrange
        TestApp testApp = new TestApp();
        testApp.image = new PImage(); // Assigning a mock image

        char holeColor = 1; // Example hole color
        Hole hole = new Hole(4, 2, holeColor,level); // Replace Hole with your class name

        // Act
        hole.draw(testApp);

        // Assert
        assertTrue(testApp.spriteCalled, "getSprite should be called");
        assertEquals("hole" + holeColor, testApp.spriteName,
                "getSprite should be called with the correct sprite name");

        assertTrue(testApp.imageCalled, "image should be called");
        assertEquals(4 * App.CELLSIZE, testApp.imageX, 0.01, "X coordinate should match");
        assertEquals(2 * App.CELLSIZE + App.TOPBAR, testApp.imageY, 0.01, "Y coordinate should match");
    }

}
