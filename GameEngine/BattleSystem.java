package GameEngine;

import Characters.*;                                                 
import Foundation.GameCharacter;
import java.util.*;
import Foundation.Skill;

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
                System.out.println("You have selected A-Vin");
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
                character = new Soleilmooncrest();
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

    public void inBattle(GameCharacter player1, GameCharacter ai) {
        while(player1.isCharacterAlive() && ai.isCharacterAlive()) {
            System.out.println("\nYour HP: " + player1.getCharacterCurrentHealthPoints() + "/" + player1.getCharacterMaxHealthPoints() + " | Your Mana: " + player1.getCharacterCurrentMana() + "/" + player1.getCharacterMaxMana());
            System.out.println("AI HP: " + ai.getCharacterCurrentHealthPoints() + "/" + ai.getCharacterMaxHealthPoints() + " | AI Mana: " + ai.getCharacterCurrentMana() + "/" + ai.getCharacterMaxMana());
            System.out.print("Choose your action:\n1. Use Skill 1\n2. Use Skill 2\n3. Use Skill 3\nEnter the number corresponding to your action: ");
            int action = sc.nextInt();
            
            if(action < 1 || action > 3) {
                System.out.println("Invalid action. Please choose a valid skill.");
                continue;
            }
            if(player1.getCharacterCurrentMana() < 0) {
                System.out.println("You don't have enough mana to use a skill. Please select a different action.");
                continue;
            }

            player1.useSkill(action, ai);
            System.out.println("You used Skill " + action + "!");

            if(!ai.isCharacterAlive()) {
                System.out.println("Congratulations! You have defeated the AI!");
                break;
            }
            // AI's turn (simple random action)
            int aiAction = rand.nextInt(3) + 1;

            if(aiAction < 1 || aiAction > 3) {
                System.out.println("Invalid action. Please choose a valid skill.");
                continue;
            }
            if(ai.getCharacterCurrentMana() <= 0) {
                System.out.println("AI doesn't have enough mana to use a skill. Selecting a different action.");
                continue;
            }

            ai.useSkill(aiAction, player1);
            System.out.println("AI used Skill " + aiAction + "!");

            if(!player1.isCharacterAlive()) {
                System.out.println("You have been defeated by the AI. Better luck next time!");
                break;
            }
        }
    }
        

}

