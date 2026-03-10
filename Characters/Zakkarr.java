package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

public class Zakkarr extends GameCharacter implements _SkillsInterface {

    private Skill guardiansBlade;
    private Skill shieldOfValor;
    private Skill deathsReturn;

    private int bladeStacks = 0;
    private boolean hasRevived = false;

    // Passive: Guardian Warrior
    private boolean guardianBoost = false;

    public Zakkarr() {
        super("Zakkarr", "Spirit", "Warrior", 250, 20, 100);

        guardiansBlade = new Skill("Guardian's Blade", 5, 0, 5, 0);
        shieldOfValor = new Skill("Shield of Valor", 20, 0, 0, 2);
        deathsReturn = new Skill("Death's Return", 0, 50, 0, 999);
    }

    // Passive trigger when taking damage
    @Override
    public void takeDamage(int amount) {
        super.takeDamage(amount);

        if(isCharacterAlive()) {
            guardianBoost = true;
        }
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {

        switch(skillNumber) {

            case 1:
                if(guardiansBlade.isSkillAvailable()) {

                    int damage = guardiansBlade.getSkillDamage() + (bladeStacks * 5);

                    // Passive bonus
                    if(guardianBoost) {
                        damage += damage * 10 / 100;
                        guardianBoost = false;
                    }

                    target.takeDamage(damage);

                    regenMana(guardiansBlade.getSkillManaRegen());

                    if(bladeStacks < 2) {
                        bladeStacks++;
                    }

                    guardiansBlade.triggerSkillCooldown();
                }
            break;

            case 2:
                if(shieldOfValor.isSkillAvailable()) {

                    int damage = shieldOfValor.getSkillDamage();

                    if(guardianBoost) {
                        damage += damage * 10 / 100;
                        guardianBoost = false;
                    }

                    target.takeDamage(damage);

                    heal(20);

                    shieldOfValor.triggerSkillCooldown();
                }
            break;

            case 3:
                if(!hasRevived && deathsReturn.isSkillAvailable() && getCharacterCurrentMana() >= deathsReturn.getSkillManaCost()) {

                    useMana(deathsReturn.getSkillManaCost());

                    heal(150);

                    regenMana(100);

                    hasRevived = true;

                    deathsReturn.triggerSkillCooldown();
                }
            break;
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