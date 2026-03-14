package GameEngine;

import Characters.*;                                                 
import Foundation.*;
import java.util.*;

public class BattleSystem extends BattleSystemAbs {
    Scanner sc = new Scanner(System.in);
    Random rand = new Random();
    

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
    public void attack(GameCharacter attacker, GameCharacter defender, int action, boolean isPlayer) {
        _SkillsInterface charSkills = (_SkillsInterface) attacker;

        if(action == 1) {
            if(defender.getIsBlocking()) {
                attacker.useMana(charSkills.getSkill1().getSkillManaCost()); // Refund mana cost if attack is blocked
                attacker.regenMana(charSkills.getSkill1().getSkillManaRegen()); // Refund mana regen if attack is blocked
                attacker.useSkill(action, defender);
                return;
            }
            if(attacker.getIsStunned()) {
                attacker.useSkill(action, defender);
                return;
            }
            attacker.useSkill(action, defender);
            if(isPlayer) {
                System.out.println("\nYou used " + charSkills.getSkill1().getSkillName() + "!");
            } else {
                System.out.println("\nAI used " + charSkills.getSkill1().getSkillName() + "!");
            }
            if(isPlayer) {
                System.out.println("You dealt " + charSkills.getSkill1().getSkillDamage() + " damage to the enemy!");
            } else {
                System.out.println("AI dealt " + charSkills.getSkill1().getSkillDamage() + " damage to you!");
            }
        } else if(action == 2) {
            if(defender.getIsBlocking()) {
                attacker.useMana(charSkills.getSkill2().getSkillManaCost()); // Refund mana cost if attack is blocked
                attacker.regenMana(charSkills.getSkill2().getSkillManaRegen()); // Refund
                attacker.useSkill(action, defender);
                return;
            }
            if(attacker.getIsStunned()) {
                attacker.useSkill(action, defender);
                return;
            }
            attacker.useSkill(action, defender);
            if(isPlayer) {
                System.out.println("\nYou used " + charSkills.getSkill2().getSkillName() + "!");
            } else {
                System.out.println("\nAI used " + charSkills.getSkill2().getSkillName() + "!");
            }
            if(isPlayer) {
                System.out.println("You dealt " + charSkills.getSkill2().getSkillDamage() + " damage to the enemy!");
            } else {
                System.out.println("AI dealt " + charSkills.getSkill2().getSkillDamage() + " damage to you!");
            }
        } else if(action == 3) {
            if(defender.getIsBlocking()) {
                attacker.useMana(charSkills.getSkill3().getSkillManaCost()); // Refund mana cost if attack is blocked
                attacker.regenMana(charSkills.getSkill3().getSkillManaRegen()); // Refund mana regen if attack is blocked
                attacker.useSkill(action, defender);
                return;
            }
            if(attacker.getIsStunned()) {
                attacker.useSkill(action, defender);
                return;
            }
            attacker.useSkill(action, defender);
            if(isPlayer) {
                System.out.println("\nYou used " + charSkills.getSkill3().getSkillName() + "!");
            } else {
                System.out.println("\nAI used " + charSkills.getSkill3().getSkillName() + "!");
            }
            
            if(isPlayer) {
                System.out.println("You dealt " + charSkills.getSkill3().getSkillDamage() + " damage to the enemy!");
            } else {
                System.out.println("AI dealt " + charSkills.getSkill3().getSkillDamage() + " damage to you!");
            }
        }
    }
    

