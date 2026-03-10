package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

public class AVin  extends GameCharacter {
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
                    overClock.triggerSkillCooldown();
                    isOverclocked = true;
                }
                break;
            case 3:
                if(logicCrash.isSkillAvailable() && (getCharacterCurrentMana() >= logicCrash.getSkillManaCost())) {
                    target.takeDamage(logicCrash.getSkillDamage());
                    useMana(logicCrash.getSkillManaCost());
                    logicCrash.triggerSkillCooldown();
                    isOverclocked = true;
                    usedUlt = true;
                }
                break;
        }
    }

    public Skill getCodeJab() { 
        return this.codeJab; 
    }

    public Skill getOverClock() { 
        return this.overClock; 
    }

    public Skill getLogicCrash() { 
        return this.logicCrash;
    }
}
