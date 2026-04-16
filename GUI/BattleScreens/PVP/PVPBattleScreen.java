package GUI.BattleScreens.PVP;

import Foundation.*;
import GameEngines.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * PVPBattleScreen — Local 2-player battle (same rules as PVE, no AI).
 *
 * BUTTON SPRITE ASSET PATHS — set these constants to swap in custom button images.
 * Set to null to use the default text buttons.
 *
 *   BTN_FIGHT_PATH   = "Assets/buttons/btn_fight.png"
 *   BTN_DEFEND_PATH  = "Assets/buttons/btn_defend.png"
 *   BTN_CHECK_PATH   = "Assets/buttons/btn_check.png"
 *   BTN_BACK_PATH    = "Assets/buttons/btn_back.png"
 *
 * P1 controls:  FIGHT → Skill 1/2/3,  DEFEND → Block,  CHECK → view skills
 * P2 controls:  same layout, second row of buttons beneath P1's row
 *
 * Character sprites:
 *   P1 uses  -left.gif  (faces right toward enemy)
 *   P2 uses  -right.gif (faces left toward enemy)
 */
public class PVPBattleScreen extends JPanel {

    // ── BUTTON SPRITE PATHS (set to null to use plain text buttons) ──────────
    private static final String BTN_FIGHT_PATH   = null; // e.g. "Assets/buttons/btn_fight.png"
    private static final String BTN_DEFEND_PATH  = null;
    private static final String BTN_CHECK_PATH   = null;
    private static final String BTN_BACK_PATH    = null;
    // ── CHARACTER SPRITE PATHS ───────────────────────────────────────────────
    private static final String P1_SPRITE_PATH   = "Assets/characters_left/";
    private static final String P1_SPRITE_SUFFIX = "-left.gif";
    private static final String P2_SPRITE_PATH   = "Assets/characters_right/";
    private static final String P2_SPRITE_SUFFIX = "-right.gif";
    private static final String BG_IMAGE_PATH    = "Assets/battleArenaScreen.gif";
    // ────────────────────────────────────────────────────────────────────────

    private final Image bgImage;
    private Image p1Sprite;
    private Image p2Sprite;

    // P1 buttons
    private JButton p1BtnFight, p1BtnDefend, p1BtnCheck, p1BtnBack;
    // P2 buttons
    private JButton p2BtnFight, p2BtnDefend, p2BtnCheck, p2BtnBack;

    private JTextArea dialogue;
    private JLabel turnLabel;

    private boolean p1DefendDisabled = false;
    private boolean p2DefendDisabled = false;

    private GameCharacter player1;
    private GameCharacter player2;

    private final BattleSystem system;
    private final GameSession session;

    private ActionState p1State = ActionState.MAIN;
    private ActionState p2State = ActionState.MAIN;

    private boolean p1TurnDone  = false;  // has P1 committed an action this turn?
    private boolean p2TurnDone  = false;
    private boolean awaitingP2  = false;  // true after P1 acts, waiting for P2

    private int p1Wins = 0;
    private int p2Wins = 0;
    private int round  = 1;

    private boolean initialized = false;
    private boolean matchOver   = false;

    public PVPBattleScreen() {
        setLayout(null);
        session = GameSession.getInstance();
        system  = new BattleSystem();
        bgImage = new ImageIcon(BG_IMAGE_PATH).getImage();
        createUI();
        setLayoutListeners();
    }

    // =========================
    // INIT
    // =========================
    public void initBattle() {
        if (initialized) return;

        player1 = session.getPlayer1();
        player2 = session.getPlayer2();

        if (player1 == null || player2 == null) {
            dialogue.setText("Both players must be selected!");
            return;
        }

        p1Sprite = new ImageIcon(P1_SPRITE_PATH + player1.getCharacterName() + P1_SPRITE_SUFFIX).getImage();
        p2Sprite = new ImageIcon(P2_SPRITE_PATH + player2.getCharacterName() + P2_SPRITE_SUFFIX).getImage();

        dialogue.setText("Round 1 — P1: " + player1.getCharacterName()
                + "  vs  P2: " + player2.getCharacterName());
        turnLabel.setText("PLAYER 1's turn");

        lockP2Buttons(true);
        lockP1Buttons(false);

        initialized = true;
    }

