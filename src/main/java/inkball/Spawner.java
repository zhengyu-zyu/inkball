package inkball;

import processing.core.PImage;

public class Spawner extends Tile{

    public Spawner(int x, int y, Level level) {
        super(x, y, level);
    }

    public void draw(App app) {

        PImage image = app.getSprite("entrypoint");

        if (image != null){
            app.image(image, x * App.CELLSIZE, y * App.CELLSIZE + App.TOPBAR);
        }
    }
}
