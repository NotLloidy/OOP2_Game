package GUI.CharacterSelectScreens;

import GUI.GameGUI;
import GameEngines.GameSession;

import javax.swing.*;

import Characters.SungJinWoo;
import Foundation.BattleMode;

import java.awt.*;
import java.awt.event.*;

public class SelectSungJinWooScreen extends JPanel {

    private Image bgImage;

    private JButton next;
    private JButton prev;
    private JButton info;
    private JButton select;
    private JButton exit;

    public SelectSungJinWooScreen(GameGUI gui) {

        this.setLayout(null);

        bgImage = new ImageIcon("Assets/character_related/character_select/sungjinwooSelect.gif").getImage();

        next = createButton();
        next.addActionListener(e -> gui.showScreen("SelectZakkarrScreen"));
        this.add(next);

        exit = createButton();
        exit.addActionListener(e -> gui.showScreen("MainMenu"));
        this.add(exit);

        prev = createButton();
        prev.addActionListener(e -> gui.showScreen("SelectSoleilScreen"));
        this.add(prev);

        info = createButton();
        info.addActionListener(e -> gui.showScreen("SungJinWooInfoScreen"));
        this.add(info);

        select = createButton();
        select.addActionListener(e -> {

            GameSession session = GameSession.getInstance();

            if (session.getMode() == null) {
                System.out.println("Mode not set!");
                return;
            }

            BattleMode mode    = session.getMode();
            int        picking = session.getSelectingPlayer();

            if (mode == BattleMode.PVP) {

                if (picking == 1) {
                    // P1 picks SungJinWoo
                    session.setPlayer1(new SungJinWoo());
                    session.setSelectingPlayer(2);
                    gui.showScreen("SelectAVinScreen");   // send P2 to character select

                } else {
                    // P2 picks SungJinWoo — check not same as P1
                    if (session.getPlayer1().getCharacterName().equals(new SungJinWoo().getCharacterName())) {
                        JOptionPane.showMessageDialog(
                                SelectSungJinWooScreen.this,
                                "Player 2 cannot pick the same character as Player 1!",
                                "Invalid Selection",
                                JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                    session.setPlayer2(new SungJinWoo());
                    session.setSelectingPlayer(1);        // reset for next match
                    gui.showScreen("PVPBattleScreen");
                }

            } else {
                // PVE or ARCADE — only P1 picks
                session.setPlayer1(new SungJinWoo());
                session.setSelectingPlayer(1);

                if (mode == BattleMode.PVE) {
                    gui.showScreen("PVEBattleScreen");
                } else {
                    gui.showScreen("ArcadeBattleScreen");
                }
            }
        });
        this.add(select);

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

        next.setBounds((int)(w * 0.90), (int)(h * 0.41), (int)(btnWidth * 0.5), (int)(btnHeight * 1.5));
        prev.setBounds((int)(w * 0.52), (int)(h * 0.41), (int)(btnWidth * 0.5), (int)(btnHeight * 1.5));
        info.setBounds((int)(w * 0.27), (int)(h * 0.69), (int)(btnWidth * 1.2), (int)(btnHeight * 0.7));
        select.setBounds((int)(w * 0.11), (int)(h * 0.69), (int)(btnWidth * 1.2), (int)(btnHeight * 0.7));
        exit.setBounds((int)(w * 0.19), (int)(h * 0.79), (int)(btnWidth * 1.2), (int)(btnHeight * 0.7));
    }

    @Override
    public void doLayout() {
        super.doLayout();
        updatePositions();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}