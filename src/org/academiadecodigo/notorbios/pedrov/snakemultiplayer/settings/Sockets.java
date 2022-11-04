package org.academiadecodigo.notorbios.pedrov.snakemultiplayer.settings;

import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.awards.Apple;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.awards.Award;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.awards.Freezer;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.awards.TypeAward;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.snake.Direction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

public class Sockets {

    private ServerSocket server;
    private Socket connection;

    private ObjectInputStream get;
    private ObjectOutputStream post;

    public Sockets() {
        if (Settings.isP1) {
            try {
                server = new ServerSocket(Settings.portTCP);
                connection = server.accept();

                post = new ObjectOutputStream(connection.getOutputStream());
                get = new ObjectInputStream(connection.getInputStream());

                post.writeObject(new int[]{Settings.numColumns, Settings.numRows, Settings.velocity});
                post.flush();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "[#00] Unexpected error.", "ERROR", JOptionPane.ERROR_MESSAGE);

                System.exit(0);
            }
        } else {
            try {
                connection = new Socket(Settings.IPOpponent, Settings.portTCP);

                get = new ObjectInputStream(connection.getInputStream());
                post = new ObjectOutputStream(connection.getOutputStream());

                int[] settings = (int[]) get.readObject();

                Settings.numColumns = settings[0];
                Settings.numRows = settings[1];
                Settings.velocity = settings[2];
            } catch (IOException | ClassNotFoundException e) {
                JOptionPane.showMessageDialog(null, "[#01] No server with this adress was found.", "ERROR", JOptionPane.ERROR_MESSAGE);

                System.exit(0);
            }
        }
    }

    public void sendStartGame() {
        try {
            post.writeObject(true);
            post.flush();
        } catch (IOException e) {
            errorLostConnection("02");
        }
    }

    public void readStartGame() {
        try {
            get.readObject();
        } catch (IOException | ClassNotFoundException e) {
            errorLostConnection("03");
        }
    }

    public void sendMove(Direction direction) {
        try {
            post.writeObject(direction);
            post.flush();
        } catch (IOException e) {
            errorLostConnection("04");
        }
    }

    public Direction readMove() {
        try {
            return (Direction) get.readObject();
        } catch (IOException | ClassNotFoundException e) {
            errorLostConnection("05");
        }

        return null;
    }

    public void sendAward(Award award) {
        try {
            post.writeObject(new int[]{(award.getTypeAward() == TypeAward.APPLE) ? 0 : 1, award.getX(), award.getY()});
            post.flush();
        } catch (IOException e) {
            errorLostConnection("06");
        }
    }

    public Award readAward() {
        try {
            int[] receivedAward = (int[]) get.readObject();

            if (receivedAward[0] == 0) {
                return new Apple(receivedAward[1], receivedAward[2]);
            } else {
                return new Freezer(receivedAward[1], receivedAward[2]);
            }
        } catch (IOException | ClassNotFoundException e) {
            errorLostConnection("07");
        }

        return null;
    }

    public void sendSnakesFrozenStates(boolean[] snakesStates) {
        try {
            post.writeObject(snakesStates);
            post.flush();
        } catch (IOException e) {
            errorLostConnection("08");
        }
    }

    public boolean[] readSnakesFrozenStates() {
        try {
            return (boolean[]) get.readObject();
        } catch (IOException | ClassNotFoundException e) {
            errorLostConnection("09");
        }

        return null;
    }

    public void close() {
        try {
            post.close();
            get.close();
            connection.close();

            if (Settings.isP1) {
                server.close();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "[#10] Unexpected error.", "ERROR", JOptionPane.ERROR_MESSAGE);

            System.exit(0);
        }
    }

    private void errorLostConnection(String idError) {
        JOptionPane.showMessageDialog(null, "[#" + idError + "] Unexpected error.\nPossibly you/yours opponent has lost the connection.", "ERROR", JOptionPane.ERROR_MESSAGE);

        System.exit(0);
    }

}