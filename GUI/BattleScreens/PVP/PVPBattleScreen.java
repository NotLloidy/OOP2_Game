package GUI.BattleScreens.PVP;

import Foundation.*;
import GameEngines.*;
import GUI.BattleScreens.BaseBattleScreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PVPBattleScreen extends BaseBattleScreen {

    // ── Sprite positions (computed in layoutUI) ───────────────────────────
    private int p1X, p1Y, p1W, p1H;
    private int p2X, p2Y, p2W, p2H;

    private final Image bgImage;
    private Image p1Sprite;
    private Image p2Sprite;

    private JButton p1BtnFight, p1BtnDefend, p1BtnCheck, p1BtnBack;
    private JButton p2BtnFight, p2BtnDefend, p2BtnCheck, p2BtnBack;

    private JTextArea dialogue;
    private JLabel turnLabel;

    private boolean p1DefendDisabled = false;
    private boolean p2DefendDisabled = false;

    private GameCharacter player1;
    private GameCharacter player2;

    private final BattleSystem system;
    private final GameSession  session;

    private ActionState p1State = ActionState.MAIN;
    private ActionState p2State = ActionState.MAIN;

    private boolean p1TurnDone = false;
    private boolean p2TurnDone = false;
    private boolean awaitingP2 = false;

    private int p1Wins = 0;
    private int p2Wins = 0;
    private int round  = 1;

    private boolean initialized = false;
    private boolean matchOver   = false;

    public PVPBattleScreen() {
        setLayout(null);
        session = GameSession.getInstance();
        system  = new BattleSystem();
        bgImage = new ImageIcon(BG_PATH).getImage();
        createUI();
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) { layoutUI(); }
        });
    }

    public void initBattle() {
        if (initialized) return;

        player1 = session.getPlayer1();
        player2 = session.getPlayer2();

        if (player1 == null || player2 == null) {
            dialogue.setText("Both players must be selected!");
            return;
        }

        p1Sprite = new ImageIcon(IDLE_LEFT_DIR  + player1.getSpriteKey() + IDLE_L_SFX).getImage();
        p2Sprite = new ImageIcon(IDLE_RIGHT_DIR + player2.getSpriteKey() + IDLE_R_SFX).getImage();

        dialogue.setText("Round 1 — P1: " + player1.getCharacterName()
                       + "  vs  P2: " + player2.getCharacterName());
        turnLabel.setText("PLAYER 1's turn");

        lockP2Buttons(true);
        lockP1Buttons(false);

        initialized = true;
        repaint();
    }

    // ── UI construction ───────────────────────────────────────────────────

    private void createUI() {
        p1BtnFight  = makeButton("FIGHT",  BTN_FIGHT_PATH);
        p1BtnDefend = makeButton("DEFEND", BTN_DEFEND_PATH);
        p1BtnCheck  = makeButton("CHECK",  BTN_CHECK_PATH);
        p1BtnBack   = makeButton("BACK",   BTN_BACK_PATH);
        p1BtnBack.setDisabledIcon(new ImageIcon(BTN_BACK_DISABLED));

        p2BtnFight  = makeButton("FIGHT",  BTN_FIGHT_PATH);
        p2BtnDefend = makeButton("DEFEND", BTN_DEFEND_PATH);
        p2BtnCheck  = makeButton("CHECK",  BTN_CHECK_PATH);
        p2BtnBack   = makeButton("BACK",   BTN_BACK_PATH);
        p2BtnBack.setDisabledIcon(new ImageIcon(BTN_BACK_DISABLED));

        add(p1BtnFight); add(p1BtnDefend); add(p1BtnCheck); add(p1BtnBack);
        add(p2BtnFight); add(p2BtnDefend); add(p2BtnCheck); add(p2BtnBack);

        dialogue = new JTextArea();
        dialogue.setEditable(false);
        dialogue.setLineWrap(true);
        dialogue.setWrapStyleWord(true);
        dialogue.setOpaque(false);
        dialogue.setForeground(Color.WHITE);
        dialogue.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(dialogue);

        turnLabel = new JLabel("PLAYER 1's turn", SwingConstants.CENTER);
        turnLabel.setForeground(new Color(255, 220, 30));
        turnLabel.setFont(new Font("Impact", Font.PLAIN, 22));
        add(turnLabel);

        // Skill animation overlays
        playerAnimLabel = new JLabel();
        playerAnimLabel.setVisible(false);
        add(playerAnimLabel);

        enemyAnimLabel = new JLabel();
        enemyAnimLabel.setVisible(false);
        add(enemyAnimLabel);

        updateP1Buttons();
        updateP2Buttons();
    }

    // ── Layout ────────────────────────────────────────────────────────────
    // Vertical zones:
    //   0.02 – 0.11  →  HP/MP bars (above sprites)
    //   0.13 – 0.60  →  character sprites
    //   0.62         →  turn label + win counter
    //   0.65 – 0.77  →  dialogue
    //   0.79 / 0.88  →  P1 and P2 button rows

    private void layoutUI() {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        // Sprite areas
        p1W = (int)(w * 0.28); p1H = (int)(h * 0.47);
        p1X = (int)(w * 0.08); p1Y = (int)(h * 0.13);
        p2W = p1W;              p2H = p1H;
        p2X = (int)(w * 0.64); p2Y = p1Y;

        playerAnimLabel.setBounds(p1X, p1Y, p1W, p1H);
        enemyAnimLabel .setBounds(p2X, p2Y, p2W, p2H);

        turnLabel.setBounds((int)(w * 0.30), (int)(h * 0.62), (int)(w * 0.40), 30);
        dialogue .setBounds((int)(w * 0.10), (int)(h * 0.66), (int)(w * 0.80), (int)(h * 0.11));

        // P1 buttons — two rows on the left
        int p1Row1 = (int)(h * 0.80);
        int p1Row2 = (int)(h * 0.88);
        sizeToIcon(p1BtnFight,  (int)(w * 0.05), p1Row1);
        sizeToIcon(p1BtnDefend, (int)(w * 0.20), p1Row1);
        sizeToIcon(p1BtnCheck,  (int)(w * 0.05), p1Row2);
        sizeToIcon(p1BtnBack,   (int)(w * 0.20), p1Row2);

        // P2 buttons — two rows on the right
        int p2Row1 = p1Row1;
        int p2Row2 = p1Row2;
        sizeToIcon(p2BtnFight,  (int)(w * 0.62), p2Row1);
        sizeToIcon(p2BtnDefend, (int)(w * 0.77), p2Row1);
        sizeToIcon(p2BtnCheck,  (int)(w * 0.62), p2Row2);
        sizeToIcon(p2BtnBack,   (int)(w * 0.77), p2Row2);
    }

    // ── Turn execution ────────────────────────────────────────────────────

    private void p1Turn(int action) {
        if (matchOver || player1 == null) return;

        if (action >= 1 && action <= 3) showPlayerSkillAnim(player1.getSpriteKey(), action);

        String result = system.performAction(player1, player2, action, true);
        log("[P1] " + result);

        player1.getSkill1().reduceSkillCooldown();
        player1.getSkill2().reduceSkillCooldown();
        player1.getSkill3().reduceSkillCooldown();
        repaint();

        if (!player2.isCharacterAlive()) {
            p1Wins++;
            endRound("P1 wins round " + round + "!");
            return;
        }

        p1TurnDone = true; awaitingP2 = true;
        lockP1Buttons(true); lockP2Buttons(false);
        turnLabel.setText("PLAYER 2's turn");
        p2State = ActionState.MAIN; updateP2Buttons();
    }

    private void p2Turn(int action) {
        if (matchOver || player2 == null) return;

        if (action >= 1 && action <= 3) showEnemySkillAnim(player2.getSpriteKey(), action);

        String result = system.performAction(player2, player1, action, false);
        log("[P2] " + result);

        player2.getSkill1().reduceSkillCooldown();
        player2.getSkill2().reduceSkillCooldown();
        player2.getSkill3().reduceSkillCooldown();
        repaint();

        if (!player1.isCharacterAlive()) {
            p2Wins++;
            endRound("P2 wins round " + round + "!");
            return;
        }

        p1TurnDone = false; p2TurnDone = false; awaitingP2 = false;
        lockP2Buttons(true); lockP1Buttons(false);
        turnLabel.setText("PLAYER 1's turn");
        p1State = ActionState.MAIN; updateP1Buttons();
    }

    private void resetRound() {
        round++;
        player1.setCharacterCurrentHealthPoints(player1.getCharacterMaxHealthPoints());
        player2.setCharacterCurrentHealthPoints(player2.getCharacterMaxHealthPoints());
        player1.setCharacterCurrentMana(player1.getCharacterMaxMana());
        player2.setCharacterCurrentMana(player2.getCharacterMaxMana());
        p1DefendDisabled = false; p2DefendDisabled = false;
        p1TurnDone = false; p2TurnDone = false; awaitingP2 = false;
        p1State = ActionState.MAIN; p2State = ActionState.MAIN;
        lockP1Buttons(false); lockP2Buttons(true);
        turnLabel.setText("PLAYER 1's turn — Round " + round);
        log("── Round " + round + " ──");
        updateP1Buttons(); updateP2Buttons();
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
        for (JButton b : new JButton[]{p1BtnFight,p1BtnDefend,p1BtnCheck,p1BtnBack,
                                       p2BtnFight,p2BtnDefend,p2BtnCheck,p2BtnBack})
            b.setEnabled(false);
    }

    private void lockP1Buttons(boolean locked) {
        p1BtnFight.setEnabled(!locked); p1BtnDefend.setEnabled(!locked);
        p1BtnCheck.setEnabled(!locked); p1BtnBack.setEnabled(!locked);
    }

    private void lockP2Buttons(boolean locked) {
        p2BtnFight.setEnabled(!locked); p2BtnDefend.setEnabled(!locked);
        p2BtnCheck.setEnabled(!locked); p2BtnBack.setEnabled(!locked);
    }

    private void log(String text) { dialogue.append("\n" + text); }

    // ── P1 button state machine ───────────────────────────────────────────

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
                setButtonLabel(p1BtnBack,   "RESET",  BTN_BACK_PATH);

                p1BtnFight.setEnabled(true); p1BtnDefend.setEnabled(!p1DefendDisabled);
                p1BtnCheck.setEnabled(true); p1BtnBack.setEnabled(true);

                p1BtnFight .addActionListener(e -> { p1State = ActionState.FIGHT;  updateP1Buttons(); });
                p1BtnDefend.addActionListener(e -> { p1State = ActionState.DEFEND; updateP1Buttons(); });
                p1BtnCheck .addActionListener(e -> { p1State = ActionState.CHECK;  updateP1Buttons(); });
                p1BtnBack  .addActionListener(e -> { reset(); initBattle(); });
            }
            case FIGHT -> {
                setButtonLabel(p1BtnFight,  "Skill 1", BTN_SKILL1_ON);
                setButtonLabel(p1BtnDefend, "Skill 2", BTN_SKILL2_ON);
                setButtonLabel(p1BtnCheck,  "Skill 3", BTN_SKILL3_ON);
                setButtonLabel(p1BtnBack,   "BACK",    BTN_BACK_PATH);

                p1BtnFight .setDisabledIcon(new ImageIcon(BTN_SKILL1_OFF));
                p1BtnDefend.setDisabledIcon(new ImageIcon(BTN_SKILL2_OFF));
                p1BtnCheck .setDisabledIcon(new ImageIcon(BTN_SKILL3_OFF));
                p1BtnBack  .setDisabledIcon(new ImageIcon(BTN_BACK_DISABLED));

                boolean s1 = player1 != null && player1.getSkill1().getSkillCurrentCooldown() == 0
                          && player1.getCharacterCurrentMana() >= player1.getSkill1().getSkillManaCost();
                boolean s2 = player1 != null && player1.getSkill2().getSkillCurrentCooldown() == 0
                          && player1.getCharacterCurrentMana() >= player1.getSkill2().getSkillManaCost();
                boolean s3 = player1 != null && player1.getSkill3().getSkillCurrentCooldown() == 0
                          && player1.getCharacterCurrentMana() >= player1.getSkill3().getSkillManaCost();

                p1BtnFight.setEnabled(s1); p1BtnDefend.setEnabled(s2);
                p1BtnCheck.setEnabled(s3); p1BtnBack.setEnabled(true);

                p1BtnFight .addActionListener(e -> p1Turn(1));
                p1BtnDefend.addActionListener(e -> p1Turn(2));
                p1BtnCheck .addActionListener(e -> p1Turn(3));
                p1BtnBack  .addActionListener(e -> { p1State = ActionState.MAIN; updateP1Buttons(); });
            }
            case DEFEND -> {
                p1BtnFight.setVisible(false); p1BtnBack.setVisible(false);
                setButtonLabel(p1BtnCheck, "BACK", BTN_CHECK_PATH);

                int blocks = player1 != null ? player1.getRemainingBlocks() : 0;
                setButtonLabel(p1BtnDefend, "Block (" + blocks + ")", blockPath(blocks));
                p1BtnDefend.setEnabled(blocks > 0 && !p1DefendDisabled);
                p1BtnCheck.setEnabled(true);

                p1BtnDefend.addActionListener(e -> {
                    p1Turn(4);
                    int nb = player1 != null ? player1.getRemainingBlocks() : 0;
                    setButtonLabel(p1BtnDefend, "Block (" + nb + ")", blockPath(nb));
                    if (nb <= 0) p1DefendDisabled = true;
                    layoutUI();
                });
                p1BtnCheck.addActionListener(e -> {
                    p1State = ActionState.MAIN;
                    p1BtnFight.setVisible(true); p1BtnBack.setVisible(true);
                    updateP1Buttons();
                });
            }
            case CHECK -> {
                if (player1 != null)
                    dialogue.setText("[P1 Skills]\n"
                        + fmtSkill(player1.getSkill1()) + "\n"
                        + fmtSkill(player1.getSkill2()) + "\n"
                        + fmtSkill(player1.getSkill3()));

                p1BtnFight.setEnabled(false); p1BtnDefend.setEnabled(false);
                p1BtnCheck.setEnabled(false); p1BtnBack.setEnabled(true);
                p1BtnBack.addActionListener(e -> {
                    dialogue.setText("What will " + (player1 != null ? player1.getCharacterName() : "?") + " do?");
                    p1State = ActionState.MAIN; updateP1Buttons();
                });
            }
        }
        if (getWidth() > 0) layoutUI();
    }

    // ── P2 button state machine ───────────────────────────────────────────

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
                setButtonLabel(p2BtnBack,   "RESET",  BTN_BACK_PATH);

                p2BtnFight.setEnabled(true); p2BtnDefend.setEnabled(!p2DefendDisabled);
                p2BtnCheck.setEnabled(true); p2BtnBack.setEnabled(true);

                p2BtnFight .addActionListener(e -> { p2State = ActionState.FIGHT;  updateP2Buttons(); });
                p2BtnDefend.addActionListener(e -> { p2State = ActionState.DEFEND; updateP2Buttons(); });
                p2BtnCheck .addActionListener(e -> { p2State = ActionState.CHECK;  updateP2Buttons(); });
                p2BtnBack  .addActionListener(e -> { reset(); initBattle(); });
            }
            case FIGHT -> {
                setButtonLabel(p2BtnFight,  "Skill 1", BTN_SKILL1_ON);
                setButtonLabel(p2BtnDefend, "Skill 2", BTN_SKILL2_ON);
                setButtonLabel(p2BtnCheck,  "Skill 3", BTN_SKILL3_ON);
                setButtonLabel(p2BtnBack,   "BACK",    BTN_BACK_PATH);

                p2BtnFight .setDisabledIcon(new ImageIcon(BTN_SKILL1_OFF));
                p2BtnDefend.setDisabledIcon(new ImageIcon(BTN_SKILL2_OFF));
                p2BtnCheck .setDisabledIcon(new ImageIcon(BTN_SKILL3_OFF));
                p2BtnBack  .setDisabledIcon(new ImageIcon(BTN_BACK_DISABLED));

                boolean s1 = player2 != null && player2.getSkill1().getSkillCurrentCooldown() == 0
                          && player2.getCharacterCurrentMana() >= player2.getSkill1().getSkillManaCost();
                boolean s2 = player2 != null && player2.getSkill2().getSkillCurrentCooldown() == 0
                          && player2.getCharacterCurrentMana() >= player2.getSkill2().getSkillManaCost();
                boolean s3 = player2 != null && player2.getSkill3().getSkillCurrentCooldown() == 0
                          && player2.getCharacterCurrentMana() >= player2.getSkill3().getSkillManaCost();

                p2BtnFight.setEnabled(s1); p2BtnDefend.setEnabled(s2);
                p2BtnCheck.setEnabled(s3); p2BtnBack.setEnabled(true);

                p2BtnFight .addActionListener(e -> p2Turn(1));
                p2BtnDefend.addActionListener(e -> p2Turn(2));
                p2BtnCheck .addActionListener(e -> p2Turn(3));
                p2BtnBack  .addActionListener(e -> { p2State = ActionState.MAIN; updateP2Buttons(); });
            }
            case DEFEND -> {
                p2BtnFight.setVisible(false); p2BtnBack.setVisible(false);
                setButtonLabel(p2BtnCheck, "BACK", BTN_CHECK_PATH);

                int blocks = player2 != null ? player2.getRemainingBlocks() : 0;
                setButtonLabel(p2BtnDefend, "Block (" + blocks + ")", blockPath(blocks));
                p2BtnDefend.setEnabled(blocks > 0 && !p2DefendDisabled);
                p2BtnCheck.setEnabled(true);

                p2BtnDefend.addActionListener(e -> {
                    p2Turn(4);
                    int nb = player2 != null ? player2.getRemainingBlocks() : 0;
                    setButtonLabel(p2BtnDefend, "Block (" + nb + ")", blockPath(nb));
                    if (nb <= 0) p2DefendDisabled = true;
                    layoutUI();
                });
                p2BtnCheck.addActionListener(e -> {
                    p2State = ActionState.MAIN;
                    p2BtnFight.setVisible(true); p2BtnBack.setVisible(true);
                    updateP2Buttons();
                });
            }
            case CHECK -> {
                if (player2 != null)
                    dialogue.setText("[P2 Skills]\n"
                        + fmtSkill(player2.getSkill1()) + "\n"
                        + fmtSkill(player2.getSkill2()) + "\n"
                        + fmtSkill(player2.getSkill3()));

                p2BtnFight.setEnabled(false); p2BtnDefend.setEnabled(false);
                p2BtnCheck.setEnabled(false); p2BtnBack.setEnabled(true);
                p2BtnBack.addActionListener(e -> {
                    dialogue.setText("What will " + (player2 != null ? player2.getCharacterName() : "?") + " do?");
                    p2State = ActionState.MAIN; updateP2Buttons();
                });
            }
        }
        if (getWidth() > 0) layoutUI();
    }

    private static String fmtSkill(Foundation.Skill sk) {
        return sk.getSkillName() + " | DMG:" + sk.getSkillDamage()
             + " MP:" + sk.getSkillManaCost()
             + " CD:" + sk.getSkillCurrentCooldown() + "/" + sk.getSkillMaxCooldown();
    }

    // ── Full reset ────────────────────────────────────────────────────────

    public void reset() {
        initialized = false; player1 = null; player2 = null;
        p1Wins = 0; p2Wins = 0; round = 1;
        p1DefendDisabled = false; p2DefendDisabled = false;
        p1TurnDone = false; p2TurnDone = false; awaitingP2 = false; matchOver = false;
        p1State = ActionState.MAIN; p2State = ActionState.MAIN;
        playerAnimating = false; enemyAnimating = false;
        playerAnimLabel.setVisible(false); enemyAnimLabel.setVisible(false);
        dialogue.setText(""); turnLabel.setText("PLAYER 1's turn");
        updateP1Buttons(); updateP2Buttons(); repaint();
    }

    // ── Paint ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();

        g.drawImage(bgImage, 0, 0, w, h, this);

        // Idle sprites — hidden while skill animation plays
        if (p1Sprite != null && !playerAnimating)
            g.drawImage(p1Sprite, p1X, p1Y, p1W, p1H, this);
        if (p2Sprite != null && !enemyAnimating)
            g.drawImage(p2Sprite, p2X, p2Y, p2W, p2H, this);

        // HP / MP bars ABOVE each character's sprite area
        int barW = (int)(w * 0.22);
        drawBars(g, player1, p1X, (int)(h * 0.02), barW);
        drawBars(g, player2, p2X, (int)(h * 0.02), barW);

        // Win counter
        int cx = w / 2;
        g.setFont(new Font("Impact", Font.PLAIN, 18));
        g.setColor(Color.WHITE);
        FontMetrics fm = g.getFontMetrics();
        String score = "P1  " + p1Wins + " - " + p2Wins + "  P2";
        g.drawString(score, cx - fm.stringWidth(score) / 2, (int)(h * 0.64));
    }

    private enum ActionState { MAIN, FIGHT, DEFEND, CHECK }
}