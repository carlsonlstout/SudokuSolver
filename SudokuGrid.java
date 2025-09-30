/**
 * SudokuGrid.java
 * Purpose: Represents and solves a Sudoku puzzle (supports 4x4 and 9x9).
 * Author: Luke Carlson
 * Date: 9/12/2025
 */

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SudokuGrid {
    private int size;      // Size of grid (4 or 9)
    private int box;       // Size of subgrid (2 or 3)
    private Cell[][] grid; // 2D array of cells
    private Path inputPath;

    /**
     * Constructor for SudokuGrid.
     * @param size Puzzle size (must be 4 or 9).
     */
    public SudokuGrid(int size) {
        if (size != 4 && size != 9)
            throw new IllegalArgumentException("Only 4x4 and 9x9 supported.");
        this.size = size;
        this.box = (int)Math.sqrt(size);
        grid = new Cell[size][size];
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                grid[r][c] = new Cell(r, c);
    }

    public void setInputPath(Path p) { this.inputPath = p; }

    /**
     * Load puzzle data from file.
     * @param file Path to the input file.
     * @return true if puzzle loaded successfully, false if invalid.
     */
    public boolean loadFromFile(Path file) {
        setInputPath(file);
        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
            return false;
        }

        // Parse puzzle values (skip first line with size)
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            if (line.toLowerCase().contains("x")) continue;
            String[] parts = line.split(",");
            if (parts.length != 3) return false;
            try {
                int r = Integer.parseInt(parts[0].trim());
                int c = Integer.parseInt(parts[1].trim());
                int v = Integer.parseInt(parts[2].trim());
                if (!inRange(r) || !inRange(c) || !inRange(v)) return false;
                r--; c--; // Convert to 0-based
                if (!grid[r][c].isEmpty()) return false; // Duplicate assignment
                grid[r][c].setValue(v);
                grid[r][c].setFixed(true);
            } catch (NumberFormatException ex) {
                return false;
            }
        }
        return isConsistent();
    }

    /** Check if number is within valid range */
    private boolean inRange(int x) {
        return x >= 1 && x <= size;
    }

    /** Ensure no conflicts exist in the loaded puzzle */
    private boolean isConsistent() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int v = grid[r][c].getValue();
                if (v != 0) {
                    grid[r][c].setValue(0);
                    boolean ok = isValid(r, c, v);
                    grid[r][c].setValue(v);
                    if (!ok) return false;
                }
            }
        }
        return true;
    }

    /** Check if placing value v at (row,col) is valid */
    private boolean isValid(int row, int col, int v) {
        // Row
        for (int c = 0; c < size; c++)
            if (grid[row][c].getValue() == v) return false;
        // Col
        for (int r = 0; r < size; r++)
            if (grid[r][col].getValue() == v) return false;
        // Box
        int br = (row / box) * box, bc = (col / box) * box;
        for (int r = br; r < br + box; r++)
            for (int c = bc; c < bc + box; c++)
                if (grid[r][c].getValue() == v) return false;
        return true;
    }

    /**
     * Backtracking Sudoku solver.
     * @return true if solved, false if unsolvable.
     */
    public boolean solve() {
        int[] pos = findEmpty();
        if (pos == null) return true; // solved
        int r = pos[0], c = pos[1];
        for (int val = 1; val <= size; val++) {
            if (isValid(r, c, val)) {
                grid[r][c].setValue(val);
                if (solve()) return true;
                grid[r][c].setValue(0);
            }
        }
        return false;
    }

    /** Find the next empty cell, or null if solved */
    private int[] findEmpty() {
        for (int r = 0; r < size; r++)
            for (int c = 0; c < size; c++)
                if (grid[r][c].isEmpty()) return new int[]{r, c};
        return null;
    }

    /** Print puzzle in readable grid form */
    public void printGrid() {
        String sep = "-".repeat(size * 2 + box + 1);
        System.out.println(sep);
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                int v = grid[r][c].getValue();
                System.out.print((v == 0 ? "." : v) + " ");
                if ((c + 1) % box == 0) System.out.print("| ");
            }
            System.out.println();
            if ((r + 1) % box == 0) System.out.println(sep);
        }
    }

    /**
     * Write solution or failure message to .sol file.
     * @param msgIfUnsolvable Message to write if puzzle is unsolvable.
     */
    public void writeSolution(String msgIfUnsolvable) {
        if (inputPath == null) return;
        String out = inputPath.toString().replaceFirst("\\.[^.]+$", "") + ".sol";
        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(out))) {
            if (msgIfUnsolvable != null) {
                bw.write(msgIfUnsolvable);
            } else {
                for (int r = 0; r < size; r++) {
                    for (int c = 0; c < size; c++) {
                        bw.write(Integer.toString(grid[r][c].getValue()));
                        if (c < size - 1) bw.write(" ");
                    }
                    bw.newLine();
                }
            }
            System.out.println("Solution written to: " + out);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}

