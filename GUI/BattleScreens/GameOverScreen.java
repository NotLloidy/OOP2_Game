package GUI.BattleScreens;

import javax.swing.*;
import java.awt.*;

public class GameOverScreen extends JPanel {

    private static final String BG_PATH    = "Assets/battle_sprites/battleArena.gif";
    // TODO: Replace BG_PATH above with the dedicated Game Over background asset once it is ready.
    //       e.g. private static final String BG_PATH = "Assets/battle_sprites/gameOverBackground.gif";

    private static final String SPRITE_DIR = "Assets/character_related/idleAnimation/left/";
    private static final String SPRITE_SFX = "-left.gif";

    // ── Button asset paths ─────────────────────────────────────────────────
    // TODO: Replace these with the real Game Over screen button assets once they are ready.
    //       e.g. private static final String BTN_PLAY_AGAIN_PATH = "Assets/gameover_buttons/play_again_btn.png";
    //            private static final String BTN_EXIT_PATH       = "Assets/gameover_buttons/exit_btn.png";
    private static final String BTN_PLAY_AGAIN_PATH = "Assets/battle_sprites/battle_buttons/actions/fight_btn.gif";
    private static final String BTN_EXIT_PATH       = "Assets/battle_sprites/battle_buttons/actions/back_btn.gif";

    private Image bgImage;
    private Image winnerSprite;

    private float curtainAlpha  = 0f;
    private float spriteX       = 0f;
    private float spriteTargetX = 0f;
    private float titleY        = 0f;
    private float titleTargetY  = 0f;
    private float titleAlpha    = 0f;
    private float glowPulse     = 0f;
    private int   phase         = 0;

    private boolean playerWon   = false;
    private String  winnerName  = "";
    private String  loserName   = "";
    private String  customTitle = null;

    private static final int PARTICLE_COUNT = 60;
    private float[] px, py, pvx, pvy, palpha;
    private Color[] pcolor;
    private boolean particlesFired = false;

    private Timer    animTimer;
    private Runnable onPlayAgain;
    private Runnable onExit;

    private Font titleFont;

    // ── Post-animation action buttons ─────────────────────────────────────
    private JButton btnPlayAgain;
    private JButton btnExit;

    public GameOverScreen() {
        setLayout(null);
        setOpaque(true);

        bgImage   = new ImageIcon(BG_PATH).getImage();
        titleFont = new Font("Impact", Font.PLAIN, 96);

        px     = new float[PARTICLE_COUNT];
        py     = new float[PARTICLE_COUNT];
        pvx    = new float[PARTICLE_COUNT];
        pvy    = new float[PARTICLE_COUNT];
        palpha = new float[PARTICLE_COUNT];
        pcolor = new Color[PARTICLE_COUNT];

        createButtons();
    }

    // ── Button construction ────────────────────────────────────────────────

    private void createButtons() {
        btnPlayAgain = makeImageButton(BTN_PLAY_AGAIN_PATH, "Play Again");
        btnExit      = makeImageButton(BTN_EXIT_PATH,       "Exit");

        // TODO: Once the real Game Over screen is ready, set both buttons to
        //       setVisible(false) here so they start hidden and are revealed
        //       by showButtons(). For now they are visible so you can verify positioning.
        btnPlayAgain.setVisible(true);
        btnExit     .setVisible(true);

        // Buttons start disabled; they become active only after the animation ends
        btnPlayAgain.setEnabled(false);
        btnExit     .setEnabled(false);

        add(btnPlayAgain);
        add(btnExit);
    }

    /**
     * Creates a borderless image button scaled to a consistent height.
     * Mirrors the helper style used in BaseBattleScreen.
     */
    private JButton makeImageButton(String imagePath, String tooltip) {
        JButton btn = new JButton();
        btn.setToolTipText(tooltip);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);

