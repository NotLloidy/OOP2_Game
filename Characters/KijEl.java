package Characters;

import Foundation.*;

public class KijEl extends GameCharacter {

    private Skill arcaneBlast;
    private Skill cinderLance;
    private Skill cataclysmSigil;

    public KijEl() {
        super("Kij-EL", "Human", "Arcane Mage", 200, 50, 100);
        
        arcaneBlast = new Skill("Arcane Blast", 10, 0, 10, 0,0);
        cinderLance = new Skill("Cinder Lance", 20, 30, 0, 2,0);
        cataclysmSigil = new Skill("Cataclysm Sigil", 60, 70, 0, 999,4);
    }   

    @Override
    public String useSkill(int skillNumber, GameCharacter target) {
        Skill skillToUse = null;
        
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
    public Skill getSkill1() { return this.arcaneBlast; }
    @Override
    public Skill getSkill2() { return this.cinderLance; }
    @Override
    public Skill getSkill3() { return this.cataclysmSigil; }
}