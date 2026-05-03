package GUI.BattleScreens;

import javax.swing.*;
import java.awt.*;

/**
 * VersusScreen — animated character-reveal screen shown before every battle.
 *
 * Characters slide in from opposite sides, VS logo fades in, then the
 * completion callback fires after a short hold.
 *
 * Animation runs on a dedicated background Thread.
 * All Swing state mutations are dispatched through SwingUtilities.invokeLater
 * so the EDT is never blocked and never accessed from the wrong thread.
 */
public class VersusScreen extends JPanel {

    // ── Asset paths ───────────────────────────────────────────────────────
    private static final String LEFT_SPRITE_PATH    = "Assets/character_related/idleAnimation/left/";
    private static final String RIGHT_SPRITE_PATH   = "Assets/character_related/idleAnimation/right/";
    private static final String LEFT_SPRITE_SUFFIX  = "-left.gif";
    private static final String RIGHT_SPRITE_SUFFIX = "-right.gif";
    private static final String BG_IMAGE_PATH       = "Assets/battle_sprites/versusBackGround.gif";
    private static final String VS_OVERLAY_PATH     = "Assets/battle_sprites/versusLogo.gif";

    // ── Animation timing ──────────────────────────────────────────────────
    /** Target frame time in ms (~60 fps). */
    private static final int FRAME_MS  = 16;
    /** How long to hold the completed screen before firing onComplete (ms). */
    private static final int HOLD_MS   = 1500;

    // ── Images ────────────────────────────────────────────────────────────
    private Image bgImage;
    private Image vsOverlay;
    private Image leftSprite;
    private Image rightSprite;

    // ── Animation state — only written by the anim thread via invokeLater ─
    private volatile float   leftX     = -500f;
    private volatile float   rightX    = 1800f;
    private volatile float   vsAlpha   = 0f;
    private volatile float   nameAlpha = 0f;

    private String leftDisplayName  = "";
    private String rightDisplayName = "";

    // Natural sprite dimensions (pixels)
    private int leftNatW  = 200, leftNatH  = 300;
    private int rightNatW = 200, rightNatH = 300;

    private Font nameFont;
    private Font vsFont;

    // ── Thread management ─────────────────────────────────────────────────
    /** The currently running animation thread, or null if none. */
    private Thread animThread;
    /** Set to true to ask the running thread to stop early (e.g. on show() re-entry). */
    private volatile boolean stopRequested = false;

    private Runnable onComplete;

    public VersusScreen() {
        setLayout(null);
        setOpaque(true);

        bgImage   = new ImageIcon(BG_IMAGE_PATH).getImage();
        vsOverlay = loadOptional(VS_OVERLAY_PATH);
        nameFont  = new Font("Impact", Font.PLAIN, 42);
        vsFont    = new Font("Impact", Font.PLAIN, 110);
    }

    // ── Public API ────────────────────────────────────────────────────────

    /**
     * Starts the versus animation for the given characters, then invokes
     * {@code afterAnimation} on the EDT once the hold period ends.
     *
     * Safe to call from the EDT or any other thread.
     */
    public void show(String player1Name, String player2Name, Runnable afterAnimation) {
        // Stop any animation already in flight
        stopCurrentThread();

        this.leftDisplayName  = player1Name;
        this.rightDisplayName = player2Name;
        this.onComplete       = afterAnimation;

        // Load sprites and measure them on a loader thread so MediaTracker
        // blocking never stalls the EDT.
        Thread loaderThread = new Thread(() -> {
            String leftKey  = toFileKey(player1Name);
            String rightKey = toFileKey(player2Name);

            Image ls = new ImageIcon(LEFT_SPRITE_PATH  + leftKey  + LEFT_SPRITE_SUFFIX ).getImage();
            Image rs = new ImageIcon(RIGHT_SPRITE_PATH + rightKey + RIGHT_SPRITE_SUFFIX).getImage();

            // Block until images are fully loaded so we get correct dimensions
            MediaTracker mt = new MediaTracker(VersusScreen.this);
            mt.addImage(ls, 0);
            mt.addImage(rs, 1);
            try { mt.waitForAll(); } catch (InterruptedException ignored) { return; }

            final int lw = Math.max(1, ls.getWidth(null));
            final int lh = Math.max(1, ls.getHeight(null));
            final int rw = Math.max(1, rs.getWidth(null));
            final int rh = Math.max(1, rs.getHeight(null));

            // Push all state onto the EDT, then kick off the anim thread
            SwingUtilities.invokeLater(() -> {
                leftSprite  = ls;
                rightSprite = rs;
                leftNatW = lw;  leftNatH = lh;
                rightNatW = rw; rightNatH = rh;

                // Reset animation positions before the thread starts
                int panelW = getWidth();
                leftX    = -(lw + 100f);
                rightX   = panelW + rw + 100f;
                vsAlpha  = 0f;
                nameAlpha = 0f;

                repaint();
                startAnimThread();
            });
        }, "VersusScreen-Loader");

        loaderThread.setDaemon(true);
        loaderThread.start();
    }

