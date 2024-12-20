/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #13
 * 1- 5026231020- Diva Nesia Putri
 * 2- 5026231114- Imanuel Dwi Prasetyo
 * 3- 5026231196- Ni Kadek Adelia Paramita Putri
 */

package connectfour;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.sound.sampled.*;
import javax.swing.*;

/**
 * Tic-Tac-Toe: Two-player Graphic version with better OO design.
 * The Board and Cell classes are separated in their own classes.
 */
public class ConnectFour extends JPanel {
    private static final long serialVersionUID = 1L; // to prevent serializable warning

    // Define named constants for the drawing graphics
    public static final String TITLE = "Connect Four";
    public static final Color COLOR_BG = Color.WHITE;
    public static final Color COLOR_BG_STATUS = new Color(216, 216, 216);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);  // Red #EF6950
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225); // Blue #409AE1
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    // Timer constants
    private static final int TIME_LIMIT_SECONDS = 10;
    private static final Color COLOR_TIMER_FULL = new Color(76, 175, 80); // Green
    private static final Color COLOR_TIMER_MEDIUM = new Color(255, 152, 0); // Orange
    private static final Color COLOR_TIMER_LOW = new Color(244, 67, 54); // Red

    // Define game objects
    private Board board;         // the game board
    private State currentState;  // the current state of the game
    private Seed currentPlayer;  // the current player
    private JLabel statusBar;    // for displaying status message
    private JButton restartButton; // restart the game
    private BGM bgm;
    private JButton toggleMusicButton;
    private JPanel timerPanel;   // Panel for visual timer representation

    // Timer-related variables
    private Timer gameTimer;
    private int remainingTime;
    private TimerTask currentTimerTask;

    /** Constructor to setup the UI and game components */
    public ConnectFour() {
        bgm = new BGM("src/audio/bgm-ttt.wav");
        bgm.play();

        board = new Board();

        // This JPanel fires MouseEvent
        super.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {  // mouse-clicked handler
                int mouseX = e.getX();
                int mouseY = e.getY();
                // Get the row and column clicked
                int row = mouseY / Cell.SIZE;
                int col = mouseX / Cell.SIZE;

                if (currentState == State.PLAYING) {
                    if (col >= 0 && col < Board.COLS) {
                        // Look for an empty cell starting from the bottom row
                        for (int rowI = Board.ROWS -1; rowI >= 0; rowI--) {
                            if (board.cells[rowI][col].content == Seed.NO_SEED) {
                                board.cells[rowI][col].content = currentPlayer; // Make a move

                                // Check if the move results in a win
                                if (board.hasWon(currentPlayer, rowI, col)) {
                                    currentState = (currentPlayer == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
                                    SoundEffect.YEAY.play();
                                } else {
                                    // Check for DRAW
                                    boolean isDraw = true;
                                    for (int r = 0; r < Board.ROWS; r++) {
                                        for (int c = 0; c < Board.COLS; c++) {
                                            if (board.cells[r][c].content == Seed.NO_SEED) {
                                                isDraw = false;
                                                break;
                                            }
                                        }
                                        if (!isDraw) break;
                                    }
                                    currentState = isDraw ? State.DRAW : State.PLAYING;
                                }
                                // Play the different sound
                                if (currentPlayer == Seed.CROSS) {
                                    SoundEffect.CROSS.play();
                                } else {
                                    SoundEffect.NOUGHT.play();
                                }

                                // Stop the current timer
                                stopTimer();

                                // Switch player
                                currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                                // Start timer for new player
                                startTimer();
                                break;
                            }
                        }
                    }
                } else {        // game over
                    newGame();  // restart the game
                    SoundEffect.DIE.play();
                }
                // Refresh the drawing canvas
                repaint();  // Callback paintComponent().
            }
        });

        // Create visual timer panel
        timerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                // Calculate timer visualization
                int panelWidth = getWidth();
                int panelHeight = getHeight();

                // Calculate width based on remaining time
                double timeRatio = (double) remainingTime / TIME_LIMIT_SECONDS;
                int timerWidth = (int) (panelWidth * timeRatio);

                // Choose color based on remaining time
                Color timerColor;
                if (timeRatio > 0.5) {
                    timerColor = COLOR_TIMER_FULL;
                } else if (timeRatio > 0.25) {
                    timerColor = COLOR_TIMER_MEDIUM;
                } else {
                    timerColor = COLOR_TIMER_LOW;
                }

                // Draw timer bar
                g.setColor(timerColor);
                g.fillRect(0, 0, timerWidth, panelHeight);

                // Draw time text
                g.setColor(Color.BLACK);
                g.setFont(new Font("Arial", Font.BOLD, 12));
                String timeText = remainingTime + "s";
                FontMetrics fm = g.getFontMetrics();
                int textX = (panelWidth - fm.stringWidth(timeText)) / 2;
                int textY = (panelHeight + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(timeText, textX, textY);
            }
        };
        timerPanel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, 20));
        timerPanel.setBackground(Color.LIGHT_GRAY);

        // Tombol toggle musik
        toggleMusicButton = new JButton("Disable Music"); // Set default to "Disable Music"
        toggleMusicButton.setPreferredSize(new Dimension(120, 30)); // Sesuaikan ukuran tombol
        toggleMusicButton.addActionListener(e -> {
            if (bgm.isMusicEnabled()) {
                bgm.stop();  // Stop music if it's currently playing
                System.out.println("Music stopped");
            } else {
                bgm.play();  // Start playing music if it's currently stopped
                System.out.println("Music started");
            }
            updateMusicButtonText();  // Update the button text
        });

        // Set up status bar
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setBackground(COLOR_BG_STATUS);
        statusBar.setOpaque(true);
        statusBar.setPreferredSize(new Dimension(300, 30)); // Adjust size as needed
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 12));

        // Set up restart button
        restartButton = new JButton("Restart");
        restartButton.setPreferredSize(new Dimension(100, 30));
        restartButton.addActionListener(e -> {
            newGame();  // Restart the game immediately when clicked
            SoundEffect.DIE.play();
            repaint();  // Refresh the view to reset the game
        });

        // Create a panel for the music button
        JPanel musicPanel = new JPanel();
        musicPanel.add(toggleMusicButton);

        // Create a panel for status and restart button
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.add(statusBar, BorderLayout.WEST);
        controlPanel.add(restartButton, BorderLayout.EAST);
        controlPanel.add(musicPanel, BorderLayout.CENTER);

        // Create the bottom panel with timer first, then controls
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(timerPanel, BorderLayout.NORTH);      // Timer at the top of bottom panel
        bottomPanel.add(controlPanel, BorderLayout.CENTER);   // Controls below timer

        // Add the main game board to the center
        super.setLayout(new BorderLayout());
        super.add(bottomPanel, BorderLayout.SOUTH);          // All controls at the bottom

        // Set up the final panel size
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 80));
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Initialize game
        initGame();
        newGame();
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board();  // allocate the game-board
        gameTimer = new Timer(); // initialize the timer
    }

    private void startTimer() {
        stopTimer();
        remainingTime = TIME_LIMIT_SECONDS;

        currentTimerTask = new TimerTask() {
            @Override
            public void run() {
                remainingTime--;

                SwingUtilities.invokeLater(() -> {
                    timerPanel.repaint();

                    if (remainingTime <= 0) {
                        // Switch player due to time out
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;

                        // Stop current timer
                        stopTimer();

                        // Start timer for new player
                        startTimer();

                        // Play sound effect
                        SoundEffect.DIE.play();

                        // Repaint to update status
                        repaint();
                    }
                });
            }
        };

        gameTimer.scheduleAtFixedRate(currentTimerTask, 1000, 1000);
    }

    private void stopTimer() {
        if (currentTimerTask != null) {
            currentTimerTask.cancel();
        }
    }

    /** Reset the game-board contents and the current-state, ready for new game */
    public void newGame() {
        board.newGame();
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState = State.PLAYING;  // ready to play

        // Reset and start timer
        stopTimer();
        startTimer();
    }

    /** Custom painting codes on this JPanel */
    @Override
    public void paintComponent(Graphics g) {  // Callback via repaint()
        super.paintComponent(g);
        setBackground(COLOR_BG); // set its background color

        board.paint(g);  // ask the game board to paint itself

        // Print status-bar message
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText((currentPlayer == Seed.CROSS) ? "X's Turn" : "O's Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
            stopTimer();
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
            stopTimer();
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
            stopTimer();
        }
    }

    /** The entry "main" method */
    public static void play() {
        // Run GUI construction codes in Event-Dispatching thread for thread safety
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(TITLE);
                // Set the content-pane of the JFrame to an instance of main JPanel
                frame.setContentPane(new ConnectFour());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.pack();
                frame.setLocationRelativeTo(null); // center the application window
                frame.setVisible(true);            // show it
            }
        });
    }
    private void updateMusicButtonText() {
        if (bgm.isMusicEnabled()) {
            toggleMusicButton.setText("Disable Music");
        } else {
            toggleMusicButton.setText("Enable Music");
        }
    }
}
