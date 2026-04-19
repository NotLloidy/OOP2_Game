package GUI.BattleScreens;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * GameOverScreen — Animated "YOU WIN / YOU LOSE" reveal screen.
 *
 * Modeled after VersusScreen. Call:
 *   show(winnerName, loserName, didPlayerWin, onDone)
 *
 * Animation sequence:
 *   1. Dark curtain fades in
 *   2. Winner sprite slides in from the side
 *   3. "WINNER!" / "DEFEATED" text slams in from top
 *   4. Particle burst effect
 *   5. Holds 2s → calls onDone callback
 *
 * ASSET PATHS:
 *   BG_PATH    = "Assets/battleArenaScreen.gif"
 *   SPRITE_DIR = "Assets/characters_idle/"
 *   SPRITE_SFX = "-idle.gif"
 */
public class GameOverScreen extends JPanel {

    // ── Asset paths ──────────────────────────────────────────────────────────
    private static final String BG_PATH    = "Assets/battleArenaScreen.gif";
    private static final String SPRITE_DIR = "Assets/characters_idle/";
    private static final String SPRITE_SFX = "-idle.gif";
    // ────────────────────────────────────────────────────────────────────────

    private Image bgImage;
    private Image winnerSprite;

    // Animation state
    private float curtainAlpha = 0f;   // black curtain fade
    private float spriteX      = 0f;
    private float spriteTargetX= 0f;
    private float titleY       = 0f;   // "YOU WIN" / "YOU LOSE" Y position
    private float titleTargetY = 0f;
    private float titleAlpha   = 0f;
    private float subAlpha     = 0f;   // winner name sub-text
    private float glowPulse    = 0f;   // glow oscillation
    private int   phase        = 0;    // 0=curtain, 1=sprite, 2=title, 3=hold

    private boolean playerWon  = false;
    private String  winnerName = "";
    private String  loserName  = "";

    // Particles
    private static final int PARTICLE_COUNT = 60;
    private float[] px, py, pvx, pvy, palpha;
    private Color[] pcolor;
    private boolean particlesFired = false;

    private Timer    animTimer;
    private Runnable onComplete;

    // Fonts
    private Font titleFont;
    private Font subFont;
    private Font nameFont;

    public GameOverScreen() {
        setLayout(null);
        setOpaque(true);

        bgImage   = new ImageIcon(BG_PATH).getImage();

        titleFont = new Font("Impact", Font.PLAIN, 96);
        subFont   = new Font("Impact", Font.PLAIN, 42);
        nameFont  = new Font("Impact", Font.PLAIN, 32);

        // Pre-allocate particles
        px     = new float[PARTICLE_COUNT];
        py     = new float[PARTICLE_COUNT];
        pvx    = new float[PARTICLE_COUNT];
        pvy    = new float[PARTICLE_COUNT];
        palpha = new float[PARTICLE_COUNT];
        pcolor = new Color[PARTICLE_COUNT];
    }

    /**
     * Call this to trigger the animation.
     *
     * @param winnerCharName  character name of the winner (used to load sprite)
     * @param loserCharName   character name of the loser
     * @param playerWon       true if the human player won
     * @param afterAnimation  callback invoked after the screen finishes
     */
    public void show(String winnerCharName, String loserCharName,
                     boolean playerWon, Runnable afterAnimation) {

        this.winnerName  = winnerCharName;
        this.loserName   = loserCharName;
        this.playerWon   = playerWon;
        this.onComplete  = afterAnimation;

        winnerSprite = new ImageIcon(SPRITE_DIR + winnerCharName + SPRITE_SFX).getImage();

        // Reset state
        curtainAlpha    = 0f;
        titleAlpha      = 0f;
        subAlpha        = 0f;
        glowPulse       = 0f;
        particlesFired  = false;
        phase           = 0;

        // Sprite starts off-screen; winner always shows on left side
        spriteX       = -350f;
        spriteTargetX = getWidth() > 0 ? getWidth() * 0.08f : 60f;
        titleY        = -150f;
        titleTargetY  = getHeight() > 0 ? getHeight() * 0.50f : 300f;

        startAnimation();
        repaint();
    }

