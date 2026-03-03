package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

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
        switch(skillNumber) {
            case 1:
                if(arcaneBlast.isSkillAvailable() && (getCharacterCurrentMana() >= arcaneBlast.getSkillManaCost())) {
                    target.takeDamage(arcaneBlast.getSkillDamage());
                    regenMana(arcaneBlast.getSkillManaRegen());
                    arcaneBlast.triggerSkillCooldown();
                }
                break;
            case 2:
                if(cinderLance.isSkillAvailable() && (getCharacterCurrentMana() >= cinderLance.getSkillManaCost())) {
                    target.takeDamage(cinderLance.getSkillDamage());
                    useMana(cinderLance.getSkillManaCost());
                    cinderLance.triggerSkillCooldown();
                }
                break;
            case 3:
                if(cataclysmSigil.isSkillAvailable() && (getCharacterCurrentMana() >= cataclysmSigil.getSkillManaCost())) {
                    target.takeDamage(cataclysmSigil.getSkillDamage());
                    useMana(cataclysmSigil.getSkillManaCost());
                    cinderLance.triggerSkillCooldown();
                }
                break;
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
