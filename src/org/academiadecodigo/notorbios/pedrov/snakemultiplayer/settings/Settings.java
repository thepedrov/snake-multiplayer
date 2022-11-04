package org.academiadecodigo.notorbios.pedrov.snakemultiplayer.settings;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Settings {

    // VARs

    // Fixed
    public static final int paddingLeft = 10;
    public static final int paddingTop = 10;

    public static final int cellSize = 30;

    public static final int frozenTime = 5000;

    public static final int portTCP = 3075;

    // Default/Others Values
    public static int indexJFrame = 1;

    public static int numColumns; // The walls count too
    public static int numRows; // The walls count too
    // <BOARD>
    // 20x10 : 22
    // 25x15 : 27
    // 30x20 : 32
    // 35x25 : 37
    // </BOARD>

    public static int velocity;
    // <VELOCITY>
    // Slow : 125
    // Medium : 100
    // Fast : 75
    // </VELOCITY>

    public static boolean isP1; // Is the same as Server

    public static String IPOpponent;

    // METHODS
    public static void fixWindowGame() {
        Frame.getFrames()[indexJFrame].setTitle("Snake MultiPlayer");

        Frame.getFrames()[indexJFrame].setIconImage(new ImageIcon(Objects.requireNonNull(Settings.class.getResource("/icon.png"))).getImage());

        Frame.getFrames()[indexJFrame].setResizable(false);

        Frame.getFrames()[indexJFrame].addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    public static void centerWindowGame() {
        Frame.getFrames()[indexJFrame].pack();
        Frame.getFrames()[indexJFrame].setLocationRelativeTo(null);
        Frame.getFrames()[indexJFrame].setVisible(true);
    }

}