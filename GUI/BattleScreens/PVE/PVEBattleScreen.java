package GUI.BattleScreens.PVE;

import Foundation.*;
import GUI.BattleScreens.BaseBattleScreen;
import GUI.GameGUI;
import GameEngines.*;

import javax.swing.*;
import java.awt.*;

public class PVEBattleScreen extends BaseBattleScreen {

    private final Image bgImage;
    private Image playerSprite;
    private Image enemySprite;

    private JButton btnFight, btnDefend, btnCheck, btnBack;
    private JTextArea    dialogue;
    private JScrollPane  dialogueScroll;

    private GameCharacter player;
    private GameCharacter enemy;
    private boolean defendDisabled = false;
    private ActionState state = ActionState.MAIN;
    private JLabel roundLabel;

    private int playerWins = 0;
    private int enemyWins  = 0;
    private int round      = 1;

    private boolean initialized = false;
    private GameGUI gameGUI;

    private final BattleSystem system;
    private final GameSession  session;

    private int spX, spY, spW, spH;
    private int enX, enY, enW, enH;

    public PVEBattleScreen() {
        setLayout(null);
        session = GameSession.getInstance();
        system  = new BattleSystem();
        // PVE/PVP arena background
        bgImage = new ImageIcon("Assets/battle_sprites/pvp_pve_battlearena.gif").getImage();
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
        player = session.getPlayer1();
        enemy  = session.getPlayer2();

        if (player == null || enemy == null) {
            dialogue.setText("Battle not initialised!");
            scrollToBottom();
            return;
        }

        playerSprite = new ImageIcon(IDLE_LEFT_DIR  + player.getSpriteKey() + IDLE_L_SFX).getImage();
        enemySprite  = new ImageIcon(IDLE_RIGHT_DIR + enemy.getSpriteKey()  + IDLE_R_SFX).getImage();

        dialogue.setText("Battle started!\nWhat will " + player.getCharacterName() + " do?");
        scrollToBottom();
        initialized = true;
        roundLabel.setText("ROUND " + round);

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
        dialogueScroll = new JScrollPane(dialogue);
        dialogueScroll.setBorder(null);
        dialogueScroll.setOpaque(false);
        dialogueScroll.getViewport().setOpaque(false);
        dialogueScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dialogueScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(dialogueScroll);

        roundLabel = new JLabel("ROUND 1", SwingConstants.CENTER);
        roundLabel.setForeground(new Color(255, 220, 30));
        roundLabel.setFont(new Font("Impact", Font.PLAIN, 26));
        add(roundLabel);

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

        spW = (int)(w * 0.28); spH = (int)(h * 0.48);
        spX = (int)(w * 0.08); spY = (int)(h * 0.35);
        enW = spW;              enH = spH;
        enX = (int)(w * 0.64); enY = spY;


        dialogueScroll.setBounds((int)(w * 0.10), (int)(h * 0.63), (int)(w * 0.80), (int)(h * 0.16));

        int btnY = (int)(h * 0.83);
        sizeToIcon(btnFight,  (int)(w * 0.10), btnY);
        sizeToIcon(btnDefend, (int)(w * 0.30), btnY);
        sizeToIcon(btnCheck,  (int)(w * 0.55), btnY);
        sizeToIcon(btnBack,   (int)(w * 0.76), btnY);

        roundLabel.setBounds((int)(w * 0.35), (int)(h * 0.03), (int)(w * 0.30), 40);
    }

    // ── Turn logic ────────────────────────────────────────────────────────

    private void playerTurn(int action) {
        if (player == null || enemy == null) return;

        int animDelay = 0;
        if (action >= 1 && action <= 3) {
            showPlayerSkillAnim(player.getSpriteKey(), action);
            animDelay = 1500;
        }

        String result = system.performAction(player, enemy, action, true);
        dialogue.setText(result);
        scrollToBottom();
        reduceCooldowns(player);
        repaint();

        if (!enemy.isCharacterAlive()) {
            playerWins++;
            endRound("You win round " + round + "!");
            return;
        }

        if (action != 4) switchState(ActionState.MAIN);

        btnFight.setEnabled(false);
        btnDefend.setEnabled(false);
        btnCheck.setEnabled(false);

        Timer aiDelay = new Timer(animDelay, e -> {
            aiTurn();
            if (player != null && player.isCharacterAlive() && enemy != null && enemy.isCharacterAlive()) {
                updateButtons();
            }
        });
        aiDelay.setRepeats(false);
        aiDelay.start();
    }

    private void aiTurn() {
        int aiAction = system.getAIAction(enemy);
        if (aiAction >= 1 && aiAction <= 3) showEnemySkillAnim(enemy.getSpriteKey(), aiAction);

        String result = system.performAction(enemy, player, aiAction, false);
        dialogue.append("\n" + enemy.getCharacterName() + ": " + result);
        scrollToBottom();
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

        player.resetForNewRound();
        enemy.resetForNewRound();

        player.getSkill1().resetCooldown();
        player.getSkill2().resetCooldown();
        player.getSkill3().resetCooldown();
        enemy.getSkill1().resetCooldown();
        enemy.getSkill2().resetCooldown();
        enemy.getSkill3().resetCooldown();

        defendDisabled = false;
        state = ActionState.MAIN;
        dialogue.setText("-- Round " + round + " --\nWhat will " + player.getCharacterName() + " do?");
        scrollToBottom();
        roundLabel.setText("ROUND " + round);
        updateButtons();
        repaint();
    }

