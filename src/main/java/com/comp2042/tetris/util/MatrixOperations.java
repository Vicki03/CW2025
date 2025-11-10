package com.comp2042.tetris.util;

import com.comp2042.tetris.model.ClearRow;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A utility class providing static helper methods for matrix manipulation
 * within the Tetris game model.
 * <p>
 * The methods in this class handle:
 * <ul>
 *     <li>Collision detection between a brick and the game board.</li>
 *     <li>Deep copying and merging of 2D matrices.</li>
 *     <li>Detecting and removing filled rows.</li>
 *     <li>Creating immutable deep copies of shape lists.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class is non-instantiable and contains only static methods.
 * </p>
 */
public class MatrixOperations {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    //We don't want to instantiate this utility class
    private MatrixOperations(){

    }

    /**
     * Checks whether a brick overlaps with the board or goes out of bounds
     * when placed at the specified position.
     *
     * @param matrix the current game board matrix
     * @param brick  the brick’s 2D shape matrix
     * @param x      the x-coordinate (column) of the brick’s top-left corner
     * @param y      the y-coordinate (row) of the brick’s top-left corner
     * @return {@code true} if the brick would collide or go out of bounds, otherwise {@code false}
     */
    //checks if a brick overlaps with the board or goes out of bounds at (x,y)
    public static boolean intersect(final int[][] matrix, final int[][] brick, int x, int y) {
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0 && (checkOutOfBound(matrix, targetX, targetY) || matrix[targetY][targetX] != 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Helper method to check if a coordinate lies outside the game board bounds.
     *
     * @param matrix   the board matrix
     * @param targetX  the x-coordinate to check
     * @param targetY  the y-coordinate to check
     * @return {@code true} if the position is outside the board, otherwise {@code false}
     */
    //helper method to check if a position is outside the board
    private static boolean checkOutOfBound(int[][] matrix, int targetX, int targetY) {
        return !(targetX >= 0 && targetY < matrix.length && targetX < matrix[targetY].length);
    }

    /**
     * Creates a deep copy of the provided 2D integer matrix.
     *
     * @param original the matrix to copy
     * @return a deep copy of the original matrix
     */
    public static int[][] copy(int[][] original) {
        int[][] myInt = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            int[] aMatrix = original[i];
            int aLength = aMatrix.length;
            myInt[i] = new int[aLength];
            System.arraycopy(aMatrix, 0, myInt[i], 0, aLength);
        }
        return myInt;
    }

    /**
     * Merges a brick’s shape into the board matrix at the specified position.
     * <p>
     * The merge is performed on a deep copy of the board matrix to avoid
     * modifying the original input.
     * </p>
     *
     * @param filledFields the existing game board
     * @param brick        the brick’s 2D shape matrix
     * @param x            x-coordinate (column) where the brick should be merged
     * @param y            y-coordinate (row) where the brick should be merged
     * @return a new matrix representing the updated board after merging
     */
    //merges a brick into the board at (x,y)
    public static int[][] merge(int[][] filledFields, int[][] brick, int x, int y) {
        int[][] copy = copy(filledFields);
        for (int i = 0; i < brick.length; i++) {
            for (int j = 0; j < brick[i].length; j++) {
                int targetX = x + i;
                int targetY = y + j;
                if (brick[j][i] != 0) {
                    copy[targetY][targetX] = brick[j][i];
                }
            }
        }
        return copy;
    }

    /**
     * Checks the board for fully filled rows, removes them, and calculates
     * a score bonus proportional to the number of lines cleared.
     *
     * @param matrix the current game board matrix
     * @return a {@link ClearRow} object describing:
     *         <ul>
     *             <li>Number of cleared lines</li>
     *             <li>Updated board matrix</li>
     *             <li>Score bonus</li>
     *         </ul>
     */
    //checks for filled rows in the board and removes them, returns the number of cleared rows and the new board
    public static ClearRow checkRemoving(final int[][] matrix) {
        int[][] tmp = new int[matrix.length][matrix[0].length];
        Deque<int[]> newRows = new ArrayDeque<>();
        List<Integer> clearedRows = new ArrayList<>();

        for (int i = 0; i < matrix.length; i++) {
            int[] tmpRow = new int[matrix[i].length];
            boolean rowToClear = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (matrix[i][j] == 0) {
                    rowToClear = false;
                }
                tmpRow[j] = matrix[i][j];
            }
            if (rowToClear) {
                clearedRows.add(i);
            } else {
                newRows.add(tmpRow);
            }
        }
        for (int i = matrix.length - 1; i >= 0; i--) {
            int[] row = newRows.pollLast();
            if (row != null) {
                tmp[i] = row;
            } else {
                break;
            }
        }
        int scoreBonus = 50 * clearedRows.size() * clearedRows.size();
        return new ClearRow(clearedRows.size(), tmp, scoreBonus);
    }

    /**
     * Creates a deep copy of a list of 2D matrices.
     * <p>
     * Used by {@link com.comp2042.tetris.model.brick.Brick} implementations
     * to duplicate their rotation matrices safely.
     * </p>
     *
     * @param list the list of 2D arrays to copy
     * @return a new list containing deep-copied matrices
     */
    public static List<int[][]> deepCopyList(List<int[][]> list){
        return list.stream().map(MatrixOperations::copy).collect(Collectors.toList());
    }

}
