import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {

    private boolean running;

    private ServerSocket serverSocket;

    private static List<Connection> cons;
    private static List<Player> players;
    private static HashMap<Integer, Integer> scores;

    private static Connection newPCon;

    private static List<String> colors;
    private static List<Integer> chosen;

    private static int ready;
    private static boolean start;
    //0: waiting for pJoin/game to start, 1: typing answers in, 2: typing scores in
    private static int state;
    private static int turn;
    private static int type;
    private static int scoreInc;

    private static HashMap<Player, List<Integer>> playersPos;
    private static Timer timer;

    private static List<Integer> OpenConID;
    private static int numP;

    public static List<String> COLOR;

    static {
        COLOR = new LinkedList<>(Arrays.asList("Normal",
                "<html><p style=\"color:#FF0000\";>Red</p></html>",
                "<html><p style=\"color:#333399\";>Blue</p></html>",
                "<html><p style=\"color:#66CC66\";>Green</p></html>",
                "<html><p style=\"color:#FFCC00\";>Yellow</p></html>",
                "<html><p style=\"color:#999999\";>Gray</p></html>",
                "<html><p style=\"color:#FF99FF\";>Pink</p></html>",
                "<html><p style=\"color:#66CCCC\";>Teal</p></html>",
                "<html><p style=\"color:#9966CC\";>Purple</p></html>",
                "<html><p style=\"color:#FF9966\";>Orange</p></html>",
                "<html><p style=\"color:#6699CC\";>Light Blue</p></html>",
                "Special"));
    }


    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        System.out.println("Server is running...");
        players = new ArrayList<>();
        cons = new ArrayList<>();
        scores = new HashMap<>();
        //"<html>N<span style=\"color:#FFCC00\";>o</span>r<span style=\"color:#FFCC00\";>m</span>a<span style=\"color:#FFCC00\";>l</span>"
        colors = new LinkedList<>(Arrays.asList("Normal",
                "<html><p style=\"color:#FF0000\";>Red</p></html>",
                "<html><p style=\"color:#333399\";>Blue</p></html>",
                "<html><p style=\"color:#66CC66\";>Green</p></html>",
                "<html><p style=\"color:#FFCC00\";>Yellow</p></html>",
                "<html><p style=\"color:#999999\";>Gray</p></html>",
                "<html><p style=\"color:#FF99FF\";>Pink</p></html>",
                "<html><p style=\"color:#66CCCC\";>Teal</p></html>",
                "<html><p style=\"color:#9966CC\";>Purple</p></html>",
                "<html><p style=\"color:#FF9966\";>Orange</p></html>",
                "<html><p style=\"color:#6699CC\";>Light Blue</p></html>",
                "Special"));
        chosen = new ArrayList<>();

        ready = 0;
        start = false;
        numP = 0;
        state = 0;
        turn = 0;
        scoreInc = 0;

        timer = new Timer(false);

        OpenConID = new LinkedList<>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10,11));

        try {
            serverSocket = new ServerSocket(Game.PORT);
            addNewPlayers();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewPlayers() {
        try {
            while (!OpenConID.isEmpty()) {
                Socket socket = serverSocket.accept();
                Connection c = new Connection(socket);
                System.out.println("Client " + numP + " has connected");
                if (!start) {
                    for (Player p : players) {
                        c.sendPacket(p);
                    }
                } else {
                    System.out.println("new player while game is running...");
                    newPCon = c;
                    cons.get(0).sendPacket(new PosPacket());
                    try {
                        cons.get(1).sendPacket(new PosPacket());
                    } catch (IndexOutOfBoundsException ignored){}
                    timer = new Timer(false);
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (playersPos != null) {
                                newPCon.sendPacket(new PosPacket(playersPos));
                                newPCon = null;
                                playersPos = null;
                                timer.cancel();
                            }
                        }
                    }, 0, 100);
                }
                int conID = OpenConID.get(0);
                cons.add(conID,c);
                OpenConID.remove(0);
                c.sendPacket(new ColorPacket(colors, chosen));
                c.sendPacket(new InitPacket(numP, conID));
                numP++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void packetReceived(Object object) {
        if (object instanceof Player) {
            Player p = (Player) object;
            players.add(p);
            for (Connection c : cons) {
                c.sendPacket(p);
            }
            colors.remove(p.getcIndex());
            chosen.add(p.getColor());

            if (start) {
                System.out.println("Adding extra ready/score to account for new player");
                if (state == 1) {
                    ready ++;
                    scoreInc ++;
                } else if (state == 2) {
                    scoreInc ++;
                }
            }

            System.out.println("------------------------");
            System.out.println("PlayerJoin check:");
            for (Player a : players) {
                System.out.println("Player: " + a.getUsername() + ", ID: " + a.getID() + ", color: " + a.getColor() + ", conID: " + a.getConID());
            }
            System.out.println("OpenConID: " + OpenConID.toString());
            System.out.println("------------------------");
        } else if (object instanceof Question) { // state -> 1
            Question q = (Question) object;
            double rand = Math.random();
            if (rand < 0.4) {
                type = 1;
            } else if (rand < 0.7) {
                type = 2;
            } else if (rand < 0.9) {
                type = 3;
            } else {
                type = 0;
            }
            //type = 1;
            System.out.println("Question/type " + type + " chosen, sending to players");
            for (Connection c : cons) {
                c.sendPacket(new QPacket(q, type));
                c.sendPacket(new UpdatePacket(1, false)); // State -1 to 1
            }
            state = 1;
            System.out.println("State is now " + state);
            System.out.println("----------------------");

            // Update state to 1 for all games

        } else if (object instanceof Boolean) { //should only be state 0 or 1??
            ready++;
            System.out.println("Ready received: " + ready);
            //if (players.size() > 2) { //TODO Player size
                //Checks to see if everyone is ready
                if (ready == players.size() && start) {
                    allReady();
                } else if (ready == players.size()) {
                    for (Connection c : cons) {
                        c.sendPacket(true);
                    }
                }
            //}
        } else if (object instanceof UpdatePacket) { // Start button has been pressed
            System.out.println("Start button pressed");
            start = true;
            for (Connection c : cons) {
                c.sendPacket(false);
            }
            allReady();
        } else if (object instanceof ScorePacket) { //state must be 2 -> -1
            ScorePacket packet = (ScorePacket) object;
            System.out.println("Score received from P" + packet.getID() + ", score = " + packet.getScore());
            scores.put(packet.getID(), packet.getScore());

            // Means everyone has inputted scores, sends pDown
            if (scores.size() + scoreInc == players.size()) {
                for (Connection c : cons) {
                    c.sendPacket(new ScorePacket(scores));
                }
                allScoresIn();
            }
        } else if (object instanceof PosPacket) {
            if (playersPos == null)
                playersPos = ((PosPacket)object).getPlayersPos();
        } else if (object instanceof LeavePacket) {
            LeavePacket packet = (LeavePacket) object;
            Player p = packet.getP();
            int ID = p.getID();
            int conID = p.getConID();
            int color = p.getColor();
            System.out.println("P" + ID + " " + p.getUsername() + " has left the game");
            for (Connection con : cons) {
                con.sendPacket(new LeavePacket(p));
            }

            int conIndex = conID;
            for (int count = 0; count < conID; count ++) {
                if (OpenConID.contains(count)) {
                    conIndex --;
                }
            }
            try {
                cons.get(conIndex).close();
            } catch (IOException e) {
                System.out.println("IOException in closing player's connection");
            }
            cons.remove(conIndex);
            System.out.println("Adding back conID " + conID);
            for (int i = 0; i < OpenConID.size(); i ++) {
                if (OpenConID.get(i) > conID) {
                    OpenConID.add(i, conID);
                    break;
                }
            }
            players.remove(p);
            scores.remove(ID);
            int sub = 0;
            for (Integer x : chosen) {
                if (x < color) {
                    sub ++;
                }
            }
            colors.add(color - sub, COLOR.get(color));
            System.out.println("Colors: " + colors.toString());
            chosen.remove((Integer) color);
            System.out.println("added color" + color + " back");

            if (ready == players.size() && start) {
                allReady();
            } else if (ready == players.size()) {
                for (Connection c : cons) {
                    c.sendPacket(true);
                }
            }

            if (scores.size() + scoreInc == players.size()) {
                allScoresIn();
            }

            System.out.println("------------------------");
            System.out.println("PlayerLeave check:");
            for (Player a : players) {
                System.out.println("Player: " + a.getUsername() + ", ID: " + a.getID() + ", color: " + a.getColor() + ", conID: " + a.getConID());
            }
            System.out.println("OpenConID: " + OpenConID.toString());
            System.out.println("------------------------");
        }
    }

    private static void allReady() {
        System.out.println("All players ready");

        // Either prompts player (== turn) to choose question (state == 0 or 2)
        // Or moves players to state 2 (state == 1)
        for (int c = 0; c < players.size(); c++) {
            if (state == 0 || state == 2) {
                if (c == turn) {
                    cons.get(c).sendPacket(new UpdatePacket(-1, true));
                } else {
                    cons.get(c).sendPacket(new UpdatePacket(-1, false));
                }
            } else {
                cons.get(c).sendPacket(new UpdatePacket(2, false));
            }
        }
        if (state == 1) {
            state = 2;
        }
        ready = 0;
        System.out.println("State is now " + state);
        System.out.println("----------------------");
        //if state = 0, now choosing waiting for question
        //if state = 1, sends update -> move to state 2
    }

    private static void allScoresIn() {
        System.out.println("All scores inputted");
        List<Integer> pUp = new ArrayList<>();
        if (type == 0) {
            pUp = getPUp();
        }
        turn = (turn + 1) % players.size();
        List<Integer> pDown = getPDown();
        for (int c = 0; c < players.size(); c++) {
            if (type == 0) {
                cons.get(c).sendPacket(new pDownPacket(pUp, true));
            }
            cons.get(c).sendPacket(new pDownPacket(pDown, false));
            if (c == turn) {
                cons.get(c).sendPacket(new UpdatePacket(-1, true));
            } else {
                cons.get(c).sendPacket(new UpdatePacket(-1, false));
            }
        }
        scoreInc = 0;
        scores.clear();
        state = 0;
        playersPos = null;
    }

    private static ArrayList<Integer> getPUp() {
        ArrayList<Integer> pIDs = new ArrayList<>();
        int max = Collections.max(scores.values());
        System.out.println("max score: " + max);
        for (Map.Entry<Integer, Integer> e : scores.entrySet()) {
            if (e.getValue().equals(max)) {
                pIDs.add(e.getKey());
            }
        }
        return pIDs;
    }

    private static ArrayList<Integer> getPDown() {
        ArrayList<Integer> pIDs = new ArrayList<>();
        int count = type;
        if (type == 0) {
            count = 1;
        }
        for (int c = 0; c < count; c++) {
            int min = Collections.min(scores.values());
            System.out.println("min score: " + min);
            for (Map.Entry<Integer, Integer> e : scores.entrySet()) {
                if (e.getValue().equals(min)) {
                    pIDs.add(e.getKey());
                }
            }
            for (int ID : pIDs) {
                scores.remove(ID);
            }
            if (scores.isEmpty()) {
                break;
            }
        }
        return pIDs;
    }

    public void close() {
        System.out.println("Server closed");
        running = false;
        try {
            for (Connection c : cons) {
                c.close();
            }
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
