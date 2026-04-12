import Characters.*;                                                 
import Foundation.*;
import java.util.*;

public class BattleSystem extends BattleSystemAbs {
    Scanner sc = new Scanner(System.in);
    Random rand = new Random();
    private BattleMode mode;
    
    public BattleSystem(BattleMode mode) {
        this.mode = mode;
    }

    @Override
    public GameCharacter selectCharacter(int choice) {
        // This method will handle character selection logic
        GameCharacter character = null;
        switch(choice) {
            case 1:
                character = new AVin();
                System.out.println("Selected A-Vin");
                return character;
            case 2:
                character = new BrivanJawmir();
                System.out.println("Selected Brivan Jawmir");
                return character;
            case 3:
                character = new ChungMyung();
                System.out.println("Selected Chung Myung");
                return character;
            case 4:
                character = new Kenneth();
                System.out.println("Selected Kenneth");
                return character;
            case 5:
                character = new KijEl();
                System.out.println("Selected Kij-El");
                return character;
            case 6:
                character = new SoleilMooncrest();
                System.out.println("Selected Soleil Mooncrest");
                return character;
            case 7:
                character = new SungJinWoo();
                System.out.println("Selected Sung Jin-Woo");
                return character;
            case 8:
                character = new Zakkarr();
                System.out.println("Selected Zakkarr");
                return character;
            default:
                System.out.println("Invalid choice. Please select a valid character.");
                return null;
        }
    }

    @Override
    public void startBattle(GameCharacter player1, GameCharacter player2) {
        if (mode == BattleMode.PVE) {
            pveBattle(player1, player2);
        } else if (mode == BattleMode.PVP) {
            pvpBattle(player1, player2);
        } else if (mode == BattleMode.ARCADE) {
            arcadeMode(player1);
        }
    }

    @Override
    public void attack(GameCharacter attacker, GameCharacter defender, int action, boolean isPlayer) {
        SkillsInterface charSkills = (SkillsInterface) attacker;

        // Resolve which skill is being used
        Skill skill = switch (action) {
            case 1 -> charSkills.getSkill1();
            case 2 -> charSkills.getSkill2();
            case 3 -> charSkills.getSkill3();
            default -> null;
        };

        if (skill == null) return;

        // Stunned characters still execute the skill (e.g. stun damage tick), then return
        if (attacker.getIsStunned()) {
            attacker.useSkill(action, defender);
            return;
        }

        // Block check — negate the hit entirely, consume one block charge, clear block status
        if (defender.getIsBlocking()) {
            System.out.println(defender.getCharacterName() + " blocked the attack! It had no effect!");
            defender.setIsBlocking(false);
            defender.setRemainingBlocks(defender.getRemainingBlocks() - 1);
            return;
        }

        attacker.useSkill(action, defender);

        // Unified message, consistent between PvE and PvP
        String attackerLabel = isPlayer ? "You" : "AI (" + attacker.getCharacterName() + ")";
        String defenderLabel = isPlayer ? "the enemy" : "you";
        System.out.println("\n" + attackerLabel + " used " + skill.getSkillName() + "!");
        System.out.println(attackerLabel + " dealt " + skill.getSkillDamage() + " damage to " + defenderLabel + "!");
    }


