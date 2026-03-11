package Characters;

import Foundation.*;

public class Kenneth extends GameCharacter {

    private Skill aimedShot;
    private Skill overwatchStance;
    private Skill suppressiveVolley;

    private int disciplineStacks = 0; // Passive stacks (max 2)
    private int nextAttackBonus = 0;  // +30 damage from Overwatch
    private boolean overwatchReady = false;

    public Kenneth() {
        super("Kenneth", "Human", "Marksman", 150, 30, 60);

        aimedShot = new Skill("Aimed Shot", 20, 10, 0, 1);
        overwatchStance = new Skill("Overwatch Stance", 0, 15, 0, 2);
        suppressiveVolley = new Skill("Suppressive Volley", 40, 20, 0, 4);
    }

    private int applyMarksmanDisciplineBonus(int baseDamage) {
        int bonusDamage = baseDamage + baseDamage * disciplineStacks * 30 / 100 + nextAttackBonus;
        disciplineStacks = 0;   // Reset stacks after a damaging skill
        nextAttackBonus = 0;     // Reset Overwatch bonus after use
        return bonusDamage;
    }

    public void skipTurn() {
        if (disciplineStacks < 2) {
            disciplineStacks++;
        }
        System.out.println(getCharacterName() + " skipped their turn. Discipline stacks: " + disciplineStacks);
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        switch (skillNumber) {
            case 1: // Aimed Shot
                if (aimedShot.isSkillAvailable() && getCharacterCurrentMana() >= aimedShot.getSkillManaCost()) {
                    int damage = applyMarksmanDisciplineBonus(aimedShot.getSkillDamage());
                    if (target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        damage += damage * 20 / 100; // bonus vs low HP
                    }
                    target.takeDamage(damage);
                    useMana(aimedShot.getSkillManaCost());
                    aimedShot.triggerSkillCooldown();
                }
                break;

            case 2: // Overwatch Stance
                if (overwatchStance.isSkillAvailable() && getCharacterCurrentMana() >= overwatchStance.getSkillManaCost()) {
                    useMana(overwatchStance.getSkillManaCost());
                    overwatchReady = true; // counter next attack
                    overwatchStance.triggerSkillCooldown();
                    System.out.println(getCharacterName() + " is in Overwatch stance!");
                }
                break;

            case 3: // Suppressive Volley
                if (suppressiveVolley.isSkillAvailable() && getCharacterCurrentMana() >= suppressiveVolley.getSkillManaCost()) {
                    int damage = applyMarksmanDisciplineBonus(suppressiveVolley.getSkillDamage());
                    target.takeDamage(damage);
                    useMana(suppressiveVolley.getSkillManaCost());
                    suppressiveVolley.triggerSkillCooldown();
                }
                break;
        }
    }

    @Override
    public void takeDamage(int amount) {
        if (!isCharacterAlive() || amount <= 0) return;

        if (overwatchReady) {
            // Nullify this damage and add 30 bonus to next attack
            nextAttackBonus += 30;
            overwatchReady = false;
            System.out.println(getCharacterName() + " avoided damage and stored +30 damage for next attack!");
            return;
        }

        // Apply damage normally
        super.takeDamage(amount);
    }

    // Getters
    public Skill getAimedShot() { return aimedShot; }
    public Skill getOverwatchStance() { return overwatchStance; }
    public Skill getSuppressiveVolley() { return suppressiveVolley; }
    public int getDisciplineStacks() { return disciplineStacks; }
    public int getNextAttackBonus() { return nextAttackBonus; }
}