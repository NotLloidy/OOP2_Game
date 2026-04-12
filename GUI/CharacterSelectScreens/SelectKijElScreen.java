package GUI.CharacterSelectScreens;

import GUI.GameGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SelectKijElScreen extends JPanel {

    private Image bgImage;

    private JButton next;
    private JButton prev;

    public SelectKijElScreen(GameGUI gui) {

        this.setLayout(null);

        bgImage = new ImageIcon("Assets/character_select/characterKijEl.gif").getImage();

        next = createButton();
        next.addActionListener(e -> gui.showScreen("SelectSoleilScreen"));
        this.add(next);

        prev = createButton();
        prev.addActionListener(e -> gui.showScreen("SelectKennethScreen"));
        this.add(prev);

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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}
