package GameEngines;

import Foundation.*;

public abstract class BattleSystemAbs {
    public abstract GameCharacter selectCharacter(int choice);
    public abstract String performAction(GameCharacter attacker, GameCharacter defender, int action, boolean isPlayer);
    public abstract int getAIAction(GameCharacter ai);
}