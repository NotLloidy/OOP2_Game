package Characters;

import Foundation.*;

public class KijEl extends GameCharacter {

    private CharacterSkills arcaneBlast;
    private CharacterSkills cinderLance;
    private CharacterSkills cataclysmSigil;

    public KijEl() {
        super("Kij-EL", "Human", "Arcane Mage", 120, 50, 70);
        
        arcaneBlast = new CharacterSkills("Arcane Blast", 20, 0, 10, 0,0);
        cinderLance = new CharacterSkills("Cinder Lance", 45, 30, 0, 2,0);
        cataclysmSigil = new CharacterSkills("Cataclysm Sigil", 80, 70, 0, 999,4);
    }   

    @Override
    public String useSkill(int skillNumber, GameCharacter target) {
        CharacterSkills skillToUse = null;
        
        if(getIsStunned()) {
            setIsStunned(false);
            return getCharacterName() + " is stunned and cannot act this turn!";
        }

        switch(skillNumber) {
            case 1: skillToUse = arcaneBlast; break;
            case 2: skillToUse = cinderLance; break;
            case 3: skillToUse = cataclysmSigil; break;
        }

        if(skillToUse != null && skillToUse.isSkillAvailable() && getCharacterCurrentMana() >= skillToUse.getSkillManaCost()) {
            if(target.getIsBlocking()){
                return target.block(target);
            }
            
            int damage = skillToUse.getSkillDamage();
            String bonusMsg = "";

            if(!target.getCharacterRace().equals("Human")) {
                damage += (int)(damage * 0.25); 
                bonusMsg = " (Arcane's Oath: +25% DMG against non-humans!) ";
            }

            target.takeDamage(damage);
            regenMana(skillToUse.getSkillManaRegen());
            useMana(skillToUse.getSkillManaCost());
            skillToUse.triggerSkillCooldown();
            
            return getCharacterName() + " cast " + skillToUse.getSkillName() + "!" + bonusMsg + " Dealt " + damage + " damage.";
        }
        return "Skill is on cooldown or insufficient mana.";
    }

    @Override
    public CharacterSkills getSkill1() { return this.arcaneBlast; }
    @Override
    public CharacterSkills getSkill2() { return this.cinderLance; }
    @Override
    public CharacterSkills getSkill3() { return this.cataclysmSigil; }
}