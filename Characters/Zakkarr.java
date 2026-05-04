package Characters;

import Foundation.*;

public class Zakkarr extends GameCharacter {

    private CharacterSkills guardiansBlade;
    private CharacterSkills shieldOfValor;
    private CharacterSkills deathsReturn;

    private int bladeStacks = 0;
    private boolean hasRevived       = false;
    private boolean guardianBoost    = false;
    private boolean deathsReturnArmed = false;   // true only after Skill 3 is cast
    private int reviveTurnsRemaining = 0;

    public Zakkarr() {
        super("Zakkarr", "Spirit", "Warrior", 180, 20, 50);

        guardiansBlade = new CharacterSkills("Guardian's Blade", 10, 0, 0, 0,0);
        shieldOfValor = new CharacterSkills("Shield of Valor", 25, 20, 0, 2,0);
        deathsReturn = new CharacterSkills("Death's Return", 0, 50, 0, 999,4);
    }

    @Override
    public void takeDamage(int amount) {
        if (!isCharacterAlive()) return;
        super.takeDamage(amount);

        // Only revive if the player explicitly armed Death's Return with Skill 3
        if (deathsReturnArmed && !hasRevived && getCharacterCurrentHealthPoints() <= 0) {
            revive(getCharacterMaxHealthPoints(), getCharacterMaxMana());
            hasRevived        = true;
            deathsReturnArmed = false;
            reviveTurnsRemaining = 5;
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

        // Count down the revive window — if it expires, Zakkarr falls
        if (hasRevived && reviveTurnsRemaining > 0) {
            reviveTurnsRemaining--;
            if (reviveTurnsRemaining == 0) {
                takeDamage(getCharacterCurrentHealthPoints());
                return getCharacterName() + "'s Death's Return has expired! She collapses!";
            }
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
                    useMana(skillToUse.getSkillManaCost());
                    regenMana(skillToUse.getSkillManaRegen());
                    skillToUse.triggerSkillCooldown();
                    
                    return getCharacterName() + " used Shield of Valor! " + msg + "Dealt " + damage + " damage and healed 20 HP.";
                }
                return "Skill on cooldown.";
                
            case 3:
                if (!deathsReturn.isSkillAvailable()) return "Death's Return is on cooldown.";
                if (hasRevived)                       return "Death's Return has already been used this match.";
                if (deathsReturnArmed)                return "Death's Return is already armed.";
                if (getCharacterCurrentMana() < deathsReturn.getSkillManaCost())
                    return "Not enough mana to arm Death's Return!";

                useMana(deathsReturn.getSkillManaCost());
                deathsReturnArmed = true;
                deathsReturn.triggerSkillCooldown();
                return getCharacterName() + " channels Death's Return! She will revive once upon death (5 turns).";
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