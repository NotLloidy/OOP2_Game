package GUI.BattleScreens;

import Foundation.GameCharacter;
import GameEngines.BattleSystem;
import GameEngines.GameSession;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

/**
 * VersusScreen — Tekken-style character reveal screen.
 *
 * SPRITE ASSET PATHS — change these constants to point to your files:
 *   LEFT_SPRITE_PATH  = "Assets/characters_left/"  + name + "-left.gif"
 *   RIGHT_SPRITE_PATH = "Assets/characters_right/" + name + "-right.gif"
 *   BG_IMAGE_PATH     = "Assets/battle_sprites/versusBackGround.gif"
 *   VS_OVERLAY_PATH   = "Assets/battle_sprites/versusLogo.png"      (optional, drawn if found)
 *
 * Usage:
 *   Call show(playerName, enemyName, onDone) — animates in, then calls onDone.
 */
public class VersusScreen extends JPanel {

    // ── ASSET PATHS ─────────────────────────────────────────────────────────
    private static final String LEFT_SPRITE_PATH  = "Assets/character_related/idleAnimation/left/";
    private static final String RIGHT_SPRITE_PATH = "Assets/character_related/idleAnimation/right/";
    private static final String LEFT_SPRITE_SUFFIX  = "-left.gif";
    private static final String RIGHT_SPRITE_SUFFIX = "-right.gif";
    private static final String BG_IMAGE_PATH     = "Assets/battle_sprites/versusBackGround.gif";
    private static final String VS_OVERLAY_PATH   = "Assets/battle_sprites/versusLogo.png";
    // ────────────────────────────────────────────────────────────────────────

    private Image bgImage;
    private Image vsOverlay;
    private Image leftSprite;
    private Image rightSprite;

    // Animation state
    private float leftX    = -400f;   // slides in from left
    private float rightX   = 1400f;   // slides in from right
    private float vsAlpha  = 0f;      // VS text fades in
    private float nameAlpha= 0f;
    private boolean animDone = false;

    private String leftName  = "";
    private String rightName = "";

    private Timer animTimer;
    private Runnable onComplete;

    // Fonts
    private Font nameFont;
    private Font vsFont;

    public VersusScreen() {
        setLayout(null);
        setOpaque(true);

        bgImage   = new ImageIcon(BG_IMAGE_PATH).getImage();
        vsOverlay = loadOptional(VS_OVERLAY_PATH);

        try {
            // Use a bold display font if available, fall back gracefully
            nameFont = new Font("Impact", Font.PLAIN, 48);
            vsFont   = new Font("Impact", Font.PLAIN, 120);
        } catch (Exception ex) {
            nameFont = new Font(Font.SANS_SERIF, Font.BOLD, 48);
            vsFont   = new Font(Font.SANS_SERIF, Font.BOLD, 120);
        }
    }

