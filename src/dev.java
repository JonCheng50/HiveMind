import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class dev {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        List<Question> qs = Question.getQuestions();

        JLabel lbl = new JLabel("Choose a question!");

        JRadioButton b1 = new JRadioButton(qs.get(0).getText());
        JRadioButton b2 = new JRadioButton(qs.get(1).getText());
        JRadioButton b3 = new JRadioButton(qs.get(2).getText());
        JRadioButton b4 = new JRadioButton("Make up your own question:");
        System.out.println("If you are making your own question, make sure the first number digit (don't use words like \"two\" or \"five\") represents the number of answers you would like, ranging from 1 to 5. If no numbers are present, everyone will get only 1 answer");

        ButtonGroup bGroup = new ButtonGroup();
        bGroup.add(b1);
        bGroup.add(b2);
        bGroup.add(b3);
        bGroup.add(b4);

        JTextField txt = new JTextField();

        JButton btn = new JButton("Ok");
        btn.setSize(50, 20);

        JLabel note = new JLabel("");

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 1));
        panel.add(lbl);
        panel.add(b1);
        panel.add(b2);
        panel.add(b3);
        panel.add(b4);

        panel.add(txt);
        //panel.add(btn);
        panel.add(note);


        JOptionPane jop = new JOptionPane(panel, JOptionPane.QUESTION_MESSAGE, JOptionPane.DEFAULT_OPTION);//, null, new Object[]{}, null);
        JDialog jd= new JDialog((JFrame)null, "Questions", true);
        jd.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        jd.setContentPane(jop);

//        jop.addPropertyChangeListener(evt -> {
//            //String s = Integer.toString((Integer)jop.getValue());
//            System.out.println(jop.getValue());
//            System.out.println("------");
////            if (JOptionPane.VALUE_PROPERTY.equals(evt.getPropertyName())) {
////                jd.dispose();
////                JOptionPane.showMessageDialog(null, "Good for you >:P");
////                System.exit(0);
////            }
//
//        });

        jop.addPropertyChangeListener(evt -> {
            System.out.println("Change listener");
            System.out.println(jop.getValue());
            //!jop.getValue().toString().equals("uninitializedValue")
            if (jd.isVisible() && jop.getValue().equals(0)) {
                System.out.println("Setting false");
                jop.setValue(1);
                jd.setVisible(false);
            }
        });

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        jd.setLocation(dim.width/2-250, dim.height/2-126);

        jd.pack();
        //frame.pack();
        System.out.println(jd.getWidth());
        System.out.println(jd.getHeight());
        jd.setVisible(true);
        Timer t = new Timer(false);
//        t.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("timer");
//                if (!jd.isVisible()) {
//                    System.out.println("Reset");
//                    jd.setVisible(true);
//                    if (!(b1.isSelected() || b2.isSelected() || b3.isSelected() || b4.isSelected())) {
//                        note.setText("Please choose an option");
//                    } else if (b4.isSelected() && txt.getText().equals("")) {
//                        note.setText("Please input a custom question");
//                    } else {
//                        t.cancel();
//                    }
//                }
//            }
//        }, 20, 750);

        while (true) {
            if (!jd.isVisible()) {
                System.out.println("Reset");

                if (!(b1.isSelected() || b2.isSelected() || b3.isSelected() || b4.isSelected())) {
                    note.setText("Please choose an option");
                } else if (b4.isSelected() && txt.getText().equals("")) {
                    note.setText("Please input a custom question");
                } else {
                    break;
                }
                jd.setVisible(true);
            }
        }
        System.out.println("DONE--------------");
        //frame.setVisible(true);


//        JFrame frame = new JFrame("Game");
//        final JPanel status_panel = new JPanel();
//        JLabel statusLbl = new JLabel("Hello world");
//        status_panel.add(statusLbl, SwingConstants.CENTER);
//        status_panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Color.BLACK));
//
//        final JPanel control_panel = new JPanel();
//
//        final JButton reset = new JButton("Reset");
//        reset.addActionListener(e13 -> {
//            System.out.println("Reset pressed");
//        });
//        control_panel.add(reset);
//
//        final JButton instructions = new JButton("Instructions");
//        instructions.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                JOptionPane.showMessageDialog(frame, "Instructions: \n" +
//                        "hi");
//            }
//        });
//        control_panel.add(instructions);
//        control_panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
//
//
//        GameBoard board = new GameBoard();
//        board.repaint();
//
//
//        GameCourt court = new GameCourt();
//        court.repaint();
//
//        GroupLayout layout = new GroupLayout(frame.getContentPane());
//
//        layout.setHorizontalGroup(
//                layout.createSequentialGroup()
//                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//                                .addComponent(control_panel)
//                                .addComponent(status_panel)
//                                .addGroup(layout.createSequentialGroup()
//                                        .addContainerGap(10, 10)
//                                        .addComponent(board)
//                                        .addContainerGap(5, 5)
//                                        .addComponent(court)
//                                        .addContainerGap(10, 10)))
//
//        );
//        layout.setVerticalGroup(
//                layout.createSequentialGroup()
//                        .addComponent(control_panel)
//                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
//                        .addGroup(layout.createParallelGroup()
//                                .addComponent(board)
//                                .addComponent(court))
//                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
//                        .addComponent(status_panel)
//        );
//
//        // Put the frame on the screen
//        frame.pack();
//        frame.getContentPane().setLayout(layout);
//        frame.setSize(1100, 700);
//        frame.setResizable(false);
//        frame.setLocation(70, 80);
//
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        //frame.addWindowListener(new Listener()) ;
//        frame.setVisible(true);
    }
}
