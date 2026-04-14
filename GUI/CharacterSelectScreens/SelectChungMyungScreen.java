package GUI.CharacterSelectScreens;

import GUI.GameGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SelectChungMyungScreen extends JPanel {

    private Image bgImage;

    private JButton next;
    private JButton prev;
    private JButton info;
    private JButton select;

    public SelectChungMyungScreen(GameGUI gui) {

        this.setLayout(null);

        bgImage = new ImageIcon("Assets/character_select/characterChungMyung.gif").getImage();

        next = createButton();
        next.addActionListener(e -> gui.showScreen("SelectKennethScreen"));
        this.add(next);

        prev = createButton();
        prev.addActionListener(e -> gui.showScreen("SelectBrivanScreen"));
        this.add(prev);

        info = createButton();
        info.addActionListener(e -> gui.showScreen("ChungInfoScreen"));
        this.add(info);

        select = createButton();
        select.addActionListener(e -> gui.showScreen("ChungInfoScreen"));
        this.add(select);

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

        next.setBounds((int)(w * 0.90), (int)(h * 0.41), (int)(btnWidth * 0.5), (int)(btnHeight * 1.5));
        prev.setBounds((int)(w * 0.52), (int)(h * 0.41), (int)(btnWidth * 0.5), (int)(btnHeight * 1.5));
        info.setBounds((int)(w * 0.27), (int)(h * 0.69), (int)(btnWidth * 1.2), (int)(btnHeight * 0.7));
        select.setBounds((int)(w * 0.11), (int)(h * 0.69), (int)(btnWidth * 1.2), (int)(btnHeight * 0.7));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
    }
}
