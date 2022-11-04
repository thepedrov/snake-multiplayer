package org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.snake;

import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.settings.Settings;
import org.academiadecodigo.simplegraphics.pictures.Picture;

import java.time.ZonedDateTime;

public class Snake {

    // VARs
    private boolean isP1;

    private Picture[] snake;
    private Direction direction;
    private Long lastChangeDirection;

    private int lastBodyX;
    private int lastBodyY;

    private int score = 0;

    private Picture[] frozen;

    // METHODS
    public Snake(boolean isP1) {
        this.isP1 = isP1;

        // Proportional: One head/body for every 5 cells
        snake = new Picture[((Settings.numColumns - 2) / 5 + (Settings.numRows - 2) / 5) / 2];

        int xHead = Settings.paddingLeft;
        int y = Settings.paddingTop;
        String imgHead = "snake/";

        if (this.isP1) {
            xHead += Settings.cellSize * snake.length;
            y += Settings.cellSize;
            imgHead += "p1/headRight.jpg";

            for (int i = 1; i < snake.length; i++) {
                snake[i] = new Picture(xHead - i * Settings.cellSize, y, "snake/p1/body.jpg");
                snake[i].draw();
            }

            direction = Direction.RIGHT;
        } else {
            xHead += Settings.cellSize * (Settings.numColumns - 1 - snake.length);
            y += Settings.cellSize * (Settings.numRows - 2);
            imgHead += "p2/headLeft.jpg";

            for (int i = 1; i < snake.length; i++) {
                snake[i] = new Picture(xHead + i * Settings.cellSize, y, "snake/p2/body.jpg");
                snake[i].draw();
            }

            direction = Direction.LEFT;
        }

        lastChangeDirection = ZonedDateTime.now().toInstant().toEpochMilli();

        snake[0] = new Picture(xHead, y, imgHead);
        snake[0].draw();
    }

    public void move(Direction direction) {
        // Not let change direction if it is the same or opposite
        if (direction != null && direction != this.direction) {
            if ((this.direction == Direction.LEFT && direction != Direction.RIGHT) || (this.direction == Direction.RIGHT && direction != Direction.LEFT)) {
                snake[0].load("snake/" + ((isP1) ? "p1" : "p2") + "/" + ((direction == Direction.UP) ? "headUp" : "headDown") + ".jpg");
                this.direction = direction;
                lastChangeDirection = ZonedDateTime.now().toInstant().toEpochMilli();
            } else if ((this.direction == Direction.UP && direction != Direction.DOWN) || (this.direction == Direction.DOWN && direction != Direction.UP)) {
                snake[0].load("snake/" + ((isP1) ? "p1" : "p2") + "/" + ((direction == Direction.LEFT) ? "headLeft" : "headRight") + ".jpg");
                this.direction = direction;
                lastChangeDirection = ZonedDateTime.now().toInstant().toEpochMilli();
            }
        }

        lastBodyX = snake[0].getX();
        lastBodyY = snake[0].getY();

        switch (this.direction) {
            case LEFT:
                snake[0].translate(-Settings.cellSize, 0);
                break;
            case UP:
                snake[0].translate(0, -Settings.cellSize);
                break;
            case RIGHT:
                snake[0].translate(+Settings.cellSize, 0);
                break;
            case DOWN:
                snake[0].translate(0, +Settings.cellSize);
                break;
        }

        int tempX = 0;
        int tempY = 0;

        for (int i = 1; i < snake.length; i++) {
            tempX = snake[i].getX();
            tempY = snake[i].getY();

            snake[i].translate(lastBodyX - snake[i].getX(), lastBodyY - snake[i].getY());

            lastBodyX = tempX;
            lastBodyY = tempY;
        }
    }

    public void grow() {
        Picture[] newSnake = new Picture[snake.length + 1];

        for (int i = 0; i < snake.length; i++) {
            newSnake[i] = snake[i];
        }

        newSnake[snake.length] = new Picture(lastBodyX, lastBodyY, "snake/" + ((isP1) ? "p1" : "p2") + "/body.jpg");
        newSnake[snake.length].draw();

        snake = newSnake;

        score++;
    }

    public void frozen() {
        frozen = new Picture[snake.length];

        for (int i = 0; i < frozen.length; i++) {
            frozen[i] = new Picture(snake[i].getX(), snake[i].getY(), "snake/frozen.png");
            frozen[i].draw();
        }
    }

    public void unfreeze() {
        for (int i = 0; i < frozen.length; i++) {
            frozen[i].delete();
        }
    }

    public boolean wasAttacked(int xSnake, int ySnake) {
        for (int i = 0; i < snake.length; i++) {
            if (snake[i].getX() == xSnake && snake[i].getY() == ySnake) {
                return true;
            }
        }

        return false;
    }

    public boolean attackedItself() {
        for (int i = 4; i < snake.length; i++) {
            if (snake[i].getX() == snake[0].getX() && snake[i].getY() == snake[0].getY()) {
                return true;
            }
        }

        return false;
    }

    public Long getLastChangeDirection() {
        return lastChangeDirection;
    }

    public int getScore() {
        return score;
    }

    public int getX() {
        return snake[0].getX();
    }

    public int getY() {
        return snake[0].getY();
    }

}