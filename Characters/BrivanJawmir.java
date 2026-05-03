package Characters;

import Foundation.*;

public class BrivanJawmir extends GameCharacter {

    private CharacterSkills riftCleaverThrust;
    private CharacterSkills crescentReaver;
    private CharacterSkills emberVault;

    public BrivanJawmir() {
        super("Brivan Jawmir", "Human", "Arcane Warden", 160, 60, 100);

        riftCleaverThrust = new CharacterSkills("Rift Cleaver Thrust", 20, 0, 10, 0,0);
        crescentReaver = new CharacterSkills("Crescent Reaver", 45, 20, 0, 3,0);
        emberVault = new CharacterSkills("Ember Vault", 80, 50, 0, 999,4);
    }

    @Override
    public String useSkill(int skillNumber, GameCharacter target) {
        if(getIsStunned()) {
            setIsStunned(false);
            return getCharacterName() + " is stunned and cannot act this turn!";
        }
        
        switch(skillNumber) {
            case 1:
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if(riftCleaverThrust.isSkillAvailable() && (getCharacterCurrentMana() >= riftCleaverThrust.getSkillManaCost())) {
                    int damage = riftCleaverThrust.getSkillDamage();
                    String critMsg = "";
                    
                    if(target.getIsMarked()) {
                        if(Math.random() < 0.25) {
                            critMsg = " Critical Strike! ";
                            damage += 2;
                        }
                    }
                    target.takeDamage(damage);
                    useMana(riftCleaverThrust.getSkillManaCost());
                    regenMana(riftCleaverThrust.getSkillManaRegen());
                    target.setIsMarked(true);
                    return getCharacterName() + " used Rift Cleaver Thrust!" + critMsg + " Dealt " + damage + " damage and marked the target.";
                }
                return "Not enough mana!";
                
            case 2:
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if(crescentReaver.isSkillAvailable() && (getCharacterCurrentMana() >= crescentReaver.getSkillManaCost())) {
                    int damage = crescentReaver.getSkillDamage();
                    String critMsg = "";
                    
                    if(target.getIsMarked()) {
                        if(Math.random() < 0.25) {
                            critMsg = " Critical Strike! ";
                            damage += 2;
                        }
                    }
                    target.takeDamage(damage);
                    useMana(crescentReaver.getSkillManaCost());
                    regenMana(crescentReaver.getSkillManaRegen());
                    crescentReaver.triggerSkillCooldown();
                    target.setIsMarked(true); 
                    return getCharacterName() + " used Crescent Reaver!" + critMsg + " Dealt " + damage + " damage and marked the target.";
                }
                return "Skill is on cooldown or insufficient mana!";
                
            case 3:
                if(target.getIsBlocking()){
                    return target.block(target);
                }
                if(emberVault.isSkillAvailable() && (getCharacterCurrentMana() >= emberVault.getSkillManaCost())) {
                    int damage = emberVault.getSkillDamage();
                    String bonusMsg = "";
                    
                    if(Math.random() < 0.60) {
                        bonusMsg = " Ignited! The target is stunned! ";
                        damage += 15;
                        target.setIsStunned(true); 
                    }
                    
                    target.takeDamage(damage);
                    useMana(emberVault.getSkillManaCost());
                    regenMana(emberVault.getSkillManaRegen());
                    emberVault.triggerSkillCooldown();
                    target.setIsMarked(true);
                    return getCharacterName() + " used Ember Vault!" + bonusMsg + " Dealt " + damage + " damage.";
                }
                return "Skill is on cooldown or insufficient mana!";
        }
        return "Invalid action.";
    }

    @Override
    public CharacterSkills getSkill1() { return this.riftCleaverThrust; }
    @Override
    public CharacterSkills getSkill2() { return this.crescentReaver; }
    @Override
    public CharacterSkills getSkill3() { return this.emberVault; }
}