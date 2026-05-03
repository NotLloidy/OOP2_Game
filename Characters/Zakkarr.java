package Characters;

import Foundation.*;

public class Zakkarr extends GameCharacter {

    private CharacterSkills guardiansBlade;
    private CharacterSkills shieldOfValor;
    private CharacterSkills deathsReturn;

    private int bladeStacks = 0;
    private boolean hasRevived = false;
    private boolean guardianBoost = false;
    private int reviveTurnsRemaining = 0;

    public Zakkarr() {
        super("Zakkarr", "Spirit", "Warrior", 250, 20, 100);

        guardiansBlade = new CharacterSkills("Guardian's Blade", 8, 0, 5, 0,0);
        shieldOfValor = new CharacterSkills("Shield of Valor", 15, 0, 0, 2,0);
        deathsReturn = new CharacterSkills("Death's Return", 0, 50, 0, 999,4);
    }

    @Override
    public void takeDamage(int amount) {
        if (!isCharacterAlive()) return;
        super.takeDamage(amount);

        // This would ideally return a String in a full event-driven engine,
        // but since takeDamage is void, the GUI will handle the health bar update 
        // to show the revival silently.
        if (!hasRevived && getCharacterCurrentHealthPoints() <= 0 && getCharacterCurrentMana() >= deathsReturn.getSkillManaCost()) {
            revive(150, 100); 
            hasRevived = true;
            deathsReturn.triggerSkillCooldown();
        }

        if (isCharacterAlive()) {
            guardianBoost = true; 
        }
    }

    @Override
    public String useSkill(int skillNumber, GameCharacter target) {
        CharacterSkills skillToUse = null;
        int damage = 0;
        String msg = "";

        if(getIsStunned()) {
            setIsStunned(false);
            return getCharacterName() + " is stunned and cannot act this turn!";
        }

        switch(skillNumber) {
            case 1: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                skillToUse = guardiansBlade;
                if(skillToUse.isSkillAvailable()) {
                    damage = skillToUse.getSkillDamage() + (bladeStacks * 5);
                    if(guardianBoost) {
                        damage += damage * 10 / 100; 
                        guardianBoost = false;      
                        msg += "(Guardian Boost Active!) ";
                    }
                    target.takeDamage(damage);
                    useMana(skillToUse.getSkillManaCost());
                    regenMana(skillToUse.getSkillManaRegen());

                    if(bladeStacks < 2) bladeStacks++;
                    skillToUse.triggerSkillCooldown();
                    
                    return getCharacterName() + " used Guardian's Blade! " + msg + "Dealt " + damage + " damage.";
                }
                return "Not enough mana!";
                
            case 2: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                skillToUse = shieldOfValor;
                if(skillToUse.isSkillAvailable()) {
                    damage = skillToUse.getSkillDamage();
                    if(guardianBoost) {
                        damage += damage * 10 / 100;
                        guardianBoost = false;
                        msg += "(Guardian Boost Active!) ";
                    }
                    target.takeDamage(damage);
                    heal(20);
                    skillToUse.triggerSkillCooldown();
                    
                    return getCharacterName() + " used Shield of Valor! " + msg + "Dealt " + damage + " damage and healed 20 HP.";
                }
                return "Skill on cooldown.";
                
            case 3:
                 return "Death's Return is a passive ultimate that activates upon death.";
        }

        if (hasRevived && reviveTurnsRemaining > 0) {
            reviveTurnsRemaining--;
            if (reviveTurnsRemaining == 0) {
                takeDamage(getCharacterCurrentHealthPoints());
                return getCharacterName() + "'s Death's Return effect ended. She has fallen!";
            }
        }
        return "Invalid action.";
    }

    @Override
    public CharacterSkills getSkill1() { return guardiansBlade; }
    @Override
    public CharacterSkills getSkill2() { return shieldOfValor; }
    @Override
    public CharacterSkills getSkill3() { return deathsReturn; }
}