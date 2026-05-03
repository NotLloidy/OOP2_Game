package GameEngines;

import Characters.*;
import Foundation.*;
import java.util.*;

public class BattleSystem extends BattleSystemAbs {

    private Random rand = new Random();
    public BattleSystem() {}

    @Override
    public GameCharacter selectCharacter(int choice) {
        return switch(choice) {
            case 1 -> new AVin();
            case 2 -> new BrivanJawmir();
            case 3 -> new ChungMyung();
            case 4 -> new Kenneth();
            case 5 -> new KijEl();
            case 6 -> new SoleilMooncrest();
            case 7 -> new SungJinWoo();
            case 8 -> new Zakkarr();
            default -> new AVin(); // Fallback
        };
    }

    @Override
    public String performAction(GameCharacter attacker, GameCharacter defender, int action, boolean isPlayer) {
        
        if (action == 4) {
            if (attacker.getRemainingBlocks() <= 0) {
                return "No blocks left!";
            }
            attacker.setIsBlocking(true);
            return (isPlayer ? "You" : attacker.getCharacterName()) + " are preparing to block!";
        }

        CharacterSkills skill = switch (action) {
            case 1 -> attacker.getSkill1();
            case 2 -> attacker.getSkill2();
            case 3 -> attacker.getSkill3();
            default -> null;
        };

        if (skill == null) return "Invalid action.";

        // Execute the skill and return the exact text to the GUI
        return attacker.useSkill(action, defender);
    }

    @Override
    public int getAIAction(GameCharacter ai) {
        List<Integer> available = new ArrayList<>();

        if (ai.getSkill1().getSkillCurrentCooldown() == 0) available.add(1);
        if (ai.getSkill2().getSkillCurrentCooldown() == 0) available.add(2);
        if (!ai.getCharacterName().equals("Zakkarr") && ai.getSkill3().getSkillCurrentCooldown() == 0) {
            available.add(3);
        }
        if (ai.getRemainingBlocks() > 0) available.add(4);

        if (available.isEmpty()) return 1;

        return available.get(rand.nextInt(available.size()));
    }
}