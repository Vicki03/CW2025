package com.comp2042.tetris.model.brick;

import com.comp2042.tetris.util.MatrixOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the "L" shaped Tetris brick (a right-facing L block).
 * <p>
 * This brick consists of three blocks in a line with one block attached
 * below the right side, forming an "L" shape. It supports four rotation states:
 * up, right, down, and left.
 * </p>
 *
 * <p>
 * Each rotation state is stored as a 4×4 integer matrix, where
 * {@code 3} indicates a filled cell and {@code 0} an empty cell.
 * The returned list from {@link #getShapeMatrix()} is deep-copied using
 * {@link MatrixOperations#deepCopyList(List)} to preserve immutability.
 * </p>
 */
final class LBrick implements Brick {

    /** All four rotation states of the L-brick. */
    private final List<int[][]> brickMatrix = new ArrayList<>();

    /**
     * Constructs a new L-brick and initializes its four rotation states.
     * <ul>
     *     <li>State 0 — horizontal with an extra block on the left below.</li>
     *     <li>State 1 — vertical with base on the right side.</li>
     *     <li>State 2 — horizontal flipped version of state 0.</li>
     *     <li>State 3 — vertical flipped version of state 1.</li>
     * </ul>
     */
    public LBrick() {
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 3},
                {0, 3, 0, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 3, 0},
                {0, 0, 3, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 0, 0, 0},
                {0, 0, 3, 0},
                {3, 3, 3, 0},
                {0, 0, 0, 0}
        });
        brickMatrix.add(new int[][]{
                {0, 3, 0, 0},
                {0, 3, 0, 0},
                {0, 3, 3, 0},
                {0, 0, 0, 0}
        });
    }

    /**
     * Returns a deep copy of all four rotation states for this L-brick.
     * <p>
     * Each element of the returned list represents one rotation matrix.
     * </p>
     *
     * @return a list of 4×4 matrices representing the L-brick’s rotations
     */
    @Override
    public List<int[][]> getShapeMatrix() {
        return MatrixOperations.deepCopyList(brickMatrix);
    }
}
