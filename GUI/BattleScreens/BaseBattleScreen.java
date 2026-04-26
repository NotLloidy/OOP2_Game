package GUI.BattleScreens;

import Foundation.GameCharacter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

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
     * Creates a borderless image button whose bounds exactly match the image's
     * natural pixel dimensions (no stretching or clipping).
     */
    protected JButton makeButton(String tooltip, String imagePath) {
        JButton btn = new JButton();
        if (imagePath != null) {
            ImageIcon icon = new ImageIcon(imagePath);
            btn.setIcon(icon);
            btn.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setToolTipText(tooltip);
        } else {
            btn.setText(tooltip);
        }
        return btn;
    }

    /**
     * Swaps the icon on an existing button and refreshes its preferred size
     * so the next layoutUI() call sizes it correctly.
     */
    protected void setButtonLabel(JButton btn, String tooltip, String imagePath) {
        if (imagePath != null) {
            ImageIcon icon = new ImageIcon(imagePath);
            btn.setIcon(icon);
            btn.setPreferredSize(new Dimension(icon.getIconWidth(), icon.getIconHeight()));
            btn.setToolTipText(tooltip);
        } else {
            btn.setIcon(null);
            btn.setText(tooltip);
            btn.setPreferredSize(new Dimension(100, 35));
        }
    }

    /**
     * Positions a button at (x, y) using its icon's natural pixel size.
     * Always call this from layoutUI() instead of setBounds(w, 100, 40).
     */
    protected void sizeToIcon(JButton btn, int x, int y) {
        Dimension d = btn.getPreferredSize();
        int bw = (d != null && d.width  > 0) ? d.width  : 100;
        int bh = (d != null && d.height > 0) ? d.height : 35;
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

    /**
     * Draws name + HP bar + MP bar for one character.
     * Total vertical footprint ≈ 50 px (name + 2 bars + gaps).
     */
    protected void drawBars(Graphics g, GameCharacter c, int x, int y, int barW) {
        if (c == null) return;
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        final int BAR_H = 14;
        final int GAP   = 3;

        // Character name
        g2.setFont(BAR_NAME_FONT);
        g2.setColor(new Color(255, 220, 30));
        g2.drawString(c.getCharacterName(), x, y);
        y += 5;

        // HP bar
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

        // MP bar
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

    /** Draws a "P  2 - 1  CPU" win counter centred at (cx, cy). */
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

        String pfx = switch (spriteKey) {
            default                -> spriteKey;
        };
        String sfx = isLeft ? "L" : "R";
        return ANIM_BASE + dir + "/" + pfx + "Skill" + skillNum + sfx + ".gif";
    }

    /**
     * Plays the player (left) skill animation for 1 500 ms.
     * {@code playerAnimating} is true for the duration so the idle sprite is hidden.
     */
    protected void showPlayerSkillAnim(String spriteKey, int skillNum) {
        if (playerAnimLabel == null) return;
        playerAnimating = true;
        playerAnimLabel.setIcon(new ImageIcon(getSkillAnimPath(spriteKey, skillNum, true)));
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

    /**
     * Plays the enemy (right) skill animation for 1 500 ms.
     * {@code enemyAnimating} is true for the duration so the idle sprite is hidden.
     */
    protected void showEnemySkillAnim(String spriteKey, int skillNum) {
        if (enemyAnimLabel == null) return;
        enemyAnimating = true;
        enemyAnimLabel.setIcon(new ImageIcon(getSkillAnimPath(spriteKey, skillNum, false)));
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
}