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

    // Define game objects
    private Board board;         // the game board
    private State currentState;  // the current state of the game
    private Seed currentPlayer;  // the current player
    private JLabel statusBar;    // for displaying status message
    private JButton restartButton; // restart the game

    /** Constructor to setup the UI and game components */
    public ConnectFour() {

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
                    //SoundEffect.EAT_FOOD.play();
                    if (col >= 0 && col < Board.COLS) {
                        // Look for an empty cell starting from the bottom row
                        for (int rowI = Board.ROWS -1; rowI >= 0; rowI--) {
                            if (board.cells[rowI][col].content == Seed.NO_SEED) {
                                board.cells[rowI][col].content = currentPlayer; // Make a move
                                //board.stepGame(currentPlayer, rowI, col); // update state
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
                                // Switch player
                                currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
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

        // Set the layout of the main panel
        super.setLayout(new BorderLayout());

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

        // Create a panel to hold the status bar and restart button horizontally
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout()); // Use BorderLayout to place components left and right
        topPanel.add(statusBar, BorderLayout.WEST); // Add status bar to the left
        topPanel.add(restartButton, BorderLayout.EAST); // Add restart button to the right

        // Add the topPanel to the main panel at the top (North)
        super.add(topPanel, BorderLayout.SOUTH);

        // Set preferred size, accounting for the top panel and the game board
        super.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT + 60)); // Increased height for topPanel
        super.setBorder(BorderFactory.createLineBorder(COLOR_BG_STATUS, 2, false));

        // Initialize game
        initGame();
        newGame();
    }

    /** Initialize the game (run once) */
    public void initGame() {
        board = new Board();  // allocate the game-board
    }

    /** Reset the game-board contents and the current-state, ready for new game */
    public void newGame() {
        board.newGame();
        /*for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED; // all cells empty
            }
        }*/
        currentPlayer = Seed.CROSS;    // cross plays first
        currentState = State.PLAYING;  // ready to play
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
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'X' Won! Click to play again.");
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'O' Won! Click to play again.");
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
}