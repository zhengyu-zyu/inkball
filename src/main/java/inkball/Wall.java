package inkball;

import processing.core.PImage;

public class Wall extends Tile{
    private char wallColor;

    public Wall(int x, int y, char wallColor, Level level) {
        super(x, y, level);
        this.wallColor = wallColor;
    }

    @Override
    public void draw(App app) {
        PImage image = app.getSprite("wall" + wallColor);

        if (image != null){
            app.image(image, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
        }
    }

    public char getWallColor() {
        return wallColor;
    }
}
