package org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.awards;

import org.academiadecodigo.simplegraphics.pictures.Picture;

public class Freezer implements Award {

    private Picture freezer;
    private TypeAward typeAward;

    public Freezer(int x, int y) {
        typeAward = TypeAward.FREEZER;

        freezer = new Picture(x, y, "map/freezer.jpg");
        freezer.draw();
    }

    @Override
    public void delete() {
        freezer.delete();
    }

    @Override
    public TypeAward getTypeAward() {
        return typeAward;
    }

    @Override
    public int getX() {
        return freezer.getX();
    }

    @Override
    public int getY() {
        return freezer.getY();
    }

}