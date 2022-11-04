package org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game;

import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.Main;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.awards.Apple;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.awards.Award;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.awards.Freezer;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.awards.TypeAward;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.snake.Direction;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.snake.Snake;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.settings.KeyboardSettings;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.settings.Settings;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.settings.Sockets;
import org.academiadecodigo.simplegraphics.graphics.Color;
import org.academiadecodigo.simplegraphics.graphics.Rectangle;
import org.academiadecodigo.simplegraphics.graphics.Text;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardEvent;
import org.academiadecodigo.simplegraphics.keyboard.KeyboardHandler;
import org.academiadecodigo.simplegraphics.pictures.Picture;

import java.awt.*;
import java.io.IOException;
import java.util.Random;
import javax.sound.sampled.*;

public class Game implements KeyboardHandler {

    // VARs
    private short gameState = 0;
    // 0 : Did not start
    // 1 : In game
    // 2 : End

    private Sockets sockets;

    private Snake snakeP1;
    private boolean snakeP1end;
    private Direction directionP1;

    private Snake snakeP2;
    private boolean snakeP2end;
    private Direction directionP2;

    private Award award;
    private boolean p1IsFrozen;
    private long endTimeFrozenP1 = 0;
    private boolean p2IsFrozen;
    private long endTimeFrozenP2 = 0;

    private Picture loading;
    private Picture gameStateMessage;
    private Picture titleP1;
    private Picture titleP2;

