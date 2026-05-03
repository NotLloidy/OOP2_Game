package GUI.BattleScreens;

import Foundation.GameCharacter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Abstract base for all three battle screens.
 * Centralises asset paths, bar drawing, skill animation overlays,
 * button construction, and idle-sprite hiding during skill animations.
 */
public abstract class BaseBattleScreen extends JPanel {

    // ── Action button assets ──────────────────────────────────────────────
    public static final String BTN_FIGHT_PATH    = "Assets/battle_sprites/battle_buttons/actions/fight_btn.gif";
    public static final String BTN_DEFEND_PATH   = "Assets/battle_sprites/battle_buttons/actions/defend_btn.gif";
    public static final String BTN_CHECK_PATH    = "Assets/battle_sprites/battle_buttons/actions/check_btn.gif";
    public static final String BTN_BACK_PATH     = "Assets/battle_sprites/battle_buttons/actions/back_btn.gif";
    public static final String BTN_BACK_DISABLED = "Assets/battle_sprites/battle_buttons/actions/back_btn_disabled.png";

    // ── Skill button assets ───────────────────────────────────────────────
    public static final String BTN_SKILL1_ON  = "Assets/battle_sprites/battle_buttons/skills/active/skill1.png";
    public static final String BTN_SKILL2_ON  = "Assets/battle_sprites/battle_buttons/skills/active/skill2.png";
    public static final String BTN_SKILL3_ON  = "Assets/battle_sprites/battle_buttons/skills/active/skill3.png";
    public static final String BTN_SKILL1_OFF = "Assets/battle_sprites/battle_buttons/skills/inactive/skill1.png";
    public static final String BTN_SKILL2_OFF = "Assets/battle_sprites/battle_buttons/skills/inactive/skill2.png";
    public static final String BTN_SKILL3_OFF = "Assets/battle_sprites/battle_buttons/skills/inactive/skill3.png";

    // ── Block button assets ───────────────────────────────────────────────
    public static final String BTN_BLOCK2 = "Assets/battle_sprites/battle_buttons/block/block_btn2.png";
    public static final String BTN_BLOCK1 = "Assets/battle_sprites/battle_buttons/block/block_btn1.png";
    public static final String BTN_BLOCK0 = "Assets/battle_sprites/battle_buttons/block/block_btn0.png";

    // ── Sprite paths ──────────────────────────────────────────────────────
    public static final String IDLE_LEFT_DIR  = "Assets/character_related/idleAnimation/left/";
    public static final String IDLE_RIGHT_DIR = "Assets/character_related/idleAnimation/right/";
    public static final String IDLE_L_SFX     = "-left.gif";
    public static final String IDLE_R_SFX     = "-right.gif";
    public static final String BG_PATH        = "Assets/battle_sprites/battleArena.gif";
    public static final String ANIM_BASE      = "Assets/character_related/skillsAnimation/";

    // ── Button row layout constants ───────────────────────────────────────
    /** Target button height as fraction of panel height. */
    protected static final float BTN_H_FRAC = 0.080f;
    /** Minimum / maximum clamped button height in pixels. */
    protected static final int   BTN_H_MIN  = 40;
    protected static final int   BTN_H_MAX  = 75;
    /** Fixed aspect ratio applied to ALL buttons so they are uniform width.
     *  Based on the action button natural size (281x119 ≈ 2.36:1). */
    protected static final float BTN_ASPECT  = 281f / 119f;

    // ── Overlay labels for skill animations ───────────────────────────────
    protected JLabel playerAnimLabel;
    protected JLabel enemyAnimLabel;

    // ── Animation-active flags: hide idle sprite while skill animation plays ─
    protected volatile boolean playerAnimating = false;
    protected volatile boolean enemyAnimating  = false;

    // ── Fonts ─────────────────────────────────────────────────────────────
    private static final Font BAR_NAME_FONT = new Font("Impact",    Font.PLAIN, 15);
    private static final Font BAR_TEXT_FONT = new Font("SansSerif", Font.BOLD,  11);
    private static final Font WIN_FONT      = new Font("Impact",    Font.PLAIN, 20);

    // ── Button helpers ────────────────────────────────────────────────────

    /**
     * Creates a borderless image button. The icon is scaled to a consistent
     * height ({@link #computeButtonHeight()} px) while preserving aspect ratio.
     */
    protected JButton makeButton(String tooltip, String imagePath) {
        JButton btn = new JButton();
        if (imagePath != null) {
            applyScaledIcon(btn, imagePath);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setToolTipText(tooltip);
        } else {
            btn.setText(tooltip);
            btn.setPreferredSize(new Dimension(100, computeButtonHeight()));
        }
        return btn;
    }

