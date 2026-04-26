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

    private final CardLayout    cardLayout;
    private final JPanel        container;
    private final VersusScreen  versusScreen;
    private final GameOverScreen gameOverScreen;

    // Floating banner shown during character select
    private final JLabel selectionBanner = new JLabel("", SwingConstants.CENTER);

    public GameGUI() {
        this.setSize(800, 600);
        this.setMinimumSize(new Dimension(640, 480));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // ── Allow the window to be resized or toggled to full-screen ──────────
        // Users can maximise with the OS window button or press F11 for
        // borderless full-screen.
        this.setResizable(true);
        installFullScreenToggle();

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

        // Resize listener — keeps container and banner filling the window
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

        // Battle Screens
        versusScreen = new VersusScreen();
        container.add(versusScreen, "VersusScreen");

        gameOverScreen = new GameOverScreen();
        container.add(gameOverScreen, "GameOverScreen");

        PVEBattleScreen pveScreen = new PVEBattleScreen();
        container.add(pveScreen, "PVEBattleScreen");
        pveScreen.setGameGUI(this);

        container.add(new PVPBattleScreen(), "PVPBattleScreen");

        ArcadeBattleScreen arcadeScreen = new ArcadeBattleScreen();
        container.add(arcadeScreen, "ArcadeBattleScreen");
        arcadeScreen.setVersusScreen(versusScreen, cardLayout, container);
        arcadeScreen.setGameGUI(this);

        this.setIconImage(new ImageIcon("Assets/others/gameLogo.gif").getImage());
        this.setVisible(true);
    }

    // =========================================================================
    // LAYOUT HELPERS
    // =========================================================================

    /** Resize the container and banner to fill whatever the content-pane is. */
    private void relayoutAll() {
        int w = getContentPane().getWidth();
        int h = getContentPane().getHeight();
        container.setBounds(0, 0, w, h);
        selectionBanner.setBounds(0, 0, w, 40);
    }

    /**
     * Installs an F11 key binding that toggles borderless full-screen mode.
     * Works on macOS, Windows, and Linux.
     */
    private void installFullScreenToggle() {
        getRootPane().registerKeyboardAction(
            e -> toggleFullScreen(),
            KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F11, 0),
            JComponent.WHEN_IN_FOCUSED_WINDOW
        );
    }

    private boolean isFullScreen = false;

    public void toggleFullScreen() {
        GraphicsDevice gd = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();

        if (!isFullScreen && gd.isFullScreenSupported()) {
            // Enter full-screen exclusive mode
            dispose();
            setUndecorated(true);
            gd.setFullScreenWindow(this);
            isFullScreen = true;
        } else {
            // Exit full-screen
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
            case "ArcadeBattleScreen" -> {
                cardLayout.show(container, "ArcadeBattleScreen");
                for (Component c : container.getComponents())
                    if (c instanceof ArcadeBattleScreen s) { s.reset(); s.initBattle(); }
            }

            default -> cardLayout.show(container, name);
        }
    }

    // =========================================================================
    // GAME OVER
    // =========================================================================
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
        SwingUtilities.invokeLater(GameGUI::new);
    }
}