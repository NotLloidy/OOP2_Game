package GUI;

import GUI.BattleScreens.ARCADE.ArcadeBattleScreen;
import GUI.BattleScreens.ARCADE.ArcadeLeaderboardScreen;
import GUI.BattleScreens.PVP.PVPBattleScreen;
import GUI.BattleScreens.VersusScreen;
import GUI.BattleScreens.GameOverScreen;
import GUI.BattleScreens.LeaderboardScreen;
import GUI.BattleScreens.PlayOrExitScreen;
import GUI.CharacterSelectScreens.*;
import GUI.BattleScreens.PVE.PVEBattleScreen;
import GUI.CharacterInfo.*;
import GameEngines.*;
import Foundation.BattleMode;
import Foundation.GameCharacter;
import UTILS.SoundManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class GameGUI extends JFrame {

    private final CardLayout       cardLayout;
    private final JPanel           container;
    private final VersusScreen     versusScreen;
    private final GameOverScreen   gameOverScreen;
    private final PlayOrExitScreen playOrExitScreen;
    private final ArcadeLeaderboardScreen arcadeLeaderboardScreen;
    private final LeaderboardScreen leaderboardScreen;

    // Yellow banner — shows which player is picking on select screens
    private final JLabel selectionBanner = new JLabel("", SwingConstants.CENTER);

    // Red top-of-screen notification (replaces JOptionPane for same-char errors etc.)
    private final NotificationBanner notificationBanner = new NotificationBanner();

    private final ArcadeBattleScreen arcadeScreen;
    private final PVEBattleScreen    pveScreen;
    private final PVPBattleScreen    pvpScreen;

    // Screens where battle BGM plays
    private static final java.util.Set<String> BATTLE_SCREENS = java.util.Set.of(
            "PVPBattleScreen", "PVEBattleScreen", "ArcadeBattleScreen", "VersusScreen");

    private boolean isFullScreen = false;

    public GameGUI() {
        this.setSize(800, 600);
        this.setMinimumSize(new Dimension(640, 480));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(true);
        installFullScreenToggle();

        // ── Selection banner ─────────────────────────────────────────────
        selectionBanner.setFont(new Font("Impact", Font.PLAIN, 24));
        selectionBanner.setForeground(new Color(255, 220, 30));
        selectionBanner.setBackground(new Color(0, 0, 0, 180));
        selectionBanner.setOpaque(true);
        selectionBanner.setVisible(false);

        cardLayout = new CardLayout();
        container  = new JPanel(cardLayout);

        JLayeredPane layered = new JLayeredPane() {
            @Override
            public void doLayout() {
                int w = getWidth(), h = getHeight();
                container.setBounds(0, 0, w, h);
                selectionBanner.setBounds(0, 0, w, 40);
                notificationBanner.reposition(w);
                for (Component child : container.getComponents()) {
                    child.setBounds(0, 0, w, h);
                    if (child instanceof JPanel p) {
                        p.revalidate();
                        for (java.awt.event.ComponentListener cl : p.getComponentListeners())
                            cl.componentResized(new java.awt.event.ComponentEvent(
                                    p, java.awt.event.ComponentEvent.COMPONENT_RESIZED));
                    }
                }
            }
        };
        layered.setLayout(null);
        container.setBounds(0, 0, 800, 600);
        layered.add(container,           JLayeredPane.DEFAULT_LAYER);
        layered.add(selectionBanner,     JLayeredPane.PALETTE_LAYER);
        layered.add(notificationBanner,  JLayeredPane.POPUP_LAYER);
        this.setContentPane(layered);

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                getContentPane().doLayout();
                getContentPane().repaint();
            }
        });

        // ── Screens ───────────────────────────────────────────────────────
        container.add(new TitleScreen(this),    "TitleScreen");
        container.add(new AccountScreen(this),  "AccountScreen");
        container.add(new LoginScreen(this),    "LoginScreen");
        container.add(new RegisterScreen(this), "RegisterScreen");
        container.add(new MainMenu(this),       "MainMenu");

        container.add(new SelectAVinScreen(this),       "SelectAVinScreen");
        container.add(new SelectBrivanScreen(this),     "SelectBrivanScreen");
        container.add(new SelectChungMyungScreen(this), "SelectChungMyungScreen");
        container.add(new SelectKennethScreen(this),    "SelectKennethScreen");
        container.add(new SelectSoleilScreen(this),     "SelectSoleilScreen");
        container.add(new SelectSungJinWooScreen(this), "SelectSungJinWooScreen");
        container.add(new SelectZakkarrScreen(this),    "SelectZakkarrScreen");
        container.add(new SelectKijElScreen(this),      "SelectKijElScreen");

        container.add(new AVinInfoScreen(this),       "AVinInfoScreen");
        container.add(new BrivanInfoScreen(this),     "BrivanInfoScreen");
        container.add(new ChungInfoScreen(this),      "ChungInfoScreen");
        container.add(new KennethInfoScreen(this),    "KennethInfoScreen");
        container.add(new SoleilInfoScreen(this),     "SoleilInfoScreen");
        container.add(new SungJinWooInfoScreen(this), "SungJinWooInfoScreen");
        container.add(new ZakkarrInfoScreen(this),    "ZakkarrInfoScreen");
        container.add(new KijElInfoScreen(this),      "KijElInfoScreen");

        versusScreen = new VersusScreen();
        container.add(versusScreen, "VersusScreen");

        gameOverScreen = new GameOverScreen();
        container.add(gameOverScreen, "GameOverScreen");

        playOrExitScreen = new PlayOrExitScreen();
        container.add(playOrExitScreen, "PlayOrExitScreen");

        arcadeLeaderboardScreen = new ArcadeLeaderboardScreen(this);
        container.add(arcadeLeaderboardScreen, "ArcadeLeaderboardScreen");

        leaderboardScreen = new LeaderboardScreen(this);
        container.add(leaderboardScreen, "LeaderboardScreen");

        pveScreen = new PVEBattleScreen();
        container.add(pveScreen, "PVEBattleScreen");
        pveScreen.setGameGUI(this);

        pvpScreen = new PVPBattleScreen();
        container.add(pvpScreen, "PVPBattleScreen");
        pvpScreen.setGameGUI(this);

        arcadeScreen = new ArcadeBattleScreen();
        container.add(arcadeScreen, "ArcadeBattleScreen");
        arcadeScreen.setVersusScreen(versusScreen, cardLayout, container);
        arcadeScreen.setGameGUI(this);

        this.setIconImage(new ImageIcon("Assets/others/gameLogo.gif").getImage());
        this.setTitle("Crucible Clash");
        this.setVisible(true);

        // Start menu BGM on launch
        SoundManager.playBGM(SoundManager.BGM_MENU);
    }

    // =========================================================================
    // PUBLIC API
    // =========================================================================

    /**
     * Shows a non-blocking red notification banner at the top of the screen.
     * Use instead of JOptionPane for in-game messages like "Cannot choose same character".
     */
    public void showNotification(String message) {
        SwingUtilities.invokeLater(() -> notificationBanner.show(message));
    }

    // =========================================================================
    // LAYOUT HELPERS
    // =========================================================================

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
        SwingUtilities.invokeLater(() -> {
            getContentPane().doLayout();
            getContentPane().repaint();
        });
    }

    // =========================================================================
    // SHOW SCREEN
    // =========================================================================

    public void showScreen(String name) {
        updateSelectionBanner(name);
        updateBGM(name);

        switch (name) {

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
                    pveScreen.reset();
                    pveScreen.initBattle();
                });
            }

            case "PVPBattleScreen" -> {
                GameSession session = GameSession.getInstance();
                cardLayout.show(container, "VersusScreen");
                versusScreen.show(
                        session.getPlayer1().getCharacterName(),
                        session.getPlayer2().getCharacterName(),
                        () -> {
                            cardLayout.show(container, "PVPBattleScreen");
                            pvpScreen.reset();
                            pvpScreen.initBattle();
                        }
                );
            }

            case "ArcadeBattleScreen" -> {
                GameSession   session = GameSession.getInstance();
                GameCharacter player  = session.getPlayer1();

                arcadeScreen.reset();
                String firstOpponent = arcadeScreen.prepareAndGetFirstOpponent();

                if (firstOpponent == null) {
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

            case "LeaderboardScreen" -> {
                leaderboardScreen.refresh();
                cardLayout.show(container, "LeaderboardScreen");
            }

            default -> cardLayout.show(container, name);
        }
    }

    // =========================================================================
    // GAME OVER
    // =========================================================================

    // ── PVE ──────────────────────────────────────────────────────────────────
    public void showGameOver(String winnerName, String loserName,
                             boolean playerWon, String nextScreen) {

        Runnable playAgain = () -> {
            GameSession session = GameSession.getInstance();
            cardLayout.show(container, "VersusScreen");
            versusScreen.show(
                session.getPlayer1().getCharacterName(),
                session.getPlayer2().getCharacterName(),
                () -> {
                    cardLayout.show(container, "PVEBattleScreen");
                    pveScreen.reset();
                    pveScreen.initBattle();
                }
            );
        };

        Runnable exit = () -> showScreen(nextScreen);

        playOrExitScreen.setup(playAgain, exit);
        cardLayout.show(container, "GameOverScreen");
        updateBGM("GameOverScreen");
        gameOverScreen.show(winnerName, loserName, playerWon,
                () -> cardLayout.show(container, "PlayOrExitScreen"));
    }

    // ── PVP ──────────────────────────────────────────────────────────────────
    public void showGameOver(String winnerName, String loserName,
                             boolean playerWon, String customTitle, String nextScreen) {

        Runnable playAgain = () -> {
            GameSession session = GameSession.getInstance();
            cardLayout.show(container, "VersusScreen");
            versusScreen.show(
                session.getPlayer1().getCharacterName(),
                session.getPlayer2().getCharacterName(),
                () -> {
                    cardLayout.show(container, "PVPBattleScreen");
                    pvpScreen.reset();
                    pvpScreen.initBattle();
                }
            );
        };

        Runnable exit = () -> showScreen(nextScreen);

        playOrExitScreen.setup(playAgain, exit);
        cardLayout.show(container, "GameOverScreen");
        updateBGM("GameOverScreen");
        gameOverScreen.show(winnerName, loserName, playerWon, customTitle,
                () -> cardLayout.show(container, "PlayOrExitScreen"));
    }

    // ── ARCADE ───────────────────────────────────────────────────────────────
    public void showGameOverArcade(String winnerName, String loserName,
                                   boolean playerWon, String nextScreen) {

        Runnable playAgain = () -> {
            GameSession   session = GameSession.getInstance();
            GameCharacter player  = session.getPlayer1();

            arcadeScreen.reset();
            String firstOpponent = arcadeScreen.prepareAndGetFirstOpponent();

            if (firstOpponent == null) {
                cardLayout.show(container, "ArcadeBattleScreen");
                arcadeScreen.initBattle();
                return;
            }

            cardLayout.show(container, "VersusScreen");
            versusScreen.show(player.getCharacterName(), firstOpponent, () -> {
                cardLayout.show(container, "ArcadeBattleScreen");
                arcadeScreen.initBattle();
            });
        };

        Runnable exit = () -> showScreen(nextScreen);

        playOrExitScreen.setup(playAgain, exit);
        cardLayout.show(container, "GameOverScreen");
        updateBGM("GameOverScreen");
        gameOverScreen.show(winnerName, loserName, playerWon,
                () -> cardLayout.show(container, "PlayOrExitScreen"));
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
    // BGM
    // =========================================================================

    private void updateBGM(String screen) {
        if (BATTLE_SCREENS.contains(screen)) {
            SoundManager.playBGM(SoundManager.BGM_BATTLE);
        } else {
            SoundManager.playBGM(SoundManager.BGM_MENU);
        }
    }

    // =========================================================================
    // MAIN
    // =========================================================================

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameGUI::new);
    }
}