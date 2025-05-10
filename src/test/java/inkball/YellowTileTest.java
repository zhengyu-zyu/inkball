package inkball;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PImage;
import processing.data.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;


public class YellowTileTest {
    private YellowTile yellowTile;
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
    void testMoveRight() {
        // Move to (0, 0) to set up for moving right
        yellowTile = new YellowTile(0, 0); // Start at (0, 0)

        // Now it should be at (0, 0)
        assertEquals(0, yellowTile.getX());
        assertEquals(0, yellowTile.getY());

        // Move right to (1, 0)
        yellowTile.move();
        assertEquals(1, yellowTile.getX(), "Expected x to be 1");
        assertEquals(0, yellowTile.getY(), "Expected y to remain 0");
    }

    @Test
    void testMoveDown() {
        // Move to (App.BOARD_WIDTH - 1, 0) to set up for moving down
        yellowTile = new YellowTile(App.BOARD_WIDTH - 1, 0); // Start at (App.BOARD_WIDTH - 1, 0)

        // Now it should be at (App.BOARD_WIDTH - 1, 0)
        assertEquals(App.BOARD_WIDTH - 1, yellowTile.getX());
        assertEquals(0, yellowTile.getY());

        // Move down to (App.BOARD_WIDTH - 1, 1)
        yellowTile.move();
        assertEquals(App.BOARD_WIDTH - 1, yellowTile.getX(), "Expected x to remain at App.BOARD_WIDTH - 1");
        assertEquals(1, yellowTile.getY(), "Expected y to be 1");
    }

    @Test
    void testMoveLeft() {
        // Move to (App.BOARD_WIDTH - 1, App.BOARD_HEIGHT - 3) to set up for moving left
        yellowTile = new YellowTile(App.BOARD_WIDTH - 1, App.BOARD_HEIGHT - 3); // Start at (App.BOARD_WIDTH - 1, App.BOARD_HEIGHT - 3)

        // Now it should be at (App.BOARD_WIDTH - 1, App.BOARD_HEIGHT - 3)
        assertEquals(App.BOARD_WIDTH - 1, yellowTile.getX());
        assertEquals(App.BOARD_HEIGHT - 3, yellowTile.getY());

        // Move left to (App.BOARD_WIDTH - 2, App.BOARD_HEIGHT - 3)
        yellowTile.move();
        assertEquals(App.BOARD_WIDTH - 2, yellowTile.getX(), "Expected x to be App.BOARD_WIDTH - 2");
        assertEquals(App.BOARD_HEIGHT - 3, yellowTile.getY(), "Expected y to remain App.BOARD_HEIGHT - 3");
    }

    @Test
    void testMoveUp() {
        // Move to (0, 1) to set up for moving up
        yellowTile = new YellowTile(0, 1); // Start at (0, 1)

        // Now it should be at (0, 1)
        assertEquals(0, yellowTile.getX());
        assertEquals(1, yellowTile.getY());

        // Move up to (0, 0)
        yellowTile.move();
        assertEquals(0, yellowTile.getX(), "Expected x to remain 0");
        assertEquals(0, yellowTile.getY(), "Expected y to be 0");
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
    public void testDraw() {
        // Create an instance of the TestApp
        TestApp testApp = new TestApp();

        // Create an instance of the YellowTile class
        YellowTile yellowTile = new YellowTile(3,5); // Assuming YellowTile has suitable constructors


        // Call the draw method
        yellowTile.draw(testApp);

        // Assertions similar to your example
        assertTrue(testApp.spriteCalled, "getSprite should be called");
        assertEquals("wall4", testApp.spriteName, "getSprite should be called with 'wall4'");

        assertTrue(testApp.imageCalled, "image should be called");
        assertEquals(3 * App.CELLSIZE, testApp.imageX, 0.01, "X coordinate should match");
        assertEquals(5 * App.CELLSIZE + App.TOPBAR, testApp.imageY, 0.01, "Y coordinate should match");
    }

}
