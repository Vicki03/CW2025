package com.comp2042.tetris.model.brick;

import java.util.List;

/**
 * Represents a generic Tetris brick shape.
 * <p>
 * Each concrete brick type (e.g., I, O, T, L, J, S, Z) implements this
 * interface to provide its rotation states as 2D integer matrices.
 * The matrices define which cells of the brick are occupied.
 * </p>
 *
 * <p>
 * A cell value of <b>1</b> typically indicates a filled tile, and
 * <b>0</b> an empty one. The list returned by
 * {@link #getShapeMatrix()} contains multiple rotation states,
 * in order (0°, 90°, 180°, 270°).
 * </p>
 */
public interface Brick {

    /**
     * Returns all rotation states of this brick as 2D matrices.
     * <p>
     * Each element of the list represents one rotation state, where
     * {@code matrix[row][col] == 1} means the cell is occupied.
     * </p>
     *
     * @return a list of integer matrices representing the brick’s rotations
     */
    List<int[][]> getShapeMatrix();
}
