package Characters;

import Foundation.*;

public class ChungMyung extends GameCharacter {

    private Skill blossomThrust;
    private Skill plumPetalDance;
    private Skill saintVerdict;

    // Passive: Plum Resolve
    private int plumResolveStacks = 0; // Current passive stacks
    private boolean lastSkillWasBlossom = false; // Track for Plum Petal Dance bonus

    public ChungMyung() {
        super("Chung-Myung", "Human", "Sword Saint", 190, 50, 100);

        blossomThrust = new Skill("Blossom Thrust", 10, 0, 5, 0);
        plumPetalDance = new Skill("Plum Petal Dance", 25, 20, 0, 3);
        saintVerdict = new Skill("Saint's Verdict", 46, 70, 0, 999);
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        switch (skillNumber) {
            case 1: // Blossom Thrust
                if (blossomThrust.isSkillAvailable() && getCharacterCurrentMana() >= blossomThrust.getSkillManaCost()) {
                    int damage = applyPlumResolve(blossomThrust.getSkillDamage());

                    // Bonus if target below 50% HP
                    if (target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        damage += damage * 15 / 100;
                    }

                    target.takeDamage(damage);
                    regenMana(blossomThrust.getSkillManaRegen());
                    blossomThrust.triggerSkillCooldown();

                    // Increase passive stack
                    addPlumResolveStack(damage);

                    lastSkillWasBlossom = true; // track for Plum Petal Dance bonus
                }
                break;
            case 2: // Plum Petal Dance
                if (plumPetalDance.isSkillAvailable() && getCharacterCurrentMana() >= plumPetalDance.getSkillManaCost()) {
                    int damage = plumPetalDance.getSkillDamage();

                    // Bonus if Blossom Thrust was used last turn
                    if (lastSkillWasBlossom) {
                        damage += 10; // additional fixed 10 damage
                    }

                    // Apply Plum Resolve passive
                    damage = applyPlumResolve(damage);

                    target.takeDamage(damage);
                    useMana(plumPetalDance.getSkillManaCost());
                    plumPetalDance.triggerSkillCooldown();

                    addPlumResolveStack(damage);
                    lastSkillWasBlossom = false;
                }
                break;
            case 3: // Saint's Verdict
                if (saintVerdict.isSkillAvailable() && getCharacterCurrentMana() >= saintVerdict.getSkillManaCost()) {
                    int damage = saintVerdict.getSkillDamage();

                    addPlumResolveStack(damage);
                    // Apply Plum Resolve passive
                    damage = applyPlumResolve(damage);
                    plumResolveStacks = 0;

                    target.takeDamage(damage);
                    useMana(saintVerdict.getSkillManaCost());
                    saintVerdict.triggerSkillCooldown();

                    lastSkillWasBlossom = false;
                }
                break;
        }
    }

    // Apply Plum Resolve stacks to damage
    public int applyPlumResolve(int baseDamage) {
        int damage = baseDamage;

        if (plumResolveStacks > 0 && plumResolveStacks < 3) {
            damage += damage * (plumResolveStacks * 5) / 100; // +5% per stack
        } else if (plumResolveStacks >= 3) {
            damage += damage * (plumResolveStacks * 10) / 100; // +30% bonus at 3 stacks
            regenMana(20); // restore 20 mana
            plumResolveStacks = 0; // reset stacks after boost
        }

        return damage;
    }

    public void addPlumResolveStack(int damageTaken) {
        if (plumResolveStacks < 3 && damageTaken > 0) {
            plumResolveStacks++;
        }
    }

    // Getters for skills
    public Skill getBlossomThrust() { return blossomThrust; }
    public Skill getPlumPetalDance() { return plumPetalDance; }
    public Skill getSaintVerdict() { return saintVerdict; }
    public int getPlumResolveStacks() { return plumResolveStacks; }
}