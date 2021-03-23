import javax.swing.*;
import javax.swing.Timer;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GameCourt extends JPanel {

    private static final int COURT_WIDTH = 600;
    private static final int COURT_HEIGHT = 400;

    private JButton ready;
    private JButton start;

    private List<JTextField> answers;
    private List<JTextField> scores;
    private List<JLabel> ansText;
    private List<JLabel> scoText;
    private JLabel errorLbl;

    private JOptionPane jop;
    private JRadioButton b1;
    private JRadioButton b2;
    private JRadioButton b3;
    private JRadioButton b4;
    private JTextField customQ;
    private JLabel note;

    private JDialog askQ;

    private List<Question> alreadyAsked;

    private JLabel question;
    private JLabel waitLbl;
    private Timer waitTimer;
    //private int waitCount;

    private JScrollPane pane;
    private String[] colNames;
    private JTable table;
    private Object[][] tabData;

    private int state;
    // 0 = init, -1 = waiting for other players, 1 = writing answers, 2 = writing scores
    // -2, new player, waiting for next round
    // Pressing btn while -1 -> waiting for other players text pops up

    // Number of answers
    private int qNum;
    private boolean choosingQ;

    private Game game;

    public GameCourt(Game game) {
        this.game = game;

        this.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        this.setBackground(new Color(255, 255, 204));
        this.setLayout(null);

        //table = new JTable();
        colNames = new String[]{"Player", "Score"};
        pane = new JScrollPane();
        pane.setBounds(225, 250, 150, 200);
        pane.setVisible(false);
        this.add(pane);

        answers = new ArrayList<>();
        scores = new ArrayList<>();
        ansText = new ArrayList<>();
        scoText = new ArrayList<>();
        int yPos = 200;
        for (int count = 0; count < 5; count++) {
            JTextField ans = new JTextField();
            ans.setBounds(50, yPos, 450, 45);
            ans.setFont(new Font("Arial", Font.PLAIN, 16));
            ans.setOpaque(true);
            ans.setBackground(new Color(224, 224, 224));
            ans.setVisible(false);
            this.add(ans);
            answers.add(ans);

            JTextField score = new JTextField();
            score.setBounds(500, yPos, 50, 45);
            score.setFont(new Font("Arial", Font.PLAIN, 16));
            score.setHorizontalAlignment(JTextField.CENTER);
            score.setOpaque(true);
            score.setBackground(new Color(224, 224, 224));
            score.setVisible(false);
            this.add(score);
            scores.add(score);

            JLabel aText = new JLabel();
            aText.setBounds(50, yPos, 450, 45);
            aText.setFont(new Font("Arial", Font.PLAIN, 18));
            aText.setVisible(false);
            this.add(aText);
            ansText.add(aText);

            JLabel sText = new JLabel();
            sText.setBounds(500, yPos, 50, 45);
            sText.setFont(new Font("Arial", Font.PLAIN, 18));
            sText.setHorizontalAlignment(SwingConstants.CENTER);
            sText.setVisible(false);
            this.add(sText);
            scoText.add(sText);

            yPos += 60;
        }

        JLabel askLbl = new JLabel("Choose a question!");
        b1 = new JRadioButton();
        b2 = new JRadioButton();
        b3 = new JRadioButton();
        b4 = new JRadioButton("Make up your own question:");
        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(b1);
        bGroup.add(b2);
        bGroup.add(b3);
        bGroup.add(b4);
        customQ = new JTextField();
        note = new JLabel();
        note.setForeground(Color.RED);

        JPanel askPnl = new JPanel();
        askPnl.setLayout(new GridLayout(7, 1));
        askPnl.add(askLbl);
        askPnl.add(b1);
        askPnl.add(b2);
        askPnl.add(b3);
        askPnl.add(b4);
        askPnl.add(customQ);
        askPnl.add(note);

        jop = new JOptionPane(askPnl, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION);
        askQ = new JDialog((JFrame)null, "Questions", true);
        askQ.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        askQ.setContentPane(jop);
        jop.addPropertyChangeListener(evt -> {
            if (askQ.isVisible() && jop.getValue().equals(0)) {
                jop.setValue(1);
                askQ.setVisible(false);
            }
        });

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        askQ.setLocation(dim.width/2-250, dim.height/2-126);
        askQ.setVisible(false);
        askQ.pack();

        alreadyAsked = new ArrayList<>();

        errorLbl = new JLabel("");
        errorLbl.setBounds(100, 550, 400, 40);
        errorLbl.setHorizontalAlignment(SwingConstants.CENTER);
        errorLbl.setForeground(Color.RED);
        errorLbl.setFont(new Font("Tahoma", Font.PLAIN, 12));
        errorLbl.setVisible(false);
        this.add(errorLbl);

        waitLbl = new JLabel("Waiting for other players");
        waitLbl.setBounds(50, 500, 500, 60);
        waitLbl.setHorizontalAlignment(SwingConstants.CENTER);
        waitLbl.setForeground(Color.RED);
        waitLbl.setFont(new Font("Tahoma", Font.BOLD, 20));
        waitLbl.setVisible(false);
        this.add(waitLbl);

        waitTimer = new Timer(2000, e -> {
            String txt = waitLbl.getText();
            int c = txt.lastIndexOf(".") - txt.indexOf(".") + 1;
            if (c >= 3) {
                waitLbl.setText(txt.substring(0, txt.indexOf(".")));
            } else {
                waitLbl.setText(txt + ".");
            }
        });

        start = new JButton("Start Game!");
        start.setBounds(240, 500, 120, 60);
        start.setFont(new Font("Tahoma", Font.BOLD, 16));
        start.setForeground(new Color(0, 255, 128));
        start.setVisible(false);
        start.addActionListener(evt -> {
            game.inputReceived(new UpdatePacket(1, false));
            //start.setVisible(false);
        });
        this.add(start);

        ready = new JButton("Ready!");
        ready.setBounds(240, 500, 120, 60);
        ready.setFont(new Font("Tahoma", Font.BOLD, 16));
        ready.addActionListener(evt -> {
            if (state == 0) {
                updateState(-1, false);
                game.inputReceived(true);
            } else if (state == 1) {
                if (checkText(1)) {
                    for (int c = 0; c < qNum; c++) {
                        JTextField ans = answers.get(c);
                        ansText.get(c).setText(ans.getText());
                        ans.setText("");
                        ans.setVisible(false);
                        ansText.get(c).setVisible(true);
                    }
                    updateState(-1, false);
                    game.inputReceived(true);
                }
            } else if (state == 2) {
                if (checkText(2)) {
                    int scoreTotal = 0;
                    for (int c = 0; c < qNum; c++) {
                        JTextField score = scores.get(c);
                        scoText.get(c).setText(score.getText());
                        scoreTotal += Integer.parseInt(score.getText());
                        score.setText("");
                        score.setVisible(false);
                        scoText.get(c).setVisible(true);
                    }
                    updateState(-1, false);
                    game.inputReceived(scoreTotal);
                }
            } else if (state == -1) {
                System.out.println("Ready -1");
                waitLbl.setVisible(true);
                waitTimer.start();
            }
        });
        this.add(ready);

        question = new JLabel("Press ready to start!");
        question.setBounds(30, 20, 540, 150);
        question.setHorizontalAlignment(SwingConstants.CENTER);
        question.setVerticalAlignment(SwingConstants.CENTER);
        question.setFont(new Font("Tahoma", Font.BOLD, 20));
        question.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        question.setBackground(new Color(250, 214, 35));
        question.setOpaque(true);
        this.add(question);

        state = 0;
        qNum = 0;
        choosingQ = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(COURT_WIDTH, COURT_HEIGHT);
    }

    public boolean getCQ() {
        return choosingQ;
    }

    public void setQ(Question q) {
        alreadyAsked.add(q);
        question.setText("<html><body style='text-align:center'>Question: " + q.getText());
        setqNum(q.getNum());
    }

    public void setqNum(int x) {
        qNum = x;
    }

    public int getState() {
        return state;
    }

    public void showStart() {
        waitLbl.setVisible(false);
        waitTimer.stop();
        start.setVisible(true);
    }

    public void hideStart() {
        if (start.isVisible()) {
            start.setVisible(false);
            waitLbl.setVisible(true);
            waitTimer.start();
        }
    }

    public void showTable(HashMap<Player, Integer> scores) {
        System.out.println("Showing table");
        tabData = new Object[scores.size()][2];
        Object[] entries = sort(scores);
        List<Player> p = new ArrayList<>(scores.keySet());
        for (int c = 0; c < scores.size(); c ++) {
        //for (Object o : entries) {
            Player x = ((Map.Entry<Player, Integer>) entries[c]).getKey();
            tabData[c][0] = x.getUsername();
            tabData[c][1] = ((Map.Entry<Player, Integer>) entries[c]).getValue();
        }
        table = new JTable(tabData, colNames);
        DefaultTableCellRenderer rend = (DefaultTableCellRenderer) table.getDefaultRenderer(String.class);
        rend.setHorizontalAlignment(SwingConstants.CENTER);
        ((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);
        table.setFillsViewportHeight(true);
        table.setShowHorizontalLines(true);
        table.setVisible(true);
        pane.getViewport().add(table);
        pane.setVisible(true);
    }

    private Object[] sort(HashMap<Player, Integer> m) {
        Object[] a = m.entrySet().toArray();
        Arrays.sort(a, (Comparator) (o1, o2) -> ((Map.Entry<Player, Integer>) o2).getValue()
                .compareTo(((Map.Entry<Player, Integer>) o1).getValue()));
        return a;
    }

    public void hideTable() {
        System.out.println("hiding table");
        if (table != null) {
            pane.getViewport().remove(table);
        }
        pane.setVisible(false);
    }

    // t: 1 is answers, 2 is scores
    private boolean checkText(int t) {
        for (int c = 0; c < qNum; c++) {
            if (t == 1) {
                if (answers.get(c).getText().equals("")) {
                    errorMessage("Please enter a response in each blank");
                    return false;
                }
            } else {
                try {
                    String score = scores.get(c).getText();
                    if (score.equals("")) {
                        errorMessage("Please enter a score in each blank");
                        return false;
                    } else if (Integer.parseInt(score) < 1) {
                        errorMessage("The minimum score for each response is 1");
                        return false;
                    } else if (Integer.parseInt(score) > 12) {
                        errorMessage("The maximum score is 12");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    errorMessage("Please enter only integers for scores");
                    return false;
                }
            }
        }
        return true;
    }

    public void errorMessage(String msg) {
        System.out.println("Showing error");
        errorLbl.setText(msg);
        errorLbl.setVisible(true);
        Timer timer = new Timer(4000, e -> errorLbl.setVisible(false));
        timer.setRepeats(false);
        timer.restart();
    }

    public void updateState(int x, boolean qWait) {
        if (state == -2 && x == 2) { // Waiting for next round
            return;
        }
        state = x;
        System.out.println("State is now " + state);
        if (state == 1) {
            waitLbl.setVisible(false);
            waitTimer.stop();
            ready.setVisible(true);
            for (int c = 0; c < qNum; c++) {
                answers.get(c).setVisible(true);
            }
        } else if (state == 2) {
            waitLbl.setVisible(false);
            waitTimer.stop();
            ready.setVisible(true);
            for (int c = 0; c < qNum; c++) {
                scores.get(c).setVisible(true);
            }
        } else if (state == -1) {
            ready.setVisible(false);
            if (qWait) {
                question.setText("");
                for (int c = 0; c < 5; c++) {
                    ansText.get(c).setVisible(false);
                    scoText.get(c).setVisible(false);
                }
                waitLbl.setText("Waiting for next question to be chosen");
                waitLbl.setVisible(true);
                waitTimer.start();
            } else {
                waitLbl.setText("Waiting for other players");
                waitLbl.setVisible(true);
                waitTimer.start();
            }
        } else if (state == -2) {
            ready.setVisible(false);
            waitLbl.setText("Please wait for the next round");
            waitLbl.setVisible(true);
        }
        System.out.println("---------------------------------");
    }

    public void chooseQ() {
        choosingQ = true;
        System.out.println("Choosing Question");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            //Thread.currentThread().interrupt();
        }

        List<Question> Qs = Question.getQuestions();
        List<Question> common = new ArrayList<>(alreadyAsked);
        common.retainAll(Qs);
        while (!common.isEmpty()) {
            System.out.println("Hi");
            Qs = Question.getQuestions();
            common = new ArrayList<>(alreadyAsked);
            common.retainAll(Qs);
        }
        b1.setText(Qs.get(0).getText());
        b2.setText(Qs.get(1).getText());
        b3.setText(Qs.get(2).getText());
        customQ.setText("");

        askQ.setContentPane(jop);
        askQ.pack();
        askQ.setVisible(true);
        while (true) {
            if (!askQ.isVisible()) {
                if (!(b1.isSelected() || b2.isSelected() || b3.isSelected() || b4.isSelected())) {
                    note.setText("Please choose an option");
                } else if (b4.isSelected() && customQ.getText().equals("")) {
                    note.setText("Please input a custom question");
                } else {
                    break;
                }
                askQ.setVisible(true);
            }
        }
        note.setText("");
        if (b1.isSelected()) {
            game.inputReceived(Qs.get(0));
        } else if (b2.isSelected()) {
            game.inputReceived(Qs.get(1));
        } else if (b3.isSelected()) {
            game.inputReceived(Qs.get(2));
        } else {
            String q = customQ.getText();
            Pattern p = Pattern.compile("\\d+");
            Matcher m = p.matcher(q);
            if (m.find()) {
                int num = Integer.parseInt(m.group());
                if (num > 5) {
                    num = 5;
                }
                game.inputReceived(new Question(q, num));
            } else {
                game.inputReceived(new Question(q, 1));
            }
        }
        System.out.println("Question chosen!");

        //game.inputReceived(qs.get((int) (Math.random() * 3)));
        choosingQ = false;
    }

    public void gameOver() {
        waitLbl.setText("Game Over!");
        waitLbl.setVisible(true);
        waitTimer.stop();
    }
}