    @Override
    public void pveBattle(GameCharacter player1, GameCharacter ai) {

        SkillsInterface playerSkills = (SkillsInterface) player1;
        SkillsInterface aiSkills = (SkillsInterface) ai;

        while (player1.isCharacterAlive() && ai.isCharacterAlive()) {

            // ── STATUS DISPLAY ─────────────────────────────
            System.out.println("\n===== PLAYER STATUS =====");
            System.out.println("HP: " + player1.getCharacterCurrentHealthPoints() + "/" + player1.getCharacterMaxHealthPoints()
                    + " | Mana: " + player1.getCharacterCurrentMana() + "/" + player1.getCharacterMaxMana());

            System.out.println("\n===== ENEMY STATUS =====");
            System.out.println("HP: " + ai.getCharacterCurrentHealthPoints() + "/" + ai.getCharacterMaxHealthPoints()
                    + " | Mana: " + ai.getCharacterCurrentMana() + "/" + ai.getCharacterMaxMana());

            // ── PLAYER TURN ────────────────────────────────
            showSkills(player1);
            System.out.print("Choose your action: ");
            int action = sc.nextInt();

            if (action >= 1 && action <= 3) {
                attack(player1, ai, action, true);

                if (!ai.isCharacterAlive()) {
                    System.out.println("\nYOU WIN!");
                    break;
                }
            } else if (action == 4) {
                if (player1.getRemainingBlocks() <= 0) {
                    System.out.println("No block charges left.");
                } else {
                    player1.setIsBlocking(true);
                    System.out.println("You are blocking the next attack!");
                }
            } else {
                System.out.println("Invalid action.");
                continue;
            }

            // ── PLAYER COOLDOWNS ───────────────────────────
            playerSkills.getSkill1().reduceSkillCooldown();
            playerSkills.getSkill2().reduceSkillCooldown();
            playerSkills.getSkill3().reduceSkillCooldown();

            if (!ai.isCharacterAlive()) {
                break;
            }

            // ── AI TURN ────────────────────────────────────
            int aiAction = rand.nextInt(4) + 1;

            if (aiAction >= 1 && aiAction <= 3) {
                attack(ai, player1, aiAction, false);

                if (!player1.isCharacterAlive()) {
                    System.out.println("\nYOU LOSE!");
                    break;
                }
            } else if (aiAction == 4) {
                if (ai.getRemainingBlocks() > 0) {
                    ai.setIsBlocking(true);
                }
            }

            // ── AI COOLDOWNS ───────────────────────────────
            aiSkills.getSkill1().reduceSkillCooldown();
            aiSkills.getSkill2().reduceSkillCooldown();
            aiSkills.getSkill3().reduceSkillCooldown();
        }
    }


    public void pvpBattle(GameCharacter p1, GameCharacter p2) {

        SkillsInterface p1Skills = (SkillsInterface) p1;
        SkillsInterface p2Skills = (SkillsInterface) p2;

        while (p1.isCharacterAlive() && p2.isCharacterAlive()) {

            // Clear any block left over from last round (it only lasts one round)
            p1.setIsBlocking(false);
            p2.setIsBlocking(false);

            // ── Player 1 input ──────────────────────────────────────────────────
            System.out.println("\n================ PLAYER 1 TURN ================");
            System.out.println("Player 1  HP: " + p1.getCharacterCurrentHealthPoints()
                    + "/" + p1.getCharacterMaxHealthPoints()
                    + "  |  Mana: " + p1.getCharacterCurrentMana() + "/" + p1.getCharacterMaxMana());
            System.out.println("Player 2  HP: " + p2.getCharacterCurrentHealthPoints()
                    + "/" + p2.getCharacterMaxHealthPoints()
                    + "  |  Mana: " + p2.getCharacterCurrentMana() + "/" + p2.getCharacterMaxMana());
            showSkills(p1);

            int action1 = getValidPvpAction(p1, p1Skills, "Player 1");

            // ── Player 2 input ──────────────────────────────────────────────────
            System.out.println("\n================ PLAYER 2 TURN ================");
            System.out.println("Player 2  HP: " + p2.getCharacterCurrentHealthPoints()
                    + "/" + p2.getCharacterMaxHealthPoints()
                    + "  |  Mana: " + p2.getCharacterCurrentMana() + "/" + p2.getCharacterMaxMana());
            System.out.println("Player 1  HP: " + p1.getCharacterCurrentHealthPoints()
                    + "/" + p1.getCharacterMaxHealthPoints()
                    + "  |  Mana: " + p1.getCharacterCurrentMana() + "/" + p1.getCharacterMaxMana());
            showSkills(p2);

            int action2 = getValidPvpAction(p2, p2Skills, "Player 2");

            // ── Resolve turn ────────────────────────────────────────────────────
            System.out.println("\n================ TURN RESULT ================");

            // Apply block status before attacks resolve — a block chosen this round
            // intercepts the opponent's attack this same round, then clears next turn
            if (action1 == 4) {
                p1.setIsBlocking(true);
                System.out.println("Player 1 used Protect!");
            }
            if (action2 == 4) {
                p2.setIsBlocking(true);
                System.out.println("Player 2 used Protect!");
            }

            // Resolve attacks
            if (action1 >= 1 && action1 <= 3) {
                attack(p1, p2, action1, true);
                if (!p2.isCharacterAlive()) {
                    System.out.println("\nPLAYER 1 WINS!");
                    return;
                }
            }

            if (action2 >= 1 && action2 <= 3) {
                attack(p2, p1, action2, false);
                if (!p1.isCharacterAlive()) {
                    System.out.println("\nPLAYER 2 WINS!");
                    return;
                }
            }

            // ── Reduce cooldowns ────────────────────────────────────────────────
            p1Skills.getSkill1().reduceSkillCooldown();
            p1Skills.getSkill2().reduceSkillCooldown();
            p1Skills.getSkill3().reduceSkillCooldown();

            p2Skills.getSkill1().reduceSkillCooldown();
            p2Skills.getSkill2().reduceSkillCooldown();
            p2Skills.getSkill3().reduceSkillCooldown();
        }
    }