    private void startAnimation() {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();

        animTimer = new Timer(16, e -> {
            int w = getWidth();
            int h = getHeight();

            // Recalculate targets in case the panel was resized after show() was called
            spriteTargetX = w * 0.08f;
            titleTargetY  = h * 0.50f;

            glowPulse += 0.07f;

            switch (phase) {

                // Phase 0: fade in dark curtain
                case 0 -> {
                    curtainAlpha = Math.min(0.72f, curtainAlpha + 0.04f);
                    if (curtainAlpha >= 0.72f) phase = 1;
                }

                // Phase 1: slide sprite in
                case 1 -> {
                    spriteX += (spriteTargetX - spriteX) * 0.14f;
                    if (Math.abs(spriteX - spriteTargetX) < 2f) {
                        spriteX = spriteTargetX;
                        phase   = 2;
                    }
                }

                // Phase 2: slam title text down + fade sub-text
                case 2 -> {
                    titleY    += (titleTargetY - titleY) * 0.18f;
                    titleAlpha = Math.min(1f, titleAlpha + 0.10f);
                    subAlpha   = Math.min(1f, subAlpha   + 0.05f);

                    boolean titleSettled = Math.abs(titleY - titleTargetY) < 2f;

                    // Fire particles once title settles
                    if (titleSettled && !particlesFired) {
                        fireParticles(w / 2, (int)titleY, w, h);
                        particlesFired = true;
                    }

                    if (titleSettled && subAlpha >= 1f) {
                        phase = 3;
                        // Hold 2.2 s then invoke callback
                        Timer hold = new Timer(2200, ev -> {
                            animTimer.stop();
                            if (onComplete != null) onComplete.run();
                        });
                        hold.setRepeats(false);
                        hold.start();
                    }
                }
            }

            // Tick particles every frame regardless of phase
            tickParticles();

            repaint();
        });

        animTimer.start();
    }

    // ── Particles ─────────────────────────────────────────────────────────────
    private void fireParticles(int cx, int cy, int w, int h) {
        Color[] palette = playerWon
                ? new Color[]{ new Color(255,220,30), new Color(255,160,20),
                               new Color(255,255,180), new Color(200,255,100) }
                : new Color[]{ new Color(200,40,40),   new Color(255,80,80),
                               new Color(160,0,0),     new Color(100,0,0) };

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i]     = cx + (float)(Math.random() * 200 - 100);
            py[i]     = cy;
            double angle = Math.random() * Math.PI * 2;
            float  speed = (float)(Math.random() * 8 + 2);
            pvx[i]    = (float)(Math.cos(angle) * speed);
            pvy[i]    = (float)(Math.sin(angle) * speed) - 4f;  // slight upward bias
            palpha[i] = 1f;
            pcolor[i] = palette[(int)(Math.random() * palette.length)];
        }
    }

    private void tickParticles() {
        for (int i = 0; i < PARTICLE_COUNT; i++) {
            if (palpha[i] <= 0) continue;
            px[i]     += pvx[i];
            py[i]     += pvy[i];
            pvy[i]    += 0.25f; // gravity
            palpha[i] -= 0.018f;
        }
    }

    // ── Paint ─────────────────────────────────────────────────────────────────
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
            int sy = (int)(h * 0.15);
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

        // Title text ("YOU WIN" / "YOU LOSE")
        if (titleAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, titleAlpha));

            String titleText = playerWon ? "YOU WIN!" : "YOU LOSE";

            // Pulsing glow ring behind text
            float glow = 0.35f + 0.25f * (float)Math.sin(glowPulse);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, glow * titleAlpha));
            Color glowColor = playerWon ? new Color(255, 220, 30) : new Color(200, 30, 30);
            g2.setFont(titleFont);
            FontMetrics fmT = g2.getFontMetrics();
            int tx = (w - fmT.stringWidth(titleText)) / 2;
            int ty = (int)titleY;
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

            // Thin outline
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2f));
            g2.draw(new java.awt.font.TextLayout(titleText, titleFont, g2.getFontRenderContext())
                    .getOutline(java.awt.geom.AffineTransform.getTranslateInstance(tx, ty)));

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }
}