package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

public class ChungMyung extends GameCharacter implements _SkillsInterface {

    private Skill blossomThrust;
    private Skill plumPetalDance;
    private Skill saintVerdict;

    public ChungMyung() {
        super("Chung-Myung", "Human", "Sword Saint", 190, 50, 100);


        blossomThrust = new Skill("Blossom Thrust", 10, 0, 5, 0);
        plumPetalDance = new Skill("Plum Petal Dance", 25, 20, 0, 3);
        saintVerdict = new Skill("Sant's Verdict", 46, 70, 0, 999);
    }   

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {
        switch(skillNumber) {
            case 1:
                if(blossomThrust.isSkillAvailable() && (getCharacterCurrentMana() >= blossomThrust.getSkillManaCost())) {
                    target.takeDamage(blossomThrust.getSkillDamage());
                    regenMana(blossomThrust.getSkillManaRegen());
                    blossomThrust.triggerSkillCooldown();
                }
                break;
            case 2:
                if(plumPetalDance.isSkillAvailable() && (getCharacterCurrentMana() >= plumPetalDance.getSkillManaCost())) {
                    target.takeDamage(plumPetalDance.getSkillDamage());
                    useMana(plumPetalDance.getSkillManaCost());
                    plumPetalDance.triggerSkillCooldown();
                }
                break;
            case 3:
                if(saintVerdict.isSkillAvailable() && (getCharacterCurrentMana() >= saintVerdict.getSkillManaCost())) {
                    target.takeDamage(saintVerdict.getSkillDamage());
                    useMana(saintVerdict.getSkillManaCost());
                    saintVerdict.triggerSkillCooldown();
                }
                break;
        }
    }

    @Override
    public Skill getSkill1() { 
        return this.blossomThrust; 
    }

    @Override
    public Skill getSkill2() { 
        return this.plumPetalDance; 
    }

    @Override
    public Skill getSkill3() { 
        return this.saintVerdict; 
    }
}