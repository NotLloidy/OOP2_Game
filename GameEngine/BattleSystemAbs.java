import Foundation.*;

public abstract class BattleSystemAbs {
    public abstract GameCharacter selectCharacter(int choice);
    public abstract void startBattle(GameCharacter player1, GameCharacter player2);
    public abstract void pveBattle(GameCharacter player1, GameCharacter ai);
    public abstract void pvpBattle(GameCharacter p1, GameCharacter p2);
    public abstract void aiTurn(GameCharacter attacker, GameCharacter defender);
    public abstract void attack(GameCharacter attacker, GameCharacter defender, int action, boolean isPlayer);
    public abstract void arcadeMode(GameCharacter player);
}
    