package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import UTILS.FileHandler;

public class LoginScreen extends JPanel {

    private Image bgImage;
    private JButton enterButton;
    private JButton back;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginScreen(GameGUI gui) {

        this.setLayout(null);

        bgImage = new ImageIcon("Assets/navigation/accountLogin.gif").getImage();

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

        back = createButton();
        back.addActionListener(e -> gui.showScreen("AccountScreen"));
        this.add(back);

        enterButton = createButton();
        this.add(enterButton);

        enterButton.addActionListener(e -> {

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Fill all fields!");
                return;
            }

            if (FileHandler.login(username, password)) {
                gui.showScreen("MainMenu");
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password!");
            }
        });

        updatePositions();

        
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
        usernameField.setBounds((int)(w * 0.35), (int)(h * 0.44), (int)(w * 0.35), (int)(h * 0.05));

        // Password field
        passwordField.setBounds((int)(w * 0.45), (int)(h * 0.58), (int)(w * 0.25), (int)(h * 0.05));

        // Button
        enterButton.setBounds((int)(w * 0.44), (int)(h * 0.66),
                (int)(w * 0.15), (int)(h * 0.08));
        
        // Back button
        back.setBounds((int)(w * 0.44), (int)(h * 0.77),
                (int)(w * 0.15), (int)(h * 0.08));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}