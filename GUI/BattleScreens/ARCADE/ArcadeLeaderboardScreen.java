package GUI.BattleScreens.ARCADE;

import javax.swing.*;

import Foundation.BattleMode;

import java.awt.*;
import java.awt.event.*;
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
    public void doLayout() {
        double scaleX = getWidth() / 960.0;
        double scaleY = getHeight() / 540.0;

        play.setBounds(
            (int)(300 * scaleX),
            (int)(350 * scaleY),
            (int)(355 * scaleX),
            (int)(80 * scaleY)
        );

        leaderboard.setBounds(
            (int)(300 * scaleX),
            (int)(250 * scaleY),
            (int)(355 * scaleX),
            (int)(80 * scaleY)
        );

        back.setBounds(
            (int)(885 * scaleX),
            (int)(10 * scaleY),
            (int)(60 * scaleX),
            (int)(50 * scaleY)
        );
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
