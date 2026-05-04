package Foundation;

public interface Skill {
    public CharacterSkills getSkill1();
    public CharacterSkills getSkill2();
    public CharacterSkills getSkill3();
    public String useSkill(int skillNumber, GameCharacter target);
}