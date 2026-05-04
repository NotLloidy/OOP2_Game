package GUI;

import javax.swing.*;

import UTILS.SoundManager;

import java.awt.*;

public class TitleScreen extends JPanel {

    private Image bgImage;  // field to store the GIF

    public TitleScreen(GameGUI gui) {
        this.setLayout(null);

        // Load the GIF image
        ImageIcon icon = new ImageIcon("Assets/navigation/titleScreen.gif");
        bgImage = icon.getImage();

        // Create full-screen invisible button
        JButton start = new JButton();
        start.setOpaque(false);
        start.setContentAreaFilled(false);
        start.setBorderPainted(false);
        start.addActionListener(e -> {
            SoundManager.playSFX(SoundManager.SFX_BUTTON);
            gui.showScreen("AccountScreen");
        });

        this.add(start);
    }

    // Override paintComponent to draw the image scaled to current panel size
    @Override
    public void doLayout() {
        super.doLayout();
        revalidate();
        repaint();
    }

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