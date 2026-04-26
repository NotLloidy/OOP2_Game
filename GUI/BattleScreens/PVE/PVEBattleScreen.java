package GUI.BattleScreens.PVE;

import Foundation.*;
import GUI.BattleScreens.BaseBattleScreen;
import GUI.GameGUI;
import GameEngines.*;

import javax.swing.*;
import java.awt.*;

public class PVEBattleScreen extends BaseBattleScreen {

    // ── Sprites ───────────────────────────────────────────────────────────
    private final Image bgImage;
    private Image playerSprite;
    private Image enemySprite;

    // ── Buttons ───────────────────────────────────────────────────────────
    private JButton btnFight, btnDefend, btnCheck, btnBack;

    // ── Dialogue ──────────────────────────────────────────────────────────
    private JTextArea dialogue;

    // ── State ─────────────────────────────────────────────────────────────
    private GameCharacter player;
    private GameCharacter enemy;
    private boolean defendDisabled = false;
    private ActionState state = ActionState.MAIN;

    private int playerWins = 0;
    private int enemyWins  = 0;
    private int round      = 1;

    private boolean initialized = false;
    private GameGUI gameGUI;

    private final BattleSystem system;
    private final GameSession  session;

    // ── Sprite layout fields (set in layoutUI) ────────────────────────────
    private int spX, spY, spW, spH;
    private int enX, enY, enW, enH;

