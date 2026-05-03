package Characters;

import Foundation.*;

public class SungJinWoo extends GameCharacter {
    
    private CharacterSkills shadowSlash;
    private CharacterSkills shadowExtraction;
    private CharacterSkills summonIgris;
    private boolean isIgrisSummoned;
    private boolean isShadowSummoned;
    private int shadowMasteryStacks;

    public SungJinWoo() {
        super("Sung Jin-Woo", "Primordial", "Shadow Monarch", 180, 40, 80);

        shadowSlash = new CharacterSkills("Shadow Slash", 15, 5, 0, 0,0);
        shadowExtraction = new CharacterSkills("Shadow Extraction", 25, 20, 8, 2,0);
        summonIgris = new CharacterSkills("Summon Igris", 60, 50, 0, 999,4);

        isIgrisSummoned = false;
        isShadowSummoned = false;
        shadowMasteryStacks = 0;
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
                if(shadowSlash.isSkillAvailable() && (getCharacterCurrentMana() >= shadowSlash.getSkillManaCost())) {
                    int totalDamage = shadowSlash.getSkillDamage();
                    String msg = getCharacterName() + " used Shadow Slash. ";
                    
                    if(isShadowSummoned) {
                        msg += "Shadow Empowered (+4 DMG)! ";
                        totalDamage += 4;
                        isShadowSummoned = false; 
                    }
                    if(target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        msg += "Critical on weakened target (+2 DMG)! ";
                        totalDamage += 2;
                    }
                    
                    shadowMasteryStacks++;
                    if(shadowMasteryStacks >= 3) {
                        msg += "Shadow Mastery Triggered (+3 DMG)! ";
                        totalDamage += 3;
                        shadowMasteryStacks = 0; 
                    }
                    
                    target.takeDamage(totalDamage);
                    useMana(shadowSlash.getSkillManaCost());
                    regenMana(shadowSlash.getSkillManaRegen());
                    
                    if(isIgrisSummoned) {
                        msg += " Igris attacks as well for 30 DMG!";
                        target.takeDamage(30);
                        isIgrisSummoned = false;
                    }
                    return msg + " Dealt " + totalDamage + " damage total.";
                }
                return "Not enough mana!";
                
            case 2:
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if(shadowExtraction.isSkillAvailable() && (getCharacterCurrentMana() >= shadowExtraction.getSkillManaCost())) {
                    String msg = getCharacterName() + " used Shadow Extraction. ";
                    if(Math.random() < 0.10) {
                        isShadowSummoned = true;
                        msg += "A Shadow was extracted! Next attack boosted. ";
                    }
                    
                    shadowMasteryStacks++;
                    if(shadowMasteryStacks == 3) {
                        msg += "Shadow Mastery Triggered! (+5 DMG) ";
                        target.takeDamage(shadowExtraction.getSkillDamage() + 5);
                        shadowMasteryStacks = 0; 
                    } else {
                        target.takeDamage(shadowExtraction.getSkillDamage());
                    }
                    
                    useMana(shadowExtraction.getSkillManaCost());
                    regenMana(shadowExtraction.getSkillManaRegen());
                    shadowExtraction.triggerSkillCooldown();
                    
                    if(isIgrisSummoned) {
                        msg += " Igris also attacks!";
                        target.takeDamage(summonIgris.getSkillDamage());
                        isIgrisSummoned = false;
                    }
                    return msg + "Dealt damage.";
                }
                return "Skill on cooldown or insufficient mana.";
                
            case 3:
                if(target.getIsBlocking()){
                    isIgrisSummoned = true;
                    target.block(target);
                    return "Summoned Igris, but the attack was blocked!";
                }
                if(summonIgris.isSkillAvailable() && (getCharacterCurrentMana() >= summonIgris.getSkillManaCost())) {
                    String msg = getCharacterName() + " summoned the mighty Igris! ";
                    if(isShadowSummoned) {
                        msg += "Shadow Empowered (+4 DMG)! ";
                        target.takeDamage(shadowSlash.getSkillDamage() + 4);
                        isShadowSummoned = false; 
                    } else {
                        target.takeDamage(summonIgris.getSkillDamage());
                    }
                    
                    useMana(summonIgris.getSkillManaCost());
                    regenMana(summonIgris.getSkillManaRegen());
                    summonIgris.triggerSkillCooldown();
                    isIgrisSummoned = true;
                    return msg;
                }
                return "Skill on cooldown or insufficient mana.";
        }
        return "Invalid Action.";
    }

    @Override
    public CharacterSkills getSkill1() { return this.shadowSlash; }
    @Override
    public CharacterSkills getSkill2() { return this.shadowExtraction; }
    @Override
    public CharacterSkills getSkill3() { return this.summonIgris; }
}