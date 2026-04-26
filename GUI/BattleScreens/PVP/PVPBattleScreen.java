package GUI.BattleScreens.PVP;

import Foundation.*;
import GameEngines.*;
import GUI.BattleScreens.BaseBattleScreen;
import GUI.GameGUI;

import javax.swing.*;
import java.awt.*;

public class PVPBattleScreen extends BaseBattleScreen {

    private int p1X, p1Y, p1W, p1H;
    private int p2X, p2Y, p2W, p2H;

    private final Image bgImage;
    private Image p1Sprite;
    private Image p2Sprite;

    // ── Single shared button set (same as PVE) ────────────────────────────
    private JButton btnFight, btnDefend, btnCheck, btnBack;

    private JTextArea dialogue;
    private JLabel    turnLabel;

    private boolean p1DefendDisabled = false;
    private boolean p2DefendDisabled = false;

    private GameCharacter player1;
    private GameCharacter player2;

    private final BattleSystem system;
    private final GameSession  session;

    // Whose turn it is — 1 = P1, 2 = P2
    private int currentTurn = 1;

    private ActionState state = ActionState.MAIN;

    private int p1Wins = 0;
    private int p2Wins = 0;
    private int round  = 1;

    private boolean initialized = false;
    private boolean matchOver   = false;

    private GameGUI gameGUI;