    // =========================
    // UI SETUP
    // =========================
    private void createUI() {

        // ── P1 Buttons ───────────────────────────────────────────────────────
        p1BtnFight  = makeButton("FIGHT",  BTN_FIGHT_PATH);
        p1BtnDefend = makeButton("DEFEND", BTN_DEFEND_PATH);
        p1BtnCheck  = makeButton("CHECK",  BTN_CHECK_PATH);
        p1BtnBack   = makeButton("BACK",   BTN_BACK_PATH);

        // ── P2 Buttons ───────────────────────────────────────────────────────
        p2BtnFight  = makeButton("FIGHT",  BTN_FIGHT_PATH);
        p2BtnDefend = makeButton("DEFEND", BTN_DEFEND_PATH);
        p2BtnCheck  = makeButton("CHECK",  BTN_CHECK_PATH);
        p2BtnBack   = makeButton("BACK",   BTN_BACK_PATH);

        add(p1BtnFight);  add(p1BtnDefend);  add(p1BtnCheck);  add(p1BtnBack);
        add(p2BtnFight);  add(p2BtnDefend);  add(p2BtnCheck);  add(p2BtnBack);

        // ── Dialogue ─────────────────────────────────────────────────────────
        dialogue = new JTextArea();
        dialogue.setEditable(false);
        dialogue.setLineWrap(true);
        dialogue.setWrapStyleWord(true);
        dialogue.setOpaque(false);
        dialogue.setForeground(Color.WHITE);
        dialogue.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(dialogue);

        // ── Turn indicator label ─────────────────────────────────────────────
        turnLabel = new JLabel("PLAYER 1's turn", SwingConstants.CENTER);
        turnLabel.setForeground(new Color(255, 220, 30));
        turnLabel.setFont(new Font("Impact", Font.PLAIN, 22));
        add(turnLabel);

        // Attach listeners
        bindP1Listeners();
        bindP2Listeners();
        updateP1Buttons();
        updateP2Buttons();
    }

    // ─── Make button — uses sprite image if path given, else plain text ──────
    private JButton makeButton(String text, String imagePath) {
        JButton btn = new JButton();
        if (imagePath != null) {
            ImageIcon icon = new ImageIcon(imagePath);
            btn.setIcon(icon);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setToolTipText(text);
        } else {
            btn.setText(text);
        }
        return btn;
    }

    // ─── Set button label/icon dynamically ───────────────────────────────────
    private void setButtonLabel(JButton btn, String text, String imagePath) {
        if (imagePath != null) {
            btn.setIcon(new ImageIcon(imagePath));
            btn.setToolTipText(text);
        } else {
            btn.setText(text);
        }
    }

    // =========================
    // P1 ACTION FLOW
    // =========================
    private void p1Turn(int action) {
        if (matchOver || player1 == null) return;

        String result = system.performAction(player1, player2, action, true);
        log("[P1] " + result);
        repaint();

        if (!player2.isCharacterAlive()) {
            p1Wins++;
            endRound("P1 wins round " + round + "!");
            return;
        }

        // P1 done — now unlock P2
        p1TurnDone = true;
        awaitingP2 = true;
        lockP1Buttons(true);
        lockP2Buttons(false);
        turnLabel.setText("PLAYER 2's turn");
        p2State = ActionState.MAIN;
        updateP2Buttons();
    }

