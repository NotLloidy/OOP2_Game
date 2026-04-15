package GameEngines;

import Characters.*;
import Foundation.*;

import java.util.*;

public class BattleSystem extends BattleSystemAbs {

    private Random rand = new Random();

    public BattleSystem() {}

    // =========================
    // CHARACTER SELECTION
    // =========================
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
            default -> null;
        };
    }

    // =========================
    // PLAYER ACTION (GUI CALLS THIS)
    // =========================
    @Override
    public String performAction(GameCharacter attacker, GameCharacter defender, int action, boolean isPlayer) {

        SkillsInterface skills = (SkillsInterface) attacker;

        // BLOCK
        if (action == 4) {
            if (attacker.getRemainingBlocks() <= 0) {
                return "No blocks left!";
            }
            attacker.setIsBlocking(true);
            return (isPlayer ? "You" : attacker.getCharacterName()) + " is blocking!";
        }

        Skill skill = switch (action) {
            case 1 -> skills.getSkill1();
            case 2 -> skills.getSkill2();
            case 3 -> skills.getSkill3();
            default -> null;
        };

        if (skill == null) return "Invalid action.";

        // STUN CHECK
        if (attacker.getIsStunned()) {
            attacker.useSkill(action, defender);
            return attacker.getCharacterName() + " is stunned but still dealt damage.";
        }

        // BLOCK CHECK
        if (defender.getIsBlocking()) {
            defender.setIsBlocking(false);
            defender.setRemainingBlocks(defender.getRemainingBlocks() - 1);
            return defender.getCharacterName() + " blocked the attack!";
        }

        attacker.useSkill(action, defender);

        return (isPlayer ? "You" : "AI") + " used " + skill.getSkillName()
                + " dealing " + skill.getSkillDamage() + " damage!";
    }

    // =========================
    // AI DECISION
    // =========================
    @Override
    public int getAIAction(GameCharacter ai) {

        SkillsInterface skills = (SkillsInterface) ai;

        List<Integer> available = new ArrayList<>();

        if (skills.getSkill1().getSkillCurrentCooldown() == 0) available.add(1);
        if (skills.getSkill2().getSkillCurrentCooldown() == 0) available.add(2);
        if (!ai.getCharacterName().equals("Zakkarr") &&
                skills.getSkill3().getSkillCurrentCooldown() == 0) {
            available.add(3);
        }
        if (ai.getRemainingBlocks() > 0) available.add(4);

        if (available.isEmpty()) return 1;

        return available.get(rand.nextInt(available.size()));
    }
}