        ImageIcon raw = new ImageIcon(imagePath);
        int targetH = 55; // button height in pixels — easy to change when aligning
        int targetW = (int)(targetH * (281f / 119f)); // same aspect ratio as battle buttons
        if (raw.getIconWidth() > 0 && raw.getIconHeight() > 0) {
            Image scaled = raw.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
            btn.setIcon(new ImageIcon(scaled));
        }
        btn.setPreferredSize(new Dimension(targetW, targetH));
        return btn;
    }

    /**
     * Positions the two action buttons after the panel has a valid size.
     * <p>
     * TODO: Once the dedicated Game Over screen layout is known, update the
     *       X/Y constants below to align the buttons with that screen's design.
     *       All magic numbers are grouped here so they are easy to find and tweak.
     */
    private void layoutButtons() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        Dimension dpa = btnPlayAgain.getPreferredSize();
        Dimension dex = btnExit    .getPreferredSize();

        // ── POSITION KNOBS ────────────────────────────────────────────────
        // Vertical position: 72 % down the panel (easy to move as a single fraction)
        int btnY = (int)(h * 0.72f);

        // Horizontal centres for each button.
        // Play Again sits left-of-centre, Exit sits right-of-centre.
        // Adjust the fractions to snap them onto the new screen's buttons.
        int playAgainCentreX = (int)(w * 0.35f);
        int exitCentreX      = (int)(w * 0.65f);
        // ─────────────────────────────────────────────────────────────────

        btnPlayAgain.setBounds(
            playAgainCentreX - dpa.width  / 2, btnY,
            dpa.width,  dpa.height
        );
        btnExit.setBounds(
            exitCentreX      - dex.width  / 2, btnY,
            dex.width,  dex.height
        );
    }

    // ── Show API ──────────────────────────────────────────────────────────

    // ── Used by PVE and Arcade ────────────────────────────────────────────
    public void show(String winnerCharName, String loserCharName,
                     boolean playerWon, Runnable afterAnimation) {
        this.customTitle = null;
        showInternal(winnerCharName, loserCharName, playerWon,
                     afterAnimation,   // onPlayAgain == same "restart" runnable
                     afterAnimation);  // onExit      == same runnable for now
                                       // GameGUI passes the correct runnables via the
                                       // four-argument overload below whenever it needs
                                       // different behaviour per button.
    }

    /**
     * Full overload used by GameGUI.
     *
     * @param onPlayAgain runnable to execute when "Play Again" is pressed
     * @param onExit      runnable to execute when "Exit" is pressed
     */
    public void show(String winnerCharName, String loserCharName,
                     boolean playerWon,
                     Runnable onPlayAgain, Runnable onExit) {
        this.customTitle = null;
        showInternal(winnerCharName, loserCharName, playerWon, onPlayAgain, onExit);
    }

    // ── Used by PVP — shows a custom title like "A-Vin Won!" ─────────────
    public void show(String winnerCharName, String loserCharName,
                     boolean playerWon, String customTitle, Runnable afterAnimation) {
        this.customTitle = customTitle;
        showInternal(winnerCharName, loserCharName, playerWon,
                     afterAnimation, afterAnimation);
    }

    /**
     * Full PVP overload with separate play-again / exit runnables.
     */
    public void show(String winnerCharName, String loserCharName,
                     boolean playerWon, String customTitle,
                     Runnable onPlayAgain, Runnable onExit) {
        this.customTitle = customTitle;
        showInternal(winnerCharName, loserCharName, playerWon, onPlayAgain, onExit);
    }

    private void showInternal(String winnerCharName, String loserCharName,
                               boolean playerWon,
                               Runnable onPlayAgain, Runnable onExit) {
        this.winnerName  = winnerCharName;
        this.loserName   = loserCharName;
        this.playerWon   = playerWon;
        this.onPlayAgain = onPlayAgain;
        this.onExit      = onExit;

        String fileKey = winnerCharName.toLowerCase().replaceAll("[^a-z0-9]", "");
        winnerSprite   = new ImageIcon(SPRITE_DIR + fileKey + SPRITE_SFX).getImage();

        curtainAlpha   = 0f;
        titleAlpha     = 0f;
        glowPulse      = 0f;
        particlesFired = false;
        phase          = 0;

        spriteX       = -350f;
        spriteTargetX = getWidth()  > 0 ? getWidth()  * 0.08f : 60f;
        titleY        = -150f;
        titleTargetY  = getHeight() > 0 ? getHeight() * 0.50f : 300f;

        // Hide and disable buttons until the animation finishes
        btnPlayAgain.setEnabled(false);
        btnExit     .setEnabled(false);

        // TODO: When the real screen is ready, uncomment the two lines below
        //       so buttons are invisible during the animation and only reveal
        //       once showButtons() is called.
        // btnPlayAgain.setVisible(false);
        // btnExit     .setVisible(false);

        // Wire up button actions (clear old listeners first)
        for (java.awt.event.ActionListener al : btnPlayAgain.getActionListeners())
            btnPlayAgain.removeActionListener(al);
        for (java.awt.event.ActionListener al : btnExit.getActionListeners())
            btnExit.removeActionListener(al);

        btnPlayAgain.addActionListener(e -> { if (this.onPlayAgain != null) this.onPlayAgain.run(); });
        btnExit     .addActionListener(e -> { if (this.onExit      != null) this.onExit     .run(); });

        layoutButtons();
        startAnimation();
        repaint();
    }

    /** Called at the end of the animation to enable (and reveal) the buttons. */
    private void showButtons() {
        btnPlayAgain.setEnabled(true);
        btnExit     .setEnabled(true);

        // TODO: When the real screen is ready, uncomment these lines so the
        //       buttons fade/appear only after the animation completes.
        // btnPlayAgain.setVisible(true);
        // btnExit     .setVisible(true);

        layoutButtons();
        repaint();
    }

    // ── Animation ─────────────────────────────────────────────────────────

    private void startAnimation() {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();

        animTimer = new Timer(16, e -> {
            int w = getWidth();
            int h = getHeight();

            spriteTargetX = w * 0.08f;
            titleTargetY  = h * 0.50f;

            glowPulse += 0.07f;

            switch (phase) {
                case 0 -> {
                    curtainAlpha = Math.min(0.72f, curtainAlpha + 0.04f);
                    if (curtainAlpha >= 0.72f) phase = 1;
                }
                case 1 -> {
                    spriteX += (spriteTargetX - spriteX) * 0.14f;
                    if (Math.abs(spriteX - spriteTargetX) < 2f) {
                        spriteX = spriteTargetX;
                        phase   = 2;
                    }
                }
                case 2 -> {
                    titleY    += (titleTargetY - titleY) * 0.18f;
                    titleAlpha = Math.min(1f, titleAlpha + 0.10f);

                    boolean titleSettled = Math.abs(titleY - titleTargetY) < 2f;

                    if (titleSettled && !particlesFired) {
                        fireParticles(w / 2, (int)titleY, w, h);
                        particlesFired = true;
                    }

                    if (titleSettled && titleAlpha >= 1f) {
                        phase = 3;
                        Timer hold = new Timer(2200, ev -> {
                            animTimer.stop();
                            showButtons(); // Animation done → reveal / enable buttons
                        });
                        hold.setRepeats(false);
                        hold.start();
                    }
                }
            }

            tickParticles();
            repaint();
        });

        animTimer.start();
    }

    private void fireParticles(int cx, int cy, int w, int h) {
        Color[] palette = playerWon
                ? new Color[]{ new Color(255,220,30), new Color(255,160,20),
                               new Color(255,255,180), new Color(200,255,100) }
                : new Color[]{ new Color(200,40,40), new Color(255,80,80),
                               new Color(160,0,0),   new Color(100,0,0) };

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i]  = cx + (float)(Math.random() * 200 - 100);
            py[i]  = cy;
            double angle = Math.random() * Math.PI * 2;
            float  speed = (float)(Math.random() * 8 + 2);
            pvx[i] = (float)(Math.cos(angle) * speed);
            pvy[i] = (float)(Math.sin(angle) * speed) - 4f;
            palpha[i] = 1f;
            pcolor[i] = palette[(int)(Math.random() * palette.length)];
        }
    }

    private void tickParticles() {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            if (palpha[i] <= 0) continue;
            px[i]     += pvx[i];
            py[i]     += pvy[i];
            pvy[i]    += 0.25f;
            palpha[i] -= 0.018f;
        }
    }

    // ── Layout ────────────────────────────────────────────────────────────

    @Override
    public void doLayout() {
        super.doLayout();
        layoutButtons();
        repaint();
    }

    // ── Paint ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // Background
        if (bgImage != null)
            g2.drawImage(bgImage, 0, 0, w, h, this);
        else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, w, h);
        }

        // Dark curtain
        if (curtainAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, curtainAlpha));
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, w, h);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // Winner sprite
        if (winnerSprite != null && phase >= 1) {
            int sw = (int)(w * 0.38);
            int sh = (int)(h * 0.75);
            int sy = (int)(h * 0.10);
            g2.drawImage(winnerSprite, (int)spriteX, sy, sw, sh, this);
        }

        // Particles
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            if (palpha[i] <= 0) continue;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, palpha[i]));
            g2.setColor(pcolor[i]);
            int ps = (int)(palpha[i] * 10 + 2);
            g2.fillOval((int)px[i] - ps/2, (int)py[i] - ps/2, ps, ps);
        }
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

        // Title
        if (titleAlpha > 0) {
            String titleText = (customTitle != null)
                    ? customTitle
                    : (playerWon ? "YOU WIN!" : "GAME OVER");

            // Shrink font for longer custom titles (e.g. "A-Vin Won!")
            Font drawFont = titleText.length() > 10
                    ? titleFont.deriveFont(Font.PLAIN, 64f)
                    : titleFont;
            g2.setFont(drawFont);
            FontMetrics fm = g2.getFontMetrics();

            int tx = (w - fm.stringWidth(titleText)) / 2;
            int ty = (int)titleY;

            Color glowColor = playerWon ? new Color(255, 220, 30) : new Color(200, 30, 30);

            // Pulsing glow
            float glow = 0.35f + 0.25f * (float)Math.sin(glowPulse);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glow * titleAlpha));
            for (int r = 12; r >= 4; r -= 2) {
                g2.setColor(new Color(glowColor.getRed(), glowColor.getGreen(),
                        glowColor.getBlue(), (int)(40 * glow)));
                g2.drawString(titleText, tx + r, ty + r);
                g2.drawString(titleText, tx - r, ty + r);
                g2.drawString(titleText, tx,     ty - r);
            }

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));

            // Shadow
            g2.setColor(new Color(0, 0, 0, 200));
            g2.drawString(titleText, tx + 5, ty + 5);

            // Main title
            g2.setColor(playerWon ? new Color(255, 220, 30) : new Color(220, 40, 40));
            g2.drawString(titleText, tx, ty);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }
}