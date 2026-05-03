package Characters;

import Foundation.*;

public class ChungMyung extends GameCharacter {

    private CharacterSkills blossomThrust;
    private CharacterSkills plumPetalDance;
    private CharacterSkills saintVerdict;

    private int plumResolveStacks = 0; 
    private boolean lastSkillWasBlossom = false; 

    public ChungMyung() {
        super("Chung-Myung", "Human", "Sword Saint", 190, 50, 100);

        blossomThrust = new CharacterSkills("Blossom Thrust", 15, 0, 5, 0,0);
        plumPetalDance = new CharacterSkills("Plum Petal Dance", 25, 20, 0, 3,0);
        saintVerdict = new CharacterSkills("Saint's Verdict", 60, 70, 0, 999,4);
    }

    @Override
    public String useSkill(int skillNumber, GameCharacter target) {
        if(getIsStunned()) {
            setIsStunned(false);
            return getCharacterName() + " is stunned and cannot act this turn!";
        }
        
        switch (skillNumber) {
            case 1: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if (blossomThrust.isSkillAvailable() && getCharacterCurrentMana() >= blossomThrust.getSkillManaCost()) {
                    int damage = applyPlumResolveDamage(blossomThrust.getSkillDamage());
                    String extraMsg = "";

                    if (target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        extraMsg = " (Weakened target! +15% damage) ";
                        damage += damage * 15 / 100;
                    }

                    target.takeDamage(damage);
                    useMana(blossomThrust.getSkillManaCost());
                    regenMana(blossomThrust.getSkillManaRegen());
                    blossomThrust.triggerSkillCooldown();

                    String stackMsg = addPlumResolveStack(damage);
                    lastSkillWasBlossom = true; 
                    
                    return getCharacterName() + " used Blossom Thrust!" + extraMsg + " Dealt " + damage + " damage. " + stackMsg;
                }
                return "Not enough mana!";
                
            case 2: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if (plumPetalDance.isSkillAvailable() && getCharacterCurrentMana() >= plumPetalDance.getSkillManaCost()) {
                    int damage = plumPetalDance.getSkillDamage();
                    String extraMsg = "";

                    if (lastSkillWasBlossom) {
                        extraMsg = " Empowered by previous strike! (+10 DMG) ";
                        damage += 10;
                    }

                    damage = applyPlumResolveDamage(damage);
                    target.takeDamage(damage);
                    useMana(plumPetalDance.getSkillManaCost());
                    regenMana(plumPetalDance.getSkillManaRegen());
                    plumPetalDance.triggerSkillCooldown();

                    String stackMsg = addPlumResolveStack(damage);
                    lastSkillWasBlossom = false;
                    
                    return getCharacterName() + " used Plum Petal Dance!" + extraMsg + " Dealt " + damage + " damage. " + stackMsg;
                }
                return "Skill is on cooldown or insufficient mana!";
                
            case 3: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if (saintVerdict.isSkillAvailable() && getCharacterCurrentMana() >= saintVerdict.getSkillManaCost()) {
                    int damage = saintVerdict.getSkillDamage();
                    
                    String stackMsg = addPlumResolveStack(damage);
                    damage = applyPlumResolveDamage(damage);
                    plumResolveStacks = 0; 

                    target.takeDamage(damage);
                    useMana(saintVerdict.getSkillManaCost());
                    regenMana(saintVerdict.getSkillManaRegen());
                    saintVerdict.triggerSkillCooldown();

                    lastSkillWasBlossom = false;
                    return getCharacterName() + " used Saint's Verdict! Dealt " + damage + " damage. Plum stacks consumed. " + stackMsg;
                }
                return "Ultimate is on cooldown or insufficient mana!";
        }
        return "Invalid action.";
    }

    private int applyPlumResolveDamage(int baseDamage) {
        int damage = baseDamage;
        if (plumResolveStacks > 0 && plumResolveStacks < 3) {
            damage += damage * (plumResolveStacks * 5) / 100; 
        } else if (plumResolveStacks >= 3) {
            damage += damage * 30 / 100; 
            regenMana(20); 
            plumResolveStacks = 0; 
        }
        return damage;
    }

    private String addPlumResolveStack(int damageTaken) {
        if (plumResolveStacks < 3 && damageTaken > 0) {
            plumResolveStacks++;
            if (plumResolveStacks >= 3) return "Plum Resolve is fully charged!";
            return "Plum Resolve stack added.";
        }
        return "";
    }

    @Override
    public CharacterSkills getSkill1() { return this.blossomThrust; }
    @Override
    public CharacterSkills getSkill2() { return this.plumPetalDance; }
    @Override
    public CharacterSkills getSkill3() { return this.saintVerdict; }
}