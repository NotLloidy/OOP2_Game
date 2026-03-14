package Characters;

import Foundation.*;

public class Zakkarr extends GameCharacter implements _SkillsInterface {

    private Skill guardiansBlade;
    private Skill shieldOfValor;
    private Skill deathsReturn;

    private int bladeStacks = 0;
    private boolean hasRevived = false;
    private boolean guardianBoost = false;
    private int reviveTurnsRemaining = 0;

    public Zakkarr() {
        super("Zakkarr", "Spirit", "Warrior", 250, 20, 100);

        guardiansBlade = new Skill("Guardian's Blade", 5, 0, 5, 0,0);
        shieldOfValor = new Skill("Shield of Valor", 20, 0, 0, 2,0);
        deathsReturn = new Skill("Death's Return", 0, 50, 0, 999,0);
    }

    // Passive trigger when taking damage
    @Override
    public void takeDamage(int amount) {
        if (!isCharacterAlive()) return;

        super.takeDamage(amount);

        if (!hasRevived && getCharacterCurrentHealthPoints() <= 0 && getCharacterCurrentMana() >= deathsReturn.getSkillManaCost()) {
            revive(150, 100); // revive HP and mana
            hasRevived = true;
            deathsReturn.triggerSkillCooldown();

            System.out.println(getCharacterName() + " activated Death's Return and revived!");
        }

        // Guardian Warrior passive
        if (isCharacterAlive()) {
            guardianBoost = true; 
        }
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {

        Skill skillToUse = null;
        int damage = 0;

        switch(skillNumber) {
            case 1: // Guardian's Blade
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                skillToUse = guardiansBlade;
                if(skillToUse.isSkillAvailable()) {
                    damage = skillToUse.getSkillDamage() + (bladeStacks * 5);
                    if(guardianBoost) {
                        damage += damage * 10 / 100; // +10% boost
                        guardianBoost = false;       // reset boost
                    }
                    target.takeDamage(damage);
                    useMana(skillToUse.getSkillManaCost());
                    regenMana(skillToUse.getSkillManaRegen());

                    // Blade stacking mechanic
                    if(bladeStacks < 2) bladeStacks++;
                    skillToUse.triggerSkillCooldown();
                }
                break;
            case 2: // Shield of Valor
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                skillToUse = shieldOfValor;
                if(skillToUse.isSkillAvailable()) {
                    damage = skillToUse.getSkillDamage();
                    if(guardianBoost) {
                        damage += damage * 10 / 100;
                        guardianBoost = false;
                    }
                    target.takeDamage(damage);
                    heal(20);
                    skillToUse.triggerSkillCooldown();
                }
                break;
        }

        if (hasRevived && reviveTurnsRemaining > 0) {
            reviveTurnsRemaining--;
            if (reviveTurnsRemaining == 0) {
                takeDamage(getCharacterCurrentHealthPoints());
                System.out.println(getCharacterName() + "'s Death's Return effect ended. She has fallen!");
            }
        }
}

    @Override
    public Skill getSkill1() {
        return guardiansBlade;
    }

    @Override
    public Skill getSkill2() {
        return shieldOfValor;
    }

    @Override
    public Skill getSkill3() {
        return deathsReturn;
    }
}