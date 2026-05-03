package Characters;

import Foundation.*;

public class Kenneth extends GameCharacter {

    private Skill aimedShot;
    private Skill overwatchStance;
    private Skill suppressiveVolley;
    private boolean overwatchReady;

    public Kenneth() {
        super("Kenneth", "Human", "Marksman", 150, 30, 60);

        aimedShot = new Skill("Aimed Shot", 15, 10, 10, 0,0);
        overwatchStance = new Skill("Overwatch Stance", 0, 15, 0, 2,0);
        suppressiveVolley = new Skill("Suppressive Volley", 40, 20, 0, 4,4);

        overwatchReady = false;
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
                if (aimedShot.isSkillAvailable() && getCharacterCurrentMana() >= aimedShot.getSkillManaCost()) {
                    double headshotChance = 0.15;
                    int damage = aimedShot.getSkillDamage();
                    String msg = getCharacterName() + " used Aimed Shot. ";
                    
                    if(overwatchReady) {
                        msg += "Overwatch active (+15 DMG)! ";
                        target.takeDamage(15);
                        overwatchReady = false; 
                    }
                    if (target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        headshotChance = 0.30; 
                    }
                    if(Math.random() < headshotChance) {
                        msg += "HEADSHOT! Damage doubled! ";
                        damage *= 2; 
                    } 
                    
                    target.takeDamage(damage);
                    useMana(aimedShot.getSkillManaCost());
                    regenMana(aimedShot.getSkillManaRegen());
                    aimedShot.triggerSkillCooldown();
                    
                    return msg + "Dealt " + damage + " total damage.";
                }
                return "Not enough mana!";

            case 2: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if (overwatchStance.isSkillAvailable() && getCharacterCurrentMana() >= overwatchStance.getSkillManaCost()) {
                    useMana(overwatchStance.getSkillManaCost());
                    regenMana(overwatchStance.getSkillManaRegen());
                    overwatchReady = true; 
                    overwatchStance.triggerSkillCooldown();
                    return getCharacterName() + " enters Overwatch Stance! Next attack is empowered.";
                }
                return "Skill on cooldown or insufficient mana.";

            case 3: 
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if (suppressiveVolley.isSkillAvailable() && getCharacterCurrentMana() >= suppressiveVolley.getSkillManaCost()) {
                    double headshotChance = 0.15; 
                    int damage = suppressiveVolley.getSkillDamage();
                    String msg = getCharacterName() + " used Suppressive Volley! ";
                    
                    if(overwatchReady) {
                        msg += "Overwatch active (+15 DMG)! ";
                        target.takeDamage(15);
                        overwatchReady = false; 
                    }
                    if (target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        headshotChance = 0.30; 
                    }
                    if(Math.random() < headshotChance) {
                        msg += "HEADSHOT! Damage doubled! ";
                        damage *= 2;
                    } 
                    
                    target.takeDamage(damage);
                    
                    if(Math.random() < 0.50) {
                        msg += "Target Suppressed (Stunned)! ";
                        target.setIsStunned(true); 
                    }
                    
                    useMana(suppressiveVolley.getSkillManaCost());
                    regenMana(suppressiveVolley.getSkillManaRegen());
                    suppressiveVolley.triggerSkillCooldown();
                    
                    return msg + "Dealt " + damage + " total damage.";
                }
                return "Skill on cooldown or insufficient mana.";
        }
        return "Invalid Action.";
    }

    @Override
    public Skill getSkill1() { return aimedShot; }
    @Override
    public Skill getSkill2() { return overwatchStance; }
    @Override
    public Skill getSkill3() { return suppressiveVolley; }
}