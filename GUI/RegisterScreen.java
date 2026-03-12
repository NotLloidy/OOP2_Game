package GUI;

import javax.swing.*;
import java.awt.*;

public class RegisterScreen extends JPanel {

    private Image bgImage;  // field to store the GIF
    private JButton register;

    public RegisterScreen(GameGUI gui) {
        this.setLayout(null);

        // Load the GIF image
        ImageIcon icon = new ImageIcon("Assets/accountRegisterScreen.gif");
        bgImage = icon.getImage();

        // Create full-screen invisible button
        register = createButton();  
        register.addActionListener(e -> gui.showScreen("AccountScreen"));  // go to main menu
        this.add(register);
    }

    private JButton createButton() {
        JButton btn = new JButton();

        btn.setBounds(335, 360, 120, 45);
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