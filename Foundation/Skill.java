package Foundation;

public class Skill {
    private String skillName;
    private int skillDamage;
    private int skillManaCost;
    private int skillManaRegen;
    private int skillCooldown;
    private int skillCurrentCooldown;

    public Skill(String skillName, int skillDamage, int skillManaCost, int skillManaRegen, int skillCooldown, int skillCurrentCooldown) {
        this.skillName = skillName;
        this.skillDamage = skillDamage;
        this.skillManaCost = skillManaCost;
        this.skillManaRegen = skillManaRegen;
        this.skillCooldown = skillCooldown;
        this.skillCurrentCooldown = skillCurrentCooldown;
    }

    public String getSkillName() {
        return this.skillName;
    }

    public int getSkillDamage() {
        return this.skillDamage;
    }

    public int getSkillManaCost() {
        return this.skillManaCost;
    }

    public int getSkillManaRegen() {
        return (10 + this.skillManaRegen);
    }

    public int getSkillCurrentCooldown() {
        return this.skillCurrentCooldown;
    }

    public boolean isSkillAvailable() {
        return this.skillCurrentCooldown == 0;
    }

    public void triggerSkillCooldown() {
        this.skillCurrentCooldown = this.skillCooldown;
    }

    public void reduceSkillCooldown() {
        if(this.skillCurrentCooldown > 0) {
            this.skillCurrentCooldown--;
        }
    }
}