    /**
     * Swaps the icon on an existing button, scaling it to the consistent
     * button height so all buttons in a row share the same height.
     */
    protected void setButtonLabel(JButton btn, String tooltip, String imagePath) {
        if (imagePath != null) {
            applyScaledIcon(btn, imagePath);
            btn.setToolTipText(tooltip);
        } else {
            btn.setIcon(null);
            btn.setText(tooltip);
            btn.setPreferredSize(new Dimension(100, computeButtonHeight()));
        }
    }

    /**
     * Scales an icon to the target button height (preserving aspect ratio)
     * and applies it to the button.
     */
    protected void applyScaledIcon(JButton btn, String imagePath) {
        ImageIcon raw = new ImageIcon(imagePath);
        int targetH = computeButtonHeight();
        int targetW = (int)(targetH * BTN_ASPECT);
        if (raw.getIconWidth() <= 0 || raw.getIconHeight() <= 0) {
            btn.setIcon(raw);
            btn.setPreferredSize(new Dimension(targetW, targetH));
            return;
        }
        Image scaled = raw.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
        btn.setIcon(new ImageIcon(scaled));
        btn.setPreferredSize(new Dimension(targetW, targetH));
    }

    /**
     * Same as applyScaledIcon but returns the icon for use as a
     * disabled icon — forced to the same uniform size.
     */
    protected ImageIcon makeScaledIcon(String imagePath) {
        ImageIcon raw = new ImageIcon(imagePath);
        int targetH = computeButtonHeight();
        int targetW = (int)(targetH * BTN_ASPECT);
        if (raw.getIconWidth() <= 0 || raw.getIconHeight() <= 0) return raw;
        Image scaled = raw.getImage().getScaledInstance(targetW, targetH, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Computes the current target button height from the panel size.
     * Falls back to BTN_H_MIN when the panel has not been laid out yet.
     */
    protected int computeButtonHeight() {
        int h = getHeight();
        if (h <= 0) return BTN_H_MIN;
        return Math.min(BTN_H_MAX, Math.max(BTN_H_MIN, (int)(h * BTN_H_FRAC)));
    }

    /**
     * Positions a button at (x, y) using its preferred size (which has already
     * been set to the scaled icon dimensions by {@link #applyScaledIcon}).
     */
    protected void sizeToIcon(JButton btn, int x, int y) {
        Dimension d = btn.getPreferredSize();
        int bw = (d != null && d.width  > 0) ? d.width  : 100;
        int bh = (d != null && d.height > 0) ? d.height : computeButtonHeight();
        btn.setBounds(x, y, bw, bh);
    }

    protected void clearListeners(JButton btn) {
        for (ActionListener al : btn.getActionListeners()) btn.removeActionListener(al);
    }

    protected String blockPath(int remaining) {
        if (remaining >= 2) return BTN_BLOCK2;
        if (remaining == 1) return BTN_BLOCK1;
        return BTN_BLOCK0;
    }

    // ── Stat bar renderer ─────────────────────────────────────────────────

    protected void drawBars(Graphics g, GameCharacter c, int x, int y, int barW) {
        if (c == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        final int BAR_H = 14;
        final int GAP   = 3;

        g2.setFont(BAR_NAME_FONT);
        g2.setColor(new Color(255, 220, 30));
        g2.drawString(c.getCharacterName(), x, y);
        y += 5;

        int   hp    = c.getCharacterCurrentHealthPoints();
        int   maxHp = c.getCharacterMaxHealthPoints();
        float hpPct = Math.max(0, (float) hp / maxHp);

        g2.setColor(new Color(40, 0, 0, 200));
        g2.fillRoundRect(x, y, barW, BAR_H, 6, 6);
        Color hpColor = hpPct > 0.50f ? new Color(50, 200, 50)
                      : hpPct > 0.25f ? new Color(220, 150, 0)
                                      : new Color(210, 40,  40);
        g2.setColor(hpColor);
        g2.fillRoundRect(x, y, (int)(barW * hpPct), BAR_H, 6, 6);
        g2.setFont(BAR_TEXT_FONT);
        g2.setColor(Color.WHITE);
        g2.drawString("HP " + hp + "/" + maxHp, x + 3, y + BAR_H - 2);
        y += BAR_H + GAP;

        int   mp    = c.getCharacterCurrentMana();
        int   maxMp = c.getCharacterMaxMana();
        float mpPct = Math.max(0, (float) mp / maxMp);

        g2.setColor(new Color(0, 0, 50, 200));
        g2.fillRoundRect(x, y, barW, BAR_H, 6, 6);
        g2.setColor(new Color(40, 100, 255));
        g2.fillRoundRect(x, y, (int)(barW * mpPct), BAR_H, 6, 6);
        g2.setFont(BAR_TEXT_FONT);
        g2.setColor(Color.WHITE);
        g2.drawString("MP " + mp + "/" + maxMp, x + 3, y + BAR_H - 2);
    }

    protected void drawWinCounter(Graphics g, String left, int lWins, int rWins,
                                  String right, int cx, int cy) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setFont(WIN_FONT);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        String text = left + "  " + lWins + " - " + rWins + "  " + right;
        FontMetrics fm = g2.getFontMetrics();
        int tx = cx - fm.stringWidth(text) / 2;
        g2.setColor(new Color(0, 0, 0, 160));
        g2.drawString(text, tx + 2, cy + 2);
        g2.setColor(new Color(255, 220, 30));
        g2.drawString(text, tx, cy);
    }

    // ── Skill animation helpers ───────────────────────────────────────────

    protected String getSkillAnimPath(String spriteKey, int skillNum, boolean isLeft) {
        String dir = isLeft ? "left" : "right";
        String sfx = isLeft ? "L" : "R";
        return ANIM_BASE + dir + "/" + spriteKey + "Skill" + skillNum + sfx + ".gif";
    }

    /**
     * Scales a skill animation GIF to fit within the panel, centres it on the
     * character's sprite position, and clamps it so it never goes off-screen.
     *
     * @param cx  horizontal centre of the character's sprite (pixels)
     * @param cy  vertical centre of the character's sprite (pixels)
     */
    private void placeAnimLabel(JLabel label, String path, int cx, int cy) {
        ImageIcon raw = new ImageIcon(path);
        int natW = raw.getIconWidth();
        int natH = raw.getIconHeight();
        if (natW <= 0 || natH <= 0) { label.setIcon(raw); return; }

        int panelW = getWidth();
        int panelH = getHeight();

        // Max display size: 55% of panel width, 70% of panel height
        int maxW = (int)(panelW * 0.55f);
        int maxH = (int)(panelH * 0.70f);

        // Scale proportionally to fit within the cap
        float scale = Math.min((float) maxW / natW, (float) maxH / natH);
        int dispW = (int)(natW * scale);
        int dispH = (int)(natH * scale);

        // Scale the icon so the JLabel renders it at the right size
        Image scaled = raw.getImage().getScaledInstance(dispW, dispH, Image.SCALE_DEFAULT);
        label.setIcon(new ImageIcon(scaled));

        // Centre on the character, then clamp inside panel bounds
        int x = cx - dispW / 2;
        int y = cy - dispH / 2;
        x = Math.max(0, Math.min(x, panelW - dispW));
        y = Math.max(0, Math.min(y, panelH - dispH));

        label.setBounds(x, y, dispW, dispH);
    }

    protected void showPlayerSkillAnim(String spriteKey, int skillNum) {
        if (playerAnimLabel == null) return;
        playerAnimating = true;
        String path = getSkillAnimPath(spriteKey, skillNum, true);
        int cx = playerCharCenterX();
        int cy = playerCharCenterY();
        placeAnimLabel(playerAnimLabel, path, cx, cy);
        playerAnimLabel.setVisible(true);
        repaint();
        Timer t = new Timer(1500, e -> {
            playerAnimating = false;
            playerAnimLabel.setVisible(false);
            repaint();
        });
        t.setRepeats(false);
        t.start();
    }

    protected void showEnemySkillAnim(String spriteKey, int skillNum) {
        if (enemyAnimLabel == null) return;
        enemyAnimating = true;
        String path = getSkillAnimPath(spriteKey, skillNum, false);
        int cx = enemyCharCenterX();
        int cy = enemyCharCenterY();
        placeAnimLabel(enemyAnimLabel, path, cx, cy);
        enemyAnimLabel.setVisible(true);
        repaint();
        Timer t = new Timer(1500, e -> {
            enemyAnimating = false;
            enemyAnimLabel.setVisible(false);
            repaint();
        });
        t.setRepeats(false);
        t.start();
    }

    /** Override in each subclass to return the pixel centre of the player sprite. */
    protected int playerCharCenterX() { return getWidth()  / 4; }
    protected int playerCharCenterY() { return getHeight() / 2; }
    protected int enemyCharCenterX()  { return getWidth()  * 3 / 4; }
    protected int enemyCharCenterY()  { return getHeight() / 2; }
}