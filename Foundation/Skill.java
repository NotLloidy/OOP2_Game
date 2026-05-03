package Foundation;

public interface Skill {
    CharacterSkills getSkill1();
    CharacterSkills getSkill2();
    CharacterSkills getSkill3();
    String useSkill(int skillNumber, GameCharacter target);
}