    public PVPBattleScreen() {
        setLayout(null);
        session = GameSession.getInstance();
        system  = new BattleSystem();
        bgImage = new ImageIcon(BG_PATH).getImage();
        createUI();
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                updateButtons();
                layoutUI();
            }
        });
    }

    public void setGameGUI(GameGUI gui) { this.gameGUI = gui; }

    // ── Init ──────────────────────────────────────────────────────────────

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

        currentTurn = 1;
        dialogue.setText("Round 1 — P1: " + player1.getCharacterName()
                       + "  vs  P2: " + player2.getCharacterName());
        turnLabel.setText("PLAYER 1's turn");

        initialized = true;
        updateButtons();
        repaint();
    }

    // ── UI construction ───────────────────────────────────────────────────

    private void createUI() {
        btnFight  = makeButton("FIGHT",  BTN_FIGHT_PATH);
        btnDefend = makeButton("DEFEND", BTN_DEFEND_PATH);
        btnCheck  = makeButton("CHECK",  BTN_CHECK_PATH);
        btnBack   = makeButton("BACK",   BTN_BACK_PATH);
        btnBack.setDisabledIcon(makeScaledIcon(BTN_BACK_DISABLED));

        add(btnFight); add(btnDefend); add(btnCheck); add(btnBack);

        dialogue = new JTextArea();
        dialogue.setEditable(false);
        dialogue.setLineWrap(true);
        dialogue.setWrapStyleWord(true);
        dialogue.setOpaque(false);
        dialogue.setBackground(Color.BLACK);
        dialogue.setForeground(Color.WHITE);
        dialogue.setFont(new Font("SansSerif", Font.PLAIN, 13));
        add(dialogue);

        turnLabel = new JLabel("PLAYER 1's turn", SwingConstants.CENTER);
        turnLabel.setForeground(new Color(255, 220, 30));
        turnLabel.setFont(new Font("Impact", Font.PLAIN, 22));
        add(turnLabel);

        playerAnimLabel = new JLabel();
        playerAnimLabel.setVisible(false);
        add(playerAnimLabel);

        enemyAnimLabel = new JLabel();
        enemyAnimLabel.setVisible(false);
        add(enemyAnimLabel);

        updateButtons();
    }

    // ── Layout ────────────────────────────────────────────────────────────

    private void layoutUI() {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        p1W = (int)(w * 0.28); p1H = (int)(h * 0.47);
        p1X = (int)(w * 0.08); p1Y = (int)(h * 0.13);
        p2W = p1W;              p2H = p1H;
        p2X = (int)(w * 0.64); p2Y = p1Y;

        playerAnimLabel.setBounds(p1X, p1Y, p1W, p1H);
        enemyAnimLabel .setBounds(p2X, p2Y, p2W, p2H);

        turnLabel.setBounds((int)(w * 0.30), (int)(h * 0.62), (int)(w * 0.40), 30);
        dialogue .setBounds((int)(w * 0.10), (int)(h * 0.66), (int)(w * 0.80), (int)(h * 0.11));

        // Shared buttons — same positions as PVE
        int btnY = (int)(h * 0.83);
        sizeToIcon(btnFight,  (int)(w * 0.10), btnY);
        sizeToIcon(btnDefend, (int)(w * 0.30), btnY);
        sizeToIcon(btnCheck,  (int)(w * 0.55), btnY);
        sizeToIcon(btnBack,   (int)(w * 0.76), btnY);
    }

    // ── Helpers: active player / defend flag ──────────────────────────────

    private GameCharacter attacker() { return currentTurn == 1 ? player1 : player2; }
    private GameCharacter defender() { return currentTurn == 1 ? player2 : player1; }
    private boolean defendDisabled() { return currentTurn == 1 ? p1DefendDisabled : p2DefendDisabled; }

    // ── Turn execution ────────────────────────────────────────────────────

    private void doTurn(int action) {
        if (matchOver || attacker() == null) return;

        GameCharacter atk = attacker();
        GameCharacter def = defender();
        boolean isP1      = currentTurn == 1;

        if (action >= 1 && action <= 3) {
            if (isP1) showPlayerSkillAnim(atk.getSpriteKey(), action);
            else      showEnemySkillAnim(atk.getSpriteKey(), action);
        }

        String result = system.performAction(atk, def, action, isP1);
        dialogue.setText((isP1 ? "[P1] " : "[P2] ") + result);

        atk.getSkill1().reduceSkillCooldown();
        atk.getSkill2().reduceSkillCooldown();
        atk.getSkill3().reduceSkillCooldown();
        repaint();

        // Check if defender just died
        if (!def.isCharacterAlive()) {
            if (isP1) p1Wins++; else p2Wins++;
            endRound((isP1 ? "P1" : "P2") + " wins round " + round + "!");
            return;
        }

        // Hand off to other player — only non-block actions switch state to MAIN
        if (action != 4) state = ActionState.MAIN;
        currentTurn = isP1 ? 2 : 1;
        turnLabel.setText("PLAYER " + currentTurn + "'s turn");
        turnLabel.setForeground(currentTurn == 1
                ? new Color(80, 180, 255)
                : new Color(255, 100, 100));
        updateButtons();
    }

    // ── Round / match management ──────────────────────────────────────────

    private void resetRound() {
        round++;

        player1.resetForNewRound();
        player2.resetForNewRound();

        player1.getSkill1().resetCooldown();
        player1.getSkill2().resetCooldown();
        player1.getSkill3().resetCooldown();
        player2.getSkill1().resetCooldown();
        player2.getSkill2().resetCooldown();
        player2.getSkill3().resetCooldown();

        p1DefendDisabled = false;
        p2DefendDisabled = false;
        currentTurn = 1;
        state = ActionState.MAIN;

        turnLabel.setText("PLAYER 1's turn — Round " + round);
        turnLabel.setForeground(new Color(80, 180, 255));
        log("-- Round " + round + " --");
        updateButtons();
    }

    private void endRound(String message) {
        log(message);
        if (p1Wins == 2) { endMatch(true);  return; }
        if (p2Wins == 2) { endMatch(false); return; }
        resetRound();
    }

    private void endMatch(boolean p1Won) {
        matchOver = true;

        String winner = p1Won ? player1.getCharacterName() : player2.getCharacterName();
        String loser  = p1Won ? player2.getCharacterName() : player1.getCharacterName();

        log(winner + " wins the match!");
        turnLabel.setText(winner + " wins!");
        turnLabel.setForeground(new Color(255, 220, 30));
        disableButtons();

        if (gameGUI != null) {
            Timer delay = new Timer(900, e ->
                    gameGUI.showGameOver(winner, loser, p1Won,
                            winner + " Won!", "MainMenu"));
            delay.setRepeats(false);
            delay.start();
        }
    }

    private void disableButtons() {
        btnFight.setEnabled(false); btnDefend.setEnabled(false);
        btnCheck.setEnabled(false); btnBack.setEnabled(false);
    }

    private void log(String text) { dialogue.append("\n" + text); }

    // ── State machine (mirrors PVE exactly) ───────────────────────────────

    private void switchState(ActionState next) {
        state = next;
        updateButtons();
    }

    private void updateButtons() {
        resetAllButtons();

        btnFight .setVisible(true);
        btnDefend.setVisible(true);
        btnCheck .setVisible(true);
        btnBack  .setVisible(true);

        GameCharacter active = attacker();

        switch (state) {
            case MAIN -> {
                setButtonLabel(btnFight,  "FIGHT",  BTN_FIGHT_PATH);
                setButtonLabel(btnDefend, "DEFEND", BTN_DEFEND_PATH);
                setButtonLabel(btnCheck,  "CHECK",  BTN_CHECK_PATH);
                setButtonLabel(btnBack,   "BACK",   BTN_BACK_PATH);
                btnBack.setDisabledIcon(makeScaledIcon(BTN_BACK_DISABLED));

                btnFight .setEnabled(true);
                btnDefend.setEnabled(!defendDisabled());
                btnCheck .setEnabled(true);
                btnBack  .setEnabled(false);

                btnFight .addActionListener(e -> switchState(ActionState.FIGHT));
                btnDefend.addActionListener(e -> switchState(ActionState.DEFEND));
                btnCheck .addActionListener(e -> switchState(ActionState.CHECK));
            }

            case FIGHT -> {
                setButtonLabel(btnFight,  "Skill 1", BTN_SKILL1_ON);
                setButtonLabel(btnDefend, "Skill 2", BTN_SKILL2_ON);
                setButtonLabel(btnCheck,  "Skill 3", BTN_SKILL3_ON);
                setButtonLabel(btnBack,   "BACK",    BTN_BACK_PATH);

                btnFight .setDisabledIcon(makeScaledIcon(BTN_SKILL1_OFF));
                btnDefend.setDisabledIcon(makeScaledIcon(BTN_SKILL2_OFF));
                btnCheck .setDisabledIcon(makeScaledIcon(BTN_SKILL3_OFF));
                btnBack  .setDisabledIcon(makeScaledIcon(BTN_BACK_DISABLED));

                btnFight .setEnabled(skillReady(active, 1));
                btnDefend.setEnabled(skillReady(active, 2));
                btnCheck .setEnabled(skillReady(active, 3));
                btnBack  .setEnabled(true);

                btnFight .addActionListener(e -> doTurn(1));
                btnDefend.addActionListener(e -> doTurn(2));
                btnCheck .addActionListener(e -> doTurn(3));
                btnBack  .addActionListener(e -> switchState(ActionState.MAIN));
            }

            case DEFEND -> {
                btnFight.setVisible(false);
                btnBack .setVisible(false);

                int blocks = active != null ? active.getRemainingBlocks() : 0;
                setButtonLabel(btnDefend, "Block (" + blocks + ")", blockPath(blocks));
                btnDefend.setDisabledIcon(makeScaledIcon(blockPath(0)));
                btnDefend.setEnabled(blocks > 0 && !defendDisabled());

                setButtonLabel(btnCheck, "BACK", BTN_BACK_PATH);
                btnCheck.setEnabled(true);

                btnDefend.addActionListener(e -> {
                    int nb = active != null ? active.getRemainingBlocks() : 0;
                    if (nb <= 0) return;
                    doTurn(4);
                    // Update defend-disabled flag for the player who just blocked
                    int remaining = active != null ? active.getRemainingBlocks() : 0;
                    if (remaining <= 0) {
                        if (currentTurn == 1) p1DefendDisabled = true;
                        else                  p2DefendDisabled = true;
                    }
                    switchState(ActionState.DEFEND);
                });

                btnCheck.addActionListener(e -> switchState(ActionState.MAIN));
            }

            case CHECK -> {
                btnFight .setVisible(false);
                btnDefend.setVisible(false);
                btnCheck .setVisible(false);
                btnBack  .setVisible(true);

                setButtonLabel(btnBack, "BACK", BTN_BACK_PATH);
                btnBack.setDisabledIcon(makeScaledIcon(BTN_BACK_DISABLED));
                btnBack.setEnabled(true);

                if (active != null) {
                    dialogue.setText(
                        "[P" + currentTurn + " Skills]\n" +
                        fmtSkill(active.getSkill1()) + "\n" +
                        fmtSkill(active.getSkill2()) + "\n" +
                        fmtSkill(active.getSkill3())
                    );
                }

                btnBack.addActionListener(e -> {
                    dialogue.setText("What will P" + currentTurn + " ("
                            + (active != null ? active.getCharacterName() : "?") + ") do?");
                    switchState(ActionState.MAIN);
                });
            }
        }

        if (getWidth() > 0) layoutUI();
    }

    private static boolean skillReady(GameCharacter c, int slot) {
        if (c == null) return false;
        Skill sk = switch (slot) {
            case 1 -> c.getSkill1(); case 2 -> c.getSkill2(); default -> c.getSkill3();
        };
        return sk.getSkillCurrentCooldown() == 0 && c.getCharacterCurrentMana() >= sk.getSkillManaCost();
    }

    private static String fmtSkill(Skill sk) {
        return sk.getSkillName()
             + " | DMG: "  + sk.getSkillDamage()
             + "  MP: "    + sk.getSkillManaCost()
             + "  CD: "    + sk.getSkillCurrentCooldown() + "/" + sk.getSkillMaxCooldown();
    }

    private enum ActionState { MAIN, FIGHT, DEFEND, CHECK }

    private void resetAllButtons() {
        clearListeners(btnFight); clearListeners(btnDefend);
        clearListeners(btnCheck); clearListeners(btnBack);
    }

    // ── Full reset ────────────────────────────────────────────────────────

    public void reset() {
        initialized      = false; player1 = null; player2 = null;
        p1Wins           = 0;     p2Wins  = 0;    round = 1;
        p1DefendDisabled = false; p2DefendDisabled = false;
        currentTurn      = 1;     matchOver = false;
        state            = ActionState.MAIN;
        playerAnimating  = false; enemyAnimating = false;
        playerAnimLabel.setVisible(false); enemyAnimLabel.setVisible(false);
        dialogue.setText("");
        turnLabel.setText("PLAYER 1's turn");
        turnLabel.setForeground(new Color(255, 220, 30));
        updateButtons(); repaint();
    }

    // ── Paint ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();

        g.drawImage(bgImage, 0, 0, w, h, this);

        if (p1Sprite != null && !playerAnimating)
            g.drawImage(p1Sprite, p1X, p1Y, p1W, p1H, this);
        if (p2Sprite != null && !enemyAnimating)
            g.drawImage(p2Sprite, p2X, p2Y, p2W, p2H, this);

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

        // Highlight whose side is active
        if (!matchOver) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
            g2.setColor(currentTurn == 1 ? new Color(80, 180, 255) : new Color(255, 100, 100));
            int highlightX = currentTurn == 1 ? p1X - 8 : p2X - 8;
            g2.fillRoundRect(highlightX, p1Y - 8, p1W + 16, p1H + 16, 20, 20);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        }
    }
}