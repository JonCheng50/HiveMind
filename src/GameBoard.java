import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class GameBoard extends JPanel {

    private Game game;
    private int type;

    private HashMap<Player, JLabel> players;
    private HashMap<Player, List<Integer>> playersPos; // List {layer, position}
    private List<List<Integer>> layerPos;
    private List<Player> losers;

    private JLabel downLbl;
    private JLabel upLbl;
    private JLabel newPLbl;

    private BufferedImage background;
    private BufferedImage typeImg;

    private static final int COURT_WIDTH = 470;
    private static final int COURT_HEIGHT = 650;

    private final int ICON_WIDTH = 68;
    private final int ICON_HEIGHT = 50;

    public GameBoard(Game game) {
        this.game = game;
        type = -1;
        players = new HashMap<>();
        playersPos = new HashMap<>();
        layerPos = new ArrayList<>();
        losers = new ArrayList<>();

        downLbl = new JLabel("");
        downLbl.setBounds(50, 450, 370, 120);
        downLbl.setHorizontalAlignment(SwingConstants.CENTER);
        downLbl.setVerticalAlignment(SwingConstants.CENTER);
        downLbl.setFont(new Font("Tahoma", Font.BOLD, 20));
        downLbl.setForeground(Color.WHITE);
        //downLbl.setOpaque(true);
        downLbl.setVisible(false);
        this.add(downLbl);

        upLbl = new JLabel("");
        upLbl.setBounds(50, 440, 370, 60);
        upLbl.setHorizontalAlignment(SwingConstants.CENTER);
        downLbl.setVerticalAlignment(SwingConstants.CENTER);
        upLbl.setFont(new Font("Tahoma", Font.BOLD, 20));
        upLbl.setForeground(Color.WHITE);
        //upLbl.setOpaque(true);
        upLbl.setVisible(false);
        this.add(upLbl);

        newPLbl = new JLabel("");
        newPLbl.setBounds(50, 420, 370, 60);
        newPLbl.setHorizontalAlignment(SwingConstants.CENTER);
        newPLbl.setVerticalAlignment(SwingConstants.CENTER);
        newPLbl.setFont(new Font("Tahoma", Font.BOLD, 20));
        newPLbl.setForeground(Color.WHITE);
        newPLbl.setVisible(false);
        this.add(newPLbl);

        for (int x = 0; x < 6; x++) {
            List<Integer> l = Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            layerPos.add(l);
        }

        //this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.setLayout(null);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            background = ImageIO.read(new File("images/Board.png"));
            if (type != -1) {
                typeImg = ImageIO.read(new File("images/Type" + type + ".png"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        g.drawImage(background, 0, 0, 466, 586, null);
        if (type != -1) {
            g.drawImage(typeImg, 10, 10, typeImg.getWidth(), typeImg.getHeight(), null);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }

    public void setType(int t) {
        type = t;
    }

    public HashMap<Player, List<Integer>> getPlayersPos() {
        return playersPos;
    }

    public void addPlayer(Player p, boolean newP) {
        JLabel icon = pLabel(p);
        List<Integer> position = getNextPos(0);
        icon.setBounds(position.get(1), position.get(2), ICON_WIDTH, ICON_HEIGHT); //65, 48
        this.add(icon);
        players.put(p, icon);
        playersPos.put(p, Arrays.asList(0, position.get(0)));
        if (newP) {
            String txt;
            if (p.getColor() == 1 || p.getColor() == 11) {
                txt = p.getUsername() + " has joined the game!";
            } else {
                txt = "<html><span style=\"color:#" +
                        Server.COLOR.get(p.getColor()).substring(23, 29) + "\";>" +
                        p.getUsername() + "</span>" + " has joined the game!";
            }
            newPLbl.setText(txt);
            newPMessage();
        }
        this.repaint();
    }

    private void newPMessage() {
        newPLbl.setVisible(true);
        Timer timer = new Timer(3000, e -> newPLbl.setVisible(false));
        timer.setRepeats(false);
        timer.restart();
    }

    public void setBoard(HashMap<Player, List<Integer>> board) {
        playersPos = board;
        for (Player p : board.keySet()) {
            JLabel icon = pLabel(p);
            List<Integer> pos = playersPos.get(p);
            List<Integer> nextPos = getNextPos(pos.get(0));
            pos.set(1, nextPos.get(0));
            icon.setBounds(nextPos.get(1), nextPos.get(2), ICON_WIDTH, ICON_HEIGHT);
            this.add(icon);
            players.put(p, icon);
            this.repaint();
            System.out.println("Added player " + p.getID() + " to layer " + pos.get(0));
        }
    }

    private JLabel pLabel(Player p) {
        String iconPath = "images/Bee" + p.getColor() + ".png";
        if (p.getShiny()) {
            iconPath = "images/Shiny/Bee" + p.getColor() + ".png";
        }
        ImageIcon pic = new ImageIcon(iconPath);
        Image img = pic.getImage().getScaledInstance(ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        JLabel icon = new JLabel(new ImageIcon(img));
        icon.setToolTipText(p.getUsername());
        return icon;
    }



    public void pUp(Player p) {
        List<Integer> pos = playersPos.get(p);
        int newLayer = pos.get(0) - 1;
        if (newLayer >= 0) {
            layerPos.get(pos.get(0)).set(pos.get(1), 0);
            List<Integer> nextPos = getNextPos(newLayer);
            players.get(p).setBounds(nextPos.get(1), nextPos.get(2), ICON_WIDTH, ICON_HEIGHT);
            pos.set(0, newLayer);
            pos.set(1, nextPos.get(0));
            this.repaint();
            System.out.println("P" + p.getID() + " moved up to Layer: " + playersPos.get(p).get(0) + " and position " + playersPos.get(p).get(1));
        }
    }

    public void pDown(Player p) {
        List<Integer> pos = playersPos.get(p);
        int newLayer = pos.get(0) + 1;
        if (newLayer == 6) {
            List<Player> l = new ArrayList<>(players.keySet());
            losers.add(l.get(l.indexOf(p)));
        } else {
            layerPos.get(pos.get(0)).set(pos.get(1), 0); //set old pos to 0
            List<Integer> nextPos = getNextPos(newLayer);
            players.get(p).setBounds(nextPos.get(1), nextPos.get(2), ICON_WIDTH, ICON_HEIGHT);
            pos.set(0, newLayer);
            pos.set(1, nextPos.get(0));
            this.repaint();
            System.out.println("P" + p.getID() + " moved down to Layer: " + playersPos.get(p).get(0) + " and position " + playersPos.get(p).get(1));
        }
    }

    public void showDown(List<Integer> pDown, boolean up) {
        StringBuilder txt = new StringBuilder();
        txt.append("<html>");
        if (!losers.isEmpty()) {
            game.gameOver();
            for (Player p : losers) {
                int color = p.getColor();
                if (color == 0 || color == 11) {
                    txt.append(p.getUsername()).append(", ");
                } else {
                    txt.append("<span style=\"color:#")
                            .append(Server.COLOR.get(p.getColor()), 23, 29).append("\";>")
                            .append(p.getUsername()).append("</span>")
                            .append(", ");
                }
            }
            txt.setLength(txt.length() - 2);
            txt.append(" has lost the game!");
            downLbl.setText(txt.toString());
            downLbl.setVisible(true);
        } else {
            List<Player> pList = new ArrayList<>(players.keySet());
            for (int ID : pDown) {
                Player p = pList.get(pList.indexOf(new Player(ID)));
                int color = p.getColor();
                if (color == 0 || color == 11) {
                    txt.append(p.getUsername()).append(", ");
                } else {
                    txt.append("<span style=\"color:#")
                            .append(Server.COLOR.get(p.getColor()), 23, 29).append("\";>")
                            .append(p.getUsername()).append("</span>")
                            .append(", ");
                }
            }
            txt.setLength(txt.length() - 2);
            if (up) {
                txt.append(" went UP a level!");
                upLbl.setText(txt.toString());
                upMessage();
            } else {
                txt.append(" went DOWN a level");
                downLbl.setText(txt.toString());
                downMessage();
            }
        }

    }

    private void upMessage() {
        upLbl.setVisible(true);
        Timer timer = new Timer(6000, e -> upLbl.setVisible(false));
        timer.setRepeats(false);
        timer.restart();
    }

    private void downMessage() {
        downLbl.setVisible(true);
        Timer timer = new Timer(6000, e -> downLbl.setVisible(false));
        timer.setRepeats(false);
        timer.restart();
    }

    public void pLeave(Player p) {
        String txt;
        if (p.getColor() == 0 || p.getColor() == 11) {
            txt = p.getUsername() + " has left the game";
        } else {
            txt = "<html><span style=\"color:#" +
                    Server.COLOR.get(p.getColor()).substring(23, 29) + "\";>" +
                    p.getUsername() + "</span>" + " has left the game";
        }
        newPLbl.setText(txt);
        newPMessage();
        //for (Map.Entry<Player, JLabel>)
        players.get(p).setVisible(false);
        this.remove(players.get(p));
        players.remove(p);
        List<Integer> pos = playersPos.get(p);
        layerPos.get(pos.get(0)).set(pos.get(1), 0);
        playersPos.remove(p);
    }


    // Fills position in layerPos
    // Returns {position, x, y}
    private List<Integer> getNextPos(int layerNum) {
        List<Integer> layer = layerPos.get(layerNum);
        int pos = layer.indexOf(0);
        layer.set(pos, 1);
        List<Integer> p = new ArrayList<>();
        p.add(pos);
        if (layerNum == 0) {
            if (pos == 0) {
                p.add(181);
                p.add(48);
            } else if (pos == 1) {
                p.add(238);
                p.add(48);
            } else if (pos == 2) {
                p.add(128);
                p.add(84);
            } else if (pos == 3) {
                p.add(184);
                p.add(94);
            } else if (pos == 4) {
                p.add(240);
                p.add(94);
            } else if (pos == 5) {
                p.add(298);
                p.add(84);
            } else if (pos == 6) {
                p.add(124);
                p.add(37);
            } else if (pos == 7) {
                p.add(294);
                p.add(37);
            } else if (pos == 8) {
                p.add(73);
                p.add(59);
            } else if (pos == 9) {
                p.add(348);
                p.add(59);
            } else if (pos == 10) {
                p.add(14);
                p.add(59);
            } else if (pos == 11) {
                p.add(400);
                p.add(59);
            } else {
                throw new IndexOutOfBoundsException("No open positions in layer " + layer);
            }
        } else if (layerNum == 1) {
            if (pos == 0) {
                p.add(109);
                p.add(140);
            } else if (pos == 1) {
                p.add(160);
                p.add(160);
            } else if (pos == 2) {
                p.add(212);
                p.add(171);
            } else if (pos == 3) {
                p.add(262);
                p.add(160);
            } else if (pos == 4) {
                p.add(314);
                p.add(140);
            } else if (pos == 5) {
                p.add(212);
                p.add(140);
            } else if (pos == 6) {
                p.add(53);
                p.add(120);
            } else if (pos == 7) {
                p.add(364);
                p.add(120);
            } else if (pos == 8) {
                p.add(82);
                p.add(179);
            } else if (pos == 9) {
                p.add(340);
                p.add(179);
            } else if (pos == 10) {
                p.add(26);
                p.add(158);
            } else if (pos == 11) {
                p.add(389);
                p.add(158);
            } else {
                throw new IndexOutOfBoundsException("No open positions in layer " + layer);
            }
        } else if (layerNum == 2) {
            if (pos == 0) {
                p.add(69);
                p.add(220);
            } else if (pos == 1) {
                p.add(121);
                p.add(246);
            } else if (pos == 2) {
                p.add(177);
                p.add(258);
            } else if (pos == 3) {
                p.add(235);
                p.add(258);
            } else if (pos == 4) {
                p.add(291);
                p.add(246);
            } else if (pos == 5) {
                p.add(343);
                p.add(220);
            } else if (pos == 6) {
                p.add(156);
                p.add(217);
            } else if (pos == 7) {
                p.add(256);
                p.add(217);
            } else if (pos == 8) {
                p.add(19);
                p.add(194);
            } else if (pos == 9) {
                p.add(389);
                p.add(194);
            } else if (pos == 10) {
                p.add(14);
                p.add(243);
            } else if (pos == 11) {
                p.add(399);
                p.add(243);
            } else {
                throw new IndexOutOfBoundsException("No open positions in layer " + layer);
            }
        } else if (layerNum == 3) {
            if (pos == 0) {
                p.add(66);
                p.add(304);
            } else if (pos == 1) {
                p.add(122);
                p.add(328);
            } else if (pos == 2) {
                p.add(177);
                p.add(340);
            } else if (pos == 3) {
                p.add(233);
                p.add(340);
            } else if (pos == 4) {
                p.add(288);
                p.add(328);
            } else if (pos == 5) {
                p.add(344);
                p.add(304);
            } else if (pos == 6) {
                p.add(12);
                p.add(304);
            } else if (pos == 7) {
                p.add(399);
                p.add(304);
            } else if (pos == 8) {
                p.add(156);
                p.add(302);
            } else if (pos == 9) {
                p.add(246);
                p.add(302);
            } else if (pos == 10) {
                p.add(39);
                p.add(269);
            } else if (pos == 11) {
                p.add(372);
                p.add(269);
            } else {
                throw new IndexOutOfBoundsException("No open positions in layer " + layer);
            }
        } else if (layerNum == 4) {
            if (pos == 0) {
                p.add(206);
                p.add(418);
            } else if (pos == 1) {
                p.add(258);
                p.add(413);
            } else if (pos == 2) {
                p.add(154);
                p.add(413);
            } else if (pos == 3) {
                p.add(201);
                p.add(397);
            } else if (pos == 4) {
                p.add(310);
                p.add(397);
            } else if (pos == 5) {
                p.add(361);
                p.add(380);
            } else if (pos == 6) {
                p.add(50);
                p.add(380);
            } else if (pos == 7) {
                p.add(3);
                p.add(383);
            } else if (pos == 8) {
                p.add(413);
                p.add(383);
            } else if (pos == 9) {
                p.add(27);
                p.add(346);
            } else if (pos == 10) {
                p.add(390);
                p.add(346);
            } else if (pos == 11) {
                p.add(181);
                p.add(380);
            } else {
                throw new IndexOutOfBoundsException("No open positions in layer " + layer);
            }
        } else if (layerNum == 5) {
            if (pos == 0) {
                p.add(192);
                p.add(484);
            } else if (pos == 1) {
                p.add(235);
                p.add(521);
            } else if (pos == 2) {
                p.add(269);
                p.add(477);
            } else if (pos == 3) {
                p.add(256);
                p.add(521);
            } else if (pos == 4) {
                p.add(114);
                p.add(477);
            } else if (pos == 5) {
                p.add(76);
                p.add(508);
            } else if (pos == 6) {
                p.add(36);
                p.add(458);
            } else if (pos == 7) {
                p.add(314);
                p.add(513);
            } else if (pos == 8) {
                p.add(346);
                p.add(477);
            } else if (pos == 9) {
                p.add(405);
                p.add(458);
            } else if (pos == 10) {
                p.add(16);
                p.add(503);
            } else if (pos == 11) {
                p.add(42);
                p.add(546);
            } else {
                throw new IndexOutOfBoundsException("No open positions in layer " + layer);
            }
        } else {
            throw new IndexOutOfBoundsException("Layer out of bounds");
        }
        return p;
    }
}