    private String toFileKey(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    /** Call this every time you want to show the screen. */
    public void show(String player1Name, String player2Name, Runnable afterAnimation) {
        this.leftName    = toFileKey(player1Name);
        this.rightName   = toFileKey(player2Name);
        this.onComplete  = afterAnimation;
        this.animDone    = false;

        leftSprite  = new ImageIcon(LEFT_SPRITE_PATH  + leftName + LEFT_SPRITE_SUFFIX).getImage();
        rightSprite = new ImageIcon(RIGHT_SPRITE_PATH + rightName + RIGHT_SPRITE_SUFFIX).getImage();

        leftX   = -400f;
        rightX  = getWidth() + 400f;
        vsAlpha = 0f;
        nameAlpha = 0f;

        startAnimation();
        repaint();
    }

    private void startAnimation() {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();

        animTimer = new Timer(16, e -> {
            int w = getWidth();

            // Slide characters to their target X
            float leftTarget  = w * 0.05f;
            float rightTarget = w * 0.45f;

            leftX  += (leftTarget  - leftX)  * 0.12f;
            rightX += (rightTarget - rightX) * 0.12f;

            boolean charsSettled = Math.abs(leftX - leftTarget) < 2f
                    && Math.abs(rightX - rightTarget) < 2f;

            if (charsSettled) {
                vsAlpha   = Math.min(1f, vsAlpha + 0.06f);
                nameAlpha = Math.min(1f, nameAlpha + 0.04f);
            }

            repaint();

            if (charsSettled && vsAlpha >= 1f && nameAlpha >= 1f && !animDone) {
                animDone = true;
                animTimer.stop();
                // Hold 1.5 s then call the completion callback
                Timer holdTimer = new Timer(1500, ev -> {
                    if (onComplete != null) onComplete.run();
                });
                holdTimer.setRepeats(false);
                holdTimer.start();
            }
        });
        animTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Background
        if (bgImage != null)
            g2.drawImage(bgImage, 0, 0, w, h, this);
        else {
            g2.setColor(Color.BLACK);
            g2.fillRect(0, 0, w, h);
        }

        // Dark overlay gradient for dramatic effect
        GradientPaint grad = new GradientPaint(
                0, 0, new Color(0,0,0,80),
                w, 0, new Color(0,0,0,80)
        );
        g2.setPaint(grad);
        g2.fillRect(0, 0, w, h);

        int spriteW = (int)(w * 0.38);
        int spriteH = (int)(h * 0.75);
        int spriteY = (int)(h * 0.10);

        // Left character sprite
        if (leftSprite != null)
            g2.drawImage(leftSprite, (int)leftX, spriteY, spriteW, spriteH, this);

        // Right character sprite
        if (rightSprite != null)
            g2.drawImage(rightSprite, (int)rightX, spriteY, spriteW, spriteH, this);

        // VS overlay (optional asset)
        if (vsOverlay != null && vsAlpha > 0) {
            AlphaComposite ac = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, vsAlpha);
            g2.setComposite(ac);
            int ovW = (int)(w * 0.22), ovH = (int)(h * 0.30);
            g2.drawImage(vsOverlay, (w - ovW) / 2, (h - ovH) / 2 - 20, ovW, ovH, this);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // VS text (drawn when no overlay asset found)
        if (vsOverlay == null && vsAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, vsAlpha));
            g2.setFont(vsFont);
            FontMetrics fm = g2.getFontMetrics();
            String vs = "VS";
            int tx = (w - fm.stringWidth(vs)) / 2;
            int ty = h / 2 + fm.getAscent() / 2;
            // Shadow
            g2.setColor(new Color(0, 0, 0, 180));
            g2.drawString(vs, tx + 4, ty + 4);
            // Fill
            g2.setColor(new Color(255, 220, 30));
            g2.drawString(vs, tx, ty);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }

        // Character name plates
        if (nameAlpha > 0) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, nameAlpha));
            drawNamePlate(g2, leftName,  0,       (int)(h * 0.82), w / 2,     (int)(h * 0.12), true);
            drawNamePlate(g2, rightName, w / 2,   (int)(h * 0.82), w / 2,     (int)(h * 0.12), false);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }

    private void drawNamePlate(Graphics2D g2, String name, int px, int py, int pw, int ph, boolean leftAlign) {
        // Semi-transparent backing bar
        g2.setColor(new Color(0, 0, 0, 160));
        g2.fillRect(px, py, pw, ph);

        // Gold accent line
        g2.setColor(new Color(255, 200, 20));
        g2.fillRect(px, py, pw, 3);

        g2.setFont(nameFont);
        FontMetrics fm = g2.getFontMetrics();
        int tx = leftAlign
                ? px + 20
                : px + pw - fm.stringWidth(name) - 20;
        int ty = py + (ph + fm.getAscent()) / 2 - 4;

        // Drop shadow
        g2.setColor(new Color(0, 0, 0, 200));
        g2.drawString(name, tx + 3, ty + 3);
        // White name
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