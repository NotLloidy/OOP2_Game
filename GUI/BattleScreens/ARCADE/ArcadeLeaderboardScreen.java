package GUI.BattleScreens.ARCADE;

import javax.swing.*;

import Foundation.BattleMode;

import java.awt.*;
import GUI.GameGUI;
import GameEngines.GameSession;

public class ArcadeLeaderboardScreen extends JPanel{
    private Image bgImage;

    private JButton play;
    private JButton leaderboard;
    private JButton back;

    public ArcadeLeaderboardScreen(GameGUI gui) {
        this.setLayout(null);

        bgImage = new ImageIcon("Assets/navigation/PlayArcade.gif").getImage();

        play = createButton();
        
        play.addActionListener( e -> {
            GameSession.getInstance().setMode(BattleMode.ARCADE);
            gui.showScreen("SelectAVinScreen");
        });
        this.add(play);

        leaderboard = createButton();
        leaderboard.addActionListener(e -> gui.showScreen("LeaderboardScreen"));
        this.add(leaderboard);

        back = createButton();
        back.addActionListener(e -> gui.showScreen("MainMenu"));
        this.add(back);

        play.setBounds(240, 360, 300, 80);
        leaderboard.setBounds(240, 260, 300, 80);
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
