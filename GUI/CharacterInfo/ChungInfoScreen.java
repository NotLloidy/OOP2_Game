package GUI.CharacterInfo;

import GUI.GameGUI;
import GameEngines.GameSession;

import javax.swing.*;

import Characters.ChungMyung;
import Foundation.BattleMode;

import java.awt.*;
import java.awt.event.*;

public class ChungInfoScreen extends JPanel {

    private Image bgImage;

    private JButton play;
    private JButton back;

    public ChungInfoScreen(GameGUI gui) {

        this.setLayout(null);

        bgImage = new ImageIcon("Assets/character_info/infoChungMyung.png").getImage();

        play = createButton();
        play.addActionListener(e -> {

            GameSession session = GameSession.getInstance();

            if (session.getMode() == null) {
                System.out.println("Mode not set!");
                return;
            }

            BattleMode mode    = session.getMode();
            int        picking = session.getSelectingPlayer();

            if (mode == BattleMode.PVP) {

                if (picking == 1) {
                    // P1 picks ChungMyung
                    session.setPlayer1(new ChungMyung());
                    session.setSelectingPlayer(2);
                    gui.showScreen("SelectChungScreen");   // send P2 to character select

                } else {
                    // P2 picks ChungMyung — check not same as P1
                    if (session.getPlayer1().getCharacterName().equals(new ChungMyung().getCharacterName())) {
                        JOptionPane.showMessageDialog(
                                ChungInfoScreen.this,
                                "Player 2 cannot pick the same character as Player 1!",
                                "Invalid Selection",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                    session.setPlayer2(new ChungMyung());
                    session.setSelectingPlayer(1);        // reset for next match
                    gui.showScreen("PVPBattleScreen");
                }

            } else {
                // PVE or ARCADE — only P1 picks
                session.setPlayer1(new ChungMyung());
                session.setSelectingPlayer(1);

                if (mode == BattleMode.PVE) {
                    gui.showScreen("PVEBattleScreen");
                } else {
                    gui.showScreen("ArcadeBattleScreen");
                }
            }
        });
        this.add(play);

        back = createButton();
        back.addActionListener(e -> gui.showScreen("SelectChungScreen"));
        this.add(back);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updatePositions();
            }
        });
    }

    private JButton createButton() {
        JButton btn = new JButton();
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    

    private void updatePositions() {

        int w = getWidth();
        int h = getHeight();

        int btnWidth = (int)(w * 0.10);
        int btnHeight = (int)(h * 0.10);

        play.setBounds((int)(w * 0.7950), (int)(h * 0.40), (int)(btnWidth * 1.160), (int)(btnHeight * 0.70));
        back.setBounds((int)(w * 0.7960), (int)(h * 0.53), (int)(btnWidth * 1.160), (int)(btnHeight * 0.70));
    }

    @Override
    public void doLayout() {
        super.doLayout();
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}

