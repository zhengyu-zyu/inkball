package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PImage;
import processing.data.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class AccelerationTilesTest {


    @Test
    void testDirectionToString() {
        AccelerationTiles tile = new AccelerationTiles(0, 0, 'U', null);
        assertEquals("up", tile.changeDirectionToString('U'));
        assertEquals("down", tile.changeDirectionToString('D'));
        assertEquals("left", tile.changeDirectionToString('L'));
        assertEquals("right", tile.changeDirectionToString('R'));
    }

    @Test
    void testIllegalDirection() {
        AccelerationTiles tile = new AccelerationTiles(0, 0, 'U', null);
        assertThrows(IllegalArgumentException.class, () -> tile.changeDirectionToString('X'));
    }

    @Test
    void testSetSpriteAndGetSprite() {
        App app = new App();

        // Create dummy images for each direction
        PImage upImage = new PImage();
        PImage downImage = new PImage();
        PImage leftImage = new PImage();
        PImage rightImage = new PImage();

        // Set sprites for each direction
        app.setSprite("up", upImage);
        app.setSprite("down", downImage);
        app.setSprite("left", leftImage);
        app.setSprite("right", rightImage);

        // Retrieve and assert for each direction
        assertSame(upImage, app.getSprite("up"), "The sprite for 'up' should be correct");
        assertSame(downImage, app.getSprite("down"), "The sprite for 'down' should be correct");
        assertSame(leftImage, app.getSprite("left"), "The sprite for 'left' should be correct");
        assertSame(rightImage, app.getSprite("right"), "The sprite for 'right' should be correct");

        // Update images for each direction
        PImage newUpImage = new PImage();
        PImage newDownImage = new PImage();
        PImage newLeftImage = new PImage();
        PImage newRightImage = new PImage();

        app.setSprite("up", newUpImage);
        app.setSprite("down", newDownImage);
        app.setSprite("left", newLeftImage);
        app.setSprite("right", newRightImage);

        // Assert updated sprites for each direction
        assertSame(newUpImage, app.getSprite("up"), "The updated sprite for 'up' should be correct");
        assertSame(newDownImage, app.getSprite("down"), "The updated sprite for 'down' should be correct");
        assertSame(newLeftImage, app.getSprite("left"), "The updated sprite for 'left' should be correct");
        assertSame(newRightImage, app.getSprite("right"), "The updated sprite for 'right' should be correct");
    }

    @Test
    void testSetSpriteWithInvalidInputs() {
        // Arrange
        App app = new App();
        PImage dummyImage = new PImage();

        // Assert invalid key
        assertThrows(IllegalArgumentException.class, () -> {
            app.setSprite("", dummyImage);
        }, "Setting a sprite with an empty key should throw IllegalArgumentException");

        // Assert null key
        assertThrows(IllegalArgumentException.class, () -> {
            app.setSprite(null, dummyImage);
        }, "Setting a sprite with a null key should throw IllegalArgumentException");

        // Assert null image
        assertThrows(IllegalArgumentException.class, () -> {
            app.setSprite("up", null);
        }, "Setting a sprite with a null image should throw IllegalArgumentException");
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
    void testDrawWithDirection() {
        // Arrange
        TestApp testApp = new TestApp();
        testApp.image = new PImage(); // Assigning a mock image

        AccelerationTiles obj = new AccelerationTiles(3, 5, 'U',level); // Replace SomeClass with your class name

        // Act
        obj.draw(testApp);

        // Assert
        assertTrue(testApp.spriteCalled, "getSprite should be called");
        assertEquals(obj.changeDirectionToString('U'), testApp.spriteName,
                "getSprite should be called with the correct sprite name");

        assertTrue(testApp.imageCalled, "image should be called");
        assertEquals(3 * App.CELLSIZE, testApp.imageX, 0.01, "X coordinate should match");
        assertEquals(5 * App.CELLSIZE + App.TOPBAR, testApp.imageY, 0.01, "Y coordinate should match");
    }

}
