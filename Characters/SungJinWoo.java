package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

public class SungJinWoo extends GameCharacter {
    
    private Skill shadowSlash;
    private Skill shadowExtraction;
    private Skill summonIgris;
    private boolean isIgrisSummoned;

    public SungJinWoo() {
        super("Sung Jun-Woo", "Primordial", "Shadow Monarch", 180, 40, 80);

        shadowSlash = new Skill("Shadow Slash", 15, 5, 0, 0);
        shadowExtraction = new Skill("Shadow Extraction", 25, 20, 0, 2);
        summonIgris = new Skill("Summon Igris", 30, 50, 0, 5);

        isIgrisSummoned = false;
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        switch(skillNumber) {
            case 1:
                if(shadowSlash.isSkillAvailable() && (getCharacterCurrentMana() >= shadowSlash.getSkillManaCost())) {
                    target.takeDamage(shadowSlash.getSkillDamage());
                    useMana(shadowSlash.getSkillManaCost());
                    if(isIgrisSummoned) {
                        target.takeDamage(summonIgris.getSkillDamage());
                        isIgrisSummoned = false;
                    }
                }
                break;
            case 2:
                if(shadowExtraction.isSkillAvailable() && (getCharacterCurrentMana() >= shadowExtraction.getSkillManaCost())) {
                    target.takeDamage(shadowExtraction.getSkillDamage());
                    useMana(shadowExtraction.getSkillManaCost());
                    shadowExtraction.triggerSkillCooldown();
                    if(isIgrisSummoned) {
                        target.takeDamage(summonIgris.getSkillDamage());
                        isIgrisSummoned = false;
                    }
                }
                break;
            case 3:
                if(summonIgris.isSkillAvailable() && (getCharacterCurrentMana() >= summonIgris.getSkillManaCost())) {
                    target.takeDamage(summonIgris.getSkillDamage());
                    useMana(summonIgris.getSkillManaCost());
                    summonIgris.triggerSkillCooldown();
                    isIgrisSummoned = true;
                }
                break;
        }
    }

    public Skill getShadowSlash() { 
        return this.shadowSlash; 
    }

    public Skill getShadowExtraction() { 
        return this.shadowExtraction; 
    }

    public Skill getSummonIgris() { 
        return this.summonIgris; 
    }
    
}
