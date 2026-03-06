package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

public class Kenneth extends GameCharacter {

    private Skill aimedShot;
    private Skill overwatchStance;
    private Skill suppressiveVolley;

    private int disciplineStacks = 0; // Passive stacks (max 2)

    public Kenneth() {
        super("Kenneth", "Human", "Marksman", 150, 30, 60);

        aimedShot = new Skill("Aimed Shot", 20, 10, 0, 1);
        overwatchStance = new Skill("Overwatch Stance", 0, 15, 0, 2);
        suppressiveVolley = new Skill("Suppressive Volley", 40, 20, 0, 4);
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {

        switch(skillNumber) {

            case 1:
                if(aimedShot.isSkillAvailable() && getCharacterCurrentMana() >= aimedShot.getSkillManaCost()) {

                    int damage = aimedShot.getSkillDamage();

                    // Passive: Marksman's Discipline bonus
                    damage += damage * (disciplineStacks * 30) / 100;

                    // Bonus vs enemies below 50% HP
                    if(target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        damage += damage * 20 / 100;
                    }

                    target.takeDamage(damage);

                    useMana(aimedShot.getSkillManaCost());

                    aimedShot.triggerSkillCooldown();

                    disciplineStacks = 0;
                }
            break;


            case 2:
                if(overwatchStance.isSkillAvailable() && getCharacterCurrentMana() >= overwatchStance.getSkillManaCost()) {

                    useMana(overwatchStance.getSkillManaCost());

                    // Random counter-shot damage (0–30)
                    int damage = (int)(Math.random() * 31);

                    target.takeDamage(damage);

                    // Counts as passive stack
                    if(disciplineStacks < 2) {
                        disciplineStacks++;
                    }

                    overwatchStance.triggerSkillCooldown();
                }
            break;


            case 3:
                if(suppressiveVolley.isSkillAvailable() && getCharacterCurrentMana() >= suppressiveVolley.getSkillManaCost()) {

                    int damage = suppressiveVolley.getSkillDamage();

                    // Passive bonus
                    damage += damage * (disciplineStacks * 30) / 100;

                    target.takeDamage(damage);

                    useMana(suppressiveVolley.getSkillManaCost());

                    suppressiveVolley.triggerSkillCooldown();

                    disciplineStacks = 0;
                }
            break;
        }
    }


    public Skill getAimedShot() {
        return aimedShot;
    }

    public Skill getOverwatchStance() {
        return overwatchStance;
    }

    public Skill getSuppressiveVolley() {
        return suppressiveVolley;
    }
}