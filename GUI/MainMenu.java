package GUI;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JPanel {

    private Image bgImage;  // field to store the GIF
    private JButton pve;
    private JButton pvp;   
    private JButton arcade;
    private JButton settings;

    public MainMenu (GameGUI gui) {

        this.setLayout(null);

        ImageIcon icon = new ImageIcon("Assets/mainMenuScreen.gif");
        bgImage = icon.getImage();

        pve = createButton();
        pve.addActionListener(e -> gui.showScreen("SelectAVinScreen"));
        this.add(pve);

        pvp = createButton();
        pvp.addActionListener(e -> gui.showScreen("SelectAVinScreen"));
        this.add(pvp);

        arcade = createButton();
        arcade.addActionListener(e -> gui.showScreen("SelectAVinScreen"));
        this.add(arcade);

        settings = createButton();
        settings.addActionListener(e -> gui.showScreen("SelectAVinScreen"));
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

        // PVE button
        pve.setBounds((int)(w * 0.39), (int)(h * 0.28),
                (int)(w * 0.22), (int)(h * 0.08));

        // PVP button
        pvp.setBounds((int)(w * 0.39), (int)(h * 0.42),
                (int)(w * 0.22), (int)(h * 0.08));

        // Arcade button
        arcade.setBounds((int)(w * 0.39), (int)(h * 0.57),
                (int)(w * 0.22), (int)(h * 0.08));

        // Settings button
        settings.setBounds((int)(w * 0.39), (int)(h * 0.71),
                (int)(w * 0.22), (int)(h * 0.08));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}
