import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class Game {

    static final int PORT = 9012;

    private Timer timer = new Timer(true);

    private JFrame frame;
    private GameBoard board;
    private GameCourt court;

    private int ID;
    private Player me;
    private List<Player> players;

    private boolean gameOver;

    private List<String> colors;
    private List<Integer> chosen;
    private int cIndex;

    private Socket socket;
    private Connection connection;

    public Game() {
        // Set up GUI
        frame = new JFrame("Game");
        final JPanel status_panel = new JPanel();
        JLabel statusLbl = new JLabel("Welcome to Hive Mind");
        status_panel.add(statusLbl, SwingConstants.CENTER);
        status_panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));

        final JPanel control_panel = new JPanel();

        final JButton reset = new JButton("Reset");
        reset.addActionListener(e13 -> {
            System.out.println("Reset pressed");
        });
        control_panel.add(reset);

        final JButton instructions = new JButton("Instructions");
        instructions.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Welcome to Hive Mind!\n" +
                                "The goal of the game is to not get kicked out of the hive. To do this, you want to try and get the same answers as everyone else! \n" +
                            "\nEach turn, one player is prompted to select a question. After the question is chosen, " +
                                "everyone types in their answers and presses the Ready button. \nWhen everyone has submitted " +
                                "their answers, everyone goes around and shares the answers. \nFor each answer you wrote, you " +
                                "get one point for each person who wrote that answer. \nThis means all of your answers automatically " +
                                "gets 1 point because at the very least you wrote it. \nInput these scores into each blank. \n" +
                                "At the end of the round, the lowest scores will move down a layer. The number of players " +
                                "moving down depends on the symbol in the top left of the screen. \nA honeypot bee means " +
                                "that the lowest scoring player moves down, and the highest scoring player moves up!\n" +
                                "If multiple people tie for the lowest/highest score, they all move down/up. \n" +
                                "\nIf you make up your own question, be sure the first number digit (don't use words like \"two\" or \"five\")" +
                                "represents the number of answers you would like, \nranging from 1 to 5. If no numbers are present, " +
                                "everyone will get only 1 answer."
                        );
            }
        });
        control_panel.add(instructions);
        control_panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));

        board = new GameBoard(this);
        board.repaint();

        court = new GameCourt(this);
        court.repaint();

        GroupLayout layout = new GroupLayout(frame.getContentPane());

        layout.setHorizontalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(control_panel)
                                .addComponent(status_panel)
                                .addGroup(layout.createSequentialGroup()
                                        .addContainerGap(10, 10)
                                        .addComponent(board)
                                        .addContainerGap(5, 5)
                                        .addComponent(court)
                                        .addContainerGap(10, 10)))

        );
        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(control_panel)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup()
                                .addComponent(board)
                                .addComponent(court))
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(status_panel)
        );

        // Put the frame on the screen
        //frame.pack();
        frame.getContentPane().setLayout(layout);
        frame.setSize(1100, 700);
        frame.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width/2 - 550, dim.height/2 - 350);

        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setVisible(true);

        WindowListener listener = new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (court.getState() == -2) {
                    court.errorMessage("Please wait until the next round to leave the game");
                } else if (court.getCQ()) {
                    court.errorMessage("Please choose a question before leaving the game");
                } else {
                    int reply = JOptionPane.showConfirmDialog(frame, "Are you sure you want to leave the game?", "Exit", JOptionPane.YES_NO_OPTION);
                    if (reply == JOptionPane.YES_OPTION) {
                        System.out.println("Closing game");
                        connection.sendPacket(new LeavePacket(me));
                        System.exit(0);
                    }
                }
            }
        };


        //Set up Connection

        players = new ArrayList<>();
        gameOver = false;

        ID = -1;
        me = askPlayer();

        try {
            String ip = null;
            try {
                BufferedReader reader = new BufferedReader(new FileReader("IP.txt"));
                ip = reader.readLine();
                reader.close();
            } catch (IOException ignored){}
            if (ip == null || ip.isEmpty()) {
                socket = new Socket("000.00.000.00", PORT);
            } else {
                socket = new Socket(ip, PORT);
            }
            System.out.println("Connected to Server");
            connection = new Connection(this, socket);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Server isn't running or already has max number of players!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        timer.schedule(new waitToSend(), 0, 100);
        frame.addWindowListener(listener);
    }

    private Player askPlayer() {
        String name = "";
        BufferedWriter writer;
        try {
            BufferedReader reader = new BufferedReader(new FileReader("Username.txt"));
            name = reader.readLine();
            reader.close();
        } catch (IOException ignored) {
        }
        name = JOptionPane.showInputDialog("Please input your username", name);
        if (name == null) {
            System.exit(0);
        }
        try {
            writer = new BufferedWriter(new FileWriter("Username.txt"));
            writer.write(name);
            writer.close();
        } catch (IOException ignored) {}
        return new Player(name, -1, -1);
    }

    private void setColor() {
        String color = (String) (JOptionPane.showInputDialog(frame, "Please choose a color", "Menu",
                JOptionPane.QUESTION_MESSAGE, null, colors.toArray(), colors.get(0)));
        cIndex = colors.indexOf(color);
        int cNum = 0;
        for (int x = 0; x <= cIndex; x++) {
            if (chosen.contains(cNum)) {
                x--;
            }
            cNum++;
        }
        cNum--;
        me.setcIndex(cIndex);
        me.setColor(cNum);
    }

    class waitToSend extends TimerTask {
        public void run() {
            if (ID != -1) {
                timer.cancel();
                me.setID(ID);
                setColor();
                if (me.getColor() != 0 && me.getColor() != 11) {
                    int shiny = (int) (Math.random() * 5);
                    if (shiny == 3) {
                        me.setShiny();
                    }
                }
                //For testing purposes only
//                if (me.getUsername().equals("ptl")) {
//                    me.setShiny();
//                }
                if (me.getUsername().contains("†")) {
                    me.setShiny();
                }
                connection.sendPacket(me);
                players.add(me);
                board.addPlayer(me, false);

                frame.setTitle("Game - P" + ID + " " + me.getUsername());
            }
        }
    }

    public void gameOver() {
        gameOver = true;
        court.gameOver();
    }

    public void packetReceived(Object object) {
        if (object instanceof InitPacket) {
            InitPacket packet = (InitPacket) object;
            ID = packet.getID();
            me.setConID(packet.getConID());
        } else if (object instanceof ColorPacket) {
            ColorPacket packet = (ColorPacket) object;
            colors = packet.getColors();
            chosen = packet.getChosen();
        } else if (object instanceof Player) {
            Player p = (Player) object;
            if (p.getID() != ID) {
                players.add(p);
                System.out.println("Received player " + p.getUsername());
                board.addPlayer(p, ID != -1 && p.getID() > ID);
                court.hideStart();
            }
        } else if (object instanceof PosPacket) {
            PosPacket packet = (PosPacket) object;
            if (packet.getPlayersPos() == null) {
                connection.sendPacket(new PosPacket(board.getPlayersPos()));
            } else {
                board.setBoard(packet.getPlayersPos());
                court.updateState(-2, false);
                players.addAll(packet.getPlayersPos().keySet());
            }
        } else if (object instanceof Boolean) {
            if ((Boolean) object) {
                court.showStart();
            } else {
                court.hideStart();
            }
        } else if (object instanceof UpdatePacket) {
            if (gameOver) {
                System.out.println("game over");
                return;
            }
            UpdatePacket packet = (UpdatePacket) object;
            court.updateState(packet.getState(), true);
            if (packet.isChoose()) {
                court.chooseQ();
            }
        } else if (object instanceof QPacket) {
            court.hideTable();
            QPacket packet = (QPacket) object;
            court.setQ(packet.getQuestion());
            board.setType(packet.getType());
            board.repaint();
        } else if (object instanceof pDownPacket) {
            pDownPacket packet = (pDownPacket) object;
            boolean up = packet.isUp();
            List<Integer> pDown = packet.getpDown();
            for (int ID : pDown) {
                if (!players.contains(new Player(ID))) { //If player leaves
                    continue;
                }
                if (up) {
                    board.pUp(new Player(ID));
                } else {
                    board.pDown(new Player(ID));
                }
                System.out.println("pDown received: " + players.get(players.indexOf(new Player(ID))).getUsername());
            }
            board.showDown(pDown, up);
        } else if (object instanceof ScorePacket) {
            HashMap<Integer, Integer> scores = ((ScorePacket)object).getScores();
            HashMap<Player, Integer> tabData = new HashMap<>();
            for (Integer id : scores.keySet()) {
                tabData.put(players.get(players.indexOf(new Player(id))), scores.get(id));
            }
            court.showTable(tabData);
        } else if (object instanceof LeavePacket) {
            System.out.println("Player leaving");
            LeavePacket packet = (LeavePacket) object;
            Player p = packet.getP();
            players.remove(p);
            board.pLeave(p);
        }
    }

    void inputReceived(int score) { // Score inputted
        connection.sendPacket(new ScorePacket(ID, score));
    }

    void inputReceived(boolean ready) { // Ready button pressed
        connection.sendPacket(ready);
    }

    void inputReceived(Question q) { // Question q chosen
        connection.sendPacket(q);
    }

    void inputReceived(UpdatePacket p) { // Start button pressed
        connection.sendPacket(p);
    }

}
