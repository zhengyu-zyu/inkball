package inkball;

import processing.core.PImage;

public class YellowTile {
    private int x;
    private int y;

    public YellowTile(int x, int y) {
        this.x = x;
        this.y = y;
    }
    void move() {
        if (y == 0 && x < App.BOARD_WIDTH - 1) x++;
        else if (x == App.BOARD_WIDTH - 1 && y < App.BOARD_HEIGHT - 3) y++;
        else if (y == App.BOARD_HEIGHT - 3 && x > 0) x--;
        else if (x == 0 && y > 0) y--;
    }

    void draw(App app) {
        PImage image = app.getSprite("wall" + 4);
        app.image(image, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

}
