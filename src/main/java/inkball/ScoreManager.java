package inkball;

import java.util.Map;

public class ScoreManager {
    private Map<String, Integer> scoreIncreaseRules;
    private Map<String, Integer> scoreDecreaseRules;
    public double scoreIncreaseModifier;
    public double scoreDecreaseModifier;

    public ScoreManager(Config config, int levelIndex) {
        this.scoreIncreaseRules = config.score_increase;
        this.scoreDecreaseRules = config.score_decrease;

        this.scoreIncreaseModifier = config.getLevelConfig(levelIndex).getDouble("score_increase_from_hole_capture_modifier");
        this.scoreDecreaseModifier = config.getLevelConfig(levelIndex).getDouble("score_decrease_from_wrong_hole_modifier");
    }

    public int calculateScoreIncrease(String ballColor) {
        int baseScore = scoreIncreaseRules.getOrDefault(ballColor, 0);
        return (int) (baseScore * scoreIncreaseModifier);
    }

    public int calculateScoreDecrease(String ballColor) {
        int baseScore = scoreDecreaseRules.getOrDefault(ballColor, 0);
        return (int) (baseScore * scoreDecreaseModifier);
    }
}
