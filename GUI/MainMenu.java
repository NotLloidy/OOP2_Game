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
        pve.setBounds(305, 155, 170, 50);
        pve.addActionListener(e -> gui.showScreen("AccountScreen"));
        this.add(pve);

        pvp = createButton();
        pvp.setBounds(305, 230, 170, 50);
        pvp.addActionListener(e -> gui.showScreen("AccountScreen"));
        this.add(pvp);

        arcade = createButton();
        arcade.setBounds(305, 320, 170, 50);
        arcade.addActionListener(e -> gui.showScreen("AccountScreen"));
        this.add(arcade);

        settings = createButton();
        settings.setBounds(305, 400, 170, 50);
        settings.addActionListener(e -> gui.showScreen("AccountScreen"));
        this.add(settings);
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
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}
