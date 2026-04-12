import java.util.*;
import Foundation.*;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Random rand = new Random();
        GameCharacter player1;
        GameCharacter player2;

        System.out.println("Game Modes:");
        System.out.println("1. PVE (Player vs AI)");
        System.out.println("2. PVP (Player vs Player)");
        System.out.println("3. ARCADE");
        System.out.print("Choose Game Mode: ");
        int modeChoice = sc.nextInt();

        BattleMode mode;

        switch(modeChoice) {
            case 1:
                mode = BattleMode.PVE;
                break;
            case 2:
                mode = BattleMode.PVP;
                break;
            case 3:
                mode = BattleMode.ARCADE;
                break;
            default:
                System.out.println("Invalid mode. Defaulting to PVE.");
                mode = BattleMode.PVE;
        }

        BattleSystem battleSystem = new BattleSystem(mode);

        System.out.println("\nSelect Player 1 character:");
        System.out.print("1. A-Vin\n2. Brivan Jawmir\n3. Chung Myung\n4. Kenneth\n5. Kij-El\n6. Soleil Mooncrest\n7. Sung Jin-Woo\n8. Zakkarr\nChoice: ");
        int choice1 = sc.nextInt();

        player1 = battleSystem.selectCharacter(choice1);

        if(player1 == null) {
            System.out.println("Invalid choice. Restart game.");
            sc.close();
            return;
        }

        if(mode == BattleMode.PVE) {

            System.out.println("\nAI selecting character...");

            while(true) {
                int tempChoice = rand.nextInt(8) + 1;
                player2 = battleSystem.selectCharacter(tempChoice);

                if(player2 != null && tempChoice != choice1) {
                    break;
                }
            }

        } 
        else if(mode == BattleMode.PVP) {

            System.out.println("\nPlayer 2 select character:");
            System.out.print("1. A-Vin\n2. Brivan Jawmir\n3. Chung Myung\n4. Kenneth\n5. Kij-El\n6. Soleil Mooncrest\n7. Sung Jin-Woo\n8. Zakkarr\nChoice: ");
            int choice2 = sc.nextInt();

            player2 = battleSystem.selectCharacter(choice2);

            if(player2 == null) {
                System.out.println("Invalid choice. Restart game.");
                sc.close();
                return;
            }
        }
        else {
            System.out.println("\nArcade Mode starting...");
            battleSystem.arcadeMode(player1);
            sc.close();
            return;
        }

        System.out.println("\nLet the battle begin!");

        battleSystem.startBattle(player1, player2);

        sc.close();
    }
}