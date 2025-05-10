package inkball;

import processing.core.PImage;

public class AccelerationTiles extends Tile {
    private char direction;

    public AccelerationTiles(int x, int y, char direction, Level level) {
        super(x, y, level);
        this.direction = direction;
    }

    public String changeDirectionToString(char direction) {
        switch (direction) {
            case 'U': return "up";
            case 'D': return "down";
            case 'L': return "left";
            case 'R': return "right";
            default: throw new IllegalArgumentException("Unknown direction");
        }
    }

    @Override
    public void draw(App app) {
        PImage image = app.getSprite(changeDirectionToString(direction));

        if (image != null){
            app.image(image, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
        }
    }

}
