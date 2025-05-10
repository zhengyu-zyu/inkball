package inkball;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

public class AppTest {
    App app = new App();
    private Level level;
    private Ball ball;

    @BeforeEach
    public void setUp() {
        try {
            // Load the actual config file (simulating the file loading process)
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
    public void testSetUp() {
        // Ensure level and ball are properly initialized
        assertNotNull(level, "Level should be initialized");
        assertNotNull(ball, "Ball should be initialized");

        // Check if the ball's position is correct
        assertEquals(1, ball.getX(), "Ball X position should be 1");
        assertEquals(1, ball.getY(), "Ball Y position should be 1");

        // Verify the level index and loaded file
        assertEquals(1, level.getLevelIndex(), "Level index should be 1");

        // Add more assertions for ball's behavior and properties
        assertEquals('1', ball.getColorCode(), "Ball color code should be '1' (orange)");
    }

    @Test
    public void testSettings() {
        assertEquals(100, app.getWidth(), "Window width should match constant");
        assertEquals(100, app.getHeight(), "Window height should match constant");
    }


    @Test
    void testConstructorInitializesConfigPath() {

        String expectedConfigPath = "config.json";

        // Assuming there's a method to get the configPath, or you can make it public for testing
        assertEquals(expectedConfigPath, app.getConfigPath());
    }

    @Test
    public void testGameStateTransition() {

        app.gameState = App.GameState.PLAYING;
        app.togglePause();
        assertEquals(App.GameState.PAUSED, app.gameState, "Game state should be PAUSED after toggling pause");
        app.togglePause();
        assertEquals(App.GameState.PLAYING, app.gameState, "Game state should be PLAYING after toggling pause again");
    }


    @Test
    public void testKeyPressedRestartsLevelOnR() {
        app.key = 'r';
        assertTrue(app.gameState.equals(App.GameState.PLAYING), "The level should be restarted when 'r' is pressed.");
    }

    @Test
    public void testKeyPressedTogglesPauseOnSpace() {
        app.key = ' ';
        app.togglePause();
        assertTrue(app.gameState.equals(App.GameState.PAUSED), "The game should be paused after pressing space.");
        app.key = ' ';
        app.togglePause();
        assertTrue(app.gameState.equals(App.GameState.PLAYING), "The game should be unpaused after pressing space again.");
    }

    @Test
    public void testCalculateTotalScoreWithNonZeroScores() {

        // Simulate scores for different levels
        app.levelScores[0] = 10;
        app.levelScores[1] = 20;
        app.levelScores[2] = 30;

        int totalScore = app.calculateTotalScore();
        assertEquals(60, totalScore, "Total score should be the sum of level scores.");
    }

}


