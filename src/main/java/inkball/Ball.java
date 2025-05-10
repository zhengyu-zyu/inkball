package inkball;

import processing.core.PImage;
import processing.core.PVector;


public class Ball extends Tile {
    private char ballColor;
    public PVector position;
    public PVector velocity;
    public float radius;
    public boolean captured;
    public Hole attractingHole;
    public float originalRadius;


    public Ball(int x, int y, char ballColor, Level level) {
        super(x, y, level);
        this.ballColor = ballColor;
        this.position = new PVector(x * App.CELLSIZE + App.CELLSIZE / 2, y * App.CELLSIZE + App.CELLSIZE / 2 + App.TOPBAR);
        this.velocity = generateRandomVelocity();
        this.originalRadius = (App.CELLSIZE / 2.0f ) - 4;
        this.radius = originalRadius;
        this.captured = false;
        this.attractingHole = null;
    }

    PVector generateRandomVelocity() {
        float speed = 1.0f;
        return new PVector(
                Math.random() < 0.5 ? -speed : speed,
                Math.random() < 0.5 ? -speed : speed
        );
    }

    public void update() {

        if (captured) {
            return;
        }

        checkAcceleration(position);
        checkHoleAttraction();

        PVector nextPosition = PVector.add(position, velocity);
        checkCollisions(nextPosition);
        position.set(nextPosition);

        // Update x and y based on the new position
        this.x = (int) (position.x / App.CELLSIZE);
        this.y = (int) ((position.y - App.TOPBAR) / App.CELLSIZE);

        updateRadius();

    }

    private void checkAcceleration(PVector position){
        int cellX = (int) (position.x / App.CELLSIZE);
        int cellY = (int) ((position.y - App.TOPBAR) / App.CELLSIZE);

        char[][] grid = level.grid;
        if (grid != null && cellY >= 0 && cellY < grid.length &&
                cellX >= 0 && cellX < grid[cellY].length) {
            char cell = grid[cellY][cellX];
            if (cell == 'U' || cell == 'D' || cell == 'L' || cell == 'R'){
                PVector velocityChange = changeDirectionToVelocity(cell);
                this.velocity.add(velocityChange);
            }
        }
    }

    public PVector changeDirectionToVelocity(char direction) {
        switch (direction) {
            case 'U': return new PVector(0, -0.25f);
            case 'D': return new PVector(0, 0.25f);
            case 'L': return new PVector(-0.25f, 0);
            case 'R': return new PVector(0.25f, 0);
            default: throw new IllegalArgumentException("Unknown direction: " + direction);
        }
    }


    public void checkHoleAttraction() {
        Hole nearestHole = null;
        float nearestDistance = 100.0f;
        for (Tile tile : level.getTiles()) {
            if (tile instanceof Hole) {
                Hole hole = (Hole) tile;
                float distance = PVector.dist(position, new PVector(hole.getCenterX(), hole.getCenterY()));
                if (distance <= 32 && distance < nearestDistance) {
                    nearestHole = hole;
                    nearestDistance = distance;
                }
            }
        }
        attractingHole = nearestHole;

        if (attractingHole != null) {
            PVector holeCenter = new PVector(
                    attractingHole.x * App.CELLSIZE + App.CELLSIZE,
                    attractingHole.y * App.CELLSIZE + App.CELLSIZE + App.TOPBAR
            );
            PVector attractionVector = PVector.sub(holeCenter, position);
            float distance = attractionVector.mag();

            if (distance < 10) {
                captured = true;
                handleCapture();
            } else if (distance < 32 ) {
                attractionVector.normalize().mult(0.005f * distance);
                velocity.add(attractionVector);
            }
        }
    }

    public void updateRadius() {
        if (attractingHole != null) {
            PVector holeCenter = new PVector(
                    attractingHole.x * App.CELLSIZE + App.CELLSIZE,
                    attractingHole.y * App.CELLSIZE + App.CELLSIZE + App.TOPBAR
            );
            float distance = PVector.dist(position, holeCenter);
            float shrinkFactor = Math.max(0, Math.min(1, distance / 32));
            radius = originalRadius * shrinkFactor;
        } else {
            radius = originalRadius;
        }
    }

    public void handleCapture() {
        if (!captured){
            return;
        }
        boolean success = (attractingHole.getColor() == ballColor) ||
                (ballColor == '0') ||
                (attractingHole.getColor() == '0');

        if (success) {
            // Increase score
            level.increaseScore(changeBallColorToString(this));
        } else {
            // Decrease score and respawn ball
            level.decreaseScore(changeBallColorToString(this));
            level.respawnBall(this);
        }
    }

