package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PImage;
import processing.data.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class TileTest {
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
    void testDraw() {
        // Arrange
        TestApp testApp = new TestApp();
        testApp.image = new PImage(); // Assigning a mock image

        Tile tile = new Tile(5, 5, level);  // Tile with coordinates (x=5, y=5)

        // Act
        tile.draw(testApp);

        // Assert
        assertTrue(testApp.spriteCalled, "getSprite should be called");
        assertEquals("tile", testApp.spriteName, "getSprite should be called with 'tile'");

        assertTrue(testApp.imageCalled, "image should be called");
        assertEquals(5 * App.CELLSIZE, testApp.imageX, 0.01, "X coordinate should match");
        assertEquals(5 * App.CELLSIZE + App.TOPBAR, testApp.imageY, 0.01, "Y coordinate should match");
    }
}
