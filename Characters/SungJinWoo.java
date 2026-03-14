package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

public class SungJinWoo extends GameCharacter implements _SkillsInterface {
    
    private Skill shadowSlash;
    private Skill shadowExtraction;
    private Skill summonIgris;
    private boolean isIgrisSummoned;
    private boolean isShadowSummoned;
    private int shadowMasteryStacks;

    public SungJinWoo() {
        super("Sung Jun-Woo", "Primordial", "Shadow Monarch", 180, 40, 80);

        shadowSlash = new Skill("Shadow Slash", 15, 5, 0, 0,0);
        shadowExtraction = new Skill("Shadow Extraction", 25, 20, 8, 2,0);
        summonIgris = new Skill("Summon Igris", 30, 50, 0, 5,0);

        isIgrisSummoned = false;
        isShadowSummoned = false;
        shadowMasteryStacks = 0;
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        if(getIsStunned()) {
            System.out.println("\n" + getCharacterName() + " is stunned and cannot act this turn!");
            setIsStunned(false); // Remove stun after skipping turn
            return;
        }
        switch(skillNumber) {
            case 1:
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if(shadowSlash.isSkillAvailable() && (getCharacterCurrentMana() >= shadowSlash.getSkillManaCost())) {
                    int totalDamage = shadowSlash.getSkillDamage();
                    if(isShadowSummoned) {
                        System.out.println("\nShadow Slash is empowered by the summoned shadow! +4 damage!");
                        totalDamage += 4;
                        isShadowSummoned = false; // reset shadow summon after use
                    }
                    if(target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        System.out.println("\nShadow Slash hit a critical strike on the weakened target! +2 damage!");
                        totalDamage += 2;
                    }
                    shadowMasteryStacks++;
                    if(shadowMasteryStacks >= 3) {
                        System.out.println("\nShadow Mastery activated! Sung Jin-Woo will deal +3 more damage!");
                        totalDamage += 3;
                        shadowMasteryStacks = 0; // reset stacks after activation
                    }
                    target.takeDamage(totalDamage);
                    useMana(shadowSlash.getSkillManaCost());
                    regenMana(shadowSlash.getSkillManaRegen());
                    if(isIgrisSummoned) {
                        System.out.println("\nIgris attacked with 30 damage.");
                        target.takeDamage(30);
                        isIgrisSummoned = false;
                    }
                    
                }
                break;
            case 2:
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if(shadowExtraction.isSkillAvailable() && (getCharacterCurrentMana() >= shadowExtraction.getSkillManaCost())) {
                    if(Math.random() < 0.10) {
                        isShadowSummoned = true;
                        System.out.println("\nA shadow has been summoned! Boosting Sung Jin-Woo's next attack by 25% damage!");
                    }
                    shadowMasteryStacks++;
                    if(shadowMasteryStacks == 3) {
                        System.out.println("\nShadow Mastery activated! Sung Jin-Woo will deal +5 more damage!");
                        target.takeDamage(shadowExtraction.getSkillDamage() + 5);
                        shadowMasteryStacks = 0; // reset stacks after activation
                    } else {
                        target.takeDamage(shadowExtraction.getSkillDamage());
                    }
                    useMana(shadowExtraction.getSkillManaCost());
                    regenMana(shadowExtraction.getSkillManaRegen());
                    shadowExtraction.triggerSkillCooldown();
                    if(isIgrisSummoned) {
                        System.out.println("\nIgris attacked with 30 damage.");
                        target.takeDamage(summonIgris.getSkillDamage());
                        isIgrisSummoned = false;
                    }
                }
                break;
            case 3:
                if(target.getIsBlocking()){
                    isIgrisSummoned = true;
                    target.block(target);
                    return;
                }
                if(summonIgris.isSkillAvailable() && (getCharacterCurrentMana() >= summonIgris.getSkillManaCost())) {
                    if(isShadowSummoned) {
                        System.out.println("\nShadow Slash is empowered by the summoned shadow! +4 damage!");
                        target.takeDamage(shadowSlash.getSkillDamage() + 4);
                        isShadowSummoned = false; // reset  shadow summon after use
                    } else {
                        target.takeDamage(summonIgris.getSkillDamage());
                    }
                    useMana(summonIgris.getSkillManaCost());
                    regenMana(summonIgris.getSkillManaRegen());
                    summonIgris.triggerSkillCooldown();
                    isIgrisSummoned = true;
                }
                break;
        }
    }

    @Override
    public Skill getSkill1() {
        return this.shadowSlash;
    }

    @Override
    public Skill getSkill2() {
        return this.shadowExtraction;
    }

    @Override
    public Skill getSkill3() {
        return this.summonIgris;
    }
    
}