    @Override
    public void inBattle(GameCharacter player1, GameCharacter ai) {
        _SkillsInterface charSkills = (_SkillsInterface) player1;
        

        while(player1.isCharacterAlive() && ai.isCharacterAlive()) {
            System.out.println("\nYour HP: " + player1.getCharacterCurrentHealthPoints() + "/" + player1.getCharacterMaxHealthPoints() + " | Your Mana: " + player1.getCharacterCurrentMana() + "/" + player1.getCharacterMaxMana());
            System.out.println("AI HP: " + ai.getCharacterCurrentHealthPoints() + "/" + ai.getCharacterMaxHealthPoints() + " | AI Mana: " + ai.getCharacterCurrentMana() + "/" + ai.getCharacterMaxMana());
            System.out.print("Choose your action: ");
            int action = sc.nextInt();
            
            switch(action) {
                case 1:
                    if(player1.getCharacterCurrentMana() < charSkills.getSkill1().getSkillManaCost()) {
                        System.out.println("\nYou don't have enough mana to use a skill. Please choose a different action.");
                        continue;
                    }
                    if(charSkills.getSkill1().getSkillCurrentCooldown() > 0) {
                        System.out.println("\nThis skill is on cooldown for " + charSkills.getSkill1().getSkillCurrentCooldown() + " turns. Please choose a different action.");
                        continue;
                    }
                        attack(player1, ai, action, true);
                    break;
                case 2:
                    if(player1.getCharacterCurrentMana() < charSkills.getSkill2().getSkillManaCost()) {
                        System.out.println("\nYou don't have enough mana to use a skill. Please choose a different action.");
                        continue;
                    }
                    if(charSkills.getSkill2().getSkillCurrentCooldown() > 0) {
                        System.out.println("\nThis skill is on cooldown for " + charSkills.getSkill2().getSkillCurrentCooldown() + " turns. Please choose a different action.");
                        continue;
                    }
                        attack(player1, ai, action, true);
                    break;
                case 3:
                    if(player1.getCharacterName() == "Zakkarr") {
                        System.out.println("\nDeath's Return is a passive skill. You cannot manually use it. Please choose a different action.");
                        continue;
                    }
                    if(player1.getCharacterCurrentMana() < charSkills.getSkill3().getSkillManaCost()) {
                        System.out.println("\n  You don't have enough mana to use a skill. Please choose a different action.");
                        continue;
                    }
                    if(charSkills.getSkill3().getSkillCurrentCooldown() > 0) {
                        System.out.println("\nThis skill is on cooldown for " + charSkills.getSkill3().getSkillCurrentCooldown() + " turns. Please choose a different action.");
                        continue;
                    }
                        attack(player1, ai, action, true);
                    break;
                case 4:
                    if(player1.getRemainingBlocks() <= 0) {
                        System.out.println("\nYou have no blocks remaining. Please choose a different action.");
                        continue;
                    }
                    player1.setIsBlocking(true);
                    System.out.println("\nYou are blocking! You have " + (player1.getRemainingBlocks() - 1) + " block charge left.");
                    break;

                default:
                    System.out.println("\nInvalid action. Please choose a valid skill.");
                    continue;
            }

            if(!ai.isCharacterAlive()) {
                System.out.println("\nCongratulations! You have defeated the AI!");
                break;
            }

            charSkills.getSkill1().reduceSkillCooldown();
            charSkills.getSkill2().reduceSkillCooldown();
            charSkills.getSkill3().reduceSkillCooldown();

            aiTurn(ai, player1);
        }
    }
        

    @Override
    public void aiTurn(GameCharacter ai, GameCharacter player) {
        _SkillsInterface charSkills = (_SkillsInterface) ai;
        

        while(ai.isCharacterAlive() && player.isCharacterAlive()) {
            int aiAction = rand.nextInt(4) + 1;
            switch(aiAction) { 
                case 1:
                    if(ai.getCharacterCurrentMana() < charSkills.getSkill1().getSkillManaCost()) {
                        continue;
                    }
                    if(charSkills.getSkill1().getSkillCurrentCooldown() > 0) {
                        continue;
                    }
                    attack(ai, player, aiAction, false);
                    break;
                case 2:
                    if(ai.getCharacterCurrentMana() < charSkills.getSkill2().getSkillManaCost()) {
                        continue;
                    }
                    if(charSkills.getSkill2().getSkillCurrentCooldown() > 0) {
                        continue;
                    }
                    attack(ai, player, aiAction, false);
                    break;
                case 3:
                    if(ai.getCharacterName() == "Zakkarr") {
                        continue; // Zakkarr's ultimate is passive, so AI should never try to use it
                    }
                    if(ai.getCharacterCurrentMana() < charSkills.getSkill3().getSkillManaCost()) {
                        continue;
                    }
                    if(charSkills.getSkill3().getSkillCurrentCooldown() > 0) {
                        continue;
                    }
                    attack(ai, player, aiAction, false);
                    break;
                case 4:
                    if(ai.getRemainingBlocks() <= 0) {
                        continue;
                    }
                    ai.setIsBlocking(true);
                    System.out.println("\nAI is blocking! It has " + (ai.getRemainingBlocks() - 1) + " block charge left.");
                    break;
            }
            if(!player.isCharacterAlive()) {
                System.out.println("\nYou have been defeated by the AI. Better luck next time!");
                break;
            }

            charSkills.getSkill1().reduceSkillCooldown();
            charSkills.getSkill2().reduceSkillCooldown();
            charSkills.getSkill3().reduceSkillCooldown();


            return;
        }
    }
}

