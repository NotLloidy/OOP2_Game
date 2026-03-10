package GUI;

import javax.swing.*;

public class RegisterScreen extends JPanel {
    public RegisterScreen(GameGUI gui) {
        this.setLayout(null);

        JLabel bg = new JLabel(new ImageIcon("Assets/accountRegisterScreen.gif"));
        bg.setBounds(0,0,800,600);
        this.add(bg);

        JButton start = new JButton("Start");
        start.setBounds(350,450,100,40);
        start.addActionListener(e -> gui.showScreen("AccountScreen"));
        this.add(start);
    }
}
