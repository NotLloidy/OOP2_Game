package GUI.CharacterInfo;

import GUI.GameGUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KennethInfoScreen extends JPanel {

    private Image bgImage;

    private JButton play;
    private JButton back;

    public KennethInfoScreen(GameGUI gui) {

        this.setLayout(null);

        bgImage = new ImageIcon("Assets/character_info/infoKenneth.png").getImage();

        play = createButton();
        play.addActionListener(e -> {
            GameEngines.GameSession session = GameEngines.GameSession.getInstance();

            // LOCK PLAYER 1
            session.setPlayer1(new Characters.Kenneth());

            // IMPORTANT: ensure mode is set
            if (session.getMode() == null) {
                System.out.println("Mode not set!");
                return;
            }

            // GO DIRECTLY TO BATTLE
            gui.showScreen("PVEBattleScreen");
        });
        this.add(play);

        back = createButton();
        back.addActionListener(e -> gui.showScreen("SelectKennethScreen"));
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

