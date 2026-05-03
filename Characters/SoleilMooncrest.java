package Characters;

import Foundation.*;

public class SoleilMooncrest extends GameCharacter {

    private CharacterSkills moonStrike;
    private CharacterSkills moonlightShine;
    private CharacterSkills shadowBlast;
    private int moonsBlessingCooldown = 0;  
    private boolean eclipseEmpowermentUsed = false;
    private boolean reversePowerUsed = false;

    public SoleilMooncrest() {
        super("Soleil Mooncrest", "Human", "Moonlit Witch", 200, 50, 100);

        moonStrike = new CharacterSkills("Moon Strike", 15, 0, 10, 0,0);
        moonlightShine = new CharacterSkills("Moonlight Shine", 30, 30, 0, 2,0);
        shadowBlast = new CharacterSkills("Shadow Blast", 65, 50, 0, 999,4);
    }

    @Override
    public void takeDamage(int amount) {
        if (!isCharacterAlive()) return;

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
                    target.takeDamage(moonStrike.getSkillDamage());
                    regenMana(moonStrike.getSkillManaRegen());
                    return getCharacterName() + " used Moon Strike! Dealt " + moonStrike.getSkillDamage() + " damage.";
                    moonStrike.triggerSkillCooldown(); 
                }
                return "Not enough mana!";

            case 2: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if (moonlightShine.isSkillAvailable() && getCharacterCurrentMana() >= moonlightShine.getSkillManaCost()) {
                    target.takeDamage(moonlightShine.getSkillDamage());
                    useMana(moonlightShine.getSkillManaCost());
                    moonlightShine.triggerSkillCooldown();
                    return getCharacterName() + " used Moonlight Shine! Dealt " + moonlightShine.getSkillDamage() + " damage.";
                }
                return "Skill on cooldown or insufficient mana.";

            case 3: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if (shadowBlast.isSkillAvailable() && getCharacterCurrentMana() >= shadowBlast.getSkillManaCost()) {
                    target.takeDamage(shadowBlast.getSkillDamage());
                    useMana(shadowBlast.getSkillManaCost());
                    shadowBlast.triggerSkillCooldown();
                    return getCharacterName() + " cast Shadow Blast! Dealt " + shadowBlast.getSkillDamage() + " damage.";
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