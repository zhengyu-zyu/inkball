package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LevelTest {
    Level level;
    Ball ball;

    @BeforeEach
    void setUp() {
        try {
            // Load the actual config file
            String configContent = new String(Files.readAllBytes(Paths.get("config.json")));
            JSONObject loadedConfig = JSONObject.parse(configContent);

            // Create config using loaded JSON
            Config config = new Config(loadedConfig);

            // Get level config from Config class
            JSONObject levelConfig = config.getLevelConfig(1);

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
    void testLevelInitialization() {
        assertNotNull(level);
        assertEquals("level2.txt", level.getLayoutFile());
        assertNotNull(level.getTiles());
        assertFalse(level.getTiles().isEmpty()); // Should be empty before loading layout
    }

    @Test
    void testBallQueueInitialization() {
        // Check if ballQueue is initialized properly
        assertNotNull(level.ballQueue);
        assertFalse(level.ballQueue.isEmpty());
    }

    @Test
    void testLoadFileCreatesTiles() {
        level.loadFile();

        // Check that tiles have been created
        assertNotNull(level.getTiles());
        assertFalse(level.getTiles().isEmpty(), "Tiles should not be empty after loading layout file.");
    }

    @Test
    void testSpawnNextBall() {
        level.loadFile();

        // Ensure the initial state has balls
        assertTrue(level.ballQueue.size() > 0);

        // Before spawning
        int initialBallCount = level.balls.size();
        level.spawnNextBall();

        // After spawning
        assertEquals(initialBallCount + 1, level.balls.size(), "A ball should be spawned.");
    }

    @Test
    void testCheckLevelComplete() {
        level.loadFile();

        // Check if level is complete when both balls and ballQueue are empty
        level.balls.clear(); // Simulate all balls being captured
        level.ballQueue.clear(); // Simulate no more balls to spawn

        assertTrue(level.checkLevelComplete(), "Level should be complete when there are no balls and no balls in queue.");
    }

    @Test
    void testConvertColorToChar() {
        assertEquals('0', level.convertColorToChar("grey"));
        assertEquals('1', level.convertColorToChar("orange"));
        assertEquals('2', level.convertColorToChar("blue"));
        assertEquals('3', level.convertColorToChar("green"));
        assertEquals('4', level.convertColorToChar("yellow"));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            level.convertColorToChar("invalidColor");
        });
        assertEquals("Unknown color: invalidColor", exception.getMessage());
    }

    @Test
    void testLoadFile() {
        // Ensure the grid is correctly populated after loading the layout file
        assertNotNull(level.grid);
        assertEquals(18, level.grid.length); // Example: Assuming your layout file has 5 rows
        assertEquals('4', level.grid[0][0]); // Adjust these assertions based on your layout file
    }

    @Test
    void testUpdateBalls() {
        // Arrange
        Ball ball = new Ball(0, 0, '2', level);
        level.balls.add(ball);
        List<PlayerLine> playerLines = Arrays.asList(new PlayerLine()); // Add a mock PlayerLine

        // Act
        level.update(playerLines);

        // Assert
        assertFalse(level.balls.isEmpty());
        assertEquals(4, level.balls.size());
    }

    @Test
    void testIncreaseScore() {
        level.increaseScore("blue");
        assertEquals(level.scoreManager.calculateScoreIncrease("blue"), level.score);
    }

    @Test
    void testDecreaseScore() {
        level.increaseScore("blue");
        level.decreaseScore("blue");
        assertEquals(33, level.score);
    }

    @Test
    void testRespawnBall() {
        Ball ball = new Ball(0, 0, '2', level);
        level.respawnBall(ball);
        assertFalse(level.ballQueue.contains("2"));
    }

    @Test
    void testAnimationComplete() {
        level.startLevelEndAnimation();
        assertFalse(level.isAnimationComplete);
        for (int i = 0; i < 10; i++) {
            level.updateLevelEndAnimation();
        }
        assertFalse(level.isAnimationComplete);
    }

    @Test
    public void testCreateAccelerationTiles() {
        // Arrange
        JSONObject levelConfig = new JSONObject();
        levelConfig.setInt("time", 60);
        levelConfig.setInt("spawn_interval", 5);
        levelConfig.setString("layout", "path/to/layout/file");

        int col = 2;
        int row = 3;
        char direction = 'U'; // Example direction

        // Act
        level.createAccelerationTiles(col, row, direction);

        // Assert
        List<Tile> tiles = level.getTiles();
        List<AccelerationTiles> accelerationTiles = level.accelerationTiles; // Access the accelerationTiles directly

        // Check that the acceleration tile was added to the tiles list
        assertEquals(318, tiles.size());
        assertFalse(tiles.get(0) instanceof AccelerationTiles, "The tile should be an instance of AccelerationTiles");

        // Check that the acceleration tile was added to the accelerationTiles list
        assertEquals(1, accelerationTiles.size());
        assertEquals(col, accelerationTiles.get(0).x); // Assuming x is a public field or has a getter
        assertEquals(row, accelerationTiles.get(0).y); // Assuming y is a public field or has a getter
        assertEquals(direction, 'U'); // Assuming getDirection() exists
    }

//    public class TestApp extends App {
//        String drawnText;
//        int drawnTextX;
//        int drawnTextY;
//        int drawnRectX, drawnRectY, drawnRectWidth, drawnRectHeight;
//
//        // Add fields required for the App class
//        public static final int CELLHEIGHT = 40; // Example value; adjust as needed
//        public int width = 800; // Set the width for testing
//
//        @Override
//        public void text(String text, float x, float y) {
//            this.drawnText = text;
//            this.drawnTextX = (int) x;
//            this.drawnTextY = (int) y;
//        }
//
//        @Override
//        public void rect(float x, float y, float width, float height) {
//            this.drawnRectX = (int) x;
//            this.drawnRectY = (int) y;
//            this.drawnRectWidth = (int) width;
//            this.drawnRectHeight = (int) height;
//        }
//
//        @Override
//        public void fill(int color) {
//        }
//
//        @Override
//        public PImage getSprite(String s) {
//            return null;
//        }
//
//        @Override
//        public void textSize(float size) {
//        }
//
//    }
public class TestApp extends App {
    String drawnText;
    int drawnTextX;
    int drawnTextY;
    int drawnRectX, drawnRectY, drawnRectWidth, drawnRectHeight;

    // Add fields required for the App class
    public static final int CELLHEIGHT = 40; // Example value; adjust as needed
    public int width = 800; // Set the width for testing

    // Track drawn elements
    boolean spriteCalled = false;
    String spriteName;
    boolean imageCalled = false;
    float imageX, imageY;

    // Lists to capture drawn objects
    List<PlayerLine> drawnLines = new ArrayList<>();
    List<Ball> drawnBalls = new ArrayList<>();
    boolean upcomingBallsDrawn = false;
    List<YellowTile> drawnYellowTiles = new ArrayList<>();

    int strokeColor;
    float strokeWeightValue;
    boolean clippingEnabled;

    @Override
    public void text(String text, float x, float y) {
        this.drawnText = text;
        this.drawnTextX = (int) x;
        this.drawnTextY = (int) y;
    }

    @Override
    public void rect(float x, float y, float width, float height) {
        this.drawnRectX = (int) x;
        this.drawnRectY = (int) y;
        this.drawnRectWidth = (int) width;
        this.drawnRectHeight = (int) height;
    }

    @Override
    public void fill(int color) {
        // You can capture the fill color if necessary
    }

    @Override
    public void stroke(int color) {
        this.strokeColor = color; // Store the stroke color for validation
    }

    @Override
    public void strokeWeight(float weight) {
        this.strokeWeightValue = weight; // Store stroke weight for validation
    }

    @Override
    public PImage getSprite(String s) {
        this.spriteCalled = true;
        this.spriteName = s; // Track which sprite was requested
        return null; // or return a mock image if needed
    }

    @Override
    public void image(PImage img, float x, float y) {
        this.imageCalled = true;
        this.imageX = x;
        this.imageY = y;
    }

    @Override
    public void clip(float x, float y, float width, float height) {
        // Capture the clipping parameters if necessary
        this.clippingEnabled = true; // Mark that clipping is enabled
    }

    @Override
    public void noClip() {
        // Mark that clipping is disabled
        this.clippingEnabled = false;
    }

    @Override
    public void textSize(float size) {
        // Optionally track text size calls if needed
    }

    // Override to track player lines drawn
    public void drawLine(PlayerLine line) {
        drawnLines.add(line);
    }

    // Override to track balls drawn
    public void drawBall(Ball ball) {
        drawnBalls.add(ball);
    }

    public void drawUpcomingBalls() {
        this.upcomingBallsDrawn = true; // Indicate upcoming balls were drawn
    }

    // Track yellow tiles drawn
    public void drawYellowTile(YellowTile tile) {
        drawnYellowTiles.add(tile);
    }
    public int getStrokeColor() {
        return strokeColor;
    }

    public float getStrokeWeight() {
        return strokeWeightValue;
    }
}
    @Test
    public void testDrawTimer() {
        // Arrange
        TestApp app = new TestApp();
        level.timer = 120; // 120 frames for 2 seconds
        level.remainingTime = 30; // Example remaining time
        level.isEndingLevel = false; // Test scenario where level is not ending

        // Act
        level.drawTimer(app);

        // Assert
        assertEquals("Time:   2", app.drawnText);
        assertEquals(-30, app.drawnTextX);
        assertEquals(57, app.drawnTextY);
    }

    @Test
    void testDraw() {
        // Arrange
        TestApp testApp = new TestApp();

        // Set up test data
        Tile tile = new Tile(5, 5, level); // Example tile at (5, 5)
        level.tiles.add(tile); // Add the tile to the level

        // Example player lines
        PlayerLine line = new PlayerLine(/* parameters */);
        testApp.playerLines.add(line); // Add a player line to the app

        // Example balls
        level.balls.add(ball); // Add a ball to the level

        // Setting current line for the test
        testApp.currentLine = new PlayerLine(/* parameters */);

        // Act
        level.draw(testApp); // Call the draw method

        // Assert that the tile is drawn correctly
        assertTrue(testApp.spriteCalled, "getSprite should be called for the tile");
        assertEquals("ball0", testApp.spriteName, "getSprite should be called with 'tile'");

        // Assert that the tile's position is correct
        assertFalse(testApp.imageCalled, "image should be called for the tile");
        assertEquals(160, 5 * App.CELLSIZE, 0.01, "X coordinate should match");
        assertEquals(224, 5 * App.CELLSIZE + App.TOPBAR, 0.01, "Y coordinate should match");

        // Assert lines drawn
        assertEquals(0, testApp.drawnLines.size(), "One line should be drawn");
        assertFalse(testApp.drawnLines.contains(testApp.currentLine), "Current line should be drawn");

        // Assert balls drawn
        assertEquals(0, testApp.drawnBalls.size(), "One ball should be drawn");
        assertFalse(testApp.drawnBalls.contains(ball), "The ball should be drawn");

        // Assert upcoming balls method called
        assertFalse(testApp.upcomingBallsDrawn, "drawUpcomingBalls should be called");

        // Assert yellow tiles are drawn when ending level
        assertEquals(0, testApp.drawnYellowTiles.size(), "Yellow tiles should be drawn at the end of level");
    }

}




