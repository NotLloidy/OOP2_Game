package Characters;

import Foundation.*;

public class ChungMyung extends GameCharacter implements _SkillsInterface {

    private Skill blossomThrust;
    private Skill plumPetalDance;
    private Skill saintVerdict;

    // Passive: Plum Resolve
    private int plumResolveStacks = 0; // Current passive stacks
    private boolean lastSkillWasBlossom = false; // Track for Plum Petal Dance bonus

    public ChungMyung() {
        super("Chung-Myung", "Human", "Sword Saint", 190, 50, 100);

        blossomThrust = new Skill("Blossom Thrust", 10, 0, 5, 0,0);
        plumPetalDance = new Skill("Plum Petal Dance", 25, 20, 0, 3,0);
        saintVerdict = new Skill("Saint's Verdict", 46, 70, 0, 999,0);
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        if(getIsStunned()) {
            System.out.println("\n" + getCharacterName() + " is stunned and cannot act this turn!");
            setIsStunned(false); // Remove stun after skipping turn
            return;
        }
        switch (skillNumber) {
            case 1: // Blossom Thrust
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if (blossomThrust.isSkillAvailable() && getCharacterCurrentMana() >= blossomThrust.getSkillManaCost()) {
                    int damage = applyPlumResolve(blossomThrust.getSkillDamage());

                    // Bonus if target below 50% HP
                    if (target.getCharacterCurrentHealthPoints() <= target.getCharacterMaxHealthPoints() / 2) {
                        System.out.println("\nBlossom Thrust hit a critical strike on the weakened target! +15% damage!");
                        damage += damage * 15 / 100;
                    }

                    target.takeDamage(damage);
                    useMana(blossomThrust.getSkillManaCost());
                    regenMana(blossomThrust.getSkillManaRegen());
                    blossomThrust.triggerSkillCooldown();

                    // Increase passive stack
                    addPlumResolveStack(damage);

                    lastSkillWasBlossom = true; // track for Plum Petal Dance bonus
                }
                break;
            case 2: // Plum Petal Dance
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if (plumPetalDance.isSkillAvailable() && getCharacterCurrentMana() >= plumPetalDance.getSkillManaCost()) {
                    int damage = plumPetalDance.getSkillDamage();

                    // Bonus if Blossom Thrust was used last turn
                    if (lastSkillWasBlossom) {
                        System.out.println("\nPlum Petal Dance is empowered by the previous Blossom Thrust! +10 damage!");
                        damage += 10; // additional fixed 10 damage
                    }

                    // Apply Plum Resolve passive
                    damage = applyPlumResolve(damage);

                    target.takeDamage(damage);
                    useMana(plumPetalDance.getSkillManaCost());
                    regenMana(plumPetalDance.getSkillManaRegen());
                    plumPetalDance.triggerSkillCooldown();

                    addPlumResolveStack(damage);
                    lastSkillWasBlossom = false;
                }
                break;
            case 3: // Saint's Verdict
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if (saintVerdict.isSkillAvailable() && getCharacterCurrentMana() >= saintVerdict.getSkillManaCost()) {
                    int damage = saintVerdict.getSkillDamage();

                    addPlumResolveStack(damage);
                    // Apply Plum Resolve passive
                    damage = applyPlumResolve(damage);
                    plumResolveStacks = 0;

                    target.takeDamage(damage);
                    useMana(saintVerdict.getSkillManaCost());
                    regenMana(saintVerdict.getSkillManaRegen());
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
            System.out.println("\nPlum Resolve increased damage by 5%!");
        } else if (plumResolveStacks >= 3) {
            damage += damage * (plumResolveStacks * 10) / 100; // +30% bonus at 3 stacks
            regenMana(20); // restore 20 mana
            System.out.println("\nPlum Resolve fully charged! +30% damage and restored 20 mana!");
            plumResolveStacks = 0; // reset stacks after boost
        }

        return damage;
    }

    public void addPlumResolveStack(int damageTaken) {
        if (plumResolveStacks < 3 && damageTaken > 0) {
            plumResolveStacks++;
        }
    }

    @Override
    public Skill getSkill1() { 
        return this.blossomThrust; 
    }

    @Override
    public Skill getSkill2() { 
        return this.plumPetalDance; 
    }

    @Override
    public Skill getSkill3() { 
        return this.saintVerdict; 
    }
}