    // METHODS
    public Game() {
        if (!Settings.isP1) {
            sockets = new Sockets();
        }

        loading = new Picture(Settings.paddingLeft + (Settings.numColumns / 2) * Settings.cellSize - 239, Settings.paddingTop + (Settings.numRows / 2) * Settings.cellSize - 29, "titles/loading.png");
        loading.draw();

        Settings.fixWindowGame();

        createMap();

        Settings.centerWindowGame();

        snakeP1 = new Snake(true);

        loading.delete();

        if (Settings.isP1) {
            Picture waitingP2 = new Picture(Settings.paddingLeft + (Settings.numColumns / 2) * Settings.cellSize - 170, Settings.paddingTop + (Settings.numRows / 2) * Settings.cellSize - 105, "titles/waitingPlayer2.png");
            waitingP2.draw();

            Frame.getFrames()[Settings.indexJFrame].paintAll(Frame.getFrames()[Settings.indexJFrame].getGraphics());

            sockets = new Sockets();

            try {
                // Because of the time it takes to draw the window/game on the client
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            waitingP2.delete();
        }

        snakeP2 = new Snake(false);

        createTitles();

        if (Settings.isP1) {
            KeyboardSettings.startKeyboard(this);
        } else {
            Frame.getFrames()[Settings.indexJFrame].paintAll(Frame.getFrames()[Settings.indexJFrame].getGraphics());

            sockets.readStartGame();

            KeyboardSettings.startKeyboard(this);

            startGame();
        }
    }

    private void createMap() {
        Picture wall;

        // WALL TOP && BOTTOM
        int yBottom = Settings.paddingTop + Settings.numRows * Settings.cellSize - Settings.cellSize;

        for (int x = 0; x < Settings.numColumns; x++) {
            wall = new Picture(Settings.paddingLeft + x * Settings.cellSize, Settings.paddingTop, "map/wall.jpg");
            wall.draw();

            wall = new Picture(Settings.paddingLeft + x * Settings.cellSize, yBottom, "map/wall.jpg");
            wall.draw();
        }

        // WALL LEFT && RIGHT
        int xRight = Settings.paddingLeft + Settings.numColumns * Settings.cellSize - Settings.cellSize;

        for (int x = 1; x < Settings.numRows - 1; x++) {
            wall = new Picture(Settings.paddingLeft, Settings.paddingTop + x * Settings.cellSize, "map/wall.jpg");
            wall.draw();

            wall = new Picture(xRight, Settings.paddingTop + x * Settings.cellSize, "map/wall.jpg");
            wall.draw();
        }

        // BACKGROUND
        Rectangle background = new Rectangle(Settings.paddingLeft + Settings.cellSize, Settings.paddingTop + Settings.cellSize, Settings.cellSize * (Settings.numColumns - 2), Settings.cellSize * (Settings.numRows - 2));
        background.setColor(Color.BLACK);
        background.fill();

        loading.delete();
        loading.draw();
    }

    private void createTitles() {
        gameStateMessage = new Picture(Settings.paddingLeft + (Settings.numColumns / 2) * Settings.cellSize - 170, Settings.paddingTop + (Settings.numRows / 2) * Settings.cellSize - 105, (Settings.isP1) ? "titles/pressSpaceToPlay.png" : "titles/waitingPlayer1Start.png");
        gameStateMessage.draw();

        titleP1 = new Picture(Settings.paddingLeft, Settings.paddingTop, (Settings.isP1) ? "titles/topYou.png" : "titles/topP1.png");
        titleP1.draw();

        titleP2 = new Picture(Settings.paddingLeft + Settings.numColumns * Settings.cellSize - 240, Settings.paddingTop + Settings.numRows * Settings.cellSize - 30, (Settings.isP1) ? "titles/bottomP2.png" : "titles/bottomYou.png");
        titleP2.draw();
    }

    private void deleteTitles() {
        gameStateMessage.delete();
        titleP1.delete();
        titleP2.delete();
    }

    private void startGame() {
        deleteTitles();

        newAward();

        gameState = 1;

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // Because during the execution of this thread some key may be clicked and then cause errors (opposite directions between server and client)
                Direction tempDirectionP1;
                Direction tempDirectionP2;

                while (gameState == 1) {
                    if (Settings.isP1) {
                        tempDirectionP1 = directionP1;
                        sockets.sendMove(tempDirectionP1);

                        tempDirectionP2 = sockets.readMove();
                        directionP2 = tempDirectionP2;
                    } else {
                        tempDirectionP1 = sockets.readMove();
                        directionP1 = tempDirectionP1;

                        tempDirectionP2 = directionP2;
                        sockets.sendMove(tempDirectionP2);
                    }

                    moveSnakes(tempDirectionP1, tempDirectionP2);

                    gameControl(tempDirectionP1, tempDirectionP2);

                    if (p1IsFrozen || p2IsFrozen) {
                        if (Settings.isP1) {
                            if (p1IsFrozen && System.currentTimeMillis() >= endTimeFrozenP1) {
                                snakeP1.unfreeze();
                                p1IsFrozen = false;
                            } else if (p2IsFrozen && System.currentTimeMillis() >= endTimeFrozenP2) {
                                snakeP2.unfreeze();
                                p2IsFrozen = false;
                            }

                            sockets.sendSnakesFrozenStates(new boolean[]{p1IsFrozen, p2IsFrozen});
                        } else {
                            boolean[] snakesStates = sockets.readSnakesFrozenStates();

                            if (p1IsFrozen && !snakesStates[0]) {
                                snakeP1.unfreeze();
                                p1IsFrozen = false;
                            } else if (p2IsFrozen && !snakesStates[1]) {
                                snakeP2.unfreeze();
                                p2IsFrozen = false;
                            }
                        }
                    }

                    try {
                        Thread.sleep(Settings.velocity);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        t.setPriority(Thread.MAX_PRIORITY);
        t.start();
    }

    private void moveSnakes(Direction tempDirectionP1, Direction tempDirectionP2) {
        if (!p1IsFrozen) {
            snakeP1.move(tempDirectionP1);
        }

        if (!p2IsFrozen) {
            snakeP2.move(tempDirectionP2);
        }
    }

    private void gameControl(Direction tempDirectionP1, Direction tempDirectionP2) {
        // <Picking award>
        if (snakeP1.getX() == award.getX() && snakeP1.getY() == award.getY() && snakeP2.getX() == award.getX() && snakeP2.getY() == award.getY()) {
            // If they are on top of the award then they have lost, and they don't earn points
        } else if (snakeP1.getX() == award.getX() && snakeP1.getY() == award.getY()) {
            soundCatch();
            award.delete();

            if (award.getTypeAward() == TypeAward.APPLE) {
                snakeP1.grow();
            } else {
                if (!p2IsFrozen) {
                    snakeP2.frozen();

                    p2IsFrozen = true;
                }

                endTimeFrozenP2 = System.currentTimeMillis() + (Settings.frozenTime);
            }

            newAward();
        } else if (snakeP2.getX() == award.getX() && snakeP2.getY() == award.getY()) {
            soundCatch();
            award.delete();

            if (award.getTypeAward() == TypeAward.APPLE) {
                snakeP2.grow();
            } else {
                if (!p1IsFrozen) {
                    snakeP1.frozen();

                    p1IsFrozen = true;
                }

                endTimeFrozenP1 = System.currentTimeMillis() + (Settings.frozenTime);
            }

            newAward();
        }
        // </Picking award>

        // <If one attacks the other>
        if (snakeP1.getX() == snakeP2.getX() && snakeP1.getY() == snakeP2.getY()) {
            // When snakes collide face to face
            if (tempDirectionP1 == Direction.UP && tempDirectionP2 == Direction.DOWN || tempDirectionP1 == Direction.DOWN && tempDirectionP2 == Direction.UP || tempDirectionP1 == Direction.RIGHT && tempDirectionP2 == Direction.LEFT || tempDirectionP1 == Direction.LEFT && tempDirectionP2 == Direction.RIGHT) {
                if (snakeP1.getScore() > snakeP2.getScore()) {
                    snakeP2end = true;
                } else if (snakeP1.getScore() < snakeP2.getScore()) {
                    snakeP1end = true;
                } else {
                    snakeP1end = true;
                    snakeP2end = true;
                }
            } else {
                if (snakeP1.getLastChangeDirection() < snakeP2.getLastChangeDirection()) {
                    snakeP2end = true;
                } else if (snakeP1.getLastChangeDirection() > snakeP2.getLastChangeDirection()) {
                    snakeP1end = true;
                } else {
                    snakeP1end = true;
                    snakeP2end = true;
                }
            }
        } else {
            if (snakeP2.wasAttacked(snakeP1.getX(), snakeP1.getY())) {
                snakeP1end = true;
            }
            if (snakeP1.wasAttacked(snakeP2.getX(), snakeP2.getY())) {
                snakeP2end = true;
            }
        }
        // </If one attacks the other>

        // <If the snake attacks itself>
        if (snakeP2.attackedItself()) {
            snakeP2end = true;
        }
        if (snakeP1.attackedItself()) {
            snakeP1end = true;
        }
        // </If the snake attacks itself>

        // <Player 1 - Crashed into the walls>
        if (snakeP1.getX() == Settings.paddingLeft) {
            snakeP1end = true;
        } else if (snakeP1.getY() == Settings.paddingTop) {
            snakeP1end = true;
        } else if (snakeP1.getX() == Settings.paddingLeft + (Settings.numColumns - 1) * Settings.cellSize) {
            snakeP1end = true;
        } else if (snakeP1.getY() == Settings.paddingTop + (Settings.numRows - 1) * Settings.cellSize) {
            snakeP1end = true;
        }
        // </Player 1 - Crashed into the walls>

        // <Player 2 - Crashed into the walls>
        if (snakeP2.getX() == Settings.paddingLeft) {
            snakeP2end = true;
        } else if (snakeP2.getY() == Settings.paddingTop) {
            snakeP2end = true;
        } else if (snakeP2.getX() == Settings.paddingLeft + (Settings.numColumns - 1) * Settings.cellSize) {
            snakeP2end = true;
        } else if (snakeP2.getY() == Settings.paddingTop + (Settings.numRows - 1) * Settings.cellSize) {
            snakeP2end = true;
        }
        // </Player 2 - Crashed into the walls>

        if (snakeP1end || snakeP2end) {
            end();
        }
    }

    private void newAward() {
        if (Settings.isP1) {
            int numColumns = Settings.numColumns - 2;
            int numRows = Settings.numRows - 2;

            boolean canCreate = false;

            int x = 0;
            int y = 0;

            while (!canCreate) {
                canCreate = true;

                x = Settings.paddingLeft + Settings.cellSize + new Random().nextInt(numColumns) * Settings.cellSize;
                y = Settings.paddingTop + Settings.cellSize + new Random().nextInt(numRows) * Settings.cellSize;

                if (snakeP2.wasAttacked(x, y) || snakeP1.wasAttacked(x, y)) {
                    canCreate = false;
                }
            }

            if (Math.random() < 0.9) {
                award = new Apple(x, y);
            } else {
                award = new Freezer(x, y);
            }

            sockets.sendAward(award);
        } else {
            award = sockets.readAward();
        }
    }

    private void soundCatch() {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Settings.class.getResource("/sound/catch.wav")));
            clip.start();
        } catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void end() {
        Picture endLocal = null;

        if (snakeP1end) {
            endLocal = new Picture(snakeP1.getX(), snakeP1.getY(), "map/endLocal.jpg");
            endLocal.draw();
        }
        if (snakeP2end) {
            endLocal = new Picture(snakeP2.getX(), snakeP2.getY(), "map/endLocal.jpg");
            endLocal.draw();
        }

        Picture curtain = new Picture(Settings.paddingLeft, Settings.paddingTop, Settings.numColumns == 22 ? "blackTransparent20x10.png" : Settings.numColumns == 27 ? "blackTransparent25x15.png" : Settings.numColumns == 32 ? "blackTransparent30x20.png" : "blackTransparent35x25.png");
        curtain.draw();

        gameStateMessage = new Picture(Settings.paddingLeft + (Settings.numColumns / 2) * Settings.cellSize - 250, Settings.paddingTop + (Settings.numRows / 2) * Settings.cellSize - 125, "titles/" + ((Settings.isP1 && snakeP1end || !Settings.isP1 && snakeP2end) ? "lose.png" : "win.png"));
        gameStateMessage.draw();

        Text score = new Text(gameStateMessage.getX() + 383, gameStateMessage.getY() + 177, String.valueOf((Settings.isP1) ? snakeP1.getScore() : snakeP2.getScore()));
        score.draw();

        gameState = 2;

        try {
            Thread.sleep(7500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (gameState == 2) {
            close();
        }
    }

    private void close() {
        sockets.close();

        KeyboardSettings.stopKeyboard();

        // Restore values
        gameState = 0;

        snakeP1 = null;
        snakeP1end = false;
        directionP1 = null;

        snakeP2 = null;
        snakeP2end = false;
        directionP2 = null;

        award = null;
        p1IsFrozen = false;
        endTimeFrozenP1 = 0;
        p2IsFrozen = false;
        endTimeFrozenP2 = 0;

        loading = null;
        gameStateMessage = null;
        titleP1 = null;
        titleP2 = null;

        sockets = null;

        Main.mainMenu.restoreWindow();
    }

    @Override
    public void keyPressed(KeyboardEvent keyboardEvent) {
        switch (keyboardEvent.getKey()) {
            // GO LEFT
            case KeyboardEvent.KEY_LEFT:
                if (gameState == 1) {
                    if (Settings.isP1 && !p1IsFrozen) {
                        directionP1 = Direction.LEFT;
                    } else if (!Settings.isP1 && !p2IsFrozen) {
                        directionP2 = Direction.LEFT;
                    }
                }
                break;
            // GO UP
            case KeyboardEvent.KEY_UP:
                if (gameState == 1) {
                    if (Settings.isP1 && !p1IsFrozen) {
                        directionP1 = Direction.UP;
                    } else if (!Settings.isP1 && !p2IsFrozen) {
                        directionP2 = Direction.UP;
                    }
                }
                break;
            // GO RIGHT
            case KeyboardEvent.KEY_RIGHT:
                if (gameState == 1) {
                    if (Settings.isP1 && !p1IsFrozen) {
                        directionP1 = Direction.RIGHT;
                    } else if (!Settings.isP1 && !p2IsFrozen) {
                        directionP2 = Direction.RIGHT;
                    }
                }
                break;
            // GO DOWN
            case KeyboardEvent.KEY_DOWN:
                if (gameState == 1) {
                    if (Settings.isP1 && !p1IsFrozen) {
                        directionP1 = Direction.DOWN;
                    } else if (!Settings.isP1 && !p2IsFrozen) {
                        directionP2 = Direction.DOWN;
                    }
                }
                break;
            case KeyboardEvent.KEY_SPACE:
                if (gameState == 0 && Settings.isP1) {
                    sockets.sendStartGame();
                    startGame();
                } else if (gameState == 2) {
                    close();
                }
                break;
        }
    }

    @Override
    public void keyReleased(KeyboardEvent keyboardEvent) {
    }

}