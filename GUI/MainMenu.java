package GUI;

import javax.swing.*;
import java.awt.*;
import GameEngines.GameSession;
import Foundation.BattleMode;

public class MainMenu extends JPanel {

    private Image bgImage;
    private JButton pve;
    private JButton pvp;
    private JButton arcade;
    private JButton settings;

    public MainMenu(GameGUI gui) {

        this.setLayout(null);

        ImageIcon icon = new ImageIcon("Assets/mainMenuScreen.gif");
        bgImage = icon.getImage();

        pve = createButton();
        pve.addActionListener(e -> {
            GameSession.getInstance().setMode(BattleMode.PVE);
            gui.showScreen("SelectAVinScreen");
        });
        this.add(pve);

        pvp = createButton();
        pvp.addActionListener(e -> {
            GameSession.getInstance().setMode(BattleMode.PVP);
            gui.showScreen("SelectAVinScreen");
        });
        this.add(pvp);

        arcade = createButton();
        arcade.addActionListener(e -> {
            GameSession.getInstance().setMode(BattleMode.ARCADE);
            gui.showScreen("SelectAVinScreen");
        });
        this.add(arcade);

        settings = createButton();
        settings.addActionListener(e -> {
            gui.showScreen("SelectAVinScreen"); // keep or replace later
        });
        this.add(settings);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
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

        pve.setBounds((int)(w * 0.39), (int)(h * 0.28),
                (int)(w * 0.22), (int)(h * 0.08));

        pvp.setBounds((int)(w * 0.39), (int)(h * 0.42),
                (int)(w * 0.22), (int)(h * 0.08));

        arcade.setBounds((int)(w * 0.39), (int)(h * 0.57),
                (int)(w * 0.22), (int)(h * 0.08));

        settings.setBounds((int)(w * 0.39), (int)(h * 0.71),
                (int)(w * 0.22), (int)(h * 0.08));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}