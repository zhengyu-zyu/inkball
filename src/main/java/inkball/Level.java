package inkball;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

public class Level {

    private int time;
    public int timer;
    private int spawnInterval;
    int spawnTimer;
    private String layoutFile;

    public char[][] grid;
    public List<Tile> tiles;
    private List<Spawner> spawners;
    public List<Ball> balls;
    private List<Wall> walls;
    private List<Hole> holes;
    public List<AccelerationTiles> accelerationTiles;
    public List<String> ballQueue;
    float[] displayOffsets;
    private boolean isMovingBalls = false;
    private int movementCounter = 0;
    List<String> displayBall;

    public int score;
    public ScoreManager scoreManager;

    public boolean isEndingLevel;
    public int remainingTime;
    private List<YellowTile> yellowTiles;
    private int animationTimer;
    private static final float ANIMATION_INTERVAL = App.FPS * 0.067f;
    public boolean isAnimationComplete;

    public int levelIndex;



    public Level(JSONObject levelConfig, Config config,int levelIndex) {
        this.time = levelConfig.getInt("time");
        this.spawnInterval = levelConfig.getInt("spawn_interval");
        this.layoutFile = levelConfig.getString("layout");
        this.spawnTimer = 0;
        this.timer = time *App.FPS;
        this.scoreManager = new ScoreManager(config, levelIndex);
        this.levelIndex = levelIndex;

        tiles = new ArrayList<>();
        spawners = new ArrayList<>();
        balls = new ArrayList<>();
        walls = new ArrayList<>();
        holes = new ArrayList<>();
        accelerationTiles = new ArrayList<>();
        ballQueue = new LinkedList<>();
        displayBall = new ArrayList<>();


        JSONArray ballsArray = levelConfig.getJSONArray("balls");
        this.displayOffsets = new float[ballsArray.size()*3];

        for (int i = 0; i < ballsArray.size(); i++) {
            String ballColor = ballsArray.getString(i);
            ballQueue.add(ballColor);
            displayBall.add(ballColor);
        }
    }

    public char convertColorToChar(String color) {
        switch (color.toLowerCase()) {
            case "grey": return '0';
            case "orange": return '1';
            case "blue": return '2';
            case "green": return '3';
            case "yellow": return '4';
            default: throw new IllegalArgumentException("Unknown color: " + color);
        }
    }

    public String getLayoutFile(){
        return this.layoutFile;
    }

    public void loadFile() {
        String filePath = this.getLayoutFile();
        List<String> lines = new ArrayList<>();

        try (Scanner scanner = new Scanner(new File(filePath))) {
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine().trim());
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
            return;
        }

