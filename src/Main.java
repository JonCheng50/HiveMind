import javax.swing.*;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main: Class that is run to start the game
 */
public class Main {

    private static int start = 0;
    private static Timer timer = new Timer(true);       // Checks for when space has been pressed

    public static void main(String[] args) {

        JFrame menu = new JFrame();
        JButton button = new JButton();
        JLabel logo = new JLabel(new ImageIcon("images/Logo.png"));
        button.setText("Play!");        // Button not actually shown, space bar is used to press

        menu.getContentPane().setBackground(Color.WHITE);
        menu.add(button);
        menu.add(logo);
        menu.pack();
        menu.setLocation(500, 250);
        menu.setVisible(true);

        button.addActionListener(evt -> {
            menu.dispose();
            start = 1;
        });

        timer.schedule(new checker(), 0, 500);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    // Establishes connection with Server
    static class checker extends TimerTask {
        public void run() {
            if (start == 1) {
                    timer.cancel();
                    // For future note: don't poke the Socket before the game
                    // Causes deadlock in inputStream and just a whole lot of crap
                    new Game();
            }
        }
    }
}
