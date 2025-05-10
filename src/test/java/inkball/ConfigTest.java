package inkball;

import org.junit.jupiter.api.Test;
import processing.data.JSONArray;
import processing.data.JSONObject;


import static org.junit.jupiter.api.Assertions.*;


public class ConfigTest {
    private JSONObject createMockConfig() {
        JSONObject config = new JSONObject();

        // Create levels array
        JSONArray levels = new JSONArray();
        JSONObject level1 = new JSONObject();
        level1.setInt("id", 1);
        level1.setString("name", "Level 1");
        levels.append(level1);

        JSONObject level2 = new JSONObject();
        level2.setInt("id", 2);
        level2.setString("name", "Level 2");
        levels.append(level2);

        config.setJSONArray("levels", levels);

        // Create score increase config
        JSONObject scoreIncrease = new JSONObject();
        scoreIncrease.setInt("red", 100);
        scoreIncrease.setInt("blue", 150);
        config.setJSONObject("score_increase_from_hole_capture", scoreIncrease);

        // Create score decrease config
        JSONObject scoreDecrease = new JSONObject();
        scoreDecrease.setInt("red", -50);
        scoreDecrease.setInt("blue", -75);
        config.setJSONObject("score_decrease_from_wrong_hole", scoreDecrease);

        return config;
    }

    @Test
    public void testConfigInitialization() {
        JSONObject mockConfig = createMockConfig();
        Config config = new Config(mockConfig);

        // Test level configs
        assertEquals(2, config.levelConfigs.size());
        assertEquals(1, config.getLevelConfig(0).getInt("id"));
        assertEquals("Level 1", config.getLevelConfig(0).getString("name"));
        assertEquals(2, config.getLevelConfig(1).getInt("id"));
        assertEquals("Level 2", config.getLevelConfig(1).getString("name"));

        // Test score increase mappings
        assertEquals(2, config.score_increase.size());
        assertEquals(Integer.valueOf(100), config.score_increase.get("red"));
        assertEquals(Integer.valueOf(150), config.score_increase.get("blue"));

        // Test score decrease mappings
        assertEquals(2, config.score_decrease.size());
        assertEquals(Integer.valueOf(-50), config.score_decrease.get("red"));
        assertEquals(Integer.valueOf(-75), config.score_decrease.get("blue"));
    }

    @Test
    public void testGetLevelConfigWithNegativeIndex() {
        Config config = new Config(createMockConfig());
        try {
            config.getLevelConfig(-1);
            fail("Expected IndexOutOfBoundsException to be thrown");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("Invalid level index: -1", e.getMessage());
        }
    }

    @Test
    public void testGetLevelConfigWithTooLargeIndex() {
        Config config = new Config(createMockConfig());
        try {
            config.getLevelConfig(2);
            fail("Expected IndexOutOfBoundsException to be thrown");
        } catch (IndexOutOfBoundsException e) {
            assertEquals("Invalid level index: 2", e.getMessage());
        }
    }

    @Test
    public void testEmptyConfig() {
        JSONObject emptyConfig = new JSONObject();
        JSONArray emptyLevels = new JSONArray();
        emptyConfig.setJSONArray("levels", emptyLevels);
        emptyConfig.setJSONObject("score_increase_from_hole_capture", new JSONObject());
        emptyConfig.setJSONObject("score_decrease_from_wrong_hole", new JSONObject());

        Config config = new Config(emptyConfig);

        assertEquals(0, config.levelConfigs.size());
        assertEquals(0, config.score_increase.size());
        assertEquals(0, config.score_decrease.size());
    }
}
