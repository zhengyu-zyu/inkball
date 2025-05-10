package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.util.*;

public class App extends PApplet {

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 64;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 60;

    public String configPath;

    public static Random random = new Random();
    public int currentLevelScore;

    private Config config;
    private Level currentLevel;
    int levelIndex = 0;

    public GameState gameState = GameState.PLAYING;
    public PlayerLine currentLine;
    public List<PlayerLine> playerLines = new ArrayList<>();




    public enum GameState {
        PLAYING,
        PAUSED,
        GAME_OVER
    }
    // Feel free to add any additional methods or attributes you want. Please put classes in different files.

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
	@Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
    @Override
    public void setup() {
        frameRate(FPS);

        JSONObject loadedConfig = loadJSONObject(configPath);

        try {
            this.config = new Config(loadedConfig);
            JSONObject levelConfig = config.getLevelConfig(levelIndex);
            currentLevel = new Level(levelConfig, config, levelIndex);
            currentLevel.loadFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String[] spriteNames = {
                "ball0", "ball1", "ball2", "ball3", "ball4",
                "entrypoint", "hole0", "hole1", "hole2", "hole3", "hole4",
                "tile", "wall0", "wall1", "wall2", "wall3", "wall4",
                "up","down","right","left"
        };

        for (String spriteName : spriteNames) {
            getSprite(spriteName);
        }
    }

    private static HashMap<String, PImage> sprites = new HashMap<>();
    public PImage getSprite(String s) {
        PImage result = sprites.get(s);
        if (result == null) {
            try {
                result = loadImage(URLDecoder.decode(this.getClass().getResource(s + ".png").getPath(), StandardCharsets.UTF_8.name()));
                if (result == null) {
                    println("Image not found: " + s);
                } else {
                    sprites.put(s, result);
                }
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public void setSprite(String key, PImage image) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Sprite key cannot be null or empty");
        }
        if (image == null) {
            throw new IllegalArgumentException("Sprite image cannot be null");
        }

        sprites.put(key, image);
    }


    /**
     * Receive key pressed signal from the keyboard.
     */
	@Override
    public void keyPressed(KeyEvent event){
        if (event.getKey() == 'r' || event.getKey() == 'R') {
            restartLevel();
        } else if (event.getKey() == ' ') {
            togglePause();
        }
    }

    private void restartLevel() {
        try {
            if(gameState == GameState.GAME_OVER) {
                levelIndex = 0;
                levelScores[0] = 0;
                levelScores[1] = 0;
                levelScores[2] = 0;
            }
            JSONObject levelConfig = config.getLevelConfig(levelIndex);
            currentLevel = new Level(levelConfig, config, levelIndex);
            currentLevel.loadFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        levelScores[levelIndex] = 0;
        playerLines.clear();
        gameState = GameState.PLAYING;
    }

    public void togglePause() {
        if (gameState == GameState.PLAYING) {
            gameState = GameState.PAUSED;
        } else if (gameState == GameState.PAUSED) {
            gameState = GameState.PLAYING;
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
	@Override
    public void keyReleased(){
        
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // create a new player-drawn line object
            if (e.getButton() == LEFT) {
                currentLine = new PlayerLine();
                currentLine.addPoint(e.getX(), e.getY());
            }else if (e.getButton() == RIGHT) {
                removeLine(e.getX(), e.getY());
            }
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held
		
		// remove player-drawn line object if right mouse button is held 
		// and mouse position collides with the line
            if(e.getButton() == LEFT && currentLine != null) {
                currentLine.addPoint(e.getX(), e.getY());
            }else if (e.getButton() == RIGHT) {
                removeLine(e.getX(), e.getY());
            }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == LEFT && !(gameState == GameState.GAME_OVER)) {
            if (e.getButton() == LEFT &&currentLine != null) {
                playerLines.add(currentLine);
                currentLine = null;
            }
        }
    }


    public void removeLine(float x, float y) {
        playerLines.removeIf(line -> line.intersects(x, y));
    }


    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        background(200, 200, 200);

        //----------------------------------
        //display Board for current level:
        //----------------------------------
        //TODO

        currentLevel.draw(this);

        if (gameState == GameState.PLAYING) {
            currentLevel.update(playerLines);

            if (currentLevel.isAnimationComplete) {
                if (levelIndex < 3) {
                    completeLevel();
                } else {
                    gameState = GameState.GAME_OVER;
                }
            } else if (currentLevel.timer == 0) {
                gameState = GameState.GAME_OVER;
            }

        }

        //----------------------------------
        //display score
        //----------------------------------
        //TODO
        displayScore();
        
		//----------------------------------
        //----------------------------------
		//display game end message
        if (gameState == GameState.PAUSED) {
            displayPauseMessage();
        } else if (gameState == GameState.GAME_OVER) {
            displayGameOverMessage();
        }



    }

    public static int[] levelScores = new int[3];

    public void displayScore() {
        fill(0);
        textSize(20);
        if (currentLevel.score != 0) {
            levelScores[levelIndex] = currentLevel.score;
        }
        int displayScore = Math.max(0, levelScores[0] + levelScores[1] + levelScores[2]);
        text("Score:   " + displayScore, width - 130, CELLHEIGHT - 7);
    }



    void completeLevel() {
        levelIndex++;
        if (levelIndex < 3) {
            try {
                JSONObject levelConfig = config.getLevelConfig(levelIndex);
                currentLevel = new Level(levelConfig, config, levelIndex);
                currentLevel.loadFile();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            gameState = GameState.GAME_OVER;
        }
        playerLines.clear();
    }

    private void displayPauseMessage() {
        fill(0);
        textSize(24);
        text("*** PAUSED ***", WIDTH / 2 - CELLAVG * 2, TOPBAR / 2 + CELLHEIGHT / 2 - 6 );
    }

    private void displayGameOverMessage() {
        fill(0);
        textSize(24);
        text("=== ENDED ===", WIDTH / 2 - CELLAVG * 2, TOPBAR / 2 + CELLHEIGHT / 2 - 6 );
    }


    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

    public String getConfigPath() {
        return configPath;
    }
    public int getWidth(){
        return width;
    }
    public int getHeight(){
        return height;
    }
    public Object getConfig() {
        return config;
    }
    public Object getCurrentLevel() {
        return currentLevel;
    }
    public int getLevelIndex() {
        return levelIndex;
    }
    public int calculateTotalScore() {
        return Math.max(0, levelScores[0] + levelScores[1] + levelScores[2]);
    }
}
