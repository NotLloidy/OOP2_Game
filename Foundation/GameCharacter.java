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
    private boolean isBlocking;
    private int remainingBlocks;
    private boolean isMarked;
    private boolean isStunned;

    public GameCharacter(String characterName, String characterRace, String characterClass, int characterMaxHealthPoints, int characterCurrentMana, int characterMaxMana) {
        this.characterName = characterName;
        this.characterRace = characterRace;
        this.characterClass = characterClass;
        this.characterMaxHealthPoints = characterMaxHealthPoints;
        this.characterCurrentHealthPoints = characterMaxHealthPoints;
        this.characterMaxMana = characterMaxMana;
        this.characterCurrentMana = characterCurrentMana;
        this.isCharacterAlive = true;
        this.isBlocking = false;
        this.remainingBlocks = 2;
        this.isMarked = false;
        this.isStunned = false;
    }

    public String getCharacterName() { return this.characterName; }
    public String getCharacterRace() { return this.characterRace; }
    public String getCharacterClass() { return this.characterClass; }
    public int getCharacterMaxHealthPoints() { return this.characterMaxHealthPoints; }
    
    public void setCharacterCurrentHealthPoints(int hp) { this.characterCurrentHealthPoints = hp; }
    public int getCharacterCurrentHealthPoints() { return this.characterCurrentHealthPoints; }
    
    public int getCharacterMaxMana() { return this.characterMaxMana; }
    public void setCharacterCurrentMana(int mana) { this.characterCurrentMana = mana; }
    public int getCharacterCurrentMana() { return this.characterCurrentMana; }

    public void setIsBlocking(boolean isBlocking) { this.isBlocking = isBlocking; }
    public boolean getIsBlocking() { return this.isBlocking; }

    public void setRemainingBlocks(int remainingBlocks) { this.remainingBlocks = remainingBlocks; }
    public int getRemainingBlocks() { return this.remainingBlocks; }

    public void setIsStunned(boolean isStunned) { this.isStunned = isStunned; }
    public boolean getIsStunned() { return this.isStunned; }

    public void setIsMarked(boolean isMarked) { this.isMarked = isMarked; }
    public boolean getIsMarked() { return this.isMarked; }

    public String block(GameCharacter target) { 
        target.setIsBlocking(false); 
        target.setRemainingBlocks(target.getRemainingBlocks() - 1); 
        return target.getCharacterName() + " blocked the attack! Dealt no damage.";
    }

    protected void revive(int hpAmount, int manaAmount) {
        this.characterCurrentHealthPoints = Math.min(characterMaxHealthPoints, hpAmount);
        this.characterCurrentMana = Math.min(characterMaxMana, manaAmount);
        this.isCharacterAlive = true;
    }

    public boolean isCharacterAlive() { return this.isCharacterAlive; }

    /**
     * Returns a lowercase, alphanumeric-only key derived from the character name.
     * Used to locate sprite and animation assets, e.g. "A-Vin" -> "avin".
     */
    public String getSpriteKey() {
        return this.characterName.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    public void takeDamage(int amount) {
        if (amount <= 0 || !isCharacterAlive) return;
        this.characterCurrentHealthPoints -= amount;
        if (this.characterCurrentHealthPoints <= 0) {
            this.characterCurrentHealthPoints = 0;
            this.isCharacterAlive = false;
        }
    }

    public void heal(int amount) {
        if (amount <= 0 || !isCharacterAlive) return;
        this.characterCurrentHealthPoints = Math.min(this.characterMaxHealthPoints, (this.characterCurrentHealthPoints + amount));
    }

    public void useMana(int amount) {
        if (this.characterCurrentMana < amount || !isCharacterAlive) return;
        this.characterCurrentMana -= amount;
    }

    public void regenMana(int amount) {
        if (amount <= 0 || !isCharacterAlive) return;
        this.characterCurrentMana = Math.min(this.characterMaxMana, (this.characterCurrentMana + amount));
    }

    // Standardized skills for GUI access without casting
    public abstract Skill getSkill1();
    public abstract Skill getSkill2(); 
    public abstract Skill getSkill3();

    // Now returns a String to feed directly to the GUI JTextArea
    public abstract String useSkill(int skillNumber, GameCharacter target);
}