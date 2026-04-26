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

public class ArcadeBattleScreen extends JPanel {

    private static final String BTN_FIGHT_PATH   = "Assets/battle_sprites/battle_buttons/actions/fight_btn.gif";
    private static final String BTN_DEFEND_PATH  = "Assets/battle_sprites/battle_buttons/actions/defend_btn.gif";
    private static final String BTN_CHECK_PATH   = "Assets/battle_sprites/battle_buttons/actions/check_btn.gif";
    private static final String BTN_BACK_PATH    = "Assets/battle_sprites/battle_buttons/actions/back_btn.gif";
    private static final String BTN_BACK_DISABLED_PATH = "Assets/battle_sprites/battle_buttons/actions/back_btn_disabled.png";

    private static final String P1_SPRITE_PATH   = "Assets/character_related/idleAnimation/left/";
    private static final String P1_SPRITE_SUFFIX = "-left.gif";
    private static final String ENEMY_SPRITE_PATH   = "Assets/character_related/idleAnimation/right/";
    private static final String ENEMY_SPRITE_SUFFIX = "-right.gif";
    private static final String BG_IMAGE_PATH    = "Assets/battle_sprites/battleArena.gif";
    
    private static final String BTN_SKILL1_PATH  = "Assets/battle_sprites/battle_buttons/skills/active/skill1.png";
    private static final String BTN_SKILL2_PATH  = "Assets/battle_sprites/battle_buttons/skills/active/skill2.png";
    private static final String BTN_SKILL3_PATH  = "Assets/battle_sprites/battle_buttons/skills/active/skill3.png";

    private static final String BTN_SKILL1_DISABLED_PATH  = "Assets/battle_sprites/battle_buttons/skills/inactive/skill1.png";
    private static final String BTN_SKILL2_DISABLED_PATH  = "Assets/battle_sprites/battle_buttons/skills/inactive/skill2.png";
    private static final String BTN_SKILL3_DISABLED_PATH  = "Assets/battle_sprites/battle_buttons/skills/inactive/skill3.png";

    private static final String BTN_BLOCK2_PATH  = "Assets/battle_sprites/battle_buttons/block/block_btn2.png";
    private static final String BTN_BLOCK1_PATH  = "Assets/battle_sprites/battle_buttons/skills/block_btn1.png";
    private static final String BTN_BLOCK0_PATH  = "Assets/battle_sprites/battle_buttons/skills/block_btn0.png";

    private final Image bgImage;
    private Image playerSprite;
    private Image enemySprite;

    private JButton btnFight, btnDefend, btnCheck, btnBack;
    private JTextArea dialogue;
    private JLabel statusLabel; 

    private boolean defendDisabled = false;

    private GameCharacter player;
    private GameCharacter enemy;
    private String leftName;
    private String rightName;

    private final BattleSystem system;
    private final GameSession session;
    private ActionState state = ActionState.MAIN;

    private int playerWins  = 0;
    private int enemyWins   = 0;
    private int round       = 1;

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

    public void setGameGUI(GUI.GameGUI gui) { this.gameGUI = gui; }

    public void setVersusScreen(VersusScreen vs, CardLayout cl, JPanel cont) {
        this.versusScreen = vs; this.cardLayout   = cl; this.container    = cont;
    }

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

        enemy = system.selectCharacter(opponentOrder.get(currentOpponentIndex));
        session.setPlayer2(enemy);

        this.leftName    = toFileKey(player.getCharacterName());
        this.rightName   = toFileKey(enemy.getCharacterName());

        playerSprite = new ImageIcon(P1_SPRITE_PATH    + leftName + P1_SPRITE_SUFFIX).getImage();
        enemySprite  = new ImageIcon(ENEMY_SPRITE_PATH + rightName + ENEMY_SPRITE_SUFFIX).getImage();

        playerWins = 0; enemyWins  = 0; round      = 1;
        defendDisabled = false; state      = ActionState.MAIN;

        updateStatusLabel();
        dialogue.setText("Opponent " + (currentOpponentIndex + 1) + "/" + opponentOrder.size() + ": " + enemy.getCharacterName());

