package GameEngines;

import Foundation.GameCharacter;
import Foundation.BattleMode;

public class GameSession {

    private static GameSession instance;

    private BattleMode mode;
    private GameCharacter player1;
    private GameCharacter player2;

    private int selectingPlayer = 1;  // 1 = P1 picking, 2 = P2 picking

    public int getSelectingPlayer() { return selectingPlayer; }
    public void setSelectingPlayer(int p) { selectingPlayer = p; }

    // PRIVATE CONSTRUCTOR (Singleton)
    private GameSession() {}

    // GET SINGLE INSTANCE
    public static GameSession getInstance() {
        if (instance == null) {
            instance = new GameSession();
        }
        return instance;
    }

    // =========================
    // MODE
    // =========================
    public BattleMode getMode() {
        return mode;
    }

    public void setMode(BattleMode mode) {
        this.mode = mode;
    }

    // =========================
    // PLAYER 1
    // =========================
    public GameCharacter getPlayer1() {
        return player1;
    }

    public void setPlayer1(GameCharacter player1) {
        this.player1 = player1;
    }

    // =========================
    // PLAYER 2
    // =========================
    public GameCharacter getPlayer2() {
        return player2;
    }

    public void setPlayer2(GameCharacter player2) {
        this.player2 = player2;
    }

    // =========================
    // RESET SESSION
    // =========================
    public void reset() {
        player1 = null;
        player2 = null;
        mode = null;
        selectingPlayer = 1;   // <-- reset selecting turn too
    }
}