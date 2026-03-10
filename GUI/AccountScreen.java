package GUI;

import javax.swing.*;
import java.awt.*;

public class AccountScreen extends JPanel {
    private Image bgImage;  // field to store the GIF

    public AccountScreen(GameGUI gui) {
        this.setLayout(null);

        // Load the GIF image
        ImageIcon icon = new ImageIcon("Assets/gameAccountScreen.gif");
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

        int width = getWidth();
        int height = getHeight();

        // Draw the background scaled to panel size
        g.drawImage(bgImage, 0, 0, width, height, this);

        // Make the button cover the whole panel
        for (Component comp : getComponents()) {
            if (comp instanceof JButton) {
                comp.setBounds(0, 0, width, height);
            }
        }
    }
}