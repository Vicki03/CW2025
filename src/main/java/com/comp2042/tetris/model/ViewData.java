package com.comp2042.tetris.model;

import com.comp2042.tetris.util.MatrixOperations;

/**
 * Encapsulates the visual data of the active and next bricks for rendering.
 * <p>
 * A {@code ViewData} object contains:
 * <ul>
 *     <li>The current brick’s 2D shape matrix.</li>
 *     <li>The current brick’s position on the board (x, y).</li>
 *     <li>The next brick’s 2D shape matrix for preview display.</li>
 * </ul>
 * </p>
 *
 * <p>
 * This class serves as a lightweight data transfer object between
 * the model ({@link com.comp2042.tetris.model.GameBoard}) and the
 * view ({@link com.comp2042.tetris.controller.GuiController}).
 * </p>
 *
 * <p>
 * All shape matrices returned from getter methods are deep copies,
 * created using {@link MatrixOperations#copy(int[][])}, to preserve
 * immutability and prevent unintended modifications.
 * </p>
 */
//stores current brick's shape and position, and the next brick's shape
public final class ViewData {

    /** 2D array representing the current brick’s shape. */
    private final int[][] brickData; //2D array for current brick's shape

    /** X-coordinate (column) of the current brick’s top-left position. */
    private final int xPosition; //current brick's x position

    /** Y-coordinate (row) of the current brick’s top-left position. */
    private final int yPosition;

    /** 2D array representing the next brick’s shape (for preview). */
    private final int[][] nextBrickData; //2D array for next brick's shape

    /**
     * Constructs a {@code ViewData} instance representing the current and next brick states.
     *
     * @param brickData    the current brick’s 2D shape matrix
     * @param xPosition    the X-coordinate (column) of the current brick
     * @param yPosition    the Y-coordinate (row) of the current brick
     * @param nextBrickData the next brick’s 2D shape matrix for preview
     */
    public ViewData(int[][] brickData, int xPosition, int yPosition, int[][] nextBrickData) {
        this.brickData = brickData;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.nextBrickData = nextBrickData;
    }

    /**
     * Returns a deep copy of the current brick’s shape matrix.
     *
     * @return a copy of the current brick’s 2D array
     */
    public int[][] getBrickData() {
        return MatrixOperations.copy(brickData);
    }

    /**
     * Returns the X-coordinate (column) of the current brick.
     *
     * @return the brick’s X position
     */
    public int getxPosition() {
        return xPosition;
    }

    /**
     * Returns the Y-coordinate (row) of the current brick.
     *
     * @return the brick’s Y position
     */
    public int getyPosition() {
        return yPosition;
    }

    /**
     * Returns a deep copy of the next brick’s shape matrix for preview.
     *
     * @return a copy of the next brick’s 2D array
     */
    @SuppressWarnings("unused")
    public int[][] getNextBrickData() {
        return MatrixOperations.copy(nextBrickData);
    }
}