    private void endRound(String message) {
        dialogue.append("\n" + message);
        scrollToBottom();

        if (playerWins == 2) {
            dialogue.append("\nYOU WON THE MATCH!");
            scrollToBottom();
            disableButtons();
            delay(900, () -> gameGUI.showGameOver(
                    player.getCharacterName(), enemy.getCharacterName(), true, "MainMenu"));
            return;
        }
        if (enemyWins == 2) {
            dialogue.append("\nYOU LOST THE MATCH!");
            scrollToBottom();
            disableButtons();
            delay(900, () -> gameGUI.showGameOver(
                    enemy.getCharacterName(), player.getCharacterName(), false, "MainMenu"));
            return;
        }
        resetRound();
    }

    private void disableButtons() {
        for (JButton b : new JButton[]{btnFight, btnDefend, btnCheck, btnBack})
            b.setEnabled(false);
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

    private void updateButtons() {
        resetAllButtons();

        btnFight .setVisible(true);
        btnDefend.setVisible(true);
        btnCheck .setVisible(true);
        btnBack  .setVisible(true);

        switch (state) {
            case MAIN -> {
                setButtonLabel(btnFight,  "FIGHT",  BTN_FIGHT_PATH);
                setButtonLabel(btnDefend, "DEFEND", BTN_DEFEND_PATH);
                setButtonLabel(btnCheck,  "CHECK",  BTN_CHECK_PATH);
                setButtonLabel(btnBack,   "BACK",   BTN_BACK_PATH);
                btnBack.setDisabledIcon(makeScaledIcon(BTN_BACK_DISABLED));

                btnFight .setEnabled(true);
                btnDefend.setEnabled(true);
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

                btnFight .setEnabled(skillReady(player, 1));
                btnDefend.setEnabled(skillReady(player, 2));
                btnCheck .setEnabled(skillReady(player, 3));
                btnBack  .setEnabled(true);

                btnFight .addActionListener(e -> playerTurn(1));
                btnDefend.addActionListener(e -> playerTurn(2));
                btnCheck .addActionListener(e -> playerTurn(3));
                btnBack  .addActionListener(e -> switchState(ActionState.MAIN));
            }

            case DEFEND -> {
                btnFight.setVisible(false);
                btnBack .setVisible(false);

                int blocks = player != null ? player.getRemainingBlocks() : 0;
                setButtonLabel(btnDefend, "Block (" + blocks + ")", blockPath(blocks));
                btnDefend.setDisabledIcon(makeScaledIcon(blockPath(0)));
                btnDefend.setEnabled(blocks > 0 && !defendDisabled);

                setButtonLabel(btnCheck, "BACK", BTN_BACK_PATH);
                btnCheck.setEnabled(true);

                btnDefend.addActionListener(e -> {
                    int nb = player != null ? player.getRemainingBlocks() : 0;
                    if (nb <= 0) return;
                    playerTurn(4);
                    int remaining = player != null ? player.getRemainingBlocks() : 0;
                    if (remaining <= 0) defendDisabled = true;
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

                if (player != null) {
                    dialogue.setText(
                        "[Skill Stats]\n" +
                        fmtSkill(player.getSkill1()) + "\n" +
                        fmtSkill(player.getSkill2()) + "\n" +
                        fmtSkill(player.getSkill3())
                    );
                    scrollToBottom();
                }

                btnBack.addActionListener(e -> {
                    dialogue.setText("What will " + (player != null ? player.getCharacterName() : "?") + " do?");
                    scrollToBottom();
                    switchState(ActionState.MAIN);
                });
            }
        }

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

    private void scrollToBottom() {
        if (dialogueScroll == null) return;
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = dialogueScroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    // ── Full reset ────────────────────────────────────────────────────────

    public void reset() {
        initialized     = false;
        player          = null; enemy         = null;
        playerWins      = 0;    enemyWins     = 0;    round = 1;
        defendDisabled  = false; state        = ActionState.MAIN;
        playerAnimating = false; enemyAnimating = false;
        playerAnimLabel.setVisible(false);
        enemyAnimLabel .setVisible(false);
        dialogue.setText("");
        scrollToBottom();
        updateButtons();
        repaint();
    }

    // ── Paint ─────────────────────────────────────────────────────────────

    @Override
    public void doLayout() {
        super.doLayout();
        layoutUI();
    }

    @Override protected int playerCharCenterX() { return spX + spW / 2; }
    @Override protected int playerCharCenterY() { return spY + spH / 2; }
    @Override protected int enemyCharCenterX()  { return enX + enW / 2; }
    @Override protected int enemyCharCenterY()  { return enY + enH / 2; }

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

        if (player != null && enemy != null)
            drawWinCounter(g, "P", playerWins, enemyWins, "CPU",
               w / 2,
               (int)(h * 0.15));

        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect((int)(w * 0.09), (int)(h * 0.62), (int)(w * 0.82), (int)(h * 0.18), 12, 12);
    }
}