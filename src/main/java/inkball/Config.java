package inkball;

import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    public List<JSONObject> levelConfigs = new ArrayList<>();
    public Map<String, Integer> score_increase = new HashMap<>();
    public Map<String, Integer> score_decrease = new HashMap<>();

    public Config(JSONObject config){
        JSONArray levelsArray = config.getJSONArray("levels");
        for (int i = 0; i < levelsArray.size(); i++) {
            levelConfigs.add(levelsArray.getJSONObject(i));
        }

        JSONObject scoreIncrease = config.getJSONObject("score_increase_from_hole_capture");
        for (Object colour : scoreIncrease.keys()) {
            String keyString = colour.toString();
            int value = scoreIncrease.getInt(keyString);
            score_increase.put(keyString, value);
        }

        JSONObject scoreDecrease = config.getJSONObject("score_decrease_from_wrong_hole");
        for (Object colour : scoreDecrease.keys()) {
            String keyString = colour.toString();
            int value = scoreDecrease.getInt(keyString);
            score_decrease.put(keyString, value);
        }
    }

    public JSONObject getLevelConfig(int levelIndex) {
        if (levelIndex < 0 || levelIndex >= levelConfigs.size()) {
            throw new IndexOutOfBoundsException("Invalid level index: " + levelIndex);
        }
        return levelConfigs.get(levelIndex);
    }
}