    // =========================
    // P2 ACTION FLOW
    // =========================
    private void p2Turn(int action) {
        if (matchOver || player2 == null) return;

        String result = system.performAction(player2, player1, action, false);
        log("[P2] " + result);
        repaint();

        if (!player1.isCharacterAlive()) {
            p2Wins++;
            endRound("P2 wins round " + round + "!");
            return;
        }

        // Both turns done — reset for next turn
        p1TurnDone = false;
        p2TurnDone = false;
        awaitingP2 = false;
        lockP2Buttons(true);
        lockP1Buttons(false);
        turnLabel.setText("PLAYER 1's turn");
        p1State = ActionState.MAIN;
        updateP1Buttons();
    }

    // =========================
    // ROUND / MATCH MANAGEMENT
    // =========================
    private void resetRound() {
        round++;
        player1.setCharacterCurrentHealthPoints(player1.getCharacterMaxHealthPoints());
        player2.setCharacterCurrentHealthPoints(player2.getCharacterMaxHealthPoints());
        player1.setCharacterCurrentMana(player1.getCharacterMaxMana());
        player2.setCharacterCurrentMana(player2.getCharacterMaxMana());
        p1DefendDisabled = false;
        p2DefendDisabled = false;
        p1TurnDone = false;
        p2TurnDone = false;
        awaitingP2  = false;
        p1State = ActionState.MAIN;
        p2State = ActionState.MAIN;
        lockP1Buttons(false);
        lockP2Buttons(true);
        turnLabel.setText("PLAYER 1's turn — Round " + round);
        log("── Round " + round + " ──");
        updateP1Buttons();
        updateP2Buttons();
    }

    private void endRound(String message) {
        log(message);
        if (p1Wins == 2) { endMatch("PLAYER 1 WINS THE MATCH!"); return; }
        if (p2Wins == 2) { endMatch("PLAYER 2 WINS THE MATCH!"); return; }
        resetRound();
    }

    private void endMatch(String message) {
        matchOver = true;
        log(message);
        turnLabel.setText(message);
        disableAll();
    }

    private void disableAll() {
        for (JButton b : new JButton[]{
                p1BtnFight,p1BtnDefend,p1BtnCheck,p1BtnBack,
                p2BtnFight,p2BtnDefend,p2BtnCheck,p2BtnBack}) {
            b.setEnabled(false);
        }
    }

    private void lockP1Buttons(boolean locked) {
        p1BtnFight.setEnabled(!locked);
        p1BtnDefend.setEnabled(!locked);
        p1BtnCheck.setEnabled(!locked);
        p1BtnBack.setEnabled(!locked);
    }

    private void lockP2Buttons(boolean locked) {
        p2BtnFight.setEnabled(!locked);
        p2BtnDefend.setEnabled(!locked);
        p2BtnCheck.setEnabled(!locked);
        p2BtnBack.setEnabled(!locked);
    }

    private void log(String text) { dialogue.append("\n" + text); }

    // =========================
    // P1 BUTTON STATE
    // =========================
    private void bindP1Listeners() {
        p1BtnFight.addActionListener(e -> { p1State = ActionState.FIGHT;  updateP1Buttons(); });
        p1BtnDefend.addActionListener(e -> { p1State = ActionState.DEFEND; updateP1Buttons(); });
        p1BtnCheck.addActionListener(e -> { p1State = ActionState.CHECK;  updateP1Buttons(); });
        p1BtnBack.addActionListener(e -> { p1State = ActionState.MAIN;   updateP1Buttons(); });
    }

