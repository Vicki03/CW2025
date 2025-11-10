package com.comp2042.tetris.model;

import com.comp2042.tetris.util.MatrixOperations;

/**
 * Represents the result of a row-clear operation on the game board.
 * <p>
 * This class is used to report how many rows were cleared, the resulting
 * updated matrix after the clear, and the score bonus earned from it.
 * </p>
 *
 * <p>
 * The {@link #getNewMatrix()} method returns a deep copy of the
 * new board state using {@link MatrixOperations#copy(int[][])} to
 * preserve immutability.
 * </p>
 */
public final class ClearRow {

    /** Number of lines cleared in the operation. */
    private final int linesRemoved;

    /** The resulting board matrix after the cleared lines are removed. */
    private final int[][] newMatrix; //shows the new matrix after clearing the rows

    /** The score bonus awarded for clearing the lines. */
    private final int scoreBonus;

    /**
     * Constructs a {@code ClearRow} result with the given values.
     *
     * @param linesRemoved number of cleared lines
     * @param newMatrix    the updated board matrix after clearing
     * @param scoreBonus   points awarded for this clear
     */
    public ClearRow(int linesRemoved, int[][] newMatrix, int scoreBonus) {
        this.linesRemoved = linesRemoved;
        this.newMatrix = newMatrix;
        this.scoreBonus = scoreBonus;
    }

    /**
     * Returns the number of lines cleared.
     *
     * @return number of cleared lines
     */
    public int getLinesRemoved() {
        return linesRemoved;
    } //getter for no. lines removed

    /**
     * Returns a deep copy of the new matrix after clearing rows.
     *
     * @return a copy of the updated board matrix
     */
    public int[][] getNewMatrix() {
        return MatrixOperations.copy(newMatrix);
    }

    /**
     * Returns the score bonus awarded for this clear.
     *
     * @return score bonus value
     */
    public int getScoreBonus() {
        return scoreBonus;
    }
}
