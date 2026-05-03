package GUI.BattleScreens;

import GUI.GameGUI;
import UTILS.FileHandler;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * LeaderboardScreen
 *
 * Displays the top 10 arcade clear times (fastest first).
 * Only player name and time are shown — no rank numbers, no decorations.
 * The panel background is fully transparent so your background image
 * (set via GameGUI / CardLayout) shows through.
 *
 * HOW TO REPOSITION ENTRIES
 * ──────────────────────────
 * All layout knobs are in layoutEntries(). Change the four constants at
 * the top of that method to move the whole block, or tweak individual
 * row spacing from there.
 */
public class LeaderboardScreen extends JPanel {

    private static final String BG_PATH = "Assets/navigation/leaderboard.gif";
    private Image bgImage;

    // ── One label pair per rank (name + time) ─────────────────────────────
    // Up to 10 rows. Both labels are transparent — only the text is visible.
    private static final int MAX_ROWS = 10;
    private final JLabel[] nameLabels = new JLabel[MAX_ROWS];
    private final JLabel[] timeLabels = new JLabel[MAX_ROWS];

    private final GameGUI gui;

    private JButton back;

    public LeaderboardScreen(GameGUI gui) {
        this.gui = gui;
        setLayout(null);
        setOpaque(true); // paintComponent draws the bg image

        bgImage = new ImageIcon(BG_PATH).getImage();

        createLabels();

        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                layoutEntries();
            }
        });

        back = createButton();
        back.addActionListener(e -> gui.showScreen("ArcadeLeaderboardScreen"));
        this.add(back);
    }

    private JButton createButton() {
        JButton btn = new JButton();
        btn.setOpaque(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }


    // ─────────────────────────────────────────────────────────────────────
    // LABEL CREATION
    // Font / colour knobs are here. Edit to restyle.
    // ─────────────────────────────────────────────────────────────────────

    private void createLabels() {

        // ── Styling knobs ─────────────────────────────────────────────────
        Font  NAME_FONT  = new Font("Impact", Font.PLAIN, 20);
        Font  TIME_FONT  = new Font("Impact", Font.PLAIN, 20);
        Color NAME_COLOR = Color.WHITE;
        Color TIME_COLOR = new Color(255, 220, 30); // gold
        // ─────────────────────────────────────────────────────────────────

        for (int i = 0; i < MAX_ROWS; i++) {
            nameLabels[i] = makeTransparentLabel("", NAME_FONT, NAME_COLOR, SwingConstants.LEFT);
            timeLabels[i] = makeTransparentLabel("", TIME_FONT, TIME_COLOR, SwingConstants.RIGHT);
            add(nameLabels[i]);
            add(timeLabels[i]);
        }
    }

    private JLabel makeTransparentLabel(String text, Font font, Color color, int align) {
        JLabel lbl = new JLabel(text, align);
        lbl.setFont(font);
        lbl.setForeground(color);
        lbl.setOpaque(false);          // transparent — only the text shows
        lbl.setBackground(new Color(0, 0, 0, 0));
        return lbl;
    }

    // ─────────────────────────────────────────────────────────────────────
    // REFRESH — call this before showing the screen to pull fresh data
    // ─────────────────────────────────────────────────────────────────────

    public void refresh() {
        List<String[]> top = FileHandler.getTopTenTimes();

        for (int i = 0; i < MAX_ROWS; i++) {
            if (i < top.size()) {
                nameLabels[i].setText(top.get(i)[0]); // username
                timeLabels[i].setText(top.get(i)[1]); // formatted time e.g. "2:22"
            } else {
                nameLabels[i].setText("");             // empty row if fewer than 10 entries
                timeLabels[i].setText("");
            }
        }

        layoutEntries();
        repaint();
    }

    // ─────────────────────────────────────────────────────────────────────
    // LAYOUT
    //
    // ── HOW TO MOVE THE ENTRIES ──────────────────────────────────────────
    // Adjust the four POSITION KNOBS below.
    //   startY      — vertical position of the first row (fraction of height)
    //   rowSpacing  — gap between rows in pixels
    //   nameX       — left edge of the name column (fraction of width)
    //   timeX       — left edge of the time column (fraction of width)
    //   colWidth    — width of each label in pixels
    //   rowHeight   — height of each label in pixels
    // ─────────────────────────────────────────────────────────────────────

    private void layoutEntries() {
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        // ── POSITION KNOBS ────────────────────────────────────────────────
        // startY     : Moves the first entry to align with rank '1'
        // rowSpacing : Gap between the grid lines in your GIF
        // nameX      : Centers the text under the "NAME" column header
        // timeX      : Centers the text under the "TIME" column header
        // ─────────────────────────────────────────────────────────────────
        int startY     = (int)(h * 0.210f); 
        int rowSpacing = (int)(h * 0.058f); 
        int nameX      = (int)(w * 0.440f); 
        int timeX      = (int)(w * 0.610f); 
        int colWidth   = (int)(w * 0.25f);  
        int rowHeight  = 30;               
        // ─────────────────────────────────────────────────────────────────

        for (int i = 0; i < MAX_ROWS; i++) {
            int y = startY + i * rowSpacing;
            nameLabels[i].setBounds(nameX, y, colWidth, rowHeight);
            timeLabels[i].setBounds(timeX, y, (int)(w * 0.15f), rowHeight);
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // PAINT
    // ─────────────────────────────────────────────────────────────────────

    @Override
    public void doLayout() {
        super.doLayout();
        layoutEntries();

        double scaleX = getWidth() / 960.0;
        double scaleY = getHeight() / 540.0;

        back.setBounds(
            (int)(885 * scaleX),
            (int)(10 * scaleY),
            (int)(60 * scaleX),
            (int)(50 * scaleY)
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (bgImage != null)
            g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
        else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}