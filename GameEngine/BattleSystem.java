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
                    player1.useSkill(action, ai);
                    System.out.println("\nYou used " + charSkills.getSkill1().getSkillName() + "!");
                    break;
                case 2:
                    if(player1.getCharacterCurrentMana() < charSkills.getSkill2().getSkillManaCost()) {
                        System.out.println("\nYou don't have enough mana to use a skill. Please choose a different action.");
                        continue;
                    }
                    player1.useSkill(action, ai);
                    System.out.println("\nYou used " + charSkills.getSkill2().getSkillName() + "!");
                     break;
                case 3:
                    if(player1.getCharacterCurrentMana() < charSkills.getSkill3().getSkillManaCost()) {
                        System.out.println("\n  You don't have enough mana to use a skill. Please choose a different action.");
                        continue;
                    }
                    player1.useSkill(action, ai);
                    System.out.println("\nYou used " + charSkills.getSkill3().getSkillName() + "!");
                    break;
                default:
                    System.out.println("\nInvalid action. Please choose a valid skill.");
                    continue;
            }

            if(!ai.isCharacterAlive()) {
                System.out.println("Congratulations! You have defeated the AI!");
                break;
            }
            
            aiTurn(ai, player1);
        }
    }
        

    @Override
    public void aiTurn(GameCharacter ai, GameCharacter player) {
        _SkillsInterface charSkills = (_SkillsInterface) ai;
        

        while(ai.isCharacterAlive() && player.isCharacterAlive()) {
            int aiAction = rand.nextInt(3) + 1;
            switch(aiAction) {
                case 1:
                    if(ai.getCharacterCurrentMana() < charSkills.getSkill1().getSkillManaCost()) {
                        continue;
                    }
                    ai.useSkill(aiAction, player);
                    System.out.println("AI used " + charSkills.getSkill1().getSkillName() + "!");
                    break;
                case 2:
                    if(ai.getCharacterCurrentMana() < charSkills.getSkill2().getSkillManaCost()) {
                        continue;
                    }
                    ai.useSkill(aiAction, player);
                    System.out.println("AI used " + charSkills.getSkill2().getSkillName() + "!");
                    break;
                case 3:
                    if(ai.getCharacterCurrentMana() < charSkills.getSkill3().getSkillManaCost()) {
                        continue;
                    }
                    ai.useSkill(aiAction, player);
                    System.out.println("AI used " + charSkills.getSkill3().getSkillName() + "!");
                    break;
            }
            if(!player.isCharacterAlive()) {
                System.out.println("You have been defeated by the AI. Better luck next time!");
                break;
            }
            return;
        }
    }
}