        enableButtons(); updateButtons(); repaint();
    }

    private void updateStatusLabel() {
        statusLabel.setText("Opponent  " + (currentOpponentIndex + 1) + " / " + opponentOrder.size() + "   [ " + playerWins + " - " + enemyWins + " ]");
    }

    private void createUI() {
        btnFight  = makeButton("FIGHT",  BTN_FIGHT_PATH);
        btnDefend = makeButton("DEFEND", BTN_DEFEND_PATH);
        btnCheck  = makeButton("CHECK",  BTN_CHECK_PATH);
        btnBack   = makeButton("BACK",   BTN_BACK_PATH);
        btnBack.setDisabledIcon(new ImageIcon(BTN_BACK_DISABLED_PATH));

        add(btnFight); add(btnDefend); add(btnCheck); add(btnBack);

        dialogue = new JTextArea();
        dialogue.setEditable(false); dialogue.setLineWrap(true); dialogue.setWrapStyleWord(true);
        dialogue.setOpaque(false); dialogue.setForeground(Color.WHITE);
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
            btn.setIcon(new ImageIcon(imagePath)); btn.setBorderPainted(false);
            btn.setContentAreaFilled(false); btn.setFocusPainted(false); btn.setToolTipText(text);
        } else { btn.setText(text); }
        return btn;
    }

    private void setButtonLabel(JButton btn, String text, String imagePath) {
        if (imagePath != null) { btn.setIcon(new ImageIcon(imagePath)); btn.setToolTipText(text); } 
        else { btn.setText(text); }
    }

    private void switchState(ActionState newState) { state = newState; updateButtons(); }

    private void log(String text) { dialogue.append("\n" + text); }

    private void playerTurn(int action) {
        if (arcadeOver || player == null || enemy == null) return;

        String result = system.performAction(player, enemy, action, true);
        dialogue.setText(result);
        player.getSkill1().reduceSkillCooldown();
        player.getSkill2().reduceSkillCooldown();
        player.getSkill3().reduceSkillCooldown();
        repaint();

        if (!enemy.isCharacterAlive()) {
            playerWins++; updateStatusLabel(); endRound("You win round " + round + "!"); return;
        }

        aiTurn();
    }

    private void aiTurn() {
        int aiAction = system.getAIAction(enemy);
        String result = system.performAction(enemy, player, aiAction, false);
        log(result);
        enemy.getSkill1().reduceSkillCooldown();
        enemy.getSkill2().reduceSkillCooldown();
        enemy.getSkill3().reduceSkillCooldown();

        if (!player.isCharacterAlive()) {
            enemyWins++; updateStatusLabel(); endRound(enemy.getCharacterName() + " wins round " + round + "!");
        }
        repaint();
    }

    private void resetRound() {
        round++;
        player.setCharacterCurrentHealthPoints(player.getCharacterMaxHealthPoints());
        enemy.setCharacterCurrentHealthPoints(enemy.getCharacterMaxHealthPoints());
        player.setCharacterCurrentMana(player.getCharacterMaxMana());
        enemy.setCharacterCurrentMana(enemy.getCharacterMaxMana());
        defendDisabled = false; state = ActionState.MAIN;
        log("── Round " + round + " ──");
        updateStatusLabel(); updateButtons();
    }

    private void endRound(String message) {
        log(message);

        if (playerWins == 2) {
            log("You defeated " + enemy.getCharacterName() + "!");
            currentOpponentIndex++;
            if (currentOpponentIndex >= opponentOrder.size()) { arcadeClear(); return; }
            disableButtons();

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

        if (enemyWins == 2) { gameOver(); return; }

        resetRound();
    }

    private void arcadeClear() {
        arcadeOver = true;
        dialogue.setText("ARCADE CLEAR! You defeated all opponents!");
        statusLabel.setText("ARCADE CLEAR");
        disableButtons();

        if (gameGUI != null) {
            Timer delay = new Timer(900, e -> gameGUI.showGameOver(player.getCharacterName(), "", true, "MainMenu"));
            delay.setRepeats(false); delay.start();
        }
    }

    private void gameOver() {
        arcadeOver = true;
        dialogue.setText("GAME OVER");
        statusLabel.setText("GAME OVER");
        disableButtons();

        if (gameGUI != null) {
            Timer delay = new Timer(900, e -> gameGUI.showGameOver(enemy.getCharacterName(), player.getCharacterName(), false, "MainMenu"));
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

    private void setLayoutListeners() {
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) { layoutUI(); }
        });
    }

    private void layoutUI() {
        int w = getWidth(), h = getHeight();
        statusLabel.setBounds((int)(w * 0.25), (int)(h * 0.60), (int)(w * 0.50), 28);
        dialogue.setBounds((int)(w * 0.10), (int)(h * 0.65), (int)(w * 0.80), (int)(h * 0.15));
        btnFight.setBounds((int)(w * 0.15), (int)(h * 0.85), 100, 40);
        btnDefend.setBounds((int)(w * 0.35), (int)(h * 0.85), 100, 40);
        btnCheck.setBounds((int)(w * 0.55), (int)(h * 0.85), 100, 40);
        btnBack.setBounds((int)(w * 0.75), (int)(h * 0.85), 100, 40);
    }

    private void updateButtons() {
        resetAllButtons();

        switch (state) {
            case MAIN -> {
                setButtonLabel(btnFight,  "FIGHT",  BTN_FIGHT_PATH);
                setButtonLabel(btnDefend, "DEFEND", BTN_DEFEND_PATH);
                setButtonLabel(btnCheck,  "CHECK",  BTN_CHECK_PATH);
                setButtonLabel(btnBack,   "BACK",   BTN_BACK_PATH); 
                
                btnFight.setEnabled(true); 
                btnDefend.setEnabled(!defendDisabled);
                btnCheck.setEnabled(true); 
                
                // This automatically triggers the BTN_BACK_DISABLED_PATH graphic!
                btnBack.setEnabled(false); 
                
                btnFight.setVisible(true); btnBack.setVisible(true);
                btnDefend.setVisible(true); btnCheck.setVisible(true);

                btnFight.addActionListener(e -> switchState(ActionState.FIGHT));
                btnDefend.addActionListener(e -> switchState(ActionState.DEFEND));
                btnCheck.addActionListener(e -> switchState(ActionState.CHECK));
            }
            case FIGHT -> {
                setButtonLabel(btnFight,  "Skill 1", BTN_SKILL1_PATH);
                setButtonLabel(btnDefend, "Skill 2", BTN_SKILL2_PATH);
                setButtonLabel(btnCheck,  "Skill 3", BTN_SKILL3_PATH);
                setButtonLabel(btnBack,   "BACK",    BTN_BACK_PATH);

                btnFight.setDisabledIcon(new ImageIcon(BTN_SKILL1_DISABLED_PATH));
                btnDefend.setDisabledIcon(new ImageIcon(BTN_SKILL2_DISABLED_PATH));
                btnCheck.setDisabledIcon(new ImageIcon(BTN_SKILL3_DISABLED_PATH));
                btnBack.setDisabledIcon(new ImageIcon(BTN_BACK_DISABLED_PATH));

                boolean s1Ready = player != null && player.getSkill1().getSkillCurrentCooldown() == 0 &&player.getCharacterCurrentMana() >= player.getSkill1().getSkillManaCost();
                boolean s2Ready = player != null && player.getSkill2().getSkillCurrentCooldown() == 0 &&player.getCharacterCurrentMana() >= player.getSkill2().getSkillManaCost();               
                boolean s3Ready = player != null && player.getSkill3().getSkillCurrentCooldown() == 0 &&player.getCharacterCurrentMana() >= player.getSkill3().getSkillManaCost();

                btnFight.setEnabled(s1Ready); 
                btnDefend.setEnabled(s2Ready);
                btnCheck.setEnabled(s3Ready); 
                btnBack.setEnabled(true);

                btnFight.addActionListener(e -> playerTurn(1));
                btnDefend.addActionListener(e -> playerTurn(2));
                btnCheck.addActionListener(e -> playerTurn(3));
                btnBack.addActionListener(e -> switchState(ActionState.MAIN));
            }
            case DEFEND -> {
                btnFight.setVisible(false); btnBack.setVisible(false);
                setButtonLabel(btnCheck,  "BACK", BTN_CHECK_PATH);
                
                // Dynamic Block Graphic Logic
                int blocks = player != null ? player.getRemainingBlocks() : 0;
                String blockGraphic = BTN_BLOCK0_PATH;
                
                if (blocks >= 2) blockGraphic = BTN_BLOCK2_PATH;
                else if (blocks == 1) blockGraphic = BTN_BLOCK1_PATH;
                
                setButtonLabel(btnDefend, "Block (" + blocks + ")", blockGraphic);

                // Enable only if they have blocks left and haven't hit 0 this turn
                btnDefend.setEnabled(blocks > 0 && !defendDisabled);
                btnCheck.setEnabled(true);

                btnDefend.addActionListener(e -> {
                    playerTurn(4);
                    
                    // Re-check blocks after taking the turn to immediately update graphic
                    int newBlocks = player != null ? player.getRemainingBlocks() : 0;
                    String newBlockGraphic = (newBlocks == 1) ? BTN_BLOCK1_PATH : BTN_BLOCK0_PATH;
                    setButtonLabel(btnDefend, "Block (" + newBlocks + ")", newBlockGraphic);
                    
                    if (newBlocks <= 0) {
                        defendDisabled = true; 
                        btnDefend.setEnabled(false);
                    }
                });

                btnCheck.addActionListener(e -> {
                    switchState(ActionState.MAIN);
                });
            }
            case CHECK -> {
                btnBack.setEnabled(true);
                dialogue.setText(
                    player.getSkill1().getSkillName() + " | DMG: " + player.getSkill1().getSkillDamage() + " | MP: " + player.getSkill1().getSkillManaCost() + " | CD: " + player.getSkill1().getSkillCurrentCooldown()
                    + "\n\n" + player.getSkill2().getSkillName() + " | DMG: " + player.getSkill2().getSkillDamage() + " | MP: " + player.getSkill2().getSkillManaCost() + " | CD: " + player.getSkill2().getSkillCurrentCooldown()
                    + "\n\n" + player.getSkill3().getSkillName() + " | DMG: " + player.getSkill3().getSkillDamage() + " | MP: " + player.getSkill3().getSkillManaCost() + " | CD: " + player.getSkill3().getSkillCurrentCooldown()
                );
                
                btnFight.setEnabled(false); btnDefend.setEnabled(false); btnCheck.setEnabled(false);
                btnBack.addActionListener(e -> switchState(ActionState.MAIN));
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);

        int sw = (int)(getWidth()  * 0.28); int sh = (int)(getHeight() * 0.48); int sy = (int)(getHeight() * 0.10);
        if (playerSprite != null) g.drawImage(playerSprite, (int)(getWidth() * 0.08), sy, sw, sh, this);
        if (enemySprite != null) g.drawImage(enemySprite, (int)(getWidth() * 0.64), sy, sw, sh, this);

        if (enemy != null) {
            g.setFont(new Font("Impact", Font.PLAIN, 15)); g.setColor(new Color(255, 200, 20));
            g.drawString("Opp. " + (currentOpponentIndex + 1) + "/" + opponentOrder.size(), (int)(getWidth() * 0.64), (int)(getHeight() * 0.08));
        }
    }

    public void reset() {
        initialized          = false; arcadeOver           = false;
        currentOpponentIndex = 0; playerWins           = 0; enemyWins            = 0; round                = 1;
        defendDisabled       = false; state                = ActionState.MAIN;
        opponentOrder.clear(); dialogue.setText(""); statusLabel.setText("ARCADE MODE"); repaint();
    }

    private void clearListeners(JButton btn) {
        for (java.awt.event.ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }

    private void resetAllButtons() {
        clearListeners(btnFight);
        clearListeners(btnDefend);
        clearListeners(btnCheck);
        clearListeners(btnBack);
    }

    private enum ActionState { MAIN, FIGHT, DEFEND, CHECK }
}