    private void checkCollisions(PVector nextPosition) {
        // Check edge collisions
        if (nextPosition.x - radius < 0) {
            nextPosition.x = radius;
            velocity.x = Math.abs(velocity.x);
        } else if (nextPosition.x + radius > App.WIDTH + 8) {
            nextPosition.x = App.WIDTH - radius;
            velocity.x = -Math.abs(velocity.x);
        }

        if (nextPosition.y - radius < App.TOPBAR) {
            nextPosition.y = App.TOPBAR + radius;
            velocity.y = Math.abs(velocity.y);
        } else if (nextPosition.y + radius > App.HEIGHT + 8) {
            nextPosition.y = App.HEIGHT - radius;
            velocity.y = -Math.abs(velocity.y);
        }

        // Check wall collisions
        int gridX = (int) (nextPosition.x / App.CELLSIZE);
        int gridY = (int) ((nextPosition.y - App.TOPBAR) / App.CELLSIZE);

        int[][] standardDirections = {
                {0, -1},
                {0, 1},
                {-1, 0},
                {1, 0}
        };
        for (int[] dir : standardDirections) {
            int dx = dir[0];
            int dy = dir[1];
            boolean flag = true;
            checkWallInCell(gridX + dx, gridY + dy, nextPosition, flag);
        }
        int[][] diagonalDirections = {
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1}
        };
        for (int[] dir : diagonalDirections) {
            int dx = dir[0];
            int dy = dir[1];
            boolean flag = false;
            checkWallInCell(gridX + dx, gridY + dy, nextPosition, flag);
        }
    }

    private void checkWallInCell(int cellX, int cellY, PVector nextPosition, boolean flag) {
        if (cellX < 0 || cellX >= App.BOARD_WIDTH || cellY < 0 || cellY >= App.BOARD_HEIGHT) {
            return;
        }

        char[][] grid = level.grid;
        if (grid != null && cellY >= 0 && cellY < grid.length &&
                cellX >= 0 && cellX < grid[cellY].length) {
            char cell = grid[cellY][cellX];

            if (cell >= '1' && cell <= '4' || cell == 'X') {
                float wallLeft = cellX * App.CELLSIZE - 2;
                float wallRight = (cellX + 1) * App.CELLSIZE + 2;
                float wallTop = cellY * App.CELLSIZE + App.TOPBAR - 2;
                float wallBottom = (cellY + 1) * App.CELLSIZE + App.TOPBAR + 2;

                // Check for collision
                if (nextPosition.x + radius >= wallLeft && wallRight >= nextPosition.x - radius &&
                        nextPosition.y + radius >= wallTop && wallBottom >= nextPosition.y - radius) {

                    // Determine which side of the wall was hit
                    float overlapLeft = nextPosition.x + radius - wallLeft;
                    float overlapRight = wallRight - (nextPosition.x - radius);
                    float overlapTop = nextPosition.y + radius - wallTop;
                    float overlapBottom = wallBottom - (nextPosition.y - radius);

                    final float overlapTolerance = 0.001f;// set the tolerance

                    // Whether ball hits wall's corner
                    if (Math.abs(overlapLeft - overlapRight) < overlapTolerance &&
                            Math.abs(overlapTop - overlapBottom) < overlapTolerance) {
                        // Ball hit the corner
                        velocity.x *= -1;
                        velocity.y *= -1;
                        nextPosition.x = position.x;
                        nextPosition.y = position.y;
                    } else {

                        // Find the smallest overlap
                        float minOverlap = Math.min(Math.min(overlapLeft, overlapRight), Math.min(overlapTop, overlapBottom));

                        // Adjust position and velocity based on the collision side
                        if (minOverlap == overlapLeft || minOverlap == overlapRight) {
                            velocity.x *= -1;
                            nextPosition.x = position.x;
                        } else {
                            velocity.y *= -1;
                            nextPosition.y = position.y;
                        }

                        // Change ball color if necessary
                        if (cell != 'X' && ballColor != cell && flag) {
                            this.ballColor = cell;
                        }
                    }
                }
            }
        }
    }

    public void setPosition(PVector newPosition) {
        this.position = newPosition.copy();
    }

    public void setVelocity(PVector newVelocity) {
        this.velocity = newVelocity.copy();
    }

    @Override
    public void draw(App app) {
        if (captured) return;

        PImage image = app.getSprite("ball" + ballColor);

        if (image != null){
            float drawRadius = radius * 2;
            app.image(image, position.x - radius, position.y - radius, drawRadius, drawRadius);
        }
    }

    public String changeBallColorToString(Ball ball) {
        switch (ball.ballColor) {
            case '0': return "grey";
            case '1': return "orange";
            case '2': return "blue";
            case '3': return "green";
            case '4': return "yellow";
            default: throw new IllegalArgumentException("Unknown color number");
        }
    }

    public PVector getPosition() {
        return position.copy();
    }
    public PVector getVelocity() {
        return velocity.copy();
    }
    public boolean getCaptured() {
        return this.captured;
    }

    public int getX() {
        return this.x;
    }
    public int getY() {
        return this.y;
    }

    public char getColorCode() {
        return this.ballColor;
    }

    public float getRadius() {
        return this.radius;
    }
}