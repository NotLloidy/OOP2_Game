package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

public class AVin  extends GameCharacter implements _SkillsInterface {
    private Skill codeJab;
    private Skill codeSurge;
    private Skill overClock;
    private Skill logicCrash;
    private boolean isOverclocked;
    private boolean usedUlt;

    public AVin() {
        super("A-Vin", "Dalek", "Time Manipulator", 140, 40, 80);
        
        codeJab = new Skill("Code Jab", 20, 0, 10, 0);
        codeSurge = new Skill("Code Surge", 40, 10, 0, 0);
        overClock = new Skill("Overclock", 0, 15, 0, 3);
        logicCrash = new Skill("Logic Crash", 50, 60, 0, 999);

        isOverclocked = false;
        usedUlt = false;
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        switch(skillNumber) {
            case 1:
                if(usedUlt || isOverclocked) {
                    target.takeDamage(codeSurge.getSkillDamage());
                    useMana(codeSurge.getSkillManaCost());
                    regenMana(codeSurge.getSkillManaRegen());
                    if(usedUlt) {
                        usedUlt = false;
                    }
                    if(!usedUlt && isOverclocked) {
                        isOverclocked = false;
                    }
                    break;
                } else if(codeJab.isSkillAvailable() && (getCharacterCurrentMana() >= codeJab.getSkillManaCost())) {
                    target.takeDamage(codeJab.getSkillDamage());
                    regenMana(codeJab.getSkillManaRegen());
                    codeJab.triggerSkillCooldown();
                }
                break;
            case 2:
                if(overClock.isSkillAvailable() && (getCharacterCurrentMana() >= overClock.getSkillManaCost())) {
                    useMana(overClock.getSkillManaCost());
                    regenMana(overClock.getSkillManaRegen());
                    overClock.triggerSkillCooldown();
                    isOverclocked = true;
                }
                break;
            case 3:
                if(logicCrash.isSkillAvailable() && (getCharacterCurrentMana() >= logicCrash.getSkillManaCost())) {
                    target.takeDamage(logicCrash.getSkillDamage());
                    useMana(logicCrash.getSkillManaCost());
                    regenMana(logicCrash.getSkillManaRegen());
                    logicCrash.triggerSkillCooldown();
                    isOverclocked = true;
                    usedUlt = true;
                }
                break;
        }
    }

    @Override
    public Skill getSkill1() { 
        return this.codeJab; 
    }

    @Override
    public Skill getSkill2() { 
        return this.overClock; 
    }

    @Override
    public Skill getSkill3() { 
        return this.logicCrash;
    }
}