    // ── Thread helpers ────────────────────────────────────────────────────

    /** Signals the current animation thread to stop and waits for it to exit. */
    private void stopCurrentThread() {
        stopRequested = true;
        if (animThread != null && animThread.isAlive()) {
            animThread.interrupt();
            // Give it a moment to exit; we don't need to join hard here
            // because the thread is daemon and checks stopRequested each frame.
        }
        animThread    = null;
        stopRequested = false;
    }

    /**
     * Spawns the animation thread.
     * Must be called on the EDT (after image state has been set).
     */
    private void startAnimThread() {
        stopRequested = false;

        animThread = new Thread(() -> {
            try {
                runAnimationLoop();
            } catch (InterruptedException e) {
                // Normal exit path when stopCurrentThread() is called
                Thread.currentThread().interrupt();
            }
        }, "VersusScreen-Anim");

        animThread.setDaemon(true);
        animThread.start();
    }

    /**
     * The animation loop — runs entirely on the animation thread.
     * Reads panel dimensions and mutates animation state, then posts a
     * repaint request to the EDT each frame.
     *
     * @throws InterruptedException propagated so the caller can clean up
     */
    private void runAnimationLoop() throws InterruptedException {

        while (!stopRequested) {
            long frameStart = System.currentTimeMillis();

            // ── Snapshot panel dimensions (safe to read from any thread) ──
            int w = getWidth();
            int h = getHeight();

            // Sprite display sizes — fixed height, aspect-correct width
            int dispH      = (int)(h * 0.72);
            int leftDispW  = (leftNatH  > 0) ? dispH * leftNatW  / leftNatH  : dispH;
            int rightDispW = (rightNatH > 0) ? dispH * rightNatW / rightNatH : dispH;

            // Target X positions — left char in left half, right char in right half
            float leftTarget  = (float)(w / 2 - leftDispW)  * 0.10f + w * 0.02f;
            float rightTarget = (float)(w / 2 - rightDispW) * 0.14f + w * 0.02f;

            // ── Step animation state ───────────────────────────────────────
            final float newLeftX  = leftX  + (leftTarget  - leftX)  * 0.12f;
            final float newRightX = rightX + (rightTarget - rightX) * 0.12f;

            boolean charsSettled = Math.abs(newLeftX  - leftTarget)  < 2f
                                && Math.abs(newRightX - rightTarget) < 2f;

            final float newVsAlpha   = charsSettled ? Math.min(1f, vsAlpha   + 0.06f) : vsAlpha;
            final float newNameAlpha = charsSettled ? Math.min(1f, nameAlpha + 0.04f) : nameAlpha;

            boolean fullyDone = charsSettled && newVsAlpha >= 1f && newNameAlpha >= 1f;

            // ── Post state update + repaint to the EDT ────────────────────
            SwingUtilities.invokeLater(() -> {
                leftX     = newLeftX;
                rightX    = newRightX;
                vsAlpha   = newVsAlpha;
                nameAlpha = newNameAlpha;
                repaint();
            });

            if (fullyDone) {
                // Animation complete — hold for HOLD_MS then fire callback
                Thread.sleep(HOLD_MS);
                if (!stopRequested) {
                    SwingUtilities.invokeLater(() -> {
                        if (onComplete != null) onComplete.run();
                    });
                }
                return; // exit the loop naturally
            }

            // ── Sleep for the remainder of the frame ──────────────────────
            long elapsed = System.currentTimeMillis() - frameStart;
            long sleepMs = FRAME_MS - elapsed;
            if (sleepMs > 0) Thread.sleep(sleepMs);
        }
    }

