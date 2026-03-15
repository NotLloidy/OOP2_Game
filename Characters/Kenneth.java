package Characters;

import Foundation.*;

public class Kenneth extends GameCharacter implements _SkillsInterface {

    private Skill aimedShot;
    private Skill overwatchStance;
    private Skill suppressiveVolley;
    private boolean overwatchReady;

    public Kenneth() {
        super("Kenneth", "Human", "Marksman", 150, 30, 60);

        aimedShot = new Skill("Aimed Shot", 20, 10, 10, 0,0);
        overwatchStance = new Skill("Overwatch Stance", 0, 15, 0, 2,0);
        suppressiveVolley = new Skill("Suppressive Volley", 40, 20, 0, 4,0);

        overwatchReady = false;
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        if(getIsStunned()) {
            System.out.println("\n" + getCharacterName() + " is stunned and cannot act this turn!");
            setIsStunned(false); // Remove stun after skipping turn
            return;
        }
        switch (skillNumber) {
            case 1: // Aimed Shot
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if (aimedShot.isSkillAvailable() && getCharacterCurrentMana() >= aimedShot.getSkillManaCost()) {
                    double headshotChance = 0.15; // Base headshot chance
                    if(overwatchReady) {
                        System.out.println("\nOverwatch Stance is active! Aimed Shot will deal +15 damage.");
                        target.takeDamage(15);// Bonus damage for overwatch
                        overwatchReady = false; // Reset overwatch after use
                    }
                    if (target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        headshotChance = 0.30; // Increased headshot chance on weakened targets
                    }
                    if(Math.random() < headshotChance) {
                        System.out.println("\nAimed Shot hit a headshot! Damage is doubled!");
                        target.takeDamage(aimedShot.getSkillDamage() * 2); // Double damage for headshot
                        headshotChance = 0.15; // Reset headshot chance after hit
                    } else {
                        target.takeDamage(aimedShot.getSkillDamage());
                    }
                    useMana(aimedShot.getSkillManaCost());
                    regenMana(aimedShot.getSkillManaRegen());
                    aimedShot.triggerSkillCooldown();
                }
                break;

            case 2: // Overwatch Stance
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if (overwatchStance.isSkillAvailable() && getCharacterCurrentMana() >= overwatchStance.getSkillManaCost()) {
                    useMana(overwatchStance.getSkillManaCost());
                    regenMana(overwatchStance.getSkillManaRegen());
                    overwatchReady = true; // counter next attack
                    overwatchStance.triggerSkillCooldown();
                }
                break;

            case 3: // Suppressive Volley
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if (suppressiveVolley.isSkillAvailable() && getCharacterCurrentMana() >= suppressiveVolley.getSkillManaCost()) {
                    double headshotChance = 0.15; // Base headshot chance
                    if(overwatchReady) {
                        System.out.println("\nOverwatch Stance is active! Aimed Shot will deal +15 damage.");
                        target.takeDamage(15);// Bonus damage for overwatch
                        overwatchReady = false; // Reset overwatch after use
                    }
                    if (target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        headshotChance = 0.30; // Increased headshot chance on weakened targets
                    }
                    if(Math.random() < headshotChance) {
                        System.out.println("\nSuppressive Volley hit a headshot! Damage is doubled!");
                        target.takeDamage(suppressiveVolley.getSkillDamage() * 2); // Double damage for headshot
                        headshotChance = 0.15; // Reset headshot chance after hit
                    } else {
                        target.takeDamage(suppressiveVolley.getSkillDamage());
                    }
                    if(Math.random() < 0.50) {
                        System.out.println("\nSuppressive Volley suppressed the target! Target will be stunned for 1 turn.");
                        target.setIsStunned(true); 
                    }
                    useMana(suppressiveVolley.getSkillManaCost());
                    regenMana(suppressiveVolley.getSkillManaRegen());
                    suppressiveVolley.triggerSkillCooldown();
                }
                break;
        }
    }


    @Override
    public Skill getSkill1() {
        return aimedShot;
    }

    @Override
    public Skill getSkill2() {
        return overwatchStance;
    }

    @Override
    public Skill getSkill3() {
        return suppressiveVolley;
    }
}