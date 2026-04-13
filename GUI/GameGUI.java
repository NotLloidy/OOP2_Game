package GUI;

import GUI.CharacterSelectScreens.*;
import GUI.CharacterInfo.*;
import javax.swing.*;
import java.awt.*;

public class GameGUI extends JFrame {

    private CardLayout cardLayout;
    private JPanel container;

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

        this.add(container);           // add container to the single JFrame
        this.setVisible(true);         // only once
    }

    public void showScreen(String name) {
        cardLayout.show(container, name);
    }

    public static void main(String[] args) {
        new GameGUI();
    }
}