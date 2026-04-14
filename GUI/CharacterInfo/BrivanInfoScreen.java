package GUI.CharacterInfo;

import GUI.GameGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class BrivanInfoScreen extends JPanel {

    private Image bgImage;

    private JButton play;
    private JButton back;

    public BrivanInfoScreen(GameGUI gui) {

        this.setLayout(null);

        bgImage = new ImageIcon("Assets/character_info/infoBrivanJawmir.png").getImage();

        play = createButton();
        play.addActionListener(e -> gui.showScreen("MainMenu"));
        this.add(play);

        back = createButton();
        back.addActionListener(e -> gui.showScreen("SelectBrivanScreen"));
        this.add(back);

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

        int btnWidth = (int)(w * 0.10);
        int btnHeight = (int)(h * 0.10);

        play.setBounds((int)(w * 0.7950), (int)(h * 0.40), (int)(btnWidth * 1.160), (int)(btnHeight * 0.70));
        back.setBounds((int)(w * 0.7960), (int)(h * 0.53), (int)(btnWidth * 1.160), (int)(btnHeight * 0.70));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}

