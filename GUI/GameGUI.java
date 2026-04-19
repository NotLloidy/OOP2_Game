package GUI;

import GUI.BattleScreens.ARCADE.ArcadeBattleScreen;
import GUI.BattleScreens.PVP.PVPBattleScreen;
import GUI.BattleScreens.VersusScreen;
import GUI.BattleScreens.GameOverScreen;          //  NEW
import GUI.CharacterSelectScreens.*;
import GUI.BattleScreens.PVE.PVEBattleScreen;
import GUI.CharacterInfo.*;
import GameEngines.*;
import Foundation.BattleMode;
import Foundation.GameCharacter;

import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame {

    private final CardLayout    cardLayout;
    private final JPanel        container;
    private final VersusScreen  versusScreen;
    private final GameOverScreen gameOverScreen; //  NEW

    // Floating banner shown during character select
    private final JLabel selectionBanner = new JLabel("", SwingConstants.CENTER);

    public GameGUI() {
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ── Banner setup ──────────────────────────────────────────────────────
        selectionBanner.setFont(new Font("Impact", Font.PLAIN, 24));
        selectionBanner.setForeground(new Color(255, 220, 30));
        selectionBanner.setBackground(new Color(0, 0, 0, 180));
        selectionBanner.setOpaque(true);
        selectionBanner.setVisible(false);

        // ── Container (CardLayout) ────────────────────────────────────────────
        cardLayout = new CardLayout();
        container  = new JPanel(cardLayout);

        // ── Layered pane so banner floats on top ──────────────────────────────
        JLayeredPane layered = new JLayeredPane();
        container.setBounds(0, 0, 800, 600);
        layered.add(container,       JLayeredPane.DEFAULT_LAYER);
        layered.add(selectionBanner, JLayeredPane.PALETTE_LAYER);
        this.setContentPane(layered);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                int w = getContentPane().getWidth();
                int h = getContentPane().getHeight();
                container.setBounds(0, 0, w, h);
                selectionBanner.setBounds(0, 0, w, 40);
            }
        });

        // ── Screens ───────────────────────────────────────────────────────────
        container.add(new TitleScreen(this),    "TitleScreen");
        container.add(new AccountScreen(this),  "AccountScreen");
        container.add(new LoginScreen(this),    "LoginScreen");
        container.add(new RegisterScreen(this), "RegisterScreen");
        container.add(new MainMenu(this),       "MainMenu");

        // Character Select
        container.add(new SelectAVinScreen(this),       "SelectAVinScreen");
        container.add(new SelectBrivanScreen(this),     "SelectBrivanScreen");
        container.add(new SelectChungMyungScreen(this), "SelectChungScreen");
        container.add(new SelectKennethScreen(this),    "SelectKennethScreen");
        container.add(new SelectSoleilScreen(this),     "SelectSoleilScreen");
        container.add(new SelectSungJinWooScreen(this), "SelectSungJinWooScreen");
        container.add(new SelectZakkarrScreen(this),    "SelectZakkarScreen");
        container.add(new SelectKijElScreen(this),      "SelectKijElScreen");

        // Character Info
        container.add(new AVinInfoScreen(this),       "AVinInfoScreen");
        container.add(new BrivanInfoScreen(this),     "BrivanInfoScreen");
        container.add(new ChungInfoScreen(this),      "ChungInfoScreen");
        container.add(new KennethInfoScreen(this),    "KennethInfoScreen");
        container.add(new SoleilInfoScreen(this),     "SoleilInfoScreen");
        container.add(new SungJinWooInfoScreen(this), "SungJinWooInfoScreen");
        container.add(new ZakkarrInfoScreen(this),    "ZakkarrInfoScreen");
        container.add(new KijElInfoScreen(this),      "KijElInfoScreen");

        // Battle Screens
        versusScreen = new VersusScreen();
        container.add(versusScreen, "VersusScreen");

        gameOverScreen = new GameOverScreen();              //  NEW
        container.add(gameOverScreen, "GameOverScreen");   //  NEW

        PVEBattleScreen pveScreen = new PVEBattleScreen();
        container.add(pveScreen, "PVEBattleScreen");
        pveScreen.setGameGUI(this);
        container.add(new PVPBattleScreen(), "PVPBattleScreen");

        ArcadeBattleScreen arcadeScreen = new ArcadeBattleScreen();
        container.add(arcadeScreen, "ArcadeBattleScreen");
        arcadeScreen.setVersusScreen(versusScreen, cardLayout, container);
        arcadeScreen.setGameGUI(this);
             

        this.setVisible(true);
    }

    // =========================================================================
    // SHOW SCREEN
    // =========================================================================
    public void showScreen(String name) {

        updateSelectionBanner(name);

        switch (name) {

            // ── PVE ──────────────────────────────────────────────────────────
            case "PVEBattleScreen" -> {
                GameSession   session = GameSession.getInstance();
                GameCharacter p1      = session.getPlayer1();
                BattleSystem  sys     = new BattleSystem();

                GameCharacter enemy = sys.selectCharacter((int)(Math.random() * 8) + 1);
                while (enemy.getCharacterName().equals(p1.getCharacterName()))
                    enemy = sys.selectCharacter((int)(Math.random() * 8) + 1);
                session.setPlayer2(enemy);

                cardLayout.show(container, "VersusScreen");
                versusScreen.show(p1.getCharacterName(), enemy.getCharacterName(), () -> {
                    cardLayout.show(container, "PVEBattleScreen");
                    for (Component c : container.getComponents())
                        if (c instanceof PVEBattleScreen s) { s.reset(); s.initBattle(); }
                });
            }

            // ── PVP ──────────────────────────────────────────────────────────
            case "PVPBattleScreen" -> {
                GameSession session = GameSession.getInstance();
                cardLayout.show(container, "VersusScreen");
                versusScreen.show(
                        session.getPlayer1().getCharacterName(),
                        session.getPlayer2().getCharacterName(),
                        () -> {
                            cardLayout.show(container, "PVPBattleScreen");
                            for (Component c : container.getComponents())
                                if (c instanceof PVPBattleScreen s) { s.reset(); s.initBattle(); }
                        }
                );
            }

            // ── ARCADE ───────────────────────────────────────────────────────
            case "ArcadeBattleScreen" -> {
                cardLayout.show(container, "ArcadeBattleScreen");
                for (Component c : container.getComponents())
                    if (c instanceof ArcadeBattleScreen s) { s.reset(); s.initBattle(); }
            }

            default -> cardLayout.show(container, name);
        }
    }

    // =========================================================================
    // GAME OVER   NEW
    // =========================================================================
    /**
     * Show the Game Over screen then navigate to a follow-up screen.
     *
     * @param winnerName   character name of the winner (sprite loaded from Assets/characters_idle/)
     * @param loserName    character name of the loser (shown in sub-text)
     * @param playerWon    true  → "YOU WIN!" yellow  |  false → "YOU LOSE" red
     * @param nextScreen   screen key to show after the animation finishes (e.g. "MainMenu")
     */
    public void showGameOver(String winnerName, String loserName,
                             boolean playerWon, String nextScreen) {
        cardLayout.show(container, "GameOverScreen");
        gameOverScreen.show(winnerName, loserName, playerWon, () ->
                showScreen(nextScreen));
    }

    // =========================================================================
    // SELECTION BANNER
    // =========================================================================
    private void updateSelectionBanner(String screen) {

        boolean isSelectScreen = screen.startsWith("Select");

        if (!isSelectScreen) {
            selectionBanner.setVisible(false);
            return;
        }

        GameSession session = GameSession.getInstance();
        BattleMode  mode    = session.getMode();
        int         picking = session.getSelectingPlayer();

        if (mode == BattleMode.PVP) {
            if (picking == 1) {
                selectionBanner.setText("  PLAYER 1 — Choose your fighter");
                selectionBanner.setForeground(new Color(80, 180, 255));
            } else {
                selectionBanner.setText("  PLAYER 2 — Choose your fighter");
                selectionBanner.setForeground(new Color(255, 100, 100));
            }
        } else {
            selectionBanner.setText("  Choose your fighter");
            selectionBanner.setForeground(new Color(255, 220, 30));
        }

        selectionBanner.setVisible(true);
    }

    // =========================================================================
    // MAIN
    // =========================================================================
    public static void main(String[] args) {
        new GameGUI();
    }
}