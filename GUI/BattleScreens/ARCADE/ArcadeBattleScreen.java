package GUI.BattleScreens.ARCADE;

import Foundation.*;
import GUI.BattleScreens.BaseBattleScreen;
import GUI.BattleScreens.VersusScreen;
import GameEngines.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArcadeBattleScreen extends BaseBattleScreen {

    private final Image bgImage;
    private Image playerSprite;
    private Image enemySprite;

    private JButton btnFight, btnDefend, btnCheck, btnBack;
    private JTextArea    dialogue;
    private JScrollPane  dialogueScroll;
    private JLabel statusLabel;

    private int spX, spY, spW, spH;
    private int enX, enY, enW, enH;

    private boolean defendDisabled = false;

    private GameCharacter player;
    private GameCharacter enemy;

    private final BattleSystem system;
    private final GameSession  session;
    private ActionState state = ActionState.MAIN;

    private int playerWins  = 0;
    private int enemyWins   = 0;
    private int round       = 1;

    private List<Integer> opponentOrder = new ArrayList<>();
    private int currentOpponentIndex    = 0;
    private int opponentCount           = 8;

    private boolean initialized = false;
    private boolean prepared    = false;
    private boolean arcadeOver  = false;

    private VersusScreen versusScreen;
    private CardLayout cardLayout;
    private JPanel container;
    private GUI.GameGUI gameGUI;
    private JLabel roundLabel;

    public ArcadeBattleScreen() {
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

    public void setGameGUI(GUI.GameGUI gui) { this.gameGUI = gui; }

    public void setVersusScreen(VersusScreen vs, CardLayout cl, JPanel cont) {
        this.versusScreen = vs; this.cardLayout = cl; this.container = cont;
    }

    // ── Two-phase init ────────────────────────────────────────────────────

    public String prepareAndGetFirstOpponent() {
        player = session.getPlayer1();
        if (player == null) return null;

        buildOpponentOrder();
        currentOpponentIndex = 0;
        arcadeOver = false;
        prepared   = true;

        if (opponentOrder.isEmpty()) return null;

        GameCharacter first = system.selectCharacter(opponentOrder.get(0));
        return first != null ? first.getCharacterName() : null;
    }

    public void initBattle() {
        if (initialized) return;

        if (!prepared) {
            player = session.getPlayer1();
            if (player == null) { dialogue.setText("No player selected!");
        scrollToBottom(); return; }
            buildOpponentOrder();
            currentOpponentIndex = 0;
            arcadeOver = false;
        }

        prepared    = false;
        initialized = true;
        loadNextOpponent();
        roundLabel.setText("ROUND " + round);
    }

    private void buildOpponentOrder() {
        opponentOrder.clear();
        List<Integer> all = new ArrayList<>();
        for (int i = 1; i <= opponentCount; i++) all.add(i);
        Collections.shuffle(all);

        for (int id : all) {
            GameCharacter candidate = system.selectCharacter(id);
            if (candidate != null && !candidate.getCharacterName().equals(player.getCharacterName())) {
                opponentOrder.add(id);
            }
        }
    }

    private String toFileKey(String name) {
        return name.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    private void loadNextOpponent() {
        if (currentOpponentIndex >= opponentOrder.size()) { arcadeClear(); return; }

        initialized = false; // allow re-init for next opponent
        enemy = system.selectCharacter(opponentOrder.get(currentOpponentIndex));
        session.setPlayer2(enemy);

        String leftKey  = toFileKey(player.getCharacterName());
        String rightKey = toFileKey(enemy.getCharacterName());

        playerSprite = new ImageIcon(IDLE_LEFT_DIR  + leftKey  + IDLE_L_SFX).getImage();
        enemySprite  = new ImageIcon(IDLE_RIGHT_DIR + rightKey + IDLE_R_SFX).getImage();

        playerWins     = 0; enemyWins = 0; round = 1;
        roundLabel.setText("ROUND " + round);
        defendDisabled = false; state = ActionState.MAIN;

        playerAnimating = false;
        enemyAnimating  = false;
        if (playerAnimLabel != null) playerAnimLabel.setVisible(false);
        if (enemyAnimLabel  != null) enemyAnimLabel .setVisible(false);

        updateStatusLabel();
        dialogue.setText("Opponent " + (currentOpponentIndex + 1) + "/" + opponentOrder.size()
                       + ": " + enemy.getCharacterName());
        scrollToBottom();

        enableButtons(); updateButtons(); repaint();
    }

    private void updateStatusLabel() {
        statusLabel.setText("Opponent  " + (currentOpponentIndex + 1) + " / " + opponentOrder.size()
                          + "   [ " + playerWins + " - " + enemyWins + " ]");
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
        dialogue.setEditable(false); dialogue.setLineWrap(true); dialogue.setWrapStyleWord(true);
        dialogue.setOpaque(false); dialogue.setForeground(Color.WHITE);
        dialogue.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dialogueScroll = new JScrollPane(dialogue);
        dialogueScroll.setBorder(null);
        dialogueScroll.setOpaque(false);
        dialogueScroll.getViewport().setOpaque(false);
        dialogueScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        dialogueScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(dialogueScroll);

        statusLabel = new JLabel("ARCADE MODE", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(255, 200, 20));
        statusLabel.setFont(new Font("Impact", Font.PLAIN, 20));
        add(statusLabel);

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
        spX = (int)(w * 0.08); spY = (int)(h * 0.10);
        enW = spW; enH = spH;
        enX = (int)(w * 0.64); enY = spY;

        int animW = spW * 2, animH = spH * 2;
        playerAnimLabel.setBounds(spX - spW / 2, spY - spH / 2, animW, animH);
        enemyAnimLabel .setBounds(enX - enW / 2, enY - enH / 2, animW, animH);

        dialogueScroll.setBounds((int)(w * 0.10), (int)(h * 0.65), (int)(w * 0.80), (int)(h * 0.15));

        int btnY = (int)(h * 0.85);
        sizeToIcon(btnFight,  (int)(w * 0.10), btnY);
        sizeToIcon(btnDefend, (int)(w * 0.30), btnY);
        sizeToIcon(btnCheck,  (int)(w * 0.55), btnY);
        sizeToIcon(btnBack,   (int)(w * 0.76), btnY);

        roundLabel.setBounds((int)(w * 0.35), (int)(h * 0.03), (int)(w * 0.30), 40);

        int spacing = 5;
        statusLabel.setBounds(
            roundLabel.getX(),
            roundLabel.getY() + roundLabel.getHeight() + spacing,
            roundLabel.getWidth(),
            28
        );
    }

    // ── Turn logic ────────────────────────────────────────────────────────

    private void playerTurn(int action) {
        if (arcadeOver || player == null || enemy == null) return;

        int animDelay = 0;
        if (action >= 1 && action <= 3) {
            showPlayerSkillAnim(player.getSpriteKey(), action);
            animDelay = 1500;
        }

        String result = system.performAction(player, enemy, action, true);
        dialogue.setText(result);
        scrollToBottom();
        player.getSkill1().reduceSkillCooldown();
        player.getSkill2().reduceSkillCooldown();
        player.getSkill3().reduceSkillCooldown();
        repaint();

        if (!enemy.isCharacterAlive()) {
            playerWins++; updateStatusLabel(); endRound("You win round " + round + "!"); return;
        }

        if (action != 4) switchState(ActionState.MAIN);

        // Disable buttons while animation plays and AI is responding
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
        dialogue.append("\n" + result);
        scrollToBottom();
        enemy.getSkill1().reduceSkillCooldown();
        enemy.getSkill2().reduceSkillCooldown();
        enemy.getSkill3().reduceSkillCooldown();

        if (!player.isCharacterAlive()) {
            enemyWins++; updateStatusLabel();
            endRound(enemy.getCharacterName() + " wins round " + round + "!");
        }
        repaint();
    }

    // ── Round management ──────────────────────────────────────────────────

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
        dialogue.append("\n-- Round " + round + " --");
        scrollToBottom();
        roundLabel.setText("ROUND " + round);

        updateStatusLabel();
        updateButtons();
        repaint();
    }

    private void endRound(String message) {
        dialogue.append("\n" + message);
        scrollToBottom();

        if (playerWins == 2) {
            dialogue.append("\nYou defeated " + enemy.getCharacterName() + "!");
        scrollToBottom();
            currentOpponentIndex++;
            if (currentOpponentIndex >= opponentOrder.size()) { arcadeClear(); return; }
            disableButtons();

            GameCharacter nextEnemy = system.selectCharacter(opponentOrder.get(currentOpponentIndex));
            player.resetForNewRound();

            if (versusScreen != null && cardLayout != null) {
                cardLayout.show(container, "VersusScreen");
                versusScreen.show(player.getCharacterName(), nextEnemy.getCharacterName(), () -> {
                    cardLayout.show(container, "ArcadeBattleScreen");
                    loadNextOpponent();
                });
            } else {
                Timer t = new Timer(1800, e -> loadNextOpponent());
                t.setRepeats(false); t.start();
            }
            return;
        }

        if (enemyWins == 2) { gameOver(); return; }

        resetRound();
    }

    private void arcadeClear() {
        arcadeOver = true;
        dialogue.setText("ARCADE CLEAR! You defeated all opponents!");
        scrollToBottom();
        statusLabel.setText("ARCADE CLEAR");
        disableButtons();

        if (gameGUI != null) {
            Timer delay = new Timer(900, e ->
                gameGUI.showGameOver(player.getCharacterName(), "", true, "MainMenu"));
            delay.setRepeats(false); delay.start();
        }
    }

    private void gameOver() {
        arcadeOver = true;
        dialogue.setText("GAME OVER");
        scrollToBottom();
        statusLabel.setText("GAME OVER");
        disableButtons();

        if (gameGUI != null) {
            Timer delay = new Timer(900, e ->
                gameGUI.showGameOver(enemy.getCharacterName(), player.getCharacterName(), false, "MainMenu"));
            delay.setRepeats(false); delay.start();
        }
    }

    private void enableButtons() {
        btnFight.setEnabled(true); btnDefend.setEnabled(true);
        btnCheck.setEnabled(true); btnBack.setEnabled(true);
    }

    private void disableButtons() {
        btnFight.setEnabled(false); btnDefend.setEnabled(false);
        btnCheck.setEnabled(false); btnBack.setEnabled(false);
    }

    // ── State machine ─────────────────────────────────────────────────────

    private void switchState(ActionState newState) { state = newState; updateButtons(); }

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

                boolean s1 = player != null && player.getSkill1().getSkillCurrentCooldown() == 0
                          && player.getCharacterCurrentMana() >= player.getSkill1().getSkillManaCost();
                boolean s2 = player != null && player.getSkill2().getSkillCurrentCooldown() == 0
                          && player.getCharacterCurrentMana() >= player.getSkill2().getSkillManaCost();
                boolean s3 = player != null && player.getSkill3().getSkillCurrentCooldown() == 0
                          && player.getCharacterCurrentMana() >= player.getSkill3().getSkillManaCost();

                btnFight .setEnabled(s1);
                btnDefend.setEnabled(s2);
                btnCheck .setEnabled(s3);
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
                    dialogue.setText("Opponent " + (currentOpponentIndex + 1) + "/"
                                   + opponentOrder.size() + ": " + (enemy != null ? enemy.getCharacterName() : "?"));
        scrollToBottom();
                    switchState(ActionState.MAIN);
                });
            }
        }

        if (getWidth() > 0) layoutUI();
    }

    private static String fmtSkill(Foundation.Skill sk) {
        return sk.getSkillName()
             + " | DMG: "  + sk.getSkillDamage()
             + "  MP: "    + sk.getSkillManaCost()
             + "  CD: "    + sk.getSkillCurrentCooldown() + "/" + sk.getSkillMaxCooldown();
    }

    private void resetAllButtons() {
        clearListeners(btnFight); clearListeners(btnDefend);
        clearListeners(btnCheck); clearListeners(btnBack);
    }

    // ── Paint ─────────────────────────────────────────────────────────────

    @Override
    public void doLayout() {
        super.doLayout();
        layoutUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);

        if (playerSprite != null && !playerAnimating)
            g.drawImage(playerSprite, spX, spY, spW, spH, this);
        if (enemySprite != null && !enemyAnimating)
            g.drawImage(enemySprite, enX, enY, enW, enH, this);

        int w = getWidth(), h = getHeight();
        int barW = (int)(w * 0.22);
        drawBars(g, player, spX, (int)(h * 0.02), barW);
        drawBars(g, enemy,  enX, (int)(h * 0.02), barW);

        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect((int)(w * 0.09), (int)(h * 0.64), (int)(w * 0.82), (int)(h * 0.17), 12, 12);
    }

    // ── Scroll dialogue to latest entry ──────────────────────────────────
    private void scrollToBottom() {
        if (dialogueScroll == null) return;
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = dialogueScroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    // ── Full reset ────────────────────────────────────────────────────────

    public void reset() {
        initialized          = false; arcadeOver           = false;
        prepared             = false;
        currentOpponentIndex = 0;     playerWins           = 0;
        enemyWins            = 0;     round                = 1;
        defendDisabled       = false; state                = ActionState.MAIN;
        playerAnimating      = false; enemyAnimating       = false;
        opponentOrder.clear();
        if (playerAnimLabel != null) playerAnimLabel.setVisible(false);
        if (enemyAnimLabel  != null) enemyAnimLabel .setVisible(false);
        dialogue.setText("");
        scrollToBottom();
        statusLabel.setText("ARCADE MODE");
        repaint();
    }

    private enum ActionState { MAIN, FIGHT, DEFEND, CHECK }
}