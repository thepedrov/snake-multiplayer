package org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.awards;

import org.academiadecodigo.simplegraphics.pictures.Picture;

public class Apple implements Award {

    private Picture apple;
    private TypeAward typeAward;

    public Apple(int x, int y) {
        typeAward = TypeAward.APPLE;

        apple = new Picture(x, y, "map/apple.jpg");
        apple.draw();
    }

    @Override
    public void delete() {
        apple.delete();
    }

    @Override
    public TypeAward getTypeAward() {
        return typeAward;
    }

    @Override
    public int getX() {
        return apple.getX();
    }

    @Override
    public int getY() {
        return apple.getY();
    }

}