        grid = new char[lines.size()][lines.get(0).length()];

        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            for (int col = 0; col < line.length(); col++) {
                char tileChar = line.charAt(col);
                grid[row][col] = tileChar;

                if (row > 0 && col > 0) {
                    if (grid[row - 1][col] == 'H') {
                        col++;
                        System.out.println("Incremented col to: " + col);
                        continue;
                    }
                }
                if (tileChar == 'S') {
                    createSpawners(col, row);
                } else if (tileChar == 'H') {
                    if (col + 1 < line.length()) {
                        char holeColor = line.charAt(col + 1);
                        createHole(col, row, holeColor);
                        col++;
                    } else {
                        System.out.println("Error: Hole at (" + col + ", " + row + ") does not have a color character.");
                    }
                } else if (tileChar == 'B') {
                    Tile tile = new Tile(col, row, this);
                    tiles.add(tile);
                    if (col + 1 < line.length()) {
                        char ballColor = line.charAt(col + 1);
                        createBall(col, row, ballColor);
                        col++;
                        Tile tile1 = new Tile(col, row, this);
                        tiles.add(tile1);
                    } else {
                        System.out.println("Error: Ball at (" + col + ", " + row + ") does not have a color character.");
                    }
                }else if (tileChar == '1' || tileChar == '2' || tileChar == '3' || tileChar == '4') {
                    createWall(col, row, tileChar);
                } else if (tileChar == 'X') {
                    createWall(col, row, '0');
                } else if (tileChar == 'U' || tileChar == 'D' || tileChar == 'L' || tileChar == 'R') {
                    createAccelerationTiles(col, row, tileChar);
                } else{
                    Tile tile = new Tile(col, row, this);
                    tiles.add(tile);
                }
            }
        }
    }

    private void createBall(int col, int row, char ballColor) {
        Ball ball = new Ball(col, row, ballColor, this);
        tiles.add(ball);
        balls.add(ball);
    }
    private void createHole(int col, int row, char holeColor) {
        Hole hole = new Hole(col, row, holeColor, this);
        tiles.add(hole);
        holes.add(hole);

    }
    private void createWall(int col, int row, char wallColor) {
        Wall wall = new Wall(col, row, wallColor, this);
        tiles.add(wall);
        walls.add(wall);
    }
    private void createSpawners(int col, int row) {
        Spawner spawner = new Spawner(col, row,this);
        tiles.add(spawner);
        spawners.add(spawner);
    }
    public void createAccelerationTiles(int col, int row, char direction) {
        AccelerationTiles accelerationTile = new AccelerationTiles(col, row, direction, this);
        tiles.add(accelerationTile);
        accelerationTiles.add(accelerationTile);
    }

    public List<Tile> getTiles() {
        return this.tiles;
    }


    List<PlayerLine> linesToRemove = new ArrayList<>();
    List<Ball> ballsToRemove = new ArrayList<>();

    public void update(List<PlayerLine> playerLines){
        if (!isEndingLevel) {
            for (Ball ball : balls) {
                PVector originalPosition = ball.getPosition().copy();
                PVector originalVelocity = ball.getVelocity().copy();

                ball.update();


                for (PlayerLine playerLine : playerLines) {

                    List<Line> segments = playerLine.getSegments();
                    Line collidingSegment = Hitbox.findCollidingSegment(originalPosition, originalVelocity, segments);

                    if (collidingSegment != null) {
                        PVector newVelocity = Hitbox.calculateNewTrajectory(originalPosition, originalVelocity, collidingSegment);
                        ball.setVelocity(newVelocity);
                        PVector adjustedPosition = originalPosition.copy().add(newVelocity.copy().normalize().mult(0.1f));
                        ball.setPosition(adjustedPosition);
                        linesToRemove.add(playerLine);
                        break;
                    }
                }
                if (ball.getCaptured()) {
                    ballsToRemove.add(ball);
                }
                playerLines.removeAll(linesToRemove);
            }
            balls.removeAll(ballsToRemove);

            if (timer != 0) {
                timer--;
            }

            spawnTimer--;
            if (spawnTimer <= 0 && !ballQueue.isEmpty()) {
                spawnNextBall();
                spawnTimer = spawnInterval * App.FPS;
                isMovingBalls = true;
                movementCounter = App.CELLSIZE;
            }
            if (isMovingBalls && movementCounter > 0) {
                for (int i = 0; i < displayOffsets.length; i++) {
                    displayOffsets[i] += 0.88f;
                }
                movementCounter -= 0.25f;
            }

            if (movementCounter == 0) {
                isMovingBalls = false;
            }

            if (checkLevelComplete()) {
                startLevelEndAnimation();
            }
        }else {
            updateLevelEndAnimation();
        }
    }

    public void startLevelEndAnimation() {
        isEndingLevel = true;
        remainingTime = timer/App.FPS;
        yellowTiles = new ArrayList<>();
        yellowTiles.add(new YellowTile(0, 0));
        yellowTiles.add(new YellowTile(App.BOARD_WIDTH - 1, App.BOARD_HEIGHT - 3));
        animationTimer = 0;
        isAnimationComplete = false;
    }

    public void updateLevelEndAnimation() {
        animationTimer++;
        if (animationTimer >= ANIMATION_INTERVAL) {
            animationTimer = 0;

            // Move yellow tiles
            for (YellowTile tile : yellowTiles) {
                tile.move();
            }

            // Add score
            if (remainingTime > 0) {
                score++;
                remainingTime--;
                if (remainingTime <= 0) {
                    isAnimationComplete = true;
                }
            }
        }


    }

    public void increaseScore(String ballColor) {
        score += scoreManager.calculateScoreIncrease(ballColor);
    }

    public void decreaseScore(String ballColor) {
        score -= scoreManager.calculateScoreDecrease(ballColor);
    }


    public void spawnNextBall() {
        if (!spawners.isEmpty() && !ballQueue.isEmpty()) {
            char ballColor = convertColorToChar(ballQueue.get(0));
            ballQueue.remove(0);
            Random random = new Random();
            int randomIndex = random.nextInt(spawners.size());
            Spawner spawner = spawners.get(randomIndex);
            createBall(spawner.x, spawner.y, ballColor);
        }
    }

    public void drawTimer(App app) {

        int seconds = timer / App.FPS;
        app.fill(0);
        app.textSize(20);
        app.text("Time:   " + (isEndingLevel ? remainingTime : seconds), app.width - 130, app.CELLHEIGHT * 2 - 7);

    }

    public void drawUpcomingBalls(App app) {
        app.rect(9,20,140,30);
        app.fill(0);

        app.clip(8, 19, 140, 30);

        int startX = 12;
        int y = 20;
        int spacing = App.CELLSIZE - 4;



        int index = 0;
        for (String ballColor : displayBall) {
            char colorChar = convertColorToChar(ballColor);
            PImage ballImage = app.getSprite("ball" + colorChar);
            if (ballImage != null) {
                app.image(ballImage, startX + (index * spacing) - displayOffsets[index], y + 4);
            }
                index++;
        }

        app.noClip();

        if (!ballQueue.isEmpty()) {
            float seconds = spawnTimer / (float)App.FPS;
            String timerText = String.format("%.1f", seconds);
            app.textSize(18);
            app.fill(0);
            app.text(timerText, startX + 5 * spacing + 15, y + App.TOPBAR - App.CELLSIZE - 3);
        }
    }

    public void respawnBall(Ball ball) {
        ballQueue.add(ball.changeBallColorToString(ball));
        displayBall.add(ball.changeBallColorToString(ball));

    }

    public boolean checkLevelComplete() {
        return balls.isEmpty() && ballQueue.isEmpty();
    }

    public void draw(App app) {
        // tiles
        for (Tile tile : tiles) {
            tile.draw(app);
        }

        //lines
        for (PlayerLine line : app.playerLines) {
            line.draw(app);
        }
        if (app.currentLine != null) {
            app.currentLine.draw(app);
        }

        //balls
        for (Ball ball : balls) {
            ball.draw(app);
        }

        drawUpcomingBalls(app);
        drawTimer(app) ;

        if (isEndingLevel) {
            for (YellowTile tile : yellowTiles) {
                tile.draw(app);
            }
        }
    }

    public int getLevelIndex() {
        return this.levelIndex;
    }
}