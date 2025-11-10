package com.comp2042.tetris.model.brick;

import com.comp2042.tetris.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "Z" shaped Tetris brick (the standard zigzag tetromino).
 * <p>
 * This brick forms a Z shape — two blocks on the top row and two offset
 * on the bottom row. It supports two rotation states: horizontal and vertical.
 * </p>
 *
 * <p>
 * Each rotation state is defined as a 4×4 integer matrix, where
 * {@code 7} represents a filled cell and {@code 0} an empty cell.
 * The returned list from {@link #getShapeMatrix()} is deep-copied using
 * {@link MatrixOperations#deepCopyList(List)} to prevent external modification.
 * </p>
 */
final class ZBrick implements Brick {

    /** Both rotation states of the Z-brick (horizontal and vertical). */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new Z-brick and initializes its two rotation states.
     * <ul>
     *   <li>State 0 — horizontal “Z” shape.</li>
     *   <li>State 1 — vertical “Z” shape (rotated 90°).</li>
     * </ul>
     */
    public ZBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {7, 7, 0, 0},
                {0, 7, 7, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 7, 0, 0},
                {7, 7, 0, 0},
                {7, 0, 0, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns a deep copy of the Z-brick’s two rotation states.
     *
     * @return a list of 4×4 matrices representing the Z-brick’s rotations
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
