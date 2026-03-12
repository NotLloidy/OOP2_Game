package GUI;

import javax.swing.*;
import java.awt.*;

public class LoginScreen extends JPanel {

    private Image bgImage;  // field to store the GIF
    private JButton enterButton;

    public LoginScreen(GameGUI gui) {
        this.setLayout(null);

        // Load the GIF image
        ImageIcon icon = new ImageIcon("Assets/accountLogInScreen.gif");
        bgImage = icon.getImage();

        
        enterButton = createButton();  
        enterButton.addActionListener(e -> gui.showScreen("MainMenu"));  // go to main menu
        this.add(enterButton);
    }

    private JButton createButton() {
        JButton btn = new JButton();

        btn.setBounds(345, 370, 115, 45);
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    // Override paintComponent to draw the image scaled to current panel size
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}