package GUI.CharacterSelectScreens;

import GUI.GameGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SelectSungJinWooScreen extends JPanel {

    private Image bgImage;

    private JButton next;
    private JButton prev;
    private JButton info;
    private JButton select;

    public SelectSungJinWooScreen(GameGUI gui) {

        this.setLayout(null);

        bgImage = new ImageIcon("Assets/character_select/characterSungJinWoo.gif").getImage();

        next = createButton();
        next.addActionListener(e -> gui.showScreen("SelectZakkarScreen"));
        this.add(next);

        prev = createButton();
        prev.addActionListener(e -> gui.showScreen("SelectSoleilScreen"));
        this.add(prev);

        info = createButton();
        info.addActionListener(e -> gui.showScreen("SungJinWooInfoScreen"));
        this.add(info);

        select = createButton();
        select.addActionListener(e -> gui.showScreen("SungJinWooInfoScreen"));
        this.add(select);

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

        next.setBounds((int)(w * 0.90), (int)(h * 0.41), 50, 80);
        prev.setBounds((int)(w * 0.50), (int)(h * 0.41), 50, 80);
        info.setBounds((int)(w * 0.27), (int)(h * 0.68), 95, 50);
        select.setBounds((int)(w * 0.11), (int)(h * 0.68), 95, 50);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}
