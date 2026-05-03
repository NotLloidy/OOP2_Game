package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import UTILS.SoundManager;

public class AccountScreen extends JPanel {

    private Image bgImage;
    private JButton loginBtn;
    private JButton registerBtn;
    private JButton backBtn;
    private JButton guestBtn;

    public AccountScreen(GameGUI gui) {

        this.setLayout(null);

        ImageIcon icon = new ImageIcon("Assets/navigation/loginScreen.gif");
        bgImage = icon.getImage();

        loginBtn = createButton();
        loginBtn.addActionListener(e -> {
            SoundManager.playSFX(SoundManager.SFX_BUTTON);
            gui.showScreen("LoginScreen");
        });
        this.add(loginBtn);

        registerBtn = createButton();
        registerBtn.addActionListener(e -> {
            SoundManager.playSFX(SoundManager.SFX_BUTTON);
            gui.showScreen("RegisterScreen");
        });
        this.add(registerBtn);

        backBtn = createButton();
        backBtn.addActionListener(e -> {
            SoundManager.playSFX(SoundManager.SFX_BUTTON);
            gui.showScreen("TitleScreen");
        });
        this.add(backBtn);

        guestBtn = createButton();
        guestBtn.addActionListener(e -> {
            SoundManager.playSFX(SoundManager.SFX_BUTTON);
            gui.showScreen("MainMenu");
        });
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


        loginBtn.setBounds((int)(w * 0.4020), (int)(h * 0.3280), (int)(btnWidth * 1.366), (int)(btnHeight * 1.2));
        registerBtn.setBounds((int)(w * 0.4020), (int)(h * 0.4489), (int)(btnWidth * 1.366), (int)(btnHeight * 1.2));
        guestBtn.setBounds((int)(w * 0.4020), (int)(h * 0.5589), (int)(btnWidth * 1.366), (int)(btnHeight * 1.2));
        backBtn.setBounds((int)(w * 0.4020), (int)(h * 0.6799), (int)(btnWidth * 1.366), (int)(btnHeight * 1.2));
    }

    @Override
    public void doLayout() {
        super.doLayout();
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}