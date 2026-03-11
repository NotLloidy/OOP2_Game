package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AccountScreen extends JPanel {

    private Image bgImage;
    private JButton loginBtn;
    private JButton registerBtn;
    private JButton backBtn;
    private JButton guestBtn;

    public AccountScreen(GameGUI gui) {

        this.setLayout(null);

        ImageIcon icon = new ImageIcon("Assets/gameAccountScreen.gif");
        bgImage = icon.getImage();

        loginBtn = createButton();
        loginBtn.addActionListener(e -> gui.showScreen("LoginScreen"));
        this.add(loginBtn);

        registerBtn = createButton();
        registerBtn.addActionListener(e -> gui.showScreen("RegisterScreen"));
        this.add(registerBtn);

        backBtn = createButton();
        backBtn.addActionListener(e -> gui.showScreen("TitleScreen"));
        this.add(backBtn);

        guestBtn = createButton();
        guestBtn.addActionListener(e -> gui.showScreen("MainMenu"));
        this.add(guestBtn);

        // Resize listener
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                updateButtonPositions();
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

    private void updateButtonPositions() {

        int w = getWidth();
        int h = getHeight();

        int btnWidth = (int)(w * 0.18);
        int btnHeight = (int)(h * 0.06);


        loginBtn.setBounds((int)(w * 0.39), (int)(h * 0.31), (int)(btnWidth * 1.5), (int)(btnHeight * 1.8));
        registerBtn.setBounds((int)(w * 0.39), (int)(h * 0.4934), (int)(btnWidth * 1.5), (int)(btnHeight * 1.8));
        backBtn.setBounds((int)(w * 0.39), (int)(h * 0.6799), (int)(btnWidth * 1.5), (int)(btnHeight * 1.8));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}