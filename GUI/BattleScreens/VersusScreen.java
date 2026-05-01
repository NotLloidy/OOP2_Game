package GUI.BattleScreens;

import Foundation.GameCharacter;
import GameEngines.BattleSystem;
import GameEngines.GameSession;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * VersusScreen — animated character-reveal screen shown before every battle.
 *
 * Characters slide in from opposite sides, VS logo fades in, then the
 * completion callback fires after a short hold.
 *
 * Sprites are rendered at a fixed HEIGHT (72 % of the panel height) while
 * their WIDTH is computed from the image's natural aspect ratio, so no
 * character is ever squished or stretched.
 */
public class VersusScreen extends JPanel {

    // ── Asset paths ──────────────────────────────────────────────────────
    private static final String LEFT_SPRITE_PATH    = "Assets/character_related/idleAnimation/left/";
    private static final String RIGHT_SPRITE_PATH   = "Assets/character_related/idleAnimation/right/";
    private static final String LEFT_SPRITE_SUFFIX  = "-left.gif";
    private static final String RIGHT_SPRITE_SUFFIX = "-right.gif";
    private static final String BG_IMAGE_PATH       = "Assets/battle_sprites/versusBackGround.gif";
    private static final String VS_OVERLAY_PATH     = "Assets/battle_sprites/versusLogo.gif";

    private Image bgImage;
    private Image vsOverlay;
    private Image leftSprite;
    private Image rightSprite;

    // Animation state
    private float leftX   = -500f;
    private float rightX  = 1800f;
    private float vsAlpha = 0f;
    private float nameAlpha = 0f;
    private boolean animDone = false;

    // The actual character names (for name plates)
    private String leftDisplayName  = "";
    private String rightDisplayName = "";

    private Timer animTimer;
    private Runnable onComplete;

    // Computed natural sprite sizes (pixels) – set in show()
    private int leftNatW  = 200, leftNatH  = 300;
    private int rightNatW = 200, rightNatH = 300;

    private Font nameFont;
    private Font vsFont;

    public VersusScreen() {
        setLayout(null);
        setOpaque(true);
        bgImage   = new ImageIcon(BG_IMAGE_PATH).getImage();
        vsOverlay = loadOptional(VS_OVERLAY_PATH);

        nameFont = new Font("Impact", Font.PLAIN, 42);
        vsFont   = new Font("Impact", Font.PLAIN, 110);
    }

