package Characters;

import Foundation.*;

public class SoleilMooncrest extends GameCharacter {

    private CharacterSkills moonStrike;
    private CharacterSkills moonlightShine;
    private CharacterSkills shadowBlast;
    private int moonsBlessingCooldown = 0;  
    private boolean eclipseEmpowermentUsed = false;
    private boolean reversePowerUsed = false;
    private boolean reflectNextAttack = false;

    public SoleilMooncrest() {
        super("Soleil Mooncrest", "Human", "Moonlit Witch", 200, 50, 100);

        moonStrike = new CharacterSkills("Moon Strike", 20, 0, 10, 0,0);
        moonlightShine = new CharacterSkills("Moonlight Shine", 45, 30, 0, 2,0);
        shadowBlast = new CharacterSkills("Shadow Blast", 80, 50, 0, 999,4);
    }

    @Override
    public void takeDamage(int amount) {
        if (!isCharacterAlive()) return;
        
        // This reflection needs special handling in a real turn-based engine, 
        // but for now, we just negate the damage if reflected.
        if (reflectNextAttack) {
            reflectNextAttack = false;
            return;
        }
        super.takeDamage(amount);
    }   

    @Override
    public String useSkill(int skillNumber, GameCharacter target) {
        if(getIsStunned()) {
            setIsStunned(false);
            return getCharacterName() + " is stunned and cannot act this turn!";
        }

        if (moonsBlessingCooldown > 0) {
            moonsBlessingCooldown--;
        }

        switch(skillNumber) {
            case 1: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if (moonStrike.isSkillAvailable()) {
                    if (target == this && moonsBlessingCooldown == 0) {
                        heal(10);
                        useMana(moonStrike.getSkillManaCost());
                        regenMana(20);
                        moonsBlessingCooldown = 2; 
                        return getCharacterName() + " activated Moon's Blessing! Healed 10 HP and restored Mana.";
                    } else if (target != this) {
                        target.takeDamage(moonStrike.getSkillDamage());
                        regenMana(moonStrike.getSkillManaRegen());
                        return getCharacterName() + " used Moon Strike! Dealt " + moonStrike.getSkillDamage() + " damage.";
                    }
                    moonStrike.triggerSkillCooldown(); 
                }
                return "Not enough mana!";

            case 2: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if (moonlightShine.isSkillAvailable() && getCharacterCurrentMana() >= moonlightShine.getSkillManaCost()) {
                    if (target == this && !eclipseEmpowermentUsed) {
                        int boostedDamage = (int)(moonlightShine.getSkillDamage() * 1.25);
                        useMana(moonlightShine.getSkillManaCost());
                        regenMana(moonlightShine.getSkillManaRegen());
                        target.takeDamage(boostedDamage);
                        eclipseEmpowermentUsed = true;
                        moonlightShine.triggerSkillCooldown();
                        return getCharacterName() + " activated Eclipse Empowerment! Dealt " + boostedDamage + " massive damage!";
                    } else {
                        target.takeDamage(moonlightShine.getSkillDamage());
                        useMana(moonlightShine.getSkillManaCost());
                        moonlightShine.triggerSkillCooldown();
                        return getCharacterName() + " used Moonlight Shine! Dealt " + moonlightShine.getSkillDamage() + " damage.";
                    }
                }
                return "Skill on cooldown or insufficient mana.";

            case 3: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if (shadowBlast.isSkillAvailable() && getCharacterCurrentMana() >= shadowBlast.getSkillManaCost()) {
                    if (target == this && !reversePowerUsed) {
                        reversePowerUsed = true;
                        reflectNextAttack = true; 
                        useMana(shadowBlast.getSkillManaCost());
                        regenMana(shadowBlast.getSkillManaRegen());
                        shadowBlast.triggerSkillCooldown();
                        return getCharacterName() + " activated Reverse Power! The next attack will be reflected.";
                    } else {
                        target.takeDamage(shadowBlast.getSkillDamage());
                        useMana(shadowBlast.getSkillManaCost());
                        shadowBlast.triggerSkillCooldown();
                        return getCharacterName() + " cast Shadow Blast! Dealt " + shadowBlast.getSkillDamage() + " damage.";
                    }
                }
                return "Skill on cooldown or insufficient mana.";
        }
        return "Invalid Action.";
    }

    @Override
    public CharacterSkills getSkill1() { return moonStrike; }
    @Override
    public CharacterSkills getSkill2() { return moonlightShine; }
    @Override
    public CharacterSkills getSkill3() { return shadowBlast; }
}