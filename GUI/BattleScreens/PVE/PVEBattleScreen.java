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
    private int spX, spY, spW, spH;  // player sprite area
    private int enX, enY, enW, enH;  // enemy  sprite area

    public PVEBattleScreen() {
        setLayout(null);
        session = GameSession.getInstance();
        system  = new BattleSystem();
        bgImage = new ImageIcon(BG_PATH).getImage();
        createUI();
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) { layoutUI(); }
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
        btnBack.setDisabledIcon(new ImageIcon(BTN_BACK_DISABLED));

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

    // ── Layout ────────────────────────────────────────────────────────────
    // Vertical zones (% of panel height):
    //   0.02 – 0.11  →  HP/MP bars (above sprites)
    //   0.13 – 0.61  →  character sprites
    //   0.63 – 0.79  →  dialogue tray
    //   0.82         →  action buttons

    private void layoutUI() {
        int w = getWidth(), h = getHeight();
        if (w == 0 || h == 0) return;

        // Sprite areas
        spW = (int)(w * 0.28); spH = (int)(h * 0.48);
        spX = (int)(w * 0.08); spY = (int)(h * 0.13);
        enW = spW;              enH = spH;
        enX = (int)(w * 0.64); enY = spY;

        // Skill animation overlays sit exactly over the sprite areas
        playerAnimLabel.setBounds(spX, spY, spW, spH);
        enemyAnimLabel .setBounds(enX, enY, enW, enH);

        // Dialogue sits below the sprites
        dialogue.setBounds((int)(w * 0.10), (int)(h * 0.63), (int)(w * 0.80), (int)(h * 0.16));

        // Buttons — sized to their icon's natural pixel dimensions
        int btnY = (int)(h * 0.83);
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

    private void updateButtons() {
        resetAllButtons();

        btnFight.setVisible(true); btnDefend.setVisible(true);
        btnCheck.setVisible(true); btnBack.setVisible(true);

        switch (state) {
            case MAIN -> {
                setButtonLabel(btnFight,  "FIGHT",   BTN_FIGHT_PATH);
                setButtonLabel(btnDefend, "DEFEND",  BTN_DEFEND_PATH);
                setButtonLabel(btnCheck,  "CHECK",   BTN_CHECK_PATH);
                setButtonLabel(btnBack,   "REMATCH", BTN_BACK_PATH);

                btnFight.setEnabled(true);
                btnDefend.setEnabled(!defendDisabled);
                btnCheck.setEnabled(true);
                btnBack.setEnabled(true);

                btnFight .addActionListener(e -> switchState(ActionState.FIGHT));
                btnDefend.addActionListener(e -> switchState(ActionState.DEFEND));
                btnCheck .addActionListener(e -> switchState(ActionState.CHECK));
                btnBack  .addActionListener(e -> { reset(); initBattle(); });
            }
            case FIGHT -> {
                setButtonLabel(btnFight,  "Skill 1", BTN_SKILL1_ON);
                setButtonLabel(btnDefend, "Skill 2", BTN_SKILL2_ON);
                setButtonLabel(btnCheck,  "Skill 3", BTN_SKILL3_ON);
                setButtonLabel(btnBack,   "BACK",    BTN_BACK_PATH);

                btnFight .setDisabledIcon(new ImageIcon(BTN_SKILL1_OFF));
                btnDefend.setDisabledIcon(new ImageIcon(BTN_SKILL2_OFF));
                btnCheck .setDisabledIcon(new ImageIcon(BTN_SKILL3_OFF));

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
                setButtonLabel(btnCheck,  "BACK",                    BTN_CHECK_PATH);

                btnDefend.setEnabled(blocks > 0 && !defendDisabled);
                btnCheck .setEnabled(true);

                btnDefend.addActionListener(e -> {
                    playerTurn(4);
                    int nb = player != null ? player.getRemainingBlocks() : 0;
                    setButtonLabel(btnDefend, "Block (" + nb + ")", blockPath(nb));
                    if (nb <= 0) { defendDisabled = true; btnDefend.setEnabled(false); }
                    layoutUI();
                });
                btnCheck.addActionListener(e -> switchState(ActionState.MAIN));
            }
            case CHECK -> {
                if (player != null) {
                    dialogue.setText(
                        "[Skill Stats]\n" +
                        fmtSkill(player.getSkill1()) + "\n" +
                        fmtSkill(player.getSkill2()) + "\n" +
                        fmtSkill(player.getSkill3())
                    );
                }
                btnFight.setEnabled(false); btnDefend.setEnabled(false); btnCheck.setEnabled(false);
                btnBack.setEnabled(true);
                btnBack.addActionListener(e -> {
                    dialogue.setText("What will " + (player != null ? player.getCharacterName() : "?") + " do?");
                    switchState(ActionState.MAIN);
                });
            }
        }

        // Re-apply button positions after icon swap
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
        return sk.getSkillName() + " | DMG:" + sk.getSkillDamage()
             + " MP:" + sk.getSkillManaCost()
             + " CD:" + sk.getSkillCurrentCooldown() + "/" + sk.getSkillMaxCooldown();
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

        // Idle sprites — hidden while skill animation is playing
        if (playerSprite != null && !playerAnimating)
            g.drawImage(playerSprite, spX, spY, spW, spH, this);
        if (enemySprite  != null && !enemyAnimating)
            g.drawImage(enemySprite,  enX, enY, enW, enH, this);

        // HP / MP bars drawn ABOVE the character sprites
        int barW = (int)(w * 0.22);
        drawBars(g, player, spX,              (int)(h * 0.02), barW);
        drawBars(g, enemy,  enX,              (int)(h * 0.02), barW);

        // Win counter centred between the two characters
        if (player != null && enemy != null) {
            drawWinCounter(g, "P", playerWins, enemyWins, "CPU",
                           w / 2, (int)(h * 0.65));
        }

        // Semi-transparent tray behind the dialogue text
        g.setColor(new Color(0, 0, 0, 140));
        g.fillRoundRect((int)(w * 0.09), (int)(h * 0.62), (int)(w * 0.82), (int)(h * 0.18), 12, 12);
    }
}