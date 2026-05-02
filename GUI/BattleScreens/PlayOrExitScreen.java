package GUI.BattleScreens;

import javax.swing.*;
import java.awt.*;

/**
 * PlayOrExitScreen
 *
 * Shown immediately after the Game Over animation finishes.
 * Displays PlayOrExit.gif as its background and two plain white buttons:
 *   • Play Again — restarts the match (PVE / PVP) or the full arcade run
 *   • Exit        — returns to the Main Menu
 *
 * GameGUI calls setup() before card-switching to this screen so the
 * correct runnables are wired up for each game mode.
 */
public class PlayOrExitScreen extends JPanel {

    // ── Background ────────────────────────────────────────────────────────
    private static final String BG_PATH = "Assets/navigation/PlayOrExit.gif";

    private Image bgImage;

    // ── Buttons ───────────────────────────────────────────────────────────
    private JButton btnPlayAgain;
    private JButton btnExit;

    // ── Runnables set by GameGUI before showing this screen ───────────────
    private Runnable onPlayAgain;
    private Runnable onExit;

    public PlayOrExitScreen() {
        setLayout(null);
        setOpaque(true);

        bgImage = new ImageIcon(BG_PATH).getImage();

        createButtons();

        // Re-layout whenever the panel is resized
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                layoutButtons();
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────
    // SETUP — called by GameGUI right before card-switching to this screen
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Wires up the two button actions for the current game mode.
     *
     * @param onPlayAgain runnable that restarts the match / arcade run
     * @param onExit      runnable that goes to the Main Menu
     */
    public void setup(Runnable onPlayAgain, Runnable onExit) {
        this.onPlayAgain = onPlayAgain;
        this.onExit      = onExit;

        // Clear any stale listeners from a previous game
        for (java.awt.event.ActionListener al : btnPlayAgain.getActionListeners())
            btnPlayAgain.removeActionListener(al);
        for (java.awt.event.ActionListener al : btnExit.getActionListeners())
            btnExit.removeActionListener(al);

        btnPlayAgain.addActionListener(e -> { if (this.onPlayAgain != null) this.onPlayAgain.run(); });
        btnExit     .addActionListener(e -> { if (this.onExit      != null) this.onExit     .run(); });

        layoutButtons();
        repaint();
    }

    // ─────────────────────────────────────────────────────────────────────
    // BUTTON CREATION
    // All styling knobs are grouped at the top of this method.
    // ─────────────────────────────────────────────────────────────────────

    private void createButtons() {

        // ── Styling knobs — edit here to restyle the buttons ─────────────
        int   BTN_WIDTH  = 210;
        int   BTN_HEIGHT = 90;
        Color BTN_BG     = Color.WHITE;
        Color BTN_FG     = Color.BLACK;
        Font  BTN_FONT   = new Font("Impact", Font.PLAIN, 18);
        // ─────────────────────────────────────────────────────────────────

        btnPlayAgain = new JButton();
        btnExit      = new JButton();

        for (JButton btn : new JButton[]{ btnPlayAgain, btnExit }) {
            btn.setBackground(BTN_BG);
            btn.setForeground(BTN_FG);
            btn.setFont(BTN_FONT);
            btn.setFocusPainted(false);
            btn.setOpaque(false);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setPreferredSize(new Dimension(BTN_WIDTH, BTN_HEIGHT));
            add(btn);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // BUTTON POSITIONING
    //
    // ── HOW TO MOVE THE BUTTONS ──────────────────────────────────────────
    // All position logic is here in one place. Adjust the knobs below
    // to align the buttons with wherever your PlayOrExit.gif design puts
    // them. Button size is controlled in createButtons() above.
    // ─────────────────────────────────────────────────────────────────────

    private void layoutButtons() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        Dimension d = btnPlayAgain.getPreferredSize();

        // ── POSITION KNOBS ────────────────────────────────────────────────
        // Vertical centre of both buttons (fraction of panel height).
        int btnY = (int)(h * 0.61f);

        // Horizontal centre of each button (fraction of panel width).
        int playAgainCentreX = (int)(w * 0.35f);
        int exitCentreX      = (int)(w * 0.65f);
        // ─────────────────────────────────────────────────────────────────

        btnPlayAgain.setBounds(
                playAgainCentreX - d.width  / 2,
                btnY             - d.height / 2,
                d.width, d.height);

        btnExit.setBounds(
                exitCentreX - d.width  / 2,
                btnY        - d.height / 2,
                d.width, d.height);
    }

    // ─────────────────────────────────────────────────────────────────────
    // LAYOUT & PAINT
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public void doLayout() {
        super.doLayout();
        layoutButtons();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null)
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}
