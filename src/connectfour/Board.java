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
/**
 * The Board class models the ROWS-by-COLS game board.
 */
public class Board {
    // Define named constants
    public static final int ROWS = 6;  // ROWS x COLS cells
    public static final int COLS = 7;
    // Define named constants for drawing
    public static final int CANVAS_WIDTH = Cell.SIZE * COLS;  // the drawing canvas
    public static final int CANVAS_HEIGHT = Cell.SIZE * ROWS;
    public static final int GRID_WIDTH = 8;  // Grid-line's width
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2; // Grid-line's half-width
    public static final Color COLOR_GRID = Color.LIGHT_GRAY;  // grid lines
    public static final int Y_OFFSET = 1;  // Fine tune for better display

    // Define properties (package-visible)
    /** Composes of 2D array of ROWS-by-COLS Cell instances */
    Cell[][] cells;


    /** Constructor to initialize the game board */
    public Board() {
        initGame();
    }

    /** Initialize the game objects (run once) */
    public void initGame() {
        cells = new Cell[ROWS][COLS]; // allocate the array
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                // Allocate element of the array
                cells[row][col] = new Cell(row, col);
                // Cells are initialized in the constructor
            }
        }
    }

    /** Reset the game board, ready for new game */
    public void newGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].newGame(); // clear the cell content
            }
        }
    }

    /**
     *  The given player makes a move on (selectedRow, selectedCol).
     *  Update cells[selectedRow][selectedCol]. Compute and return the
     *  new game state (PLAYING, DRAW, CROSS_WON, NOUGHT_WON).
     */
    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        // Update game board
        cells[selectedRow][selectedCol].content = player;

        // Compute and return the new game state
        if (hasWon(player, selectedRow, selectedCol)) {
            SoundEffect.YEAY.play();
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        } else {
            // Nobody win. Check for DRAW (all cells occupied) or PLAYING.
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    if (cells[row][col].content == Seed.NO_SEED) {
                        return State.PLAYING; // still have empty cells
                    }
                }
            }
            return State.DRAW; // no empty cell, it's a draw
        }
    }

    public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
        // Check horizontally
        int count = 0;
        for (int col = 0; col < COLS; ++col) {
            if (cells[rowSelected][col].content == theSeed) {
                ++count;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check vertically
        count = 0;
        for (int row = 0; row < ROWS; ++row) {
            if (cells[row][colSelected].content == theSeed) {
                ++count;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check main diagonal (\)
        count = 0;
        for (int delta = -3; delta <= 3; ++delta) {
            int row = rowSelected + delta;
            int col = colSelected + delta;
            if (row >= 0 && row < ROWS && col >= 0 && col < COLS && cells[row][col].content == theSeed) {
                ++count;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        // Check anti-diagonal (/)
        count = 0;
        for (int delta = -3; delta <= 3; ++delta) {
            int row = rowSelected + delta;
            int col = colSelected - delta;
            if (row >= 0 && row < ROWS && col >= 0 && col < COLS && cells[row][col].content == theSeed) {
                ++count;
                if (count == 4) return true;
            } else {
                count = 0;
            }
        }

        return false;
    }


    /** Paint itself on the graphics canvas, given the Graphics context */
    public void paint(Graphics g) {


        // Draw the grid-lines
        g.setColor(COLOR_GRID);
        for (int row = 1; row < ROWS; ++row) {
            g.fillRoundRect(0, Cell.SIZE * row - GRID_WIDTH_HALF,
                    CANVAS_WIDTH - 1, GRID_WIDTH,
                    GRID_WIDTH, GRID_WIDTH);
        }
        for (int col = 1; col < COLS; ++col) {
            g.fillRoundRect(Cell.SIZE * col - GRID_WIDTH_HALF, 0 + Y_OFFSET,
                    GRID_WIDTH, CANVAS_HEIGHT - 1,
                    GRID_WIDTH, GRID_WIDTH);
        }

        // Draw all the cells
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                cells[row][col].paint(g);  // ask the cell to paint itself
            }
        }
    }
}