    public PVEBattleScreen() {
        setLayout(null);
        session = GameSession.getInstance();
        system  = new BattleSystem();
        bgImage = new ImageIcon(BG_PATH).getImage();
        createUI();
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                rescaleButtons();
                layoutUI();
            }
        });
    }

    public void setGameGUI(GameGUI gui) { this.gameGUI = gui; }

    // ── Initialise battle ─────────────────────────────────────────────────

    public void initBattle() {
        if (initialized) return;
        player = session.getPlayer1();
        enemy  = session.getPlayer2();

        if (player == null || enemy == null) {
            dialogue.setText("Battle not initialised!");
            return;
        }

        playerSprite = new ImageIcon(IDLE_LEFT_DIR  + player.getSpriteKey() + IDLE_L_SFX).getImage();
        enemySprite  = new ImageIcon(IDLE_RIGHT_DIR + enemy.getSpriteKey()  + IDLE_R_SFX).getImage();

        dialogue.setText("Battle started!\nWhat will " + player.getCharacterName() + " do?");
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
        dialogue.setForeground(Color.WHITE);
        dialogue.setFont(new Font("SansSerif", Font.PLAIN, 13));
        add(dialogue);

        playerAnimLabel = new JLabel();
        playerAnimLabel.setVisible(false);
        add(playerAnimLabel);

        enemyAnimLabel = new JLabel();
        enemyAnimLabel.setVisible(false);
        add(enemyAnimLabel);

        updateButtons();
    }

    // ── Rescale all button icons when panel is resized ────────────────────

    private void rescaleButtons() {
        // Re-apply the current icon for each button so it gets scaled to the
        // new computeButtonHeight() value. We track which image each button
        // is currently showing via its tooltip (or we just re-run updateButtons).
        updateButtons();
    }

    // ── Layout ────────────────────────────────────────────────────────────

    private void layoutUI() {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        // Sprite areas
        spW = (int)(w * 0.28); spH = (int)(h * 0.48);
        spX = (int)(w * 0.08); spY = (int)(h * 0.13);
        enW = spW;              enH = spH;
        enX = (int)(w * 0.64); enY = spY;

        playerAnimLabel.setBounds(spX, spY, spW, spH);
        enemyAnimLabel .setBounds(enX, enY, enW, enH);

        dialogue.setBounds((int)(w * 0.10), (int)(h * 0.63), (int)(w * 0.80), (int)(h * 0.16));

        // Button row — all buttons share the same Y baseline
        int btnY = (int)(h * 0.83);

        // In DEFEND state only btnDefend and btnCheck are shown;
        // in CHECK state all action buttons are hidden except btnBack.
        // sizeToIcon() uses the button's current preferred size (set during
        // icon scaling) so width adapts automatically.
        sizeToIcon(btnFight,  (int)(w * 0.10), btnY);
        sizeToIcon(btnDefend, (int)(w * 0.30), btnY);
        sizeToIcon(btnCheck,  (int)(w * 0.55), btnY);
        sizeToIcon(btnBack,   (int)(w * 0.76), btnY);
    }

    // ── Turn logic ────────────────────────────────────────────────────────

    private void playerTurn(int action) {
        if (player == null || enemy == null) return;

        if (action >= 1 && action <= 3) showPlayerSkillAnim(player.getSpriteKey(), action);

        String result = system.performAction(player, enemy, action, true);
        dialogue.setText(result);
        reduceCooldowns(player);
        repaint();

        if (!enemy.isCharacterAlive()) {
            playerWins++;
            endRound("You win round " + round + "!");
            return;
        }

        switchState(ActionState.MAIN);
        aiTurn();
    }

    private void aiTurn() {
        int aiAction = system.getAIAction(enemy);
        if (aiAction >= 1 && aiAction <= 3) showEnemySkillAnim(enemy.getSpriteKey(), aiAction);

        String result = system.performAction(enemy, player, aiAction, false);
        dialogue.append("\n" + enemy.getCharacterName() + ": " + result);
        reduceCooldowns(enemy);

        if (!player.isCharacterAlive()) {
            enemyWins++;
            endRound(enemy.getCharacterName() + " wins round " + round + "!");
        }
        repaint();
    }

    private static void reduceCooldowns(GameCharacter c) {
        c.getSkill1().reduceSkillCooldown();
        c.getSkill2().reduceSkillCooldown();
        c.getSkill3().reduceSkillCooldown();
    }

    // ── Round / match management ──────────────────────────────────────────

    private void resetRound() {
        round++;
        restoreCharacter(player);
        restoreCharacter(enemy);
        defendDisabled = false;
        state = ActionState.MAIN;
        dialogue.setText("── Round " + round + " ──\nWhat will " + player.getCharacterName() + " do?");
        updateButtons();
    }

    private void endRound(String message) {
        dialogue.append("\n" + message);

        if (playerWins == 2) {
            dialogue.append("\nYOU WON THE MATCH!");
            disableButtons();
            delay(900, () -> gameGUI.showGameOver(player.getCharacterName(), enemy.getCharacterName(), true, "MainMenu"));
            return;
        }
        if (enemyWins == 2) {
            dialogue.append("\nYOU LOST THE MATCH!");
            disableButtons();
            delay(900, () -> gameGUI.showGameOver(enemy.getCharacterName(), player.getCharacterName(), false, "MainMenu"));
            return;
        }
        resetRound();
    }

    private static void restoreCharacter(GameCharacter c) {
        c.setCharacterCurrentHealthPoints(c.getCharacterMaxHealthPoints());
        c.setCharacterCurrentMana(c.getCharacterMaxMana());
    }

    private void disableButtons() {
        for (JButton b : new JButton[]{btnFight, btnDefend, btnCheck, btnBack}) b.setEnabled(false);
    }

    private static void delay(int ms, Runnable r) {
        Timer t = new Timer(ms, e -> r.run());
        t.setRepeats(false);
        t.start();
    }

    // ── State machine ─────────────────────────────────────────────────────

    private void switchState(ActionState next) {
        state = next;
        updateButtons();
    }

    /**
     * Central button state machine.
     *
     * MAIN   → Fight / Defend / Check visible; Back disabled (shows disabled graphic).
     * FIGHT  → Skill 1/2/3 replace Fight/Defend/Check; Back re-enabled → returns to MAIN.
     * DEFEND → Fight hidden, Back hidden; Defend shows block count; Check becomes "BACK".
     *           When blocks reach 0 the Defend button shows BTN_BLOCK0 and is disabled;
     *           if user re-enters DEFEND after leaving it stays that way.
     * CHECK  → Only Back visible/enabled; dialogue shows skill stats.
     */
    private void updateButtons() {
        resetAllButtons();

        // Default visibility — each case can override
        btnFight .setVisible(true);
        btnDefend.setVisible(true);
        btnCheck .setVisible(true);
        btnBack  .setVisible(true);

        switch (state) {
            // ── MAIN ──────────────────────────────────────────────────────
            case MAIN -> {
                setButtonLabel(btnFight,  "FIGHT",  BTN_FIGHT_PATH);
                setButtonLabel(btnDefend, "DEFEND", BTN_DEFEND_PATH);
                setButtonLabel(btnCheck,  "CHECK",  BTN_CHECK_PATH);
                setButtonLabel(btnBack,   "BACK",   BTN_BACK_PATH);

                // Refresh disabled icon to match current scale
                btnBack.setDisabledIcon(makeScaledIcon(BTN_BACK_DISABLED));

                btnFight .setEnabled(true);
                btnDefend.setEnabled(true);
                btnCheck .setEnabled(true);
                btnBack  .setEnabled(false);   // shows disabled graphic

                btnFight .addActionListener(e -> switchState(ActionState.FIGHT));
                btnDefend.addActionListener(e -> switchState(ActionState.DEFEND));
                btnCheck .addActionListener(e -> switchState(ActionState.CHECK));
            }

            // ── FIGHT ─────────────────────────────────────────────────────
            case FIGHT -> {
                setButtonLabel(btnFight,  "Skill 1", BTN_SKILL1_ON);
                setButtonLabel(btnDefend, "Skill 2", BTN_SKILL2_ON);
                setButtonLabel(btnCheck,  "Skill 3", BTN_SKILL3_ON);
                setButtonLabel(btnBack,   "BACK",    BTN_BACK_PATH);

                btnFight .setDisabledIcon(makeScaledIcon(BTN_SKILL1_OFF));
                btnDefend.setDisabledIcon(makeScaledIcon(BTN_SKILL2_OFF));
                btnCheck .setDisabledIcon(makeScaledIcon(BTN_SKILL3_OFF));
                btnBack  .setDisabledIcon(makeScaledIcon(BTN_BACK_DISABLED));

                btnFight .setEnabled(skillReady(player, 1));
                btnDefend.setEnabled(skillReady(player, 2));
                btnCheck .setEnabled(skillReady(player, 3));
                btnBack  .setEnabled(true);   // re-enabled in FIGHT state

                btnFight .addActionListener(e -> playerTurn(1));
                btnDefend.addActionListener(e -> playerTurn(2));
                btnCheck .addActionListener(e -> playerTurn(3));
                btnBack  .addActionListener(e -> switchState(ActionState.MAIN));
            }

            // ── DEFEND ────────────────────────────────────────────────────
            case DEFEND -> {
                // Fight and Back are hidden in DEFEND
                btnFight.setVisible(false);
                btnBack .setVisible(false);

                int blocks = player != null ? player.getRemainingBlocks() : 0;

                // Defend button always shows current block graphic
                setButtonLabel(btnDefend, "Block (" + blocks + ")", blockPath(blocks));
                btnDefend.setDisabledIcon(makeScaledIcon(blockPath(0)));
                btnDefend.setEnabled(blocks > 0 && !defendDisabled);

                // Check button repurposed as "BACK" in defend sub-menu
                setButtonLabel(btnCheck, "BACK", BTN_BACK_PATH);
                btnCheck.setEnabled(true);

                btnDefend.addActionListener(e -> {
                    playerTurn(4);
                    int nb = player != null ? player.getRemainingBlocks() : 0;
                    setButtonLabel(btnDefend, "Block (" + nb + ")", blockPath(nb));
                    btnDefend.setDisabledIcon(makeScaledIcon(blockPath(0)));
                    if (nb <= 0) {
                        defendDisabled = true;
                        btnDefend.setEnabled(false);
                    }
                    layoutUI();
                });

                btnCheck.addActionListener(e -> switchState(ActionState.MAIN));
            }

            // ── CHECK ─────────────────────────────────────────────────────
            case CHECK -> {
                // Only Back is visible/enabled; all others hidden
                btnFight .setVisible(false);
                btnDefend.setVisible(false);
                btnCheck .setVisible(false);
                btnBack  .setVisible(true);

                setButtonLabel(btnBack, "BACK", BTN_BACK_PATH);
                btnBack.setDisabledIcon(makeScaledIcon(BTN_BACK_DISABLED));
                btnBack.setEnabled(true);

                if (player != null) {
                    dialogue.setText(
                        "[Skill Stats]\n" +
                        fmtSkill(player.getSkill1()) + "\n" +
                        fmtSkill(player.getSkill2()) + "\n" +
                        fmtSkill(player.getSkill3())
                    );
                }

                btnBack.addActionListener(e -> {
                    dialogue.setText("What will " + (player != null ? player.getCharacterName() : "?") + " do?");
                    switchState(ActionState.MAIN);
                });
            }
        }

        // Re-apply button positions after any icon swap
        if (getWidth() > 0) layoutUI();
    }

    private static boolean skillReady(GameCharacter c, int slot) {
        if (c == null) return false;
        Foundation.Skill sk = switch (slot) {
            case 1 -> c.getSkill1(); case 2 -> c.getSkill2(); default -> c.getSkill3();
        };
        return sk.getSkillCurrentCooldown() == 0 && c.getCharacterCurrentMana() >= sk.getSkillManaCost();
    }

    private static String fmtSkill(Foundation.Skill sk) {
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
        initialized    = false;
        player         = null; enemy      = null;
        playerWins     = 0;    enemyWins  = 0;    round = 1;
        defendDisabled = false; state     = ActionState.MAIN;
        playerAnimating = false; enemyAnimating = false;
        playerAnimLabel.setVisible(false);
        enemyAnimLabel .setVisible(false);
        dialogue.setText("");
        updateButtons();
        repaint();
    }

    // ── Paint ─────────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int w = getWidth(), h = getHeight();

        g.drawImage(bgImage, 0, 0, w, h, this);

        if (playerSprite != null && !playerAnimating)
            g.drawImage(playerSprite, spX, spY, spW, spH, this);
        if (enemySprite  != null && !enemyAnimating)
            g.drawImage(enemySprite,  enX, enY, enW, enH, this);

        int barW = (int)(w * 0.22);
        drawBars(g, player, spX, (int)(h * 0.02), barW);
        drawBars(g, enemy,  enX, (int)(h * 0.02), barW);

        if (player != null && enemy != null) {
            drawWinCounter(g, "P", playerWins, enemyWins, "CPU",
                           w / 2, (int)(h * 0.65));
        }

        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect((int)(w * 0.09), (int)(h * 0.62), (int)(w * 0.82), (int)(h * 0.18), 12, 12);
    }
}