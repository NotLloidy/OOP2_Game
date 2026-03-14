package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

public class BrivanJawmir extends GameCharacter implements _SkillsInterface {

    Skill riftCleaverThrust;
    Skill crescentReaver;
    Skill emberVault;

    public BrivanJawmir() {
        super("Brivan Jawmir", "Human", "Arcane Warden", 160, 60, 10);

        riftCleaverThrust = new Skill("Rift Cleaver Thrust", 10, 0, 10, 0,0);
        crescentReaver = new Skill("Crescent Reaver", 10, 20, 0, 3,0);
        emberVault = new Skill("Ember Vault", 50, 50, 0, 4,0);
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        switch(skillNumber) {
            case 1:
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if(riftCleaverThrust.isSkillAvailable() && (getCharacterCurrentMana() >= riftCleaverThrust.getSkillManaCost())) {
                    target.takeDamage(riftCleaverThrust.getSkillDamage());
                    useMana(riftCleaverThrust.getSkillManaCost());
                    regenMana(riftCleaverThrust.getSkillManaRegen());
                }
                break;
            case 2:
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if(crescentReaver.isSkillAvailable() && (getCharacterCurrentMana() >= crescentReaver.getSkillManaCost())) {
                    target.takeDamage(crescentReaver.getSkillDamage());
                    useMana(crescentReaver.getSkillManaCost());
                    regenMana(crescentReaver.getSkillManaRegen());
                    crescentReaver.triggerSkillCooldown();
                }
                break;
            case 3:
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if(emberVault.isSkillAvailable() && (getCharacterCurrentMana() >= emberVault.getSkillManaCost())) {
                    target.takeDamage(emberVault.getSkillDamage());
                    useMana(emberVault.getSkillManaCost());
                    regenMana(emberVault.getSkillManaRegen());
                    emberVault.triggerSkillCooldown();
                }
                break;
        }
    }

    @Override
    public Skill getSkill1() { 
        return this.riftCleaverThrust; 
    }

    @Override
    public Skill getSkill2() { 
        return this.crescentReaver; 
    }
    
    @Override
    public Skill getSkill3() { 
        return this.emberVault; 
    }
}

