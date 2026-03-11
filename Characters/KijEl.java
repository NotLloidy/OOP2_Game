package Characters;

import Foundation.*;

public class KijEl extends GameCharacter {
    private Skill arcaneBlast;
    private Skill cinderLance;
    private Skill cataclysmSigil;

    public KijEl() {
        super("Kij-EL", "Human", "Arcane Mage", 200, 50, 100);
        
        arcaneBlast = new Skill("Arcane Blast", 10, 0, 10, 0);
        cinderLance = new Skill("Cinder Lance", 20, 30, 0, 2);
        cataclysmSigil = new Skill("Cataclysm Sigil", 60, 70, 0, 999);
    }   

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        Skill skillToUse = null;

        switch(skillNumber) {
            case 1: 
                skillToUse = arcaneBlast; 
                break;
            case 2: 
                skillToUse = cinderLance; 
                break;
            case 3: 
                skillToUse = cataclysmSigil; 
                break;
        }

        // check if skill can be used
        if(skillToUse.isSkillAvailable() && getCharacterCurrentMana() >= skillToUse.getSkillManaCost()) {

            // calculate damage
            int damage = skillToUse.getSkillDamage();

            // --- PASSIVE: Arcane's Oath ---
            if(!target.getCharacterRace().equals("Human")) {
                damage += (int)(damage * 0.25); // +25% damage vs non-Human
            }

            // apply damage
            target.takeDamage(damage);

            // mana adjustments
            regenMana(skillToUse.getSkillManaRegen());
            useMana(skillToUse.getSkillManaCost());

            // trigger cooldown
            skillToUse.triggerSkillCooldown();
        }
    }

    public Skill getArcaneBlast() { 
        return this.arcaneBlast; 
    }

    public Skill getCinderLance() { 
        return this.cinderLance; 
    }

    public Skill getCataclysmSigil() { 
        return this.cataclysmSigil; 
    }
}
