package GUI.BattleScreens.PVE;

import Foundation.*;
import GameEngines.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PVEBattleScreen extends JPanel {

    private final Image bgImage;
    private Image playerSprite;
    private Image enemySprite;

    private JButton btnFight, btnDefend, btnCheck, btnBack;
    private JTextArea dialogue;
    private boolean defendDisabled = false;

    private GameCharacter player;
    private GameCharacter enemy;

    private final BattleSystem system;
    private final GameSession session;
    private ActionState state = ActionState.MAIN;

    private int playerWins = 0;
    private int enemyWins = 0;
    private int round = 1;

    private boolean initialized = false;

    public PVEBattleScreen() {

        setLayout(null);

        session = GameSession.getInstance();
        system = new BattleSystem();

        bgImage = new ImageIcon("Assets/battleArenaScreen.gif").getImage();

        createUI();
        setLayoutListeners();
    }

    // =========================
    // INITIALIZE BATTLE (CALL ON SCREEN SHOW)
    // =========================
    public void initBattle() {
        if (initialized) return;
        player = session.getPlayer1();
        enemy  = session.getPlayer2();   // already set by GameGUI

        if (player == null || enemy == null) { dialogue.setText("Not initialized!"); return; }

        playerSprite = loadSprite(player.getCharacterName());
        enemySprite  = loadSprite(enemy.getCharacterName());
        dialogue.setText("Battle Started!");
        initialized = true;
    }

    // =========================
    // LOAD SPRITES
    // =========================
    private Image loadSprite(String name) {
        return new ImageIcon("Assets/characters_idle/" + name + "-idle.gif").getImage();
    }

    // =========================
    // UI SETUP
    // =========================
    private void createUI() {
        btnFight = new JButton("FIGHT");
        btnDefend = new JButton("DEFEND");
        btnCheck = new JButton("CHECK");
        btnBack = new JButton("BACK");

        add(btnFight);
        add(btnDefend);
        add(btnCheck);
        add(btnBack);

        dialogue = new JTextArea();
        dialogue.setEditable(false);
        dialogue.setLineWrap(true);
        dialogue.setWrapStyleWord(true);
        add(dialogue);

        // MAIN BUTTON ACTIONS
        btnFight.addActionListener(e -> switchState(ActionState.FIGHT));
        btnDefend.addActionListener(e -> switchState(ActionState.DEFEND));
        btnCheck.addActionListener(e -> switchState(ActionState.CHECK));
        btnBack.addActionListener(e -> switchState(ActionState.MAIN));

        updateButtons();
    }

    private void switchState(ActionState newState) {
        state = newState;
        updateButtons();
    }

    private void log(String text) {
        dialogue.append("\n" + text);
    }

    // =========================
    // PLAYER TURN
    // =========================
    private void playerTurn(int action) {

        if (player == null || enemy == null) {
            dialogue.setText("Battle not initialized!");
            return;
        }

        String result = system.performAction(player, enemy, action, true);
        dialogue.setText(result);

        repaint();

        if (!enemy.isCharacterAlive()) {
            playerWins++;
            endRound("You win round " + round);
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

        dialogue.append("\n" + result);

        if (!player.isCharacterAlive()) {
            enemyWins++;
            endRound("Enemy wins round " + round);
            return;
        }

        repaint();
    }

    //Round
    private void resetRound() {
        round++;

        player.setCharacterCurrentHealthPoints(player.getCharacterMaxHealthPoints());
        enemy.setCharacterCurrentHealthPoints(enemy.getCharacterMaxHealthPoints());

        player.setCharacterCurrentMana(player.getCharacterMaxMana());
        enemy.setCharacterCurrentMana(enemy.getCharacterMaxMana());
        
        defendDisabled = false;

        dialogue.setText("Round " + round + " starting...");
    }

    public void reset() {
        initialized   = false;
        player        = null;
        enemy         = null;
        playerWins    = 0;
        enemyWins     = 0;
        round         = 1;
        defendDisabled = false;
        state         = ActionState.MAIN;
        dialogue.setText("");
        updateButtons();
        repaint();
    }

    private void endRound(String message) {
        log(message);

        if (playerWins == 2) {
            log("YOU WON THE MATCH!");
            disableButtons();
            return;
        }

        if (enemyWins == 2) {
            log("YOU LOST THE MATCH!");
            disableButtons();
            return;
        }

        resetRound();
    }

    private void disableButtons() {
        btnFight.setEnabled(false);
        btnDefend.setEnabled(false);
        btnCheck.setEnabled(false);
        btnBack.setEnabled(false);
    }

    // =========================
    // LAYOUT
    // =========================
    private void setLayoutListeners() {
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                layoutUI();
            }
        });
    }

    private void layoutUI() {

        int w = getWidth();
        int h = getHeight();

        dialogue.setBounds(
                (int)(w * 0.1),
                (int)(h * 0.65),
                (int)(w * 0.8),
                (int)(h * 0.15)
        );

        btnFight.setBounds((int)(w * 0.15), (int)(h * 0.85), 100, 40);
        btnDefend.setBounds((int)(w * 0.35), (int)(h * 0.85), 100, 40);
        btnCheck.setBounds((int)(w * 0.55), (int)(h * 0.85), 100, 40);
        btnBack.setBounds((int)(w * 0.75), (int)(h * 0.85), 100, 40);
    }

    private void updateButtons() {

        resetAllButtons();

        switch (state) {
            case MAIN -> {
                btnFight.setText("FIGHT");
                btnDefend.setText("DEFEND");
                btnCheck.setText("CHECK");
                btnBack.setText("BACK");

                btnFight.setEnabled(true);
                btnDefend.setEnabled(true);
                btnCheck.setEnabled(true);
                btnBack.setEnabled(false);

                btnDefend.setEnabled(!defendDisabled);

                btnFight.addActionListener(e -> switchState(ActionState.FIGHT));
                btnDefend.addActionListener(e -> switchState(ActionState.DEFEND));
                btnCheck.addActionListener(e -> switchState(ActionState.CHECK));
            }

            case FIGHT -> {
                btnFight.setText("Skill 1");
                btnDefend.setText("Skill 2");
                btnCheck.setText("Skill 3");
                btnBack.setText("BACK");

                btnFight.setEnabled(true);
                btnDefend.setEnabled(true);
                btnCheck.setEnabled(true);
                btnBack.setEnabled(true);
                btnBack.setEnabled(false);

                btnFight.addActionListener(e -> playerTurn(1));
                btnDefend.addActionListener(e -> playerTurn(2));
                btnCheck.addActionListener(e -> playerTurn(3));

                btnBack.addActionListener(e -> switchState(ActionState.MAIN));
            }

            case DEFEND -> {
                btnFight.setVisible(false);
                btnBack.setVisible(false);

                btnDefend.setText("Block (" + player.getRemainingBlocks() + ")");
                btnCheck.setText("BACK");

                // Enable / disable block based on charges OR flag
                btnDefend.setEnabled(player.getRemainingBlocks() > 0 && !defendDisabled);

                btnCheck.setEnabled(true);

                // BLOCK BUTTON
                btnDefend.addActionListener(e -> {
                    playerTurn(4);

                    btnDefend.setText("Block (" + player.getRemainingBlocks() + ")");

                    // if charges reach 0 → lock defend
                    if (player.getRemainingBlocks() <= 0) {
                        defendDisabled = true;
                        btnDefend.setEnabled(false);
                    }
                });

                // BACK BUTTON
                btnCheck.addActionListener(e -> {
                    switchState(ActionState.MAIN);
                    btnFight.setVisible(true);
                    btnBack.setVisible(true);
                });
            }

            case CHECK -> {
                btnBack.setEnabled(true);
                SkillsInterface skills = (SkillsInterface) player;

                dialogue.setText(skills.getSkill1().getSkillName() + "\nDMG: " + skills.getSkill1().getSkillDamage() + " | Mana: " + skills.getSkill1().getSkillManaCost()
                        + "\n\n" + skills.getSkill2().getSkillName() + "\nDMG: " + skills.getSkill2().getSkillDamage() + " | Mana: " + skills.getSkill2().getSkillManaCost()
                        + "\n\n" + skills.getSkill3().getSkillName() + "\nDMG: " + skills.getSkill3().getSkillDamage() + " | Mana: " + skills.getSkill3().getSkillManaCost());
                
                btnBack.addActionListener(e -> switchState(ActionState.MAIN));
                btnBack.setEnabled(true);
            }
        }
    }

    private enum ActionState {
        MAIN,
        FIGHT,
        DEFEND,
        CHECK
    }

    private void clearListeners(JButton btn) {
        for (ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }

    private void resetAllButtons() {
        clearListeners(btnFight);
        clearListeners(btnDefend);
        clearListeners(btnCheck);
        clearListeners(btnBack);
    }

    // =========================
    // DRAW
    // =========================
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);

        if (playerSprite != null) {
            g.drawImage(playerSprite,
                    (int)(getWidth() * 0.15),
                    (int)(getHeight() * 0.35),
                    180, 180, this);
        }

        if (enemySprite != null) {
            g.drawImage(enemySprite,
                    (int)(getWidth() * 0.65),
                    (int)(getHeight() * 0.20),
                    180, 180, this);
        }
    }
}