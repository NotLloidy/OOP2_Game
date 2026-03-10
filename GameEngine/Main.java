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
            return;
        }

        System.out.println("Now selecting AI character...");
        Random rand = new Random();
        int aiChoice = rand.nextInt(8) + 1;

        ai = new BattleSystem().selectCharacter(aiChoice);
        if (ai == null) {
            System.out.println("Error selecting AI character.");
            return;
        }

        System.out.println("Let the battle begin!");

        battleSystem.inBattle(player1, ai);
    }
}