    private String toFileKey(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    // ── Public API ───────────────────────────────────────────────────────

    /** Shows the screen, animates both characters in, then invokes {@code afterAnimation}. */
    public void show(String player1Name, String player2Name, Runnable afterAnimation) {
        this.leftDisplayName  = player1Name;
        this.rightDisplayName = player2Name;

        this.onComplete       = afterAnimation;
        this.animDone         = false;

        String leftKey  = toFileKey(player1Name);
        String rightKey = toFileKey(player2Name);

        leftSprite  = new ImageIcon(LEFT_SPRITE_PATH  + leftKey  + LEFT_SPRITE_SUFFIX ).getImage();
        rightSprite = new ImageIcon(RIGHT_SPRITE_PATH + rightKey + RIGHT_SPRITE_SUFFIX).getImage();

        // Wait for images so we get accurate natural dimensions
        MediaTracker mt = new MediaTracker(this);
        mt.addImage(leftSprite,  0);
        mt.addImage(rightSprite, 1);
        try { mt.waitForAll(); } catch (InterruptedException ignored) {}

        leftNatW  = Math.max(1, leftSprite .getWidth(null));
        leftNatH  = Math.max(1, leftSprite .getHeight(null));
        rightNatW = Math.max(1, rightSprite.getWidth(null));
        rightNatH = Math.max(1, rightSprite.getHeight(null));

        // Reset animation positions
        leftX    = -leftNatW  - 100f;
        rightX   = getWidth() + rightNatW + 100f;
        vsAlpha  = 0f;
        nameAlpha = 0f;

        startAnimation();
        repaint();
    }

    // ── Animation ────────────────────────────────────────────────────────

    private void startAnimation() {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();

        animTimer = new Timer(16, e -> {
            int w = getWidth(), h = getHeight();

            int dispH = (int)(h * 0.72);
            // Cap each sprite width to 45% of screen so wide sprites don't overflow their half
            int maxHalfW   = (int)(w * 0.45f);
            int rawLeftW   = dispH * leftNatW  / leftNatH;
            int rawRightW  = dispH * rightNatW / rightNatH;
            int leftDispW  = Math.min(rawLeftW,  maxHalfW);
            int rightDispW = Math.min(rawRightW, maxHalfW);

            // Left character: center in the left 50% of the screen
            float leftCenterX  = w * 0.25f;
            float leftTarget   = leftCenterX - leftDispW * 0.5f;
            // Never let left sprite start off-screen left
            leftTarget = Math.max(leftTarget, w * 0.02f);

            // Right character: center in the right 50% of the screen
            float rightCenterX = w * 0.75f;
            float rightTarget  = rightCenterX - rightDispW * 0.5f;
            // Never let right sprite go off-screen right
            rightTarget = Math.min(rightTarget, w - rightDispW - w * 0.02f);

            leftX  += (leftTarget  - leftX)  * 0.12f;
            rightX += (rightTarget - rightX) * 0.12f;

            boolean leftSettled  = Math.abs(leftX  - leftTarget)  < 2f;
            boolean rightSettled = Math.abs(rightX - rightTarget) < 2f;

            if (leftSettled && rightSettled) {
                vsAlpha   = Math.min(1f, vsAlpha   + 0.06f);
                nameAlpha = Math.min(1f, nameAlpha + 0.04f);
            }

            if (Math.abs(rightX - rightTarget) >= 2f) {
                rightX += (rightTarget - rightX) * 0.12f;
            }

            repaint();

            if (leftSettled && rightSettled && vsAlpha >= 1f && nameAlpha >= 1f && !animDone) {
                animDone = true;
                animTimer.stop();
                Timer hold = new Timer(1500, ev -> { if (onComplete != null) onComplete.run(); });
                hold.setRepeats(false);
                hold.start();
            }
        });
        animTimer.start();
    }

    // ── Paint ────────────────────────────────────────────────────────────

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

        // Sprite dimensions — fixed height, aspect-ratio-correct width
        int dispH     = (int)(h * 0.72);
        int spriteY   = (int)(h * 0.05);
        int maxHalfW   = (int)(w * 0.45f);
        int leftDispW  = Math.min(dispH * leftNatW  / leftNatH, maxHalfW);
        int rightDispW = Math.min(dispH * rightNatW / rightNatH, maxHalfW);

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

        // Name plates (use actual display names, not file keys)
        if (nameAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, nameAlpha));
            int plateH = (int)(h * 0.11);
            int plateY = (int)(h * 0.82);
            drawNamePlate(g2, leftDisplayName,  0,     plateY, w / 2, plateH, true);
            drawNamePlate(g2, rightDisplayName, w / 2, plateY, w / 2, plateH, false);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private void drawNamePlate(Graphics2D g2, String name, int px, int py, int pw, int ph, boolean leftAlign) {
        // Dark backing bar
        g2.setColor(new Color(0, 0, 0, 170));
        g2.fillRect(px, py, pw, ph);

        // Gold accent stripe
        g2.setColor(new Color(255, 200, 20));
        g2.fillRect(px, py, pw, 3);

        g2.setFont(nameFont);
        FontMetrics fm = g2.getFontMetrics();

        // Scale font down if name is too wide
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

        // Shadow
        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(name, tx + 3, ty + 3);
        // White text
        g2.setColor(Color.WHITE);
        g2.drawString(name, tx, ty);
    }

    private Image loadOptional(String path) {
        try {
            ImageIcon icon = new ImageIcon(path);
            if (icon.getImageLoadStatus() == MediaTracker.COMPLETE) return icon.getImage();
        } catch (Exception ignored) {}
        return null;
    }
}