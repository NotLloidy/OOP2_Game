package Foundation;

public abstract class GameCharacter {
    private String characterName;
    private String characterRace;
    private String characterClass;
    private int characterMaxHealthPoints;
    private int characterCurrentHealthPoints;
    private int characterMaxMana;
    private int characterCurrentMana;
    private boolean isCharacterAlive;

    //Initializes all the character stats.
    public GameCharacter(String characterName, String characterRace, String characterClass, int characterMaxHealthPoints, int characterCurrentMana, int characterMaxMana) {
        this.characterName = characterName;
        this.characterRace = characterRace;
        this.characterClass = characterClass;
        this.characterMaxHealthPoints = characterMaxHealthPoints;
        this.characterCurrentHealthPoints = characterMaxHealthPoints;
        this.characterMaxMana = characterMaxMana;
        this.characterCurrentMana = characterCurrentMana;
        this.isCharacterAlive = true;
    }

    public String getCharacterName() {
        return this.characterName;
    }

    public String getCharacterRace() {
        return this.characterRace;
    }

    public String getCharacterClass() {
        return this.characterClass;
    }

    public int getCharacterMaxHealthPoints() {
        return this.characterMaxHealthPoints;
    }

    public int getCharacterCurrentHealthPoints() {
        return this.characterCurrentHealthPoints;
    }

    public int getCharacterMaxMana() {
        return this.characterMaxMana;
    }

    public int getCharacterCurrentMana() {
        return this.characterCurrentMana;
    }

    protected void revive(int hpAmount, int manaAmount) {
        this.characterCurrentHealthPoints = Math.min(characterMaxHealthPoints, hpAmount);
        this.characterCurrentMana = Math.min(characterMaxMana, manaAmount);
        this.isCharacterAlive = true;
    }

    public boolean isCharacterAlive() {
        return this.isCharacterAlive;
    }

    //Reduces characters hp with damage amount.
    public void takeDamage(int amount) {
        if (amount <= 0 || !isCharacterAlive) return;

        this.characterCurrentHealthPoints -= amount;

        if (this.characterCurrentHealthPoints <= 0) {
            this.characterCurrentHealthPoints = 0;
            this.isCharacterAlive = false;
        }
    }

    //Heals characters hp with special skills.
    public void heal(int amount) {
        if (amount <= 0 || !isCharacterAlive) return;

        //Usage of "Math.min" ensures current HP doesn't go past max HP.
        this.characterCurrentHealthPoints = Math.min(this.characterMaxHealthPoints, (this.characterCurrentHealthPoints + amount));
    }

    //Reduces characters mana with the specified skill's mana cost.
    public void useMana(int amount) {
        if (amount <= 0 || !isCharacterAlive) return;

        this.characterCurrentMana -= amount;
    }

    //Regenerates characters mana with a fixed amount of 10 + special characters skills.
    public void regenMana(int amount) {
        if (amount <= 0 || !isCharacterAlive) return;

        //Usage of "Math.min" ensures current HP doesn't go past max HP.
        this.characterCurrentMana = Math.min(this.characterMaxMana, (this.characterCurrentMana + amount));
    }

    //Abstract function to allow characters to pick a specific skill (1-3) and target (attacking/defending).
    public abstract void useSkill(int skillNumber, GameCharacter target);
}