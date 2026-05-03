package GUI.BattleScreens;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import GUI.GameGUI;

public class LeaderboardScreen extends JPanel {
    private Image bgImage;

    private JButton back;

    public LeaderboardScreen(GameGUI gui) {
        this.setLayout(null);

        bgImage = new ImageIcon("Assets/navigation/leaderboard.gif").getImage();

        back = createButton();
        back.addActionListener(e -> gui.showScreen("ArcadeLeaderboardScreen"));
        this.add(back);

        back.setBounds(720, 10, 50, 50);
    }

    private JButton createButton() {
        JButton btn = new JButton();
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null)
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
