package Characters;

import Foundation.*;

public class AVin extends GameCharacter {
    private Skill codeJab;
    private Skill codeSurge;
    private Skill overClock;
    private Skill logicCrash;
    private boolean isOverclocked;
    private boolean usedUlt;
    private Skill lastSkillUsed;
    private int codeJabCounter;

    public AVin() {
        super("A-Vin", "Dalek", "Time Manipulator", 150, 40, 80);
        
        codeJab = new Skill("Code Jab", 20, 0, 10, 0,0);
        codeSurge = new Skill("Code Surge", 45, 10, 0, 0,0);
        overClock = new Skill("Overclock", 0, 15, 0, 3,0);
        logicCrash = new Skill("Logic Crash", 50, 60, 0, 999,4);

        isOverclocked = false;
        usedUlt = false;
        lastSkillUsed = null;
        codeJabCounter = 0;
    }

    @Override
    public String useSkill(int skillNumber, GameCharacter target) {
        if(getIsStunned()) {
            setIsStunned(false);
            return getCharacterName() + " is stunned and cannot act this turn!";
        }

        switch(skillNumber) {
            case 1:
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                
                if(usedUlt || isOverclocked) {
                    int damage = codeSurge.getSkillDamage() + 10;
                    target.takeDamage(damage);
                    useMana(codeSurge.getSkillManaCost());
                    regenMana(codeSurge.getSkillManaRegen());
                    lastSkillUsed = codeSurge;
                    
                    if(usedUlt) usedUlt = false;
                    if(!usedUlt && isOverclocked) isOverclocked = false;
                    
                    return getCharacterName() + " used Code Surge with Overclock! Dealt " + damage + " damage!";
                } else if(codeJab.isSkillAvailable() && (getCharacterCurrentMana() >= codeJab.getSkillManaCost())) {
                    codeJabCounter++;
                    int damage = codeJab.getSkillDamage();
                    String comboMsg = "";
                    
                    if(codeJabCounter == 2) {
                        codeJabCounter = 0;
                        damage += 10;
                        comboMsg = " Perfect Combo! ";
                    }
                    
                    target.takeDamage(damage);
                    useMana(codeJab.getSkillManaCost());
                    regenMana(codeJab.getSkillManaRegen());
                    codeJab.triggerSkillCooldown();
                    lastSkillUsed = codeJab;
                    
                    return getCharacterName() + " used Code Jab!" + comboMsg + " Dealt " + damage + " damage!";
                }
                return "Not enough mana!";
                
            case 2:
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if(overClock.isSkillAvailable() && (getCharacterCurrentMana() >= overClock.getSkillManaCost())) {
                    useMana(overClock.getSkillManaCost());
                    regenMana(overClock.getSkillManaRegen());
                    overClock.triggerSkillCooldown();
                    isOverclocked = true;
                    return getCharacterName() + " used Overclock! Next attack is boosted.";
                }
                return "Skill is on cooldown or insufficient mana!";
                
            case 3:
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if(logicCrash.isSkillAvailable() && (getCharacterCurrentMana() >= logicCrash.getSkillManaCost())) {
                    int bonusDamage = isOverclocked ? 20 : 0;
                    int totalDamage = logicCrash.getSkillDamage() + bonusDamage;
                    
                    target.takeDamage(totalDamage);
                    useMana(logicCrash.getSkillManaCost());
                    regenMana(logicCrash.getSkillManaRegen());
                    logicCrash.triggerSkillCooldown();
                    
                    usedUlt = true;
                    isOverclocked = false; 
                    
                    if (bonusDamage > 0) {
                        return getCharacterName() + " unleashed an OVERCLOCKED Logic Crash! Dealt " + totalDamage + " damage!";
                    }
                    return getCharacterName() + " used Logic Crash! Dealt " + totalDamage + " damage!";
                }
                return "Skill not available or not enough mana!";
        }
        return "Invalid move.";
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
    public Skill getSkill2() { return this.overClock; }

    @Override
    public Skill getSkill3() { return this.logicCrash; }
}