    private void updateP1Buttons() {
        clearListeners(p1BtnFight); clearListeners(p1BtnDefend);
        clearListeners(p1BtnCheck); clearListeners(p1BtnBack);

        p1BtnFight.setVisible(true); p1BtnBack.setVisible(true);
        p1BtnDefend.setVisible(true); p1BtnCheck.setVisible(true);

        switch (p1State) {
            case MAIN -> {
                setButtonLabel(p1BtnFight,  "FIGHT",  BTN_FIGHT_PATH);
                setButtonLabel(p1BtnDefend, "DEFEND", BTN_DEFEND_PATH);
                setButtonLabel(p1BtnCheck,  "CHECK",  BTN_CHECK_PATH);
                setButtonLabel(p1BtnBack,   "BACK",   BTN_BACK_PATH);
                p1BtnFight.setEnabled(true);
                p1BtnDefend.setEnabled(!p1DefendDisabled);
                p1BtnCheck.setEnabled(true);
                p1BtnBack.setEnabled(false);
                p1BtnFight.addActionListener(e -> { p1State = ActionState.FIGHT;  updateP1Buttons(); });
                p1BtnDefend.addActionListener(e -> { p1State = ActionState.DEFEND; updateP1Buttons(); });
                p1BtnCheck.addActionListener(e -> { p1State = ActionState.CHECK;  updateP1Buttons(); });
            }
            case FIGHT -> {
                setButtonLabel(p1BtnFight,  "Skill 1", BTN_FIGHT_PATH);
                setButtonLabel(p1BtnDefend, "Skill 2", BTN_DEFEND_PATH);
                setButtonLabel(p1BtnCheck,  "Skill 3", BTN_CHECK_PATH);
                setButtonLabel(p1BtnBack,   "BACK",    BTN_BACK_PATH);
                p1BtnFight.setEnabled(true); p1BtnDefend.setEnabled(true);
                p1BtnCheck.setEnabled(true); p1BtnBack.setEnabled(true);
                p1BtnFight.addActionListener(e -> p1Turn(1));
                p1BtnDefend.addActionListener(e -> p1Turn(2));
                p1BtnCheck.addActionListener(e -> p1Turn(3));
                p1BtnBack.addActionListener(e -> { p1State = ActionState.MAIN; updateP1Buttons(); });
            }
            case DEFEND -> {
                p1BtnFight.setVisible(false); p1BtnBack.setVisible(false);
                setButtonLabel(p1BtnDefend, "Block (" + (player1 != null ? player1.getRemainingBlocks() : 0) + ")", BTN_DEFEND_PATH);
                setButtonLabel(p1BtnCheck,  "BACK", BTN_CHECK_PATH);
                p1BtnDefend.setEnabled(player1 != null && player1.getRemainingBlocks() > 0 && !p1DefendDisabled);
                p1BtnCheck.setEnabled(true);
                p1BtnDefend.addActionListener(e -> {
                    p1Turn(4);
                    if (player1 != null && player1.getRemainingBlocks() <= 0) p1DefendDisabled = true;
                    updateP1Buttons();
                });
                p1BtnCheck.addActionListener(e -> {
                    p1State = ActionState.MAIN;
                    p1BtnFight.setVisible(true); p1BtnBack.setVisible(true);
                    updateP1Buttons();
                });
            }
            case CHECK -> {
                if (player1 instanceof SkillsInterface skills) {
                    dialogue.setText("[P1 Skills]\n"
                            + skills.getSkill1().getSkillName() + " | DMG: " + skills.getSkill1().getSkillDamage() + " | MP: " + skills.getSkill1().getSkillManaCost()
                            + "\n" + skills.getSkill2().getSkillName() + " | DMG: " + skills.getSkill2().getSkillDamage() + " | MP: " + skills.getSkill2().getSkillManaCost()
                            + "\n" + skills.getSkill3().getSkillName() + " | DMG: " + skills.getSkill3().getSkillDamage() + " | MP: " + skills.getSkill3().getSkillManaCost());
                }
                p1BtnFight.setEnabled(false); p1BtnDefend.setEnabled(false); p1BtnCheck.setEnabled(false);
                p1BtnBack.setEnabled(true);
                p1BtnBack.addActionListener(e -> { p1State = ActionState.MAIN; updateP1Buttons(); });
            }
        }
    }

    // =========================
    // P2 BUTTON STATE
    // =========================
    private void bindP2Listeners() {
        // initial binding done in updateP2Buttons
    }

