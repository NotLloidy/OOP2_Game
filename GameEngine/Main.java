package GameEngine;

import java.util.*;
import Foundation.GameCharacter;


public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        GameCharacter player1;
        GameCharacter ai;

        BattleSystem battleSystem = new BattleSystem();


        System.out.println("Welcome to the Crucible Clash!");
        System.out.print("Please select your character: \n1. A-Vin\n2. Brivan Jawmir\n3. Chung Myung\n4. Kenneth\n5. Kij-El\n6. Soleil Mooncrest\n7. Sung Jin-Woo\n8. Zakkarr\nEnter the number corresponding to your character choice: ");
        int choice = sc.nextInt();

        player1 = new BattleSystem().selectCharacter(choice);
        if (player1 == null) {
            System.out.println("Invalid choice. Please restart the game.");
            sc.close();
            return;
        }

        System.out.println("Now selecting AI character...");
        Random rand = new Random();
        while(true) {
            int tempChoice = rand.nextInt(8) + 1;
            if(tempChoice != choice) {
                ai = new BattleSystem().selectCharacter(tempChoice); 
                if (ai != null) {
                    break;
                }
            }
        }

        System.out.println("Let the battle begin!");

        battleSystem.inBattle(player1, ai);
        sc.close();
    }
    
}