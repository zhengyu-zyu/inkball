package inkball;

import processing.core.PImage;

public class Tile {
    protected int x;
    protected int y;
    protected Level level;
    protected PImage image;

    public Tile(int x, int y, Level level) {
        this.x = x;
        this.y = y;
        this.level = level;
    }


    public void draw(App app) {
        image = app.getSprite("tile");
        if (image != null) {
            app.image(image, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
        }
    }

}