    /**
     * Prompts a PvP player repeatedly until they enter a valid action (mana, cooldown, block charges).
     * Returns the validated action number (1–4).
     */
    private int getValidPvpAction(GameCharacter player, SkillsInterface skills, String playerLabel) {
        while (true) {
            System.out.print("\n" + playerLabel + " choose action: ");
            int action = sc.nextInt();

            switch (action) {
                case 1 -> {
                    if (player.getCharacterCurrentMana() < skills.getSkill1().getSkillManaCost()) {
                        System.out.println("Not enough mana for " + skills.getSkill1().getSkillName() + ".");
                        continue;
                    }
                    if (skills.getSkill1().getSkillCurrentCooldown() > 0) {
                        System.out.println(skills.getSkill1().getSkillName()
                                + " is on cooldown for " + skills.getSkill1().getSkillCurrentCooldown() + " turn(s).");
                        continue;
                    }
                    return action;
                }
                case 2 -> {
                    if (player.getCharacterCurrentMana() < skills.getSkill2().getSkillManaCost()) {
                        System.out.println("Not enough mana for " + skills.getSkill2().getSkillName() + ".");
                        continue;
                    }
                    if (skills.getSkill2().getSkillCurrentCooldown() > 0) {
                        System.out.println(skills.getSkill2().getSkillName()
                                + " is on cooldown for " + skills.getSkill2().getSkillCurrentCooldown() + " turn(s).");
                        continue;
                    }
                    return action;
                }
                case 3 -> {
                    if (player.getCharacterName().equals("Zakkarr")) {
                        System.out.println("Death's Return is a passive skill and cannot be used manually.");
                        continue;
                    }
                    if (player.getCharacterCurrentMana() < skills.getSkill3().getSkillManaCost()) {
                        System.out.println("Not enough mana for " + skills.getSkill3().getSkillName() + ".");
                        continue;
                    }
                    if (skills.getSkill3().getSkillCurrentCooldown() > 0) {
                        System.out.println(skills.getSkill3().getSkillName()
                                + " is on cooldown for " + skills.getSkill3().getSkillCurrentCooldown() + " turn(s).");
                        continue;
                    }
                    return action;
                }
                case 4 -> {
                    if (player.getRemainingBlocks() <= 0) {
                        System.out.println("No block charges remaining.");
                        continue;
                    }
                    return action;
                }
                default -> {
                    System.out.println("Invalid action. Please choose 1–4.");
                }
            }
        }
    }


