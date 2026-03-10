package GUI;

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
        container.add(new LoginScreen(this), "LogIn");
        container.add(new RegisterScreen(this), "Register");
        container.add(new MainMenu(this), "MainMenu");

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