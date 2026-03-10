package GUI;

import javax.swing.*;

public class MainMenu extends JPanel {
    public MainMenu (GameGUI gui) {
        this.setLayout(null);

        JLabel bg = new JLabel(new ImageIcon("Assets/mainMenuScreen.gif"));
        bg.setBounds(0,0,800,600);
        this.add(bg);

        JButton start = new JButton("Start");
        start.setBounds(350,450,100,40);
        start.addActionListener(e -> gui.showScreen("AccountScreen"));
        this.add(start);
    }
}
