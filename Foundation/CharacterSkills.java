package Foundation;

public class CharacterSkills {
    private String skillName;
    private int skillDamage;
    private int skillManaCost;
    private int skillManaRegen;
    private int skillCooldown;
    private int skillCurrentCooldown;

    public CharacterSkills(String skillName, int skillDamage, int skillManaCost, int skillManaRegen, int skillCooldown, int skillCurrentCooldown) {
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

    public void setSkillCurrentCooldown(int skillCurrentCooldown) {
        this.skillCurrentCooldown = skillCurrentCooldown;
    }

    public int getSkillMaxCooldown() {
        return this.skillCooldown;
    }

    public boolean isSkillAvailable() {
        return this.skillCurrentCooldown == 0;
    }

    public void triggerSkillCooldown() {
        this.skillCurrentCooldown = this.skillCooldown;
    }

    public void reduceSkillCooldown() {
        // 999 is the permanent-lock sentinel for one-time ultimates — never decrement it
        if(this.skillCurrentCooldown > 0 && this.skillCurrentCooldown != 999) {
            this.skillCurrentCooldown--;
        }
    }

    public void resetCooldown() {
        this.skillCurrentCooldown = 0;
    }
}