    private void updateP2Buttons() {
        clearListeners(p2BtnFight); clearListeners(p2BtnDefend);
        clearListeners(p2BtnCheck); clearListeners(p2BtnBack);

        p2BtnFight.setVisible(true); p2BtnBack.setVisible(true);
        p2BtnDefend.setVisible(true); p2BtnCheck.setVisible(true);

        switch (p2State) {
            case MAIN -> {
                setButtonLabel(p2BtnFight,  "FIGHT",  BTN_FIGHT_PATH);
                setButtonLabel(p2BtnDefend, "DEFEND", BTN_DEFEND_PATH);
                setButtonLabel(p2BtnCheck,  "CHECK",  BTN_CHECK_PATH);
                setButtonLabel(p2BtnBack,   "BACK",   BTN_BACK_PATH);
                p2BtnFight.setEnabled(true);
                p2BtnDefend.setEnabled(!p2DefendDisabled);
                p2BtnCheck.setEnabled(true);
                p2BtnBack.setEnabled(false);
                p2BtnFight.addActionListener(e -> { p2State = ActionState.FIGHT;  updateP2Buttons(); });
                p2BtnDefend.addActionListener(e -> { p2State = ActionState.DEFEND; updateP2Buttons(); });
                p2BtnCheck.addActionListener(e -> { p2State = ActionState.CHECK;  updateP2Buttons(); });
            }
            case FIGHT -> {
                setButtonLabel(p2BtnFight,  "Skill 1", BTN_FIGHT_PATH);
                setButtonLabel(p2BtnDefend, "Skill 2", BTN_DEFEND_PATH);
                setButtonLabel(p2BtnCheck,  "Skill 3", BTN_CHECK_PATH);
                setButtonLabel(p2BtnBack,   "BACK",    BTN_BACK_PATH);
                p2BtnFight.setEnabled(true); p2BtnDefend.setEnabled(true);
                p2BtnCheck.setEnabled(true); p2BtnBack.setEnabled(true);
                p2BtnFight.addActionListener(e -> p2Turn(1));
                p2BtnDefend.addActionListener(e -> p2Turn(2));
                p2BtnCheck.addActionListener(e -> p2Turn(3));
                p2BtnBack.addActionListener(e -> { p2State = ActionState.MAIN; updateP2Buttons(); });
            }
            case DEFEND -> {
                p2BtnFight.setVisible(false); p2BtnBack.setVisible(false);
                setButtonLabel(p2BtnDefend, "Block (" + (player2 != null ? player2.getRemainingBlocks() : 0) + ")", BTN_DEFEND_PATH);
                setButtonLabel(p2BtnCheck,  "BACK", BTN_CHECK_PATH);
                p2BtnDefend.setEnabled(player2 != null && player2.getRemainingBlocks() > 0 && !p2DefendDisabled);
                p2BtnCheck.setEnabled(true);
                p2BtnDefend.addActionListener(e -> {
                    p2Turn(4);
                    if (player2 != null && player2.getRemainingBlocks() <= 0) p2DefendDisabled = true;
                    updateP2Buttons();
                });
                p2BtnCheck.addActionListener(e -> {
                    p2State = ActionState.MAIN;
                    p2BtnFight.setVisible(true); p2BtnBack.setVisible(true);
                    updateP2Buttons();
                });
            }
            case CHECK -> {
                if (player2 instanceof SkillsInterface skills) {
                    dialogue.setText("[P2 Skills]\n"
                            + skills.getSkill1().getSkillName() + " | DMG: " + skills.getSkill1().getSkillDamage() + " | MP: " + skills.getSkill1().getSkillManaCost()
                            + "\n" + skills.getSkill2().getSkillName() + " | DMG: " + skills.getSkill2().getSkillDamage() + " | MP: " + skills.getSkill2().getSkillManaCost()
                            + "\n" + skills.getSkill3().getSkillName() + " | DMG: " + skills.getSkill3().getSkillDamage() + " | MP: " + skills.getSkill3().getSkillManaCost());
                }
                p2BtnFight.setEnabled(false); p2BtnDefend.setEnabled(false); p2BtnCheck.setEnabled(false);
                p2BtnBack.setEnabled(true);
                p2BtnBack.addActionListener(e -> { p2State = ActionState.MAIN; updateP2Buttons(); });
            }
        }
    }

