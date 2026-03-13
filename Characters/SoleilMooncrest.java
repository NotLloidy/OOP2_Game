package Characters;

import Foundation.*;

public class SoleilMooncrest extends GameCharacter implements _SkillsInterface {

    private Skill moonStrike;
    private Skill moonlightShine;
    private Skill shadowBlast;
    private int moonsBlessingCooldown = 0;  
    private boolean eclipseEmpowermentUsed = false;
    private boolean reversePowerUsed = false;
    private boolean reflectNextAttack = false;

    public SoleilMooncrest() {
        super("Soleil Mooncrest", "Human", "Moonlit Witch", 200, 50, 100);

        moonStrike = new Skill("Moon Strike", 10, 0, 10, 0,0);
        moonlightShine = new Skill("Moonlight Shine", 20, 30, 0, 2,0);
        shadowBlast = new Skill("Shadow Blast", 40, 50, 0, 5,0);
    }

    @Override
    public void takeDamage(int amount) {
        if (!isCharacterAlive()) return;

        // Check Reverse Power reflection, GUI Should implement the reflect
        if (reflectNextAttack) {
            System.out.println("Reverse Power reflects " + amount + " damage back!");
            reflectNextAttack = false;
            return;
        }

        // Apply normal damage
        super.takeDamage(amount);
    }   

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {

        // Decrement Moon's Blessing cooldown at start of each skill use
        if (moonsBlessingCooldown > 0) {
            moonsBlessingCooldown--;
        }

        switch(skillNumber) {
            case 1: // Moon Strike
                if (moonStrike.isSkillAvailable()) {
                    if (target == this && moonsBlessingCooldown == 0) {
                        // Lunar's Gift: Moon's Blessing (self-heal/mana regen)
                        heal(10);
                        useMana(moonStrike.getSkillManaCost());
                        regenMana(20);
                        moonsBlessingCooldown = 2; // set passive cooldown
                        System.out.println(getCharacterName() + " activated Moon's Blessing!");
                    } else if (target != this) {
                        // Normal attack
                        target.takeDamage(moonStrike.getSkillDamage());
                        regenMana(moonStrike.getSkillManaRegen());
                    }
                    moonStrike.triggerSkillCooldown(); // skill cooldown is 0, still triggers internally
                }
                break;

            case 2: // Moonlight Shine
                if (moonlightShine.isSkillAvailable() && getCharacterCurrentMana() >= moonlightShine.getSkillManaCost()) {
                    if (target == this && !eclipseEmpowermentUsed) {
                        // Lunar's Gift: Eclipse Empowerment (one-time self-boost)
                        int boostedDamage = (int)(moonlightShine.getSkillDamage() * 1.25);
                        useMana(moonlightShine.getSkillManaCost());
                        regenMana(moonlightShine.getSkillManaRegen());
                        target.takeDamage(boostedDamage);
                        eclipseEmpowermentUsed = true;
                        System.out.println(getCharacterName() + " activated Eclipse Empowerment!");
                    } else {
                        // Normal attack
                        target.takeDamage(moonlightShine.getSkillDamage());
                        useMana(moonlightShine.getSkillManaCost());
                    }
                    moonlightShine.triggerSkillCooldown();
                }
                break;

            case 3: // Shadow Blast
                if (shadowBlast.isSkillAvailable() && getCharacterCurrentMana() >= shadowBlast.getSkillManaCost()) {
                    if (target == this && !reversePowerUsed) {
                        // Lunar's Gift: Reverse Power (one-time self-reflect)
                        reversePowerUsed = true;
                        reflectNextAttack = true; // mark self to reflect next incoming attack
                        useMana(shadowBlast.getSkillManaCost());
                        regenMana(shadowBlast.getSkillManaRegen());
                        System.out.println("Activated Reverse Power!");
                    } else {
                        // Normal attack
                        target.takeDamage(shadowBlast.getSkillDamage());
                        useMana(shadowBlast.getSkillManaCost());
                    }
                    shadowBlast.triggerSkillCooldown();
                }
                break;
        }
    }

    @Override
    public Skill getSkill1() {
        return moonStrike;
    }

    @Override
    public Skill getSkill2() {
        return moonlightShine;
    }

    @Override
    public Skill getSkill3() {
        return shadowBlast;
    }
}