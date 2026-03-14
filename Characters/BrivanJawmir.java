package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

public class BrivanJawmir extends GameCharacter implements _SkillsInterface {

    private Skill riftCleaverThrust;
    private Skill crescentReaver;
    private Skill emberVault;

    public BrivanJawmir() {
        super("Brivan Jawmir", "Human", "Arcane Warden", 160, 60, 100);

        riftCleaverThrust = new Skill("Rift Cleaver Thrust", 10, 0, 10, 0,0);
        crescentReaver = new Skill("Crescent Reaver", 10, 20, 0, 3,0);
        emberVault = new Skill("Ember Vault", 50, 50, 0, 4,0);

    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        if(getIsStunned()) {
            System.out.println("\n" + getCharacterName() + " is stunned and cannot act this turn!");
            setIsStunned(false); // Remove stun after skipping turn
            return;
        }
        switch(skillNumber) {
            case 1:
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if(riftCleaverThrust.isSkillAvailable() && (getCharacterCurrentMana() >= riftCleaverThrust.getSkillManaCost())) {
                    if(target.getIsMarked()) {
                        if(Math.random() < 0.25) {
                            System.out.println("\nBrivan's Rift Cleaver Thrust hit a critical strike on the marked target! +2 bonus damage!");
                            target.takeDamage(2); 
                        }
                    }
                    target.takeDamage(riftCleaverThrust.getSkillDamage());
                    useMana(riftCleaverThrust.getSkillManaCost());
                    regenMana(riftCleaverThrust.getSkillManaRegen());
                    target.setIsMarked(true); // Mark the target for passive bonus
                }
                break;
            case 2:
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if(crescentReaver.isSkillAvailable() && (getCharacterCurrentMana() >= crescentReaver.getSkillManaCost())) {
                    if(target.getIsMarked()) {
                        if(Math.random() < 0.25) {
                            System.out.println("\nBrivan's Crescent Reaver hit a critical strike on the marked target! +2 bonus damage!");
                            target.takeDamage(2); 
                        }
                    }
                    target.takeDamage(crescentReaver.getSkillDamage());
                    useMana(crescentReaver.getSkillManaCost());
                    regenMana(crescentReaver.getSkillManaRegen());
                    crescentReaver.triggerSkillCooldown();
                    target.setIsMarked(true); 
                }
                break;
            case 3:
                if(target.getIsBlocking()){
                    target.block(target);
                    return;
                }
                if(emberVault.isSkillAvailable() && (getCharacterCurrentMana() >= emberVault.getSkillManaCost())) {
                    if(Math.random() < 0.60) {
                        System.out.println("\nBrivan's Ember Vault ignited the target! +15 bonus damage, and stunning enemy for 1 turn!");
                        target.takeDamage(15); 
                        target.setIsStunned(true); 
                    }
                    target.takeDamage(emberVault.getSkillDamage());
                    useMana(emberVault.getSkillManaCost());
                    regenMana(emberVault.getSkillManaRegen());
                    emberVault.triggerSkillCooldown();
                    target.setIsMarked(true);
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

