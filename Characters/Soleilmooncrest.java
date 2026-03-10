package Characters;

import Foundation.GameCharacter;
import Foundation.Skill;

public class SoleilMooncrest extends GameCharacter {

    private Skill moonStrike;
    private Skill moonlightShine;
    private Skill shadowBlast;

    public SoleilMooncrest() {
        super("Soleil Mooncrest", "Human", "Moonlit Witch", 200, 50, 100);

        moonStrike = new Skill("Moon Strike", 10, 0, 10, 0);
        moonlightShine = new Skill("Moonlight Shine", 20, 30, 0, 2);
        shadowBlast = new Skill("Shadow Blast", 40, 50, 0, 5);
    }

    @Override
    public void useSkill(int skillNumber, GameCharacter target) {

        switch(skillNumber) {
            case 1:
                if(moonStrike.isSkillAvailable()) {
                    target.takeDamage(moonStrike.getSkillDamage());
                    regenMana(moonStrike.getSkillManaRegen());
                    moonStrike.triggerSkillCooldown();
                }
                break;
            case 2:
                if(moonlightShine.isSkillAvailable() && getCharacterCurrentMana() >= moonlightShine.getSkillManaCost()) {
                    target.takeDamage(moonlightShine.getSkillDamage());
                    useMana(moonlightShine.getSkillManaCost());
                    moonlightShine.triggerSkillCooldown();
                }
                break;
            case 3:
                if(shadowBlast.isSkillAvailable() && getCharacterCurrentMana() >= shadowBlast.getSkillManaCost()) {
                    target.takeDamage(shadowBlast.getSkillDamage());
                    useMana(shadowBlast.getSkillManaCost());
                    shadowBlast.triggerSkillCooldown();
                }
                 break;
        }
    }

    public Skill getMoonStrike() {
        return moonStrike;
    }

    public Skill getMoonlightShine() {
        return moonlightShine;
    }

    public Skill getShadowBlast() {
        return shadowBlast;
    }
}