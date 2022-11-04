package org.academiadecodigo.notorbios.pedrov.snakemultiplayer.gui;

import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.game.Game;
import org.academiadecodigo.notorbios.pedrov.snakemultiplayer.settings.Settings;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.*;
import java.util.Objects;
import java.util.regex.Pattern;
import javax.swing.*;

public class MainMenu {

    // VARs
    private JPanel panelMainMenu;
    private JLabel imgLogo;
    private JLabel txtInfo;
    private JLabel txtApple;
    private JLabel txtFreezer;
    private JLabel txtKeys;
    private JLabel btnRules;
    private JLabel txtYouAre;
    private JRadioButton radioClient;
    private JLabel txtServer;
    private JTextField inputServer;
    private JRadioButton radioServer;
    private JLabel txtBoard;
    private JComboBox cboxBoard;
    private JLabel txtSpeed;
    private JComboBox cboxSpeed;
    private JLabel txtIPAddress;
    private JButton btnPlay;

    private boolean waitingOrPlay;

    // METHODS
    public MainMenu() {
        getIPs();

        createWindow();

        // BUTTON RULES
        btnRules.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnRules.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(null, "1. The objective of the game is to catch apples;\n2. You can't hit the walls;\n3. You can't hit your opponent;\n4. You can't bump into yourself;\n5. Case bump face to face, the player with the most apples wins;\n6. Case bump head to head, the player who changed direction most recently loses.", "RULES", JOptionPane.PLAIN_MESSAGE, new ImageIcon(Settings.class.getResource("/icon.png")));

                if (!Frame.getFrames()[1].getTitle().equals("Snake MultiPlayer")) {
                    Settings.indexJFrame = 2;
                }
            }
        });

        // BUTTON PLAY
        btnPlay.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!waitingOrPlay) {
                    btnPlayOnClick();
                }
            }
        });
    }

    private void startGame() {
        Game game = new Game();
    }

    private void btnPlayOnClick() {
        if (radioClient.isSelected()) {
            String IP = inputServer.getText().trim();

            if (validIP(IP)) {
                Settings.isP1 = false;

                Settings.IPOpponent = IP;

                lockWindow();

                startGame();
            } else {
                JOptionPane.showMessageDialog(null, "Enter a valid IP.", "ERROR", JOptionPane.ERROR_MESSAGE);

                Settings.indexJFrame = 2;
            }
        } else {
            Settings.isP1 = true;

            lockWindow();

            switch (String.valueOf(cboxBoard.getSelectedItem())) {
                case "20x10":
                    Settings.numColumns = 22;
                    break;
                case "25x15":
                    Settings.numColumns = 27;
                    break;
                case "30x20":
                    Settings.numColumns = 32;
                    break;
                case "35x25":
                    Settings.numColumns = 37;
                    break;
            }

            Settings.numRows = Settings.numColumns - 10;

            switch (String.valueOf(cboxSpeed.getSelectedItem())) {
                case "Slow":
                    Settings.velocity = 125;
                    break;
                case "Medium":
                    Settings.velocity = 100;
                    break;
                case "Fast":
                    Settings.velocity = 75;
                    break;
            }

            startGame();
        }
    }

    private void createWindow() {
        JFrame window = new JFrame("Snake MultiPlayer");

        window.setIconImage(new ImageIcon(Objects.requireNonNull(Settings.class.getResource("/icon.png"))).getImage());

        window.setContentPane(panelMainMenu);

        window.setResizable(false);

        window.pack();

        window.setLocation((int) ((Toolkit.getDefaultToolkit().getScreenSize().getWidth() - window.getWidth())), (int) ((Toolkit.getDefaultToolkit().getScreenSize().getHeight() - window.getHeight() - 60)));

        window.setVisible(true);

        // EXIT ALL APP
        window.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                System.exit(0);
            }
        });
    }

    private void lockWindow() {
        waitingOrPlay = true;

        radioClient.setEnabled(false);
        inputServer.setEnabled(false);
        radioServer.setEnabled(false);
        cboxBoard.setEnabled(false);
        cboxSpeed.setEnabled(false);
        btnPlay.setText("Is playing...");
    }

    public void restoreWindow() {
        Frame.getFrames()[Settings.indexJFrame].dispose();

        btnPlay.setText("START/PLAY AGAIN");
        btnPlay.requestFocus();

        waitingOrPlay = false;
    }

    private void getIPs() {
        try {
            // PRIVATE IP
            String ip = InetAddress.getLocalHost().getHostAddress();

            txtIPAddress.setText("IP Address: " + ip);
            inputServer.setText(ip.substring(0, ip.lastIndexOf(".") + 1));

            // PUBLIC IP
            //txtIPAddress.setText("IP Address: " + (new BufferedReader(new InputStreamReader((new URL("http://checkip.amazonaws.com/")).openStream()))).readLine());
        } catch (UnknownHostException e) {
            txtIPAddress.setText("IP Address: No connection");
        }
    }

    private boolean validIP(String ip) {
        String zeroTo255 = "(\\d{1,2}|(0|1)\\d{2}|2[0-4]\\d|25[0-5])";

        String regex = zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255 + "\\." + zeroTo255;

        if (ip == null) {
            return false;
        }

        return Pattern.compile(regex).matcher(ip).matches();
    }

}