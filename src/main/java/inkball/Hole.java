package inkball;

import processing.core.PImage;

public class Hole extends Tile {
    private char holeColor;
    private int size = 2;

    public Hole(int x, int y, char holeColor, Level level) {
        super(x, y, level);
        this.holeColor = holeColor;
    }

    @Override
    public void draw(App app) {
        PImage image = app.getSprite("hole" + holeColor);

        if (image != null){
            app.image(image, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
        }
    }

    public char getColor() {
        return holeColor;
    }

    public boolean containsPoint(float px, float py) {
        return px >= x * App.CELLSIZE && px < (x + size) * App.CELLSIZE &&
                py >= y * App.CELLSIZE + App.TOPBAR && py < (y + size) * App.CELLSIZE + App.TOPBAR;
    }

    public float getCenterX() {
        return (x + size / 2.0f) * App.CELLSIZE;
    }

    public float getCenterY() {
        return (y + size / 2.0f) * App.CELLSIZE + App.TOPBAR;
    }

}
