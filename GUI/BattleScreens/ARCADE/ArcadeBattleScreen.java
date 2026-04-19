package GUI.BattleScreens.ARCADE;

import Foundation.*;
import GUI.BattleScreens.VersusScreen;
import GameEngines.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ArcadeBattleScreen — Gauntlet mode: fight all 8 opponents in sequence.
 *
 * BUTTON SPRITE PATHS — set these constants; set null for plain text buttons.
 *   BTN_FIGHT_PATH   = "Assets/buttons/btn_fight.png"
 *   BTN_DEFEND_PATH  = "Assets/buttons/btn_defend.png"
 *   BTN_CHECK_PATH   = "Assets/buttons/btn_check.png"
 *   BTN_BACK_PATH    = "Assets/buttons/btn_back.png"
 *
 * ARCADE RULES:
 *   • Player fights 8 opponents back-to-back (randomised order, excluding player's own character).
 *   • Best-of-3 per opponent (same as PVE).
 *   • Between fights: brief "NEXT OPPONENT" interlude, then health/mana fully restore.
 *   • If player loses a fight: GAME OVER screen shown (all buttons locked).
 *   • Clearing all 8: ARCADE CLEAR shown.
 */
public class ArcadeBattleScreen extends JPanel {

    // ── ASSET PATHS ──────────────────────────────────────────────────────────
    private static final String BTN_FIGHT_PATH   = null; // e.g. "Assets/buttons/btn_fight.png"
    private static final String BTN_DEFEND_PATH  = null;
    private static final String BTN_CHECK_PATH   = null;
    private static final String BTN_BACK_PATH    = null;
    private static final String P1_SPRITE_PATH   = "Assets/characters_left/";
    private static final String P1_SPRITE_SUFFIX = "-left.gif";
    private static final String ENEMY_SPRITE_PATH   = "Assets/characters_right/";
    private static final String ENEMY_SPRITE_SUFFIX = "-right.gif";
    private static final String BG_IMAGE_PATH    = "Assets/battleArenaScreen.gif";
    // ─────────────────────────────────────────────────────────────────────────

    private final Image bgImage;
    private Image playerSprite;
    private Image enemySprite;

    private JButton btnFight, btnDefend, btnCheck, btnBack;
    private JTextArea dialogue;
    private JLabel statusLabel;   // shows opponent counter / arcade state

    private boolean defendDisabled = false;

    private GameCharacter player;
    private GameCharacter enemy;

    private final BattleSystem system;
    private final GameSession session;
    private ActionState state = ActionState.MAIN;

    private int playerWins  = 0;
    private int enemyWins   = 0;
    private int round       = 1;

    // Arcade-specific tracking
    private List<Integer> opponentOrder = new ArrayList<>();
    private int currentOpponentIndex    = 0;
    private int opponentCount           = 8;

    private boolean initialized = false;
    private boolean arcadeOver  = false;

    private VersusScreen versusScreen;
    private CardLayout cardLayout;
    private JPanel container;

    private GUI.GameGUI gameGUI;

    public ArcadeBattleScreen() {
        setLayout(null);
        session = GameSession.getInstance();
        system  = new BattleSystem();
        bgImage = new ImageIcon(BG_IMAGE_PATH).getImage();
        createUI();
        setLayoutListeners();
    }

    public void setGameGUI(GUI.GameGUI gui) {
    this.gameGUI = gui;
}

    // Add a setter (call from GameGUI after construction):
    public void setVersusScreen(VersusScreen vs, CardLayout cl, JPanel cont) {
        this.versusScreen = vs;
        this.cardLayout   = cl;
        this.container    = cont;
    }

    // =========================
    // INIT ARCADE RUN
    // =========================
    public void initBattle() {
        if (initialized) return;

        player = session.getPlayer1();
        if (player == null) { dialogue.setText("No player selected!"); return; }

        buildOpponentOrder();
        currentOpponentIndex = 0;
        arcadeOver = false;

        loadNextOpponent();
        initialized = true;
    }

    /** Build a randomised list of the 8 character IDs, excluding the player's own. */
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
        // Ensure exactly 7 unique opponents (player excluded)
        // If shuffle left player in, already filtered above.
    }

    private void loadNextOpponent() {
        if (currentOpponentIndex >= opponentOrder.size()) {
            arcadeClear();
            return;
        }

        enemy = system.selectCharacter(opponentOrder.get(currentOpponentIndex));
        session.setPlayer2(enemy);

        enemySprite  = new ImageIcon(ENEMY_SPRITE_PATH + enemy.getCharacterName() + ENEMY_SPRITE_SUFFIX).getImage();
        playerSprite = new ImageIcon(P1_SPRITE_PATH    + player.getCharacterName() + P1_SPRITE_SUFFIX).getImage();

        playerWins = 0;
        enemyWins  = 0;
        round      = 1;
        defendDisabled = false;
        state      = ActionState.MAIN;

        updateStatusLabel();
        dialogue.setText("Opponent " + (currentOpponentIndex + 1) + "/" + opponentOrder.size()
                + ": " + enemy.getCharacterName());

        enableButtons();
        updateButtons();
        repaint();
    }

    private void updateStatusLabel() {
        statusLabel.setText("Opponent  " + (currentOpponentIndex + 1) + " / " + opponentOrder.size()
                + "   [ " + playerWins + " - " + enemyWins + " ]");
    }

    // =========================
    // UI SETUP
    // =========================
    private void createUI() {
        btnFight  = makeButton("FIGHT",  BTN_FIGHT_PATH);
        btnDefend = makeButton("DEFEND", BTN_DEFEND_PATH);
        btnCheck  = makeButton("CHECK",  BTN_CHECK_PATH);
        btnBack   = makeButton("BACK",   BTN_BACK_PATH);

        add(btnFight); add(btnDefend); add(btnCheck); add(btnBack);

        dialogue = new JTextArea();
        dialogue.setEditable(false);
        dialogue.setLineWrap(true);
        dialogue.setWrapStyleWord(true);
        dialogue.setOpaque(false);
        dialogue.setForeground(Color.WHITE);
        dialogue.setFont(new Font("SansSerif", Font.PLAIN, 13));
        add(dialogue);

        statusLabel = new JLabel("ARCADE MODE", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(255, 200, 20));
        statusLabel.setFont(new Font("Impact", Font.PLAIN, 20));
        add(statusLabel);

        btnFight.addActionListener(e -> switchState(ActionState.FIGHT));
        btnDefend.addActionListener(e -> switchState(ActionState.DEFEND));
        btnCheck.addActionListener(e -> switchState(ActionState.CHECK));
        btnBack.addActionListener(e -> switchState(ActionState.MAIN));

        updateButtons();
    }

    private JButton makeButton(String text, String imagePath) {
        JButton btn = new JButton();
        if (imagePath != null) {
            btn.setIcon(new ImageIcon(imagePath));
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setToolTipText(text);
        } else {
            btn.setText(text);
        }
        return btn;
    }

    private void setButtonLabel(JButton btn, String text, String imagePath) {
        if (imagePath != null) {
            btn.setIcon(new ImageIcon(imagePath));
            btn.setToolTipText(text);
        } else {
            btn.setText(text);
        }
    }

    private void switchState(ActionState newState) {
        state = newState;
        updateButtons();
    }

    private void log(String text) { dialogue.append("\n" + text); }

    // =========================
    // PLAYER TURN
    // =========================
    private void playerTurn(int action) {
        if (arcadeOver || player == null || enemy == null) return;

        String result = system.performAction(player, enemy, action, true);
        dialogue.setText(result);
        repaint();

        if (!enemy.isCharacterAlive()) {
            playerWins++;
            updateStatusLabel();
            endRound("You win round " + round + "!");
            return;
        }

        aiTurn();
    }

    // =========================
    // AI TURN
    // =========================
    private void aiTurn() {
        int aiAction = system.getAIAction(enemy);
        String result = system.performAction(enemy, player, aiAction, false);
        log(result);

        if (!player.isCharacterAlive()) {
            enemyWins++;
            updateStatusLabel();
            endRound(enemy.getCharacterName() + " wins round " + round + "!");
        }
        repaint();
    }

    // =========================
    // ROUND / FIGHT MANAGEMENT
    // =========================
    private void resetRound() {
        round++;
        player.setCharacterCurrentHealthPoints(player.getCharacterMaxHealthPoints());
        enemy.setCharacterCurrentHealthPoints(enemy.getCharacterMaxHealthPoints());
        player.setCharacterCurrentMana(player.getCharacterMaxMana());
        enemy.setCharacterCurrentMana(enemy.getCharacterMaxMana());
        defendDisabled = false;
        state = ActionState.MAIN;
        log("── Round " + round + " ──");
        updateStatusLabel();
        updateButtons();
    }

    private void endRound(String message) {
        log(message);

        if (playerWins == 2) {
            // Won this fight
            if (playerWins == 2) {
                log("You defeated " + enemy.getCharacterName() + "!");
                currentOpponentIndex++;
                if (currentOpponentIndex >= opponentOrder.size()) { arcadeClear(); return; }
                disableButtons();

                // Load next opponent name for VS screen, then animate
                GameCharacter nextEnemy = system.selectCharacter(opponentOrder.get(currentOpponentIndex));
                player.setCharacterCurrentHealthPoints(player.getCharacterMaxHealthPoints());
                player.setCharacterCurrentMana(player.getCharacterMaxMana());

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
        }

        if (enemyWins == 2) {
            gameOver();
            return;
        }

        resetRound();
    }

    private void arcadeClear() {
        arcadeOver = true;
    dialogue.setText("ARCADE CLEAR! You defeated all opponents!");
    statusLabel.setText("ARCADE CLEAR");
    disableButtons();

    if (gameGUI != null) {
        Timer delay = new Timer(900, e ->
                gameGUI.showGameOver(
                        player.getCharacterName(),
                        "",
                        true,
                        "MainMenu"));
        delay.setRepeats(false);
        delay.start();
    }
    }

    private void gameOver() {
        arcadeOver = true;
    dialogue.setText("GAME OVER");
    statusLabel.setText("GAME OVER");
    disableButtons();

    if (gameGUI != null) {
        Timer delay = new Timer(900, e ->
                gameGUI.showGameOver(
                        enemy.getCharacterName(),
                        player.getCharacterName(),
                        false,
                        "MainMenu"));
        delay.setRepeats(false);
        delay.start();
    }
    }

    private void enableButtons() {
        btnFight.setEnabled(true);
        btnDefend.setEnabled(true);
        btnCheck.setEnabled(true);
        btnBack.setEnabled(true);
    }

    private void disableButtons() {
        btnFight.setEnabled(false);
        btnDefend.setEnabled(false);
        btnCheck.setEnabled(false);
        btnBack.setEnabled(false);
    }

    // =================a========
    // LAYOUT
    // =========================
    private void setLayoutListeners() {
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) { layoutUI(); }
        });
    }

    private void layoutUI() {
        int w = getWidth(), h = getHeight();

        statusLabel.setBounds((int)(w * 0.25), (int)(h * 0.60), (int)(w * 0.50), 28);

        dialogue.setBounds(
                (int)(w * 0.10), (int)(h * 0.65),
                (int)(w * 0.80), (int)(h * 0.15)
        );

        btnFight.setBounds((int)(w * 0.15), (int)(h * 0.85), 100, 40);
        btnDefend.setBounds((int)(w * 0.35), (int)(h * 0.85), 100, 40);
        btnCheck.setBounds((int)(w * 0.55), (int)(h * 0.85), 100, 40);
        btnBack.setBounds((int)(w * 0.75), (int)(h * 0.85), 100, 40);
    }

    private void updateButtons() {
        resetAllButtons();

        btnFight.setVisible(true); btnBack.setVisible(true);
        btnDefend.setVisible(true); btnCheck.setVisible(true);

        switch (state) {
            case MAIN -> {
                setButtonLabel(btnFight,  "FIGHT",  BTN_FIGHT_PATH);
                setButtonLabel(btnDefend, "DEFEND", BTN_DEFEND_PATH);
                setButtonLabel(btnCheck,  "CHECK",  BTN_CHECK_PATH);
                setButtonLabel(btnBack,   "BACK",   BTN_BACK_PATH);
                btnFight.setEnabled(true);
                btnDefend.setEnabled(!defendDisabled);
                btnCheck.setEnabled(true);
                btnBack.setEnabled(false);
                btnFight.addActionListener(e -> switchState(ActionState.FIGHT));
                btnDefend.addActionListener(e -> switchState(ActionState.DEFEND));
                btnCheck.addActionListener(e -> switchState(ActionState.CHECK));
            }
            case FIGHT -> {
                setButtonLabel(btnFight,  "Skill 1", BTN_FIGHT_PATH);
                setButtonLabel(btnDefend, "Skill 2", BTN_DEFEND_PATH);
                setButtonLabel(btnCheck,  "Skill 3", BTN_CHECK_PATH);
                setButtonLabel(btnBack,   "BACK",    BTN_BACK_PATH);
                btnFight.setEnabled(true); btnDefend.setEnabled(true);
                btnCheck.setEnabled(true); btnBack.setEnabled(true);
                btnFight.addActionListener(e -> playerTurn(1));
                btnDefend.addActionListener(e -> playerTurn(2));
                btnCheck.addActionListener(e -> playerTurn(3));
                btnBack.addActionListener(e -> switchState(ActionState.MAIN));
            }
            case DEFEND -> {
                btnFight.setVisible(false); btnBack.setVisible(false);
                setButtonLabel(btnDefend, "Block (" + (player != null ? player.getRemainingBlocks() : 0) + ")", BTN_DEFEND_PATH);
                setButtonLabel(btnCheck,  "BACK", BTN_CHECK_PATH);
                btnDefend.setEnabled(player != null && player.getRemainingBlocks() > 0 && !defendDisabled);
                btnCheck.setEnabled(true);
                btnDefend.addActionListener(e -> {
                    playerTurn(4);
                    setButtonLabel(btnDefend, "Block (" + (player != null ? player.getRemainingBlocks() : 0) + ")", BTN_DEFEND_PATH);
                    if (player != null && player.getRemainingBlocks() <= 0) {
                        defendDisabled = true;
                        btnDefend.setEnabled(false);
                    }
                });
                btnCheck.addActionListener(e -> {
                    state = ActionState.MAIN;
                    btnFight.setVisible(true); btnBack.setVisible(true);
                    updateButtons();
                });
            }
            case CHECK -> {
                if (player instanceof SkillsInterface skills) {
                    dialogue.setText(
                            skills.getSkill1().getSkillName() + " | DMG: " + skills.getSkill1().getSkillDamage() + " | MP: " + skills.getSkill1().getSkillManaCost()
                                    + "\n" + skills.getSkill2().getSkillName() + " | DMG: " + skills.getSkill2().getSkillDamage() + " | MP: " + skills.getSkill2().getSkillManaCost()
                                    + "\n" + skills.getSkill3().getSkillName() + " | DMG: " + skills.getSkill3().getSkillDamage() + " | MP: " + skills.getSkill3().getSkillManaCost());
                }
                btnFight.setEnabled(false); btnDefend.setEnabled(false); btnCheck.setEnabled(false);
                btnBack.setEnabled(true);
                btnBack.addActionListener(e -> switchState(ActionState.MAIN));
            }
        }
    }

    // =========================
    // DRAW
    // =========================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);

        int sw = (int)(getWidth()  * 0.28);
        int sh = (int)(getHeight() * 0.48);
        int sy = (int)(getHeight() * 0.10);

        if (playerSprite != null)
            g.drawImage(playerSprite, (int)(getWidth() * 0.08), sy, sw, sh, this);

        if (enemySprite != null)
            g.drawImage(enemySprite, (int)(getWidth() * 0.64), sy, sw, sh, this);

        // Opponent number badge
        if (enemy != null) {
            g.setFont(new Font("Impact", Font.PLAIN, 15));
            g.setColor(new Color(255, 200, 20));
            g.drawString("Opp. " + (currentOpponentIndex + 1) + "/" + opponentOrder.size(),
                    (int)(getWidth() * 0.64), (int)(getHeight() * 0.08));
        }
    }

    public void reset() {
        initialized          = false;
        arcadeOver           = false;
        currentOpponentIndex = 0;
        playerWins           = 0;
        enemyWins            = 0;
        round                = 1;
        defendDisabled       = false;
        state                = ActionState.MAIN;
        opponentOrder.clear();
        dialogue.setText("");
        statusLabel.setText("ARCADE MODE");
        repaint();
    }

    // =========================
    // HELPERS
    // =========================
    private void clearListeners(JButton btn) {
        for (ActionListener al : btn.getActionListeners()) btn.removeActionListener(al);
    }

    private void resetAllButtons() {
        clearListeners(btnFight); clearListeners(btnDefend);
        clearListeners(btnCheck); clearListeners(btnBack);
    }

    private enum ActionState { MAIN, FIGHT, DEFEND, CHECK }
}