    @Override
    public void aiTurn(GameCharacter attacker, GameCharacter defender) {

        SkillsInterface charSkills = (SkillsInterface) attacker;

        // Build a list of currently usable actions so the AI never wastes its turn
        java.util.List<Integer> available = new java.util.ArrayList<>();

        if (attacker.getCharacterCurrentMana() >= charSkills.getSkill1().getSkillManaCost()
                && charSkills.getSkill1().getSkillCurrentCooldown() == 0) {
            available.add(1);
        }
        if (attacker.getCharacterCurrentMana() >= charSkills.getSkill2().getSkillManaCost()
                && charSkills.getSkill2().getSkillCurrentCooldown() == 0) {
            available.add(2);
        }
        if (!attacker.getCharacterName().equals("Zakkarr")
                && attacker.getCharacterCurrentMana() >= charSkills.getSkill3().getSkillManaCost()
                && charSkills.getSkill3().getSkillCurrentCooldown() == 0) {
            available.add(3);
        }
        if (attacker.getRemainingBlocks() > 0) {
            available.add(4);
        }

        // Nothing usable — skip turn
        if (available.isEmpty()) {
            System.out.println("\n" + attacker.getCharacterName() + " has no available actions and skips their turn.");
            return;
        }

        int aiAction = available.get(rand.nextInt(available.size()));

        switch (aiAction) {
            case 1 -> attack(attacker, defender, 1, false);
            case 2 -> attack(attacker, defender, 2, false);
            case 3 -> attack(attacker, defender, 3, false);
            case 4 -> {
                attacker.setIsBlocking(true);
                System.out.println("\n" + attacker.getCharacterName() + " is bracing for an attack!");
            }
        }

        // Note: cooldown reduction is now handled by the caller (pveBattle) so it isn't
        // doubled when aiTurn is called from arcadeMode as well.
    }


