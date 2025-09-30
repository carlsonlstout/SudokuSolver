/**
 * Cell.java
 * Purpose: Represents a single cell in a Sudoku puzzle.
 * Author: Luke Carlson
 * Date: 9/12/2025
 */

public class Cell {
    private int row;      // Row index (0-based)
    private int col;      // Column index (0-based)
    private int value;    // 0 = empty, otherwise assigned value
    private boolean fixed; // True if cell was pre-filled in the puzzle

    /**
     * Constructor for a cell in the Sudoku grid.
     * @param row Row index (0-based).
     * @param col Column index (0-based).
     */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.value = 0;
        this.fixed = false;
    }

    // ----- Getters -----
    public int getRow() { return row; }
    public int getCol() { return col; }
    public int getValue() { return value; }
    public boolean isFixed() { return fixed; }
    public boolean isEmpty() { return value == 0; }

    // ----- Setters -----
    public void setValue(int v) { this.value = v; }
    public void setFixed(boolean f) { this.fixed = f; }
}