    // ── Paint ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,     RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        int w = getWidth(), h = getHeight();

        // Background
        if (bgImage != null) g2.drawImage(bgImage, 0, 0, w, h, this);
        else { g2.setColor(Color.BLACK); g2.fillRect(0, 0, w, h); }

        // Subtle dark vignette
        GradientPaint grad = new GradientPaint(0, 0, new Color(0,0,0,60), w, 0, new Color(0,0,0,60));
        g2.setPaint(grad);
        g2.fillRect(0, 0, w, h);

        int dispH      = (int)(h * 0.72);
        int spriteY    = (int)(h * 0.05);
        int leftDispW  = (leftNatH  > 0) ? dispH * leftNatW  / leftNatH  : dispH;
        int rightDispW = (rightNatH > 0) ? dispH * rightNatW / rightNatH : dispH;

        // Left character
        if (leftSprite != null)
            g2.drawImage(leftSprite,  (int)leftX,  spriteY, leftDispW,  dispH, this);

        // Right character
        if (rightSprite != null)
            g2.drawImage(rightSprite, (int)rightX, spriteY, rightDispW, dispH, this);

        // VS overlay / fallback text
        if (vsAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, vsAlpha));

            if (vsOverlay != null) {
                int ovW = (int)(w * 0.20), ovH = (int)(h * 0.28);
                g2.drawImage(vsOverlay, (w - ovW) / 2, (h - ovH) / 2 - 30, ovW, ovH, this);
            } else {
                g2.setFont(vsFont);
                FontMetrics fm = g2.getFontMetrics();
                String vs = "VS";
                int tx = (w - fm.stringWidth(vs)) / 2;
                int ty = h / 2 + fm.getAscent() / 2 - 20;
                g2.setColor(new Color(0, 0, 0, 180)); g2.drawString(vs, tx + 4, ty + 4);
                g2.setColor(new Color(255, 220, 30)); g2.drawString(vs, tx, ty);
            }

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // Name plates
        if (nameAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, nameAlpha));
            int plateH = (int)(h * 0.11);
            int plateY = (int)(h * 0.82);
            drawNamePlate(g2, leftDisplayName,  0,     plateY, w / 2, plateH, true);
            drawNamePlate(g2, rightDisplayName, w / 2, plateY, w / 2, plateH, false);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private void drawNamePlate(Graphics2D g2, String name,
                                int px, int py, int pw, int ph, boolean leftAlign) {
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(px, py, pw, ph);

        g2.setColor(new Color(255, 200, 20));
        g2.fillRect(px, py, pw, 3);

        g2.setFont(nameFont);
        FontMetrics fm = g2.getFontMetrics();

        Font f = nameFont;
        while (fm.stringWidth(name) > pw - 30 && f.getSize() > 16) {
            f  = f.deriveFont((float)(f.getSize() - 2));
            fm = g2.getFontMetrics(f);
        }
        g2.setFont(f);
        fm = g2.getFontMetrics();

        int tx = leftAlign
                ? px + 18
                : px + pw - fm.stringWidth(name) - 18;
        int ty = py + (ph + fm.getAscent()) / 2 - 4;

        g2.setColor(new Color(0, 0, 0, 200)); g2.drawString(name, tx + 3, ty + 3);
        g2.setColor(Color.WHITE);              g2.drawString(name, tx,     ty);
    }

    // ── Utilities ─────────────────────────────────────────────────────────

    private String toFileKey(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private Image loadOptional(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) return icon.getImage();
        } catch (Exception ignored) {}
        return null;
    }
}