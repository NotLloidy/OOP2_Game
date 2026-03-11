package GameEngine;
import Foundation.GameCharacter;

public abstract class BattleSystemAbs {
    public abstract GameCharacter selectCharacter(int choice);
    public abstract void inBattle(GameCharacter player1, GameCharacter ai);
    public abstract void aiTurn(GameCharacter ai, GameCharacter player);
}
