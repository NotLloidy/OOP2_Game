package GUI;

import javax.swing.*;
import java.awt.*;

/**
 * A floating in-screen notification label that slides in at the top-centre
 * of the window, stays for a moment, then fades out automatically.
 *
 * Usage (from any screen that holds a reference to GameGUI):
 *   gui.showNotification("Cannot choose same character");
 */
public class NotificationBanner extends JLabel {

    private static final int   HEIGHT      = 44;
    private static final int   SHOW_MS     = 2200;   // how long it stays visible
    private static final Color BG_COLOR    = new Color(20, 20, 20, 215);
    private static final Color TEXT_COLOR  = new Color(255, 80, 80);

    private Timer hideTimer;

    public NotificationBanner() {
        super("", SwingConstants.CENTER);
        setFont(new Font("Impact", Font.PLAIN, 20));
        setForeground(TEXT_COLOR);
        setBackground(BG_COLOR);
        setOpaque(true);
        setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 50, 50)));
        setVisible(false);
    }

    /**
     * Show a message at the top-centre for {@link #SHOW_MS} ms, then hide.
     * Calling again while one is already showing resets the timer.
     */
    public void show(String message) {
        setText(message);
        setVisible(true);
        repaint();

        if (hideTimer != null && hideTimer.isRunning()) hideTimer.stop();
        hideTimer = new Timer(SHOW_MS, e -> setVisible(false));
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    /**
     * Called by the parent layered pane to position this banner at the top.
     */
    public void reposition(int panelWidth) {
        setBounds(0, 0, panelWidth, HEIGHT);
    }
}