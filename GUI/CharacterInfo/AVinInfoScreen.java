package GUI.CharacterInfo;

import GUI.GameGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AVinInfoScreen extends JPanel {

    private Image bgImage;

    private JButton play;
    private JButton back;

    public AVinInfoScreen(GameGUI gui) {

        this.setLayout(null);

        bgImage = new ImageIcon("Assets/character_info/infoAVin.png").getImage();

        play = createButton();
        play.addActionListener(e -> gui.showScreen("MainMenu"));
        this.add(play);

        back = createButton();
        back.addActionListener(e -> gui.showScreen("SelectAVinScreen"));
        this.add(back);

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

        play.setBounds((int)(w * 0.80), (int)(h * 0.39), 90, 50);
        back.setBounds((int)(w * 0.80), (int)(h * 0.52), 90, 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}