    // =========================
    // LAYOUT
    // =========================
    private void setLayoutListeners() {
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) { layoutUI(); }
        });
    }

    private void layoutUI() {
        int w = getWidth(), h = getHeight();

        turnLabel.setBounds((int)(w * 0.3), (int)(h * 0.60), (int)(w * 0.4), 30);

        dialogue.setBounds(
                (int)(w * 0.1), (int)(h * 0.64),
                (int)(w * 0.8), (int)(h * 0.12)
        );

        // P1 buttons — left side, lower
        p1BtnFight.setBounds((int)(w * 0.05), (int)(h * 0.80), 100, 40);
        p1BtnDefend.setBounds((int)(w * 0.20), (int)(h * 0.80), 100, 40);
        p1BtnCheck.setBounds((int)(w * 0.05), (int)(h * 0.88), 100, 40);
        p1BtnBack.setBounds((int)(w * 0.20), (int)(h * 0.88), 100, 40);

        // P2 buttons — right side, lower
        p2BtnFight.setBounds((int)(w * 0.62), (int)(h * 0.80), 100, 40);
        p2BtnDefend.setBounds((int)(w * 0.77), (int)(h * 0.80), 100, 40);
        p2BtnCheck.setBounds((int)(w * 0.62), (int)(h * 0.88), 100, 40);
        p2BtnBack.setBounds((int)(w * 0.77), (int)(h * 0.88), 100, 40);
    }

    // =========================
    // DRAW
    // =========================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);

        int sh = (int)(getHeight() * 0.50);
        int sy = (int)(getHeight() * 0.10);
        int sw = (int)(getWidth() * 0.28);

        if (p1Sprite != null)
            g.drawImage(p1Sprite, (int)(getWidth() * 0.08), sy, sw, sh, this);

        if (p2Sprite != null)
            g.drawImage(p2Sprite, (int)(getWidth() * 0.64), sy, sw, sh, this);

        // Draw P1 / P2 labels above buttons
        g.setColor(new Color(255, 220, 30));
        g.setFont(new Font("Impact", Font.PLAIN, 16));
        g.drawString("P1", (int)(getWidth() * 0.05), (int)(getHeight() * 0.78));
        g.drawString("P2", (int)(getWidth() * 0.62), (int)(getHeight() * 0.78));

        // Win counter
        g.setFont(new Font("Impact", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        g.drawString("P1  " + p1Wins + " - " + p2Wins + "  P2",
                (int)(getWidth() * 0.40), (int)(getHeight() * 0.62));
    }

    public void reset() {
        initialized      = false;
        player1          = null;
        player2          = null;
        p1Wins           = 0;
        p2Wins           = 0;
        round            = 1;
        p1DefendDisabled = false;
        p2DefendDisabled = false;
        p1TurnDone       = false;
        p2TurnDone       = false;
        awaitingP2       = false;
        matchOver        = false;
        p1State          = ActionState.MAIN;
        p2State          = ActionState.MAIN;
        dialogue.setText("");
        turnLabel.setText("PLAYER 1's turn");
        updateP1Buttons();
        updateP2Buttons();
        repaint();
    }

    // =========================
    // HELPERS
    // =========================
    private void clearListeners(JButton btn) {
        for (ActionListener al : btn.getActionListeners()) btn.removeActionListener(al);
    }

    private enum ActionState { MAIN, FIGHT, DEFEND, CHECK }
}