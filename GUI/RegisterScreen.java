package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import UTILS.FileHandler;

public class RegisterScreen extends JPanel {

    private Image bgImage;
    private JButton register;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public RegisterScreen(GameGUI gui) {

        this.setLayout(null);

        ImageIcon icon = new ImageIcon("Assets/accountRegisterScreen.gif");
        bgImage = icon.getImage();

        usernameField = new JTextField();
        usernameField.setOpaque(false);
        usernameField.setBackground(new Color(0,0,0,0));
        usernameField.setBorder(null);
        usernameField.setForeground(Color.WHITE);
        usernameField.setCaretColor(Color.WHITE);

        this.add(usernameField);

        passwordField = new JPasswordField();
        passwordField.setOpaque(false);
        passwordField.setBackground(new Color(0,0,0,0));
        passwordField.setBorder(null);
        passwordField.setForeground(Color.WHITE);
        passwordField.setCaretColor(Color.WHITE);

        this.add(passwordField);

        register = createButton();

        register.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!");
                return;
            }

            boolean success = FileHandler.register(username, password);

            if (success) {
                gui.showScreen("AccountScreen");
            } else {
                JOptionPane.showMessageDialog(this, "Username already exists!");
            }
        });

        this.add(register);

        
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

        // Username field
        usernameField.setBounds((int)(w * 0.34), (int)(h * 0.42), (int)(w * 0.32), (int)(h * 0.05));

        // Password field
        passwordField.setBounds((int)(w * 0.45), (int)(h * 0.55), (int)(w * 0.22), (int)(h * 0.05));

        // Button
        register.setBounds((int)(w * 0.43), (int)(h * 0.64),
                (int)(w * 0.15), (int)(h * 0.08));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}