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
    private Skill lastSkillUsed;
    private int codeJabCounter;

    public AVin() {
        super("A-Vin", "Dalek", "Time Manipulator", 140, 40, 80);
        
        codeJab = new Skill("Code Jab", 20, 0, 10, 0,0);
        codeSurge = new Skill("Code Surge", 40, 10, 0, 0,0);
        overClock = new Skill("Overclock", 0, 15, 0, 3,0);
        logicCrash = new Skill("Logic Crash", 50, 60, 0, 999,0);

        isOverclocked = false;
        usedUlt = false;
        lastSkillUsed = null;
        codeJabCounter = 0;
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
                if(usedUlt || isOverclocked) {
                    System.out.println("\n" + getCharacterName() + " used Code Surge with Overclock bonus! +10 bonus damage!");
                    target.takeDamage(codeSurge.getSkillDamage() + 10);
                    useMana(codeSurge.getSkillManaCost());
                    regenMana(codeSurge.getSkillManaRegen());
                    lastSkillUsed = codeSurge;
                    if(usedUlt) {
                        usedUlt = false;
                    }
                    if(!usedUlt && isOverclocked) {
                        isOverclocked = false;
                    }
                    break;
                } else if(codeJab.isSkillAvailable() && (getCharacterCurrentMana() >= codeJab.getSkillManaCost())) {
                    codeJabCounter++;
                    if(codeJabCounter == 2) {
                        codeJabCounter = 0;
                        System.out.println("\n" + getCharacterName() + " executed a perfect Code Jab combo! +10 bonus damage!");
                        target.takeDamage(codeJab.getSkillDamage() + 10);
                    } else {
                        target.takeDamage(codeJab.getSkillDamage());
                    }
                    target.takeDamage(codeJab.getSkillDamage());
                    useMana(codeJab.getSkillManaCost());
                    regenMana(codeJab.getSkillManaRegen());
                    codeJab.triggerSkillCooldown();
                    lastSkillUsed = codeJab;
                }
                break;
            case 2:
                if(target.getIsBlocking()){
                    target.block(target);
                }
                if(overClock.isSkillAvailable() && (getCharacterCurrentMana() >= overClock.getSkillManaCost())) {
                    useMana(overClock.getSkillManaCost());
                    regenMana(overClock.getSkillManaRegen());
                    overClock.triggerSkillCooldown();
                    isOverclocked = true;
                }
                break;
            case 3:
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if(logicCrash.isSkillAvailable() && (getCharacterCurrentMana() >= logicCrash.getSkillManaCost())) {
                    int bonusDamage = 0;
                    if(isOverclocked) {
                        bonusDamage = 10;
                        System.out.println("\n" + getCharacterName() + " unleashed an Overclocked Logic Crash! +10 bonus damage!");
                    }
                    target.takeDamage(logicCrash.getSkillDamage() + bonusDamage);
                    bonusDamage = 0; // reset bonus after use
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
        if(lastSkillUsed == codeSurge) {
            return codeSurge;
        } else {
            return codeJab; 
        }
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
