package GUI;

import GUI.BattleScreens.ARCADE.ArcadeBattleScreen;
import GUI.BattleScreens.PVP.PVPBattleScreen;
import GUI.BattleScreens.VersusScreen;
import GUI.CharacterSelectScreens.*;
import GUI.BattleScreens.PVE.PVEBattleScreen;
import GUI.CharacterInfo.*;
import GameEngines.*;
import Foundation.GameCharacter;
import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame {

    private final CardLayout cardLayout;
    private final JPanel container;
    private final VersusScreen versusScreen;

    public GameGUI() {
        this.setSize(800,600);        // or match your GIF size
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        // Add all your screens (JPanels) to the container
        container.add(new TitleScreen(this), "TitleScreen");
        container.add(new AccountScreen(this), "AccountScreen");
        container.add(new LoginScreen(this), "LoginScreen");
        container.add(new RegisterScreen(this), "RegisterScreen");
        container.add(new MainMenu(this), "MainMenu");

        //Character Select Screens
        container.add(new SelectAVinScreen(this), "SelectAVinScreen");
        container.add(new SelectBrivanScreen(this), "SelectBrivanScreen");
        container.add(new SelectChungMyungScreen(this), "SelectChungScreen");
        container.add(new SelectKennethScreen(this), "SelectKennethScreen");
        container.add(new SelectSoleilScreen(this), "SelectSoleilScreen");
        container.add(new SelectSungJinWooScreen(this), "SelectSungJinWooScreen");
        container.add(new SelectZakkarrScreen(this), "SelectZakkarScreen");
        container.add(new SelectKijElScreen(this), "SelectKijElScreen");

        // Character Info Screens
        container.add(new AVinInfoScreen(this), "AVinInfoScreen");
        container.add(new BrivanInfoScreen(this), "BrivanInfoScreen");
        container.add(new ChungInfoScreen(this), "ChungInfoScreen");
        container.add(new KennethInfoScreen(this), "KennethInfoScreen");
        container.add(new SoleilInfoScreen(this), "SoleilInfoScreen");
        container.add(new SungJinWooInfoScreen(this), "SungJinWooInfoScreen");
        container.add(new ZakkarrInfoScreen(this), "ZakkarrInfoScreen");
        container.add(new KijElInfoScreen(this), "KijElInfoScreen");

        // Battle Screens
        versusScreen = new VersusScreen();
        container.add(versusScreen, "VersusScreen");
        container.add(new PVEBattleScreen(), "PVEBattleScreen");
        container.add(new PVPBattleScreen(), "PVPBattleScreen");
        container.add(new ArcadeBattleScreen(), "ArcadeBattleScreen");

        this.add(container);           // add container to the single JFrame
        this.setVisible(true);         // only once
    }

    public void showScreen(String name) {
        cardLayout.show(container, name);

        switch (name) {
            case "PVEBattleScreen" -> {
                GameSession session = GameSession.getInstance();
                GameCharacter p1 = session.getPlayer1();
                // Trigger AI selection first so VS screen has both names
                BattleSystem sys = new BattleSystem();
                GameCharacter enemy = sys.selectCharacter((int)(Math.random() * 8) + 1);
                while (enemy.getCharacterName().equals(p1.getCharacterName()))
                    enemy = sys.selectCharacter((int)(Math.random() * 8) + 1);
                session.setPlayer2(enemy);

                cardLayout.show(container, "VersusScreen");
                versusScreen.show(p1.getCharacterName(), enemy.getCharacterName(), () -> {
                    cardLayout.show(container, "PVEBattleScreen");
                    for (Component c : container.getComponents())
                        if (c instanceof PVEBattleScreen s) s.initBattle();
                });
            }

            case "PVPBattleScreen" -> {
                GameSession session = GameSession.getInstance();
                cardLayout.show(container, "VersusScreen");
                versusScreen.show(
                        session.getPlayer1().getCharacterName(),
                        session.getPlayer2().getCharacterName(),
                        () -> {
                            cardLayout.show(container, "PVPBattleScreen");
                            for (Component c : container.getComponents())
                                if (c instanceof PVPBattleScreen s) s.initBattle();
                        }
                );
            }

            case "ArcadeBattleScreen" -> {
                // For Arcade, show VS per-opponent — handled inside ArcadeBattleScreen itself
                cardLayout.show(container, "ArcadeBattleScreen");
                for (Component c : container.getComponents())
                    if (c instanceof ArcadeBattleScreen s) s.initBattle();
            }
        }
    }

    public static void main(String[] args) {
        new GameGUI();
    }
}