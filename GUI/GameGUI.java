package GUI;

import GUI.BattleScreens.ARCADE.ArcadeBattleScreen;
import GUI.BattleScreens.PVP.PVPBattleScreen;
import GUI.BattleScreens.VersusScreen;
import GUI.BattleScreens.GameOverScreen;
import GUI.CharacterSelectScreens.*;
import GUI.BattleScreens.PVE.PVEBattleScreen;
import GUI.CharacterInfo.*;
import GameEngines.*;
import Foundation.BattleMode;
import Foundation.GameCharacter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GameGUI extends JFrame {

    private final CardLayout     cardLayout;
    private final JPanel         container;
    private final VersusScreen   versusScreen;
    private final GameOverScreen gameOverScreen;

    // Floating banner shown during character select
    private final JLabel selectionBanner = new JLabel("", SwingConstants.CENTER);

    // Direct reference needed for the two-phase arcade init
    private final ArcadeBattleScreen arcadeScreen;

    private boolean isFullScreen = false;

    public GameGUI() {
        this.setSize(800, 600);
        this.setMinimumSize(new Dimension(640, 480));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        installFullScreenToggle();

        // ── Banner ────────────────────────────────────────────────────────────
        selectionBanner.setFont(new Font("Impact", Font.PLAIN, 24));
        selectionBanner.setForeground(new Color(255, 220, 30));
        selectionBanner.setBackground(new Color(0, 0, 0, 180));
        selectionBanner.setOpaque(true);
        selectionBanner.setVisible(false);

        // ── Container ─────────────────────────────────────────────────────────
        cardLayout = new CardLayout();
        container  = new JPanel(cardLayout);

        JLayeredPane layered = new JLayeredPane();
        container.setBounds(0, 0, 800, 600);
        layered.add(container,       JLayeredPane.DEFAULT_LAYER);
        layered.add(selectionBanner, JLayeredPane.PALETTE_LAYER);
        this.setContentPane(layered);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                relayoutAll();
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

        // ── Battle Screens ────────────────────────────────────────────────────
        versusScreen = new VersusScreen();
        container.add(versusScreen, "VersusScreen");

        gameOverScreen = new GameOverScreen();
        container.add(gameOverScreen, "GameOverScreen");

        PVEBattleScreen pveScreen = new PVEBattleScreen();
        container.add(pveScreen, "PVEBattleScreen");
        pveScreen.setGameGUI(this);

        PVPBattleScreen pvpScreen = new PVPBattleScreen();
        container.add(pvpScreen, "PVPBattleScreen");
        pvpScreen.setGameGUI(this);

        arcadeScreen = new ArcadeBattleScreen();
        container.add(arcadeScreen, "ArcadeBattleScreen");
        arcadeScreen.setVersusScreen(versusScreen, cardLayout, container);
        arcadeScreen.setGameGUI(this);

        this.setIconImage(new ImageIcon("Assets/others/gameLogo.gif").getImage());
        this.setVisible(true);
    }

    // =========================================================================
    // LAYOUT HELPERS
    // =========================================================================
    private void relayoutAll() {
        int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();
        container.setBounds(0, 0, w, h);
        selectionBanner.setBounds(0, 0, w, 40);
    }

    private void installFullScreenToggle() {
        getRootPane().registerKeyboardAction(
            e -> toggleFullScreen(),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    public void toggleFullScreen() {
        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        if (!isFullScreen && gd.isFullScreenSupported()) {
            dispose();
            setUndecorated(true);
            gd.setFullScreenWindow(this);
            isFullScreen = true;
        } else {
            gd.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            setSize(800, 600);
            setVisible(true);
            isFullScreen = false;
        }
        relayoutAll();
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
            // Step 1: reset and build opponent list via prepareAndGetFirstOpponent()
            // Step 2: show VersusScreen for opponent #1
            // Step 3: after animation -> show battle and call initBattle()
            case "ArcadeBattleScreen" -> {
                GameSession   session = GameSession.getInstance();
                GameCharacter player  = session.getPlayer1();

                arcadeScreen.reset();

                String firstOpponent = arcadeScreen.prepareAndGetFirstOpponent();

                if (firstOpponent == null) {
                    // Fallback: no opponents found
                    cardLayout.show(container, "ArcadeBattleScreen");
                    arcadeScreen.initBattle();
                    return;
                }

                cardLayout.show(container, "VersusScreen");
                versusScreen.show(player.getCharacterName(), firstOpponent, () -> {
                    cardLayout.show(container, "ArcadeBattleScreen");
                    arcadeScreen.initBattle();
                });
            }

            default -> cardLayout.show(container, name);
        }
    }

    // =========================================================================
    // GAME OVER — PVE / ARCADE  ("YOU WIN!" / "GAME OVER")
    // =========================================================================
    public void showGameOver(String winnerName, String loserName,
                             boolean playerWon, String nextScreen) {
        cardLayout.show(container, "GameOverScreen");
        gameOverScreen.show(winnerName, loserName, playerWon, () ->
                showScreen(nextScreen));
    }

    // =========================================================================
    // GAME OVER — PVP  (custom title e.g. "A-Vin Won!")
    // =========================================================================
    public void showGameOver(String winnerName, String loserName,
                             boolean playerWon, String customTitle, String nextScreen) {
        cardLayout.show(container, "GameOverScreen");
        gameOverScreen.show(winnerName, loserName, playerWon, customTitle, () ->
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
                selectionBanner.setText("  PLAYER 1 - Choose your fighter");
                selectionBanner.setForeground(new Color(80, 180, 255));
            } else {
                selectionBanner.setText("  PLAYER 2 - Choose your fighter");
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
        SwingUtilities.invokeLater(GameGUI::new);
    }
}