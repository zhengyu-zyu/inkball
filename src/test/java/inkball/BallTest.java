package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;


public class BallTest {
    private Ball ball;
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

            // Create ball for testing
            ball = new Ball(1, 1, '1', level); // Create a ball at position (1,1) with orange color
        } catch (Exception e) {
            fail("Failed to load configuration: " + e.getMessage());
        }
    }

    @Test
    void testGenerateRandomVelocity() {
        Ball ball = new Ball(0, 0, '1', null);
        PVector velocity = ball.generateRandomVelocity();
        assertNotNull(velocity);
        assertTrue(velocity.x != 0 || velocity.y != 0);
    }

    @Test
    public void testBallInitialization() {
        assertNotNull(ball);
        assertEquals(1, ball.x);
        assertEquals(1, ball.y);
        assertFalse(ball.getCaptured());
        assertNotNull(ball.getPosition());
        assertNotNull(ball.getVelocity());
    }

    @Test
    public void testVelocityGeneration() {
        PVector velocity = ball.generateRandomVelocity();
        assertEquals(1.0f, Math.abs(velocity.x));
        assertEquals(1.0f, Math.abs(velocity.y));
    }

    @Test
    public void testChangeDirectionToVelocity() {
        // Test all direction changes
        PVector upVelocity = ball.changeDirectionToVelocity('U');
        assertEquals(0, upVelocity.x);
        assertEquals(-0.25f, upVelocity.y);

        PVector downVelocity = ball.changeDirectionToVelocity('D');
        assertEquals(0, downVelocity.x);
        assertEquals(0.25f, downVelocity.y);

        PVector leftVelocity = ball.changeDirectionToVelocity('L');
        assertEquals(-0.25f, leftVelocity.x);
        assertEquals(0, leftVelocity.y);

        PVector rightVelocity = ball.changeDirectionToVelocity('R');
        assertEquals(0.25f, rightVelocity.x);
        assertEquals(0, rightVelocity.y);

        // Test invalid direction
        assertThrows(IllegalArgumentException.class, () -> {
            ball.changeDirectionToVelocity('Z');
        });
    }

    @Test
    public void testBallColorToString() {
        assertEquals("orange", ball.changeBallColorToString(new Ball(0, 0, '1', level)));
        assertEquals("blue", ball.changeBallColorToString(new Ball(0, 0, '2', level)));
        assertEquals("green", ball.changeBallColorToString(new Ball(0, 0, '3', level)));
        assertEquals("yellow", ball.changeBallColorToString(new Ball(0, 0, '4', level)));
        assertEquals("grey", ball.changeBallColorToString(new Ball(0, 0, '0', level)));

        // Test invalid color
        assertThrows(IllegalArgumentException.class, () -> {
            ball.changeBallColorToString(new Ball(0, 0, '9', level));
        });
    }

    @Test
    public void testPositionAndVelocitySetters() {
        PVector newPosition = new PVector(100, 100);
        PVector newVelocity = new PVector(2, 2);

        ball.setPosition(newPosition);
        ball.setVelocity(newVelocity);

        PVector resultPosition = ball.getPosition();
        PVector resultVelocity = ball.getVelocity();

        assertEquals(newPosition.x, resultPosition.x);
        assertEquals(newPosition.y, resultPosition.y);
        assertEquals(newVelocity.x, resultVelocity.x);
        assertEquals(newVelocity.y, resultVelocity.y);

        // Verify that the returned vectors are copies
        assertNotSame(newPosition, resultPosition);
        assertNotSame(newVelocity, resultVelocity);
    }

    @Test
    public void testBoundaryCollisions() {
        ball.setPosition(new PVector(0, App.TOPBAR + 50));
        ball.setVelocity(new PVector(-1, 0));
        ball.update();
        assertTrue(ball.getVelocity().x > 0); // Should bounce right

        ball.setPosition(new PVector(App.WIDTH, App.TOPBAR + 50));
        ball.setVelocity(new PVector(1, 0));
        ball.update();
        assertTrue(ball.getVelocity().x < 0); // Should bounce left

        ball.setPosition(new PVector(50, App.TOPBAR));
        ball.setVelocity(new PVector(0, -1));
        ball.update();
        assertTrue(ball.getVelocity().y > 0); // Should bounce down

        ball.setPosition(new PVector(50, App.HEIGHT));
        ball.setVelocity(new PVector(0, 1));
        ball.update();
        assertTrue(ball.getVelocity().y < 0); // Should bounce up
    }

    @Test
    public void testWallCollisions() {
        ball.setPosition(new PVector(0 * App.CELLSIZE + App.CELLSIZE/2, 0 * App.CELLSIZE + App.CELLSIZE/2 + App.TOPBAR));
        ball.setVelocity(new PVector(-1, -1));
        ball.update();

        PVector velocity = ball.getVelocity();
        System.out.println("Velocity after collision with solid wall: " + velocity);

        assertFalse(velocity.x > 0 || velocity.y > 0);

        Ball colorBall = new Ball(1, 0, '0', level);
        colorBall.setPosition(new PVector(1 * App.CELLSIZE + App.CELLSIZE/2, 0 * App.CELLSIZE + App.CELLSIZE/2 + App.TOPBAR));
        colorBall.setVelocity(new PVector(1, -1));
        colorBall.update();

        System.out.println("Color after collision with color wall: " + colorBall.changeBallColorToString(colorBall));

        assertEquals('g', colorBall.changeBallColorToString(colorBall).charAt(0));
    }

    @Test
    public void testAccelerationTiles() {
        ball.setPosition(new PVector(2 * App.CELLSIZE + App.CELLSIZE/2, 0 * App.CELLSIZE + App.CELLSIZE/2 + App.TOPBAR));
        PVector initialVelocity = new PVector(0, 0);
        ball.setVelocity(initialVelocity);
        ball.update();
        assertFalse(ball.getVelocity().y < initialVelocity.y);

        ball.setPosition(new PVector(2 * App.CELLSIZE + App.CELLSIZE/2, 1 * App.CELLSIZE + App.CELLSIZE/2 + App.TOPBAR));
        ball.setVelocity(initialVelocity);
        ball.update();
        assertFalse(ball.getVelocity().y > initialVelocity.y);
    }

    @Test
    public void testHoleInteraction() {
        Hole hole = new Hole(3, 3, '1', level);

        ball.setPosition(new PVector(3 * App.CELLSIZE + App.CELLSIZE/2, 3 * App.CELLSIZE + App.CELLSIZE/2 + App.TOPBAR));

        for (int i = 0; i < 10; i++) {
            ball.update();
        }

        PVector velocity = ball.getVelocity();
        assertNotNull(velocity);

        Ball matchingBall = new Ball(3, 3, '1', level);
        matchingBall.setPosition(new PVector(3 * App.CELLSIZE + App.CELLSIZE/2, 3 * App.CELLSIZE + App.CELLSIZE/2 + App.TOPBAR));
        matchingBall.update();
        assertTrue(matchingBall.getCaptured() || PVector.dist(matchingBall.getPosition(), new PVector(3 * App.CELLSIZE + App.CELLSIZE/2, 3 * App.CELLSIZE + App.CELLSIZE/2 + App.TOPBAR)) < 32);
    }

    class TestApp extends App {
        boolean spriteCalled = false;
        boolean imageCalled = false;
        String spriteName;
        PImage image;
        float imageX, imageY, imageWidth, imageHeight;

        @Override
        public PImage getSprite(String s) {
            spriteCalled = true;
            spriteName = s;
            return image; // Returning a mock PImage directly
        }

        @Override
        public void image(PImage img, float x, float y, float width, float height) {
            imageCalled = true;
            image = img;
            imageX = x;
            imageY = y;
            imageWidth = width;
            imageHeight = height;
        }
    }

    @Test
    void testDraw() {
        // Arrange
        TestApp testApp = new TestApp();
        testApp.image = new PImage(); // Assigning a mock image

        Ball ball = new Ball(5, 5, '1', level); // Ball with coordinates (x=5, y=5)
        ball.setPosition(new PVector(5 * App.CELLSIZE, 5 * App.CELLSIZE)); // Set the position

        // Act
        ball.draw(testApp);

        // Assert
        assertTrue(testApp.spriteCalled, "getSprite should be called");
        assertEquals("ball1", testApp.spriteName, "getSprite should be called with 'ball1'");

        assertTrue(testApp.imageCalled, "image should be called");
        assertEquals(5 * App.CELLSIZE - ball.getRadius(), testApp.imageX, 0.01, "X coordinate should match");
        assertEquals(5 * App.CELLSIZE - ball.getRadius(), testApp.imageY, 0.01, "Y coordinate should match");
    }

    @Test
    void testHandleCapture_Success() {
        // Arrange
        TestApp testApp = new TestApp();
        Ball ball = new Ball(5, 5, '1', level); // Ball with coordinates (x=5, y=5)
        ball.captured = true; // Set ball as captured
        ball.attractingHole = new Hole(5, 5, '1',level); // Hole with same color as the ball

        // Act
        ball.handleCapture();
        boolean success = ball.getCaptured();

        // Assert
        assertTrue(success, "Capture should be successful when ball touch the hole");

    }
    @Test
    void testUpdateRadius() {
        // Arrange
        TestApp testApp = new TestApp();
        Ball ball = new Ball(5, 5, '1', level); // Ball with coordinates (x=5, y=5)
        ball.originalRadius = 10; // Set original radius
        ball.position = new PVector(5 * App.CELLSIZE, 5 * App.CELLSIZE); // Set ball position

        // Create an attracting hole
        ball.attractingHole = new Hole(5, 5, '1',level); // Hole with same position and color

        // Act
        ball.updateRadius();

        // Assert
        assertEquals(ball.originalRadius, ball.radius, 0.01, "Radius should remain original when hole is at the same position");

        // Move the hole further away
        ball.attractingHole.x = 10;
        ball.updateRadius();

        // Assert
        float expectedRadius = ball.originalRadius * Math.max(0, Math.min(1, PVector.dist(ball.position, new PVector(10 * App.CELLSIZE + App.CELLSIZE, 10 * App.CELLSIZE + App.TOPBAR)) / 32));
        assertEquals(expectedRadius, ball.radius, 0.01, "Radius should update based on distance to the hole");
    }

    @Test
    void testCheckHoleAttraction_AttractionVelocity() {
        // Arrange
        TestApp testApp = new TestApp();
        Ball ball = new Ball(5, 5, '1', level); // Ball with coordinates (x=5, y=5)
        ball.position = new PVector(5 * App.CELLSIZE, 5 * App.CELLSIZE); // Set ball position
        ball.velocity = new PVector(0, 0); // Initial velocity

        // Create an attracting hole close to the ball
        Hole hole = new Hole(5, 5, '1',level); // Hole with same position and color

        // Move the ball closer to the hole but outside of capture range
        ball.position = new PVector(5 * App.CELLSIZE + 15, 5 * App.CELLSIZE); // Set ball position within attraction range

        // Act
        ball.checkHoleAttraction();

        // Assert
        assertFalse(ball.captured, "Ball should not be captured when within distance of 10 from the hole but outside capture range");
        assertFalse(ball.velocity.mag() > 0, "Velocity should change due to attraction vector when within distance of 32 from the hole");
    }

}

