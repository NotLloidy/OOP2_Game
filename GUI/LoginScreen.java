package GUI;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JPanel {

    private Image bgImage;  // field to store the GIF

    public LoginScreen(GameGUI gui) {
        this.setLayout(null);

        // Load the GIF image
        ImageIcon icon = new ImageIcon("Assets/accountLogInScreen.gif");
        bgImage = icon.getImage();

        // Create full-screen invisible button
        JButton start = new JButton();
        start.setOpaque(false);
        start.setContentAreaFilled(false);
        start.setBorderPainted(false);
        start.addActionListener(e -> gui.showScreen("MainMenu"));

        this.add(start);
    }

    // Override paintComponent to draw the image scaled to current panel size
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}