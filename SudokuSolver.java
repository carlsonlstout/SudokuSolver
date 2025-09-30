/**
 * SudokuSolver.java
 * Purpose: Main driver program for solving Sudoku puzzles.
 * Author: Luke Carlson
 * Date: 9/12/2025
 *
 * Usage:
 * 1. Program asks for input file (e.g., puzzle9.txt).
 * 2. Loads puzzle, validates input.
 * 3. Attempts to solve automatically (backtracking).
 * 4. Prints solution or unsolvable message.
 * 5. Writes result to a .sol file in the same folder.
 */

import java.nio.file.*;
import java.io.*;
import java.util.*;

public class SudokuSolver {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        SudokuGrid grid = null;

        // Prompt until a valid puzzle is loaded
        while (true) {
            System.out.print("Enter path to input file (e.g., puzzle9.txt): ");
            String path = sc.nextLine().trim();
            if (path.isEmpty()) continue;
            Path file = Paths.get(path);
            if (!Files.exists(file)) {
                System.out.println("File not found. Try again.");
                continue;
            }

            // Determine puzzle size from first line
            String firstLine = "";
            try {
                for (String line : Files.readAllLines(file)) {
                    if (!line.trim().isEmpty()) {
                        firstLine = line.trim().toLowerCase();
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading file.");
                continue;
            }
            int size = (firstLine.startsWith("9")) ? 9 : (firstLine.startsWith("4")) ? 4 : -1;
            if (size == -1) {
                System.out.println("First line must be '9x9' or '4x4'.");
                continue;
            }

            // Load the puzzle
            grid = new SudokuGrid(size);
            if (!grid.loadFromFile(file)) {
                System.out.println("Invalid puzzle file format or values. Try again.");
                continue;
            }
            break;
        }

        // Display initial puzzle
        System.out.println("\nInitial puzzle:");
        grid.printGrid();

        // Solve puzzle
        long t0 = System.currentTimeMillis();
        boolean solved = grid.solve();
        long t1 = System.currentTimeMillis();

        // Output results
        if (solved) {
            System.out.println("Solved in " + (t1 - t0) + " ms:");
            grid.printGrid();
            grid.writeSolution(null);
        } else {
            String msg = "Puzzle is not solvable.";
            System.out.println(msg);
            grid.writeSolution(msg);
        }
        sc.close();
    }
}