    @Override
    public void arcadeMode(GameCharacter player) {
        Random rand = new Random();
        List<Integer> usedEnemies = new ArrayList<>();

        System.out.println("\n ARCADE MODE START!");

        SkillsInterface playerSkills = (SkillsInterface) player;

        for (int stage = 1; stage <= 7; stage++) {

            System.out.println("\n========================");
            System.out.println("        Stage " + stage);
            System.out.println("========================");

            GameCharacter enemy;

            //No Duplicate Characters & No Player Character as Enemy
            System.out.println("\nSelecting enemy for stage " + stage + "...");
            while (true) {
                int tempChoice = rand.nextInt(8) + 1;

                if (tempChoice != player.getCharacterName().charAt(0) - '0' && !usedEnemies.contains(tempChoice)) {
                    enemy = selectCharacter(tempChoice);
                    if (enemy != null) {
                        usedEnemies.add(tempChoice);
                        break;
                    }
                }
            }

            if (enemy == null) {
                stage--;   // retry this stage with a different random enemy
                continue;
            }

            SkillsInterface enemySkills = (SkillsInterface) enemy;

            // Reset blocks for both combatants at the start of each fight
            player.setRemainingBlocks(3);
            enemy.setRemainingBlocks(3);

            System.out.println("Enemy Appears: " + enemy.getCharacterName());

            // ── Fight loop ──────────────────────────────────────────────────────
            while (player.isCharacterAlive() && enemy.isCharacterAlive()) {

                // Block only lasts one round — clear it at the top of each new turn
                player.setIsBlocking(false);
                enemy.setIsBlocking(false);

                System.out.println("\nYour HP: "    + player.getCharacterCurrentHealthPoints()
                        + "/" + player.getCharacterMaxHealthPoints()
                        + "  |  Mana: " + player.getCharacterCurrentMana()
                        + "/" + player.getCharacterMaxMana());
                System.out.println("Enemy HP: "   + enemy.getCharacterCurrentHealthPoints()
                        + "/" + enemy.getCharacterMaxHealthPoints()
                        + "  |  Mana: " + enemy.getCharacterCurrentMana()
                        + "/" + enemy.getCharacterMaxMana());

                showSkills(player);
                System.out.print("Choose action: ");
                int action = sc.nextInt();

                switch (action) {
                    case 1 -> {
                        if (player.getCharacterCurrentMana() < playerSkills.getSkill1().getSkillManaCost()) {
                            System.out.println("Not enough mana for " + playerSkills.getSkill1().getSkillName() + "!");
                            continue;
                        }
                        if (playerSkills.getSkill1().getSkillCurrentCooldown() > 0) {
                            System.out.println(playerSkills.getSkill1().getSkillName()
                                    + " is on cooldown for " + playerSkills.getSkill1().getSkillCurrentCooldown() + " turn(s).");
                            continue;
                        }
                        attack(player, enemy, 1, true);
                    }
                    case 2 -> {
                        if (player.getCharacterCurrentMana() < playerSkills.getSkill2().getSkillManaCost()) {
                            System.out.println("Not enough mana for " + playerSkills.getSkill2().getSkillName() + "!");
                            continue;
                        }
                        if (playerSkills.getSkill2().getSkillCurrentCooldown() > 0) {
                            System.out.println(playerSkills.getSkill2().getSkillName()
                                    + " is on cooldown for " + playerSkills.getSkill2().getSkillCurrentCooldown() + " turn(s).");
                            continue;
                        }
                        attack(player, enemy, 2, true);
                    }
                    case 3 -> {
                        if (player.getCharacterName().equals("Zakkarr")) {
                            System.out.println("Death's Return is passive and cannot be used manually.");
                            continue;
                        }
                        if (player.getCharacterCurrentMana() < playerSkills.getSkill3().getSkillManaCost()) {
                            System.out.println("Not enough mana for " + playerSkills.getSkill3().getSkillName() + "!");
                            continue;
                        }
                        if (playerSkills.getSkill3().getSkillCurrentCooldown() > 0) {
                            System.out.println(playerSkills.getSkill3().getSkillName()
                                    + " is on cooldown for " + playerSkills.getSkill3().getSkillCurrentCooldown() + " turn(s).");
                            continue;
                        }
                        attack(player, enemy, 3, true);
                    }
                    case 4 -> {
                        if (player.getRemainingBlocks() <= 0) {
                            System.out.println("No block charges remaining!");
                            continue;
                        }
                        player.setIsBlocking(true);
                        System.out.println("You brace for the next attack!");
                    }
                    default -> {
                        System.out.println("Invalid action. Please choose 1–4.");
                        continue;
                    }
                }

                if (!enemy.isCharacterAlive()) {
                    System.out.println("\n✔ Enemy defeated!");
                    break;
                }

                // Reduce player cooldowns
                playerSkills.getSkill1().reduceSkillCooldown();
                playerSkills.getSkill2().reduceSkillCooldown();
                playerSkills.getSkill3().reduceSkillCooldown();

                // Enemy turn + cooldown reduction
                aiTurn(enemy, player);
                enemySkills.getSkill1().reduceSkillCooldown();
                enemySkills.getSkill2().reduceSkillCooldown();
                enemySkills.getSkill3().reduceSkillCooldown();

                if (!player.isCharacterAlive()) {
                    System.out.println("\nYou were defeated at stage " + stage + ".");
                    return;
                }
            }

            System.out.println("\n✔ Stage " + stage + " cleared!");

            // Fully restore player HP and mana before the next stage
            player.setCharacterCurrentHealthPoints(player.getCharacterMaxHealthPoints());
            player.setCharacterCurrentMana(player.getCharacterMaxMana());

            // Reset all cooldowns between stages
            playerSkills.getSkill1().setSkillCurrentCooldown(0);
            playerSkills.getSkill2().setSkillCurrentCooldown(0);
            playerSkills.getSkill3().setSkillCurrentCooldown(0);
        }

        System.out.println("\n★ YOU COMPLETED ARCADE MODE! ★");
    }

    private void showSkills(GameCharacter player) {
        SkillsInterface skills = (SkillsInterface) player;

        System.out.println("\n===== SKILLS =====");

        System.out.println("1. " + skills.getSkill1().getSkillName() +
                " | DMG: " + skills.getSkill1().getSkillDamage() +
                " | Mana Cost: " + skills.getSkill1().getSkillManaCost() +
                " | CD: " + skills.getSkill1().getSkillCurrentCooldown());

        System.out.println("2. " + skills.getSkill2().getSkillName() +
                " | DMG: " + skills.getSkill2().getSkillDamage() +
                " | Mana Cost: " + skills.getSkill2().getSkillManaCost() +
                " | CD: " + skills.getSkill2().getSkillCurrentCooldown());

        System.out.println("3. " + skills.getSkill3().getSkillName() +
                " | DMG: " + skills.getSkill3().getSkillDamage() +
                " | Mana Cost: " + skills.getSkill3().getSkillManaCost() +
                " | CD: " + skills.getSkill3().getSkillCurrentCooldown());

        System.out.println("4. BLOCK");
        System.out.println("==================